/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package sonia.scm.statistic;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.concurrent.SubjectAwareExecutorService;
import org.apache.shiro.subject.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.web.security.PrivilegedAction;

import java.io.IOException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Sebastian Sdorra
 */
public class StatisticBootstrapAction implements PrivilegedAction {

  private static final String THREAD_EXECUTOR = "StatisticExcecutor";
  private static final Logger LOG = LoggerFactory.getLogger(BootstrapExecutor.class);

  @Inject
  public StatisticBootstrapAction(RepositoryManager repositoryMananger,
                                  StatisticManager manager) {
    this.repositoryMananger = repositoryMananger;
    this.manager = manager;
  }

  @Override
  public void run() {
    Subject subject = SecurityUtils.getSubject();
    Runnable runnable = new BootstrapExecutor(repositoryMananger, manager);

    subject.associateWith(runnable);
    new Thread(runnable, THREAD_EXECUTOR).start();
  }

  private static class BootstrapExecutor implements Runnable {

    public BootstrapExecutor(RepositoryManager repositoryManager,
                             StatisticManager manager) {
      this.repositoryManager = repositoryManager;
      this.manager = manager;
    }

    @Override
    public void run() {
      ExecutorService executor = createExecutorService();

      for (Repository repository : repositoryManager.getAll()) {
        try {
          handleRepository(executor, repository);
        } catch (IOException ex) {
          LOG.warn(
            "could not create bootstrap statistic for repository ".concat(
              repository.getId()), ex);
        }
      }

      // shutdown executor after all bootstrap jobs are finished
      executor.shutdown();
    }

    private ExecutorService createExecutorService() {
      ThreadFactory factory = createThreadFactory();

      return new SubjectAwareExecutorService(
        Executors.newFixedThreadPool(2,
          factory)
      );
    }

    private ThreadFactory createThreadFactory() {
      return new ThreadFactoryBuilder().setNameFormat(
        StatisticBootstrapWorker.THREADNAME_PATTERN)
        .build();
    }

    private void handleRepository(ExecutorService executor,
                                  Repository repository)
      throws IOException {
      if (!manager.contains(repository)) {
        executor.execute(new StatisticBootstrapWorker(manager, repository));
      } else {
        StatisticData data = manager.getData(repository);

        if (data.getVersion() != StatisticData.VERSION) {
          LOG.warn(
            "data version version of {} is {} and not {}, so we have to reindex",
            repository.getId(), data.getVersion(), StatisticData.VERSION);
          manager.remove(repository);
          executor.execute(new StatisticBootstrapWorker(manager, repository));
        } else if (LOG.isDebugEnabled()) {
          LOG.debug("index of {} is ok", repository.getId());
        }
      }
    }

    private StatisticManager manager;
    private RepositoryManager repositoryManager;
  }

  private StatisticManager manager;
  private RepositoryManager repositoryMananger;
}
