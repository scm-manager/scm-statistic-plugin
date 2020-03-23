/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
