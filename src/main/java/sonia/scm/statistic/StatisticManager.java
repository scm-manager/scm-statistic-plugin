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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.plugin.ext.Extension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.ChangesetPagingResult;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryException;
import sonia.scm.repository.api.LogCommandBuilder;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
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
   * @param data
   *
   * @throws IOException
   */
  public void store(Repository repository, StatisticData data)
    throws IOException
  {
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
   * @param data
   * @param result
   */
  private void append(StatisticData data, ChangesetPagingResult result)
  {
    for (Changeset c : result)
    {
      data.add(c);
    }
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
    if (logger.isDebugEnabled())
    {
      logger.debug("create bootstrap statistic for repository {}",
        repository.getId());
    }

    StatisticData data = new StatisticData();
    RepositoryService service = null;

    try
    {
      service = serviceFactory.create(repository);

      LogCommandBuilder log = service.getLogCommand();

      // do not cache the whole changesets of a repository
      //J-
      ChangesetPagingResult result = log.setDisableCache(true)
        .setPagingStart(0)
        .setPagingLimit(PAGE_SIZE)
        .getChangesets();
      //J+

      append(data, result);

      int total = result.getTotal();

      for (int i = PAGE_SIZE; i < total; i = i + PAGE_SIZE)
      {
        //J-
        result = log.setPagingStart(i)
          .setPagingLimit(PAGE_SIZE)
          .getChangesets();
        //J+
        append(data, result);
      }
    }
    finally
    {
      Closeables.closeQuietly(service);
    }

    return data;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private RepositoryServiceFactory serviceFactory;

  /** Field description */
  private DataStore<StatisticData> store;
}
