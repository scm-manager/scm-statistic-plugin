/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
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
