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

//~--- non-JDK imports --------------------------------------------------------

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

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 *
 * @author Sebastian Sdorra
 */
public class StatisticBootstrapAction implements PrivilegedAction
{

  /** Field description */
  private static final String THREAD_EXECUTOR = "StatisticExcecutor";

  /**
   * the logger for BootstrapExecutor
   */
  private static final Logger logger =
    LoggerFactory.getLogger(BootstrapExecutor.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param repositoryMananger
   * @param manager
   */
  @Inject
  public StatisticBootstrapAction(RepositoryManager repositoryMananger,
    StatisticManager manager)
  {
    this.repositoryMananger = repositoryMananger;
    this.manager = manager;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   */
  @Override
  public void run()
  {
    Subject subject = SecurityUtils.getSubject();
    Runnable runnable = new BootstrapExecutor(repositoryMananger, manager);

    subject.associateWith(runnable);
    new Thread(runnable, THREAD_EXECUTOR).start();
  }

  //~--- inner classes --------------------------------------------------------

  /**
   * Class description
   *
   *
   * @version        Enter version here..., 12/08/02
   * @author         Enter your name here...
   */
  private static class BootstrapExecutor implements Runnable
  {

    /**
     * Constructs ...
     *
     *
     * @param repositoryManager
     * @param manager
     */
    public BootstrapExecutor(RepositoryManager repositoryManager,
      StatisticManager manager)
    {
      this.repositoryManager = repositoryManager;
      this.manager = manager;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     */
    @Override
    public void run()
    {
      ThreadFactory factory =
        new ThreadFactoryBuilder().setNameFormat(
          StatisticBootstrapWorker.THREADNAME_PATTERN).build();
      ExecutorService service =
        new SubjectAwareExecutorService(Executors.newFixedThreadPool(2,
          factory));

      for (Repository repository : repositoryManager.getAll())
      {
        try
        {
          StatisticData data = manager.get(repository);

          if (data == null)
          {
            service.execute(new StatisticBootstrapWorker(manager, repository));
          }
          else if (data.getVersion() != StatisticData.VERSION)
          {
            logger.warn(
              "data version version of {} is {} and not {}, so we have to reindex",
              repository.getId(), data.getVersion(), StatisticData.VERSION);
            manager.remove(repository);
            service.execute(new StatisticBootstrapWorker(manager, repository));
          }
        }
        catch (IOException ex)
        {
          logger.warn(
            "could not create bootstrap statistic for repository ".concat(
              repository.getId()), ex);
        }
      }

      // shutdown executor after all bootstrap jobs are finished
      service.shutdown();
    }

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private StatisticManager manager;

    /** Field description */
    private RepositoryManager repositoryManager;
  }


  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private StatisticManager manager;

  /** Field description */
  private RepositoryManager repositoryMananger;
}
