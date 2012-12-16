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

import com.google.common.base.Throwables;
import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.plugin.ext.Extension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.ChangesetPagingResult;
import sonia.scm.repository.PermissionType;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryException;
import sonia.scm.repository.api.LogCommandBuilder;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.security.RepositoryPermission;
import sonia.scm.statistic.collector.ChangesetCollector;
import sonia.scm.statistic.collector.ChangesetCollectorFactory;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

/**
 *
 * @author Sebastian Sdorra
 */
@Extension
@Singleton
public class StatisticManager
{

  /** Field description */
  private static final int PAGE_SIZE = 100;

  /** Field description */
  private static final String STORE = "statistic";

  /**
   * the logger for StatisticManager
   */
  private static final Logger logger =
    LoggerFactory.getLogger(StatisticManager.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   *
   * @param context
   * @param serviceFactory
   * @param storeFactory
   */
  @Inject
  public StatisticManager(RepositoryServiceFactory serviceFactory,
    DataStoreFactory storeFactory)
  {
    this.serviceFactory = serviceFactory;
    this.store = storeFactory.getStore(StatisticData.class, STORE);
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repository
   *
   * @return
   */
  public boolean contains(Repository repository)
  {
    return store.get(repository.getId()) != null;
  }

  /**
   * Method description
   *
   *
   * @param repository
   *
   * @throws IOException
   */
  public void createStatistic(Repository repository) throws IOException
  {
    checkPermissions(repository, PermissionType.WRITE);

    try
    {
      StatisticData data = createBootstrapStatistic(repository);

      store(repository, data);
    }
    catch (Exception ex)
    {

      Throwables.propagateIfPossible(ex, IOException.class);

      throw new StatisticException(
        "could not create statistic for repository ".concat(
          repository.getName()));
    }
  }

  /**
   * Method description
   *
   *
   * @param repository
   */
  public void remove(Repository repository)
  {
    logger.debug("remove statistic for repository {}", repository.getId());
    store.remove(repository.getId());
  }

  /**
   * Method description
   *
   *
   * @param repository
   * @param data
   *
   * @throws IOException
   */
  public void store(Repository repository, StatisticData data)
    throws IOException
  {
    checkPermissions(repository, PermissionType.WRITE);

    if (logger.isDebugEnabled())
    {
      logger.debug("update statistic for repository {}", repository.getName());
    }

    store.put(repository.getId(), data);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repository
   *
   * @return
   *
   * @throws IOException
   */
  public StatisticData get(Repository repository) throws IOException
  {
    checkPermissions(repository, PermissionType.READ);

    StatisticData data = store.get(repository.getId());

    if (data == null)
    {
      data = new StatisticData();
    }

    return data;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repository
   * @param type
   */
  private void checkPermissions(Repository repository, PermissionType type)
  {
    Subject subject = SecurityUtils.getSubject();

    subject.checkPermission(new RepositoryPermission(repository, type));
  }

  /**
   * Method description
   *
   *
   * @param repository
   *
   *
   * @return
   * @throws IOException
   * @throws RepositoryException
   */
  private StatisticData createBootstrapStatistic(Repository repository)
    throws IOException, RepositoryException
  {
    checkPermissions(repository, PermissionType.WRITE);

    if (logger.isDebugEnabled())
    {
      logger.debug("create bootstrap statistic for repository {}",
        repository.getId());
    }

    StatisticData data = new StatisticData();

    ChangesetCollector collector =
      ChangesetCollectorFactory.createCollector(serviceFactory, repository);

    collector.collect(data, PAGE_SIZE);

    return data;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private RepositoryServiceFactory serviceFactory;

  /** Field description */
  private DataStore<StatisticData> store;
}
