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


package sonia.scm.statistic.collector;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.io.Closeables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sonia.scm.repository.Changeset;
import sonia.scm.repository.ChangesetPagingResult;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryException;
import sonia.scm.repository.api.LogCommandBuilder;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.statistic.StatisticData;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

/**
 *
 * @author Sebastian Sdorra
 */
public abstract class AbstractChangesetCollector implements ChangesetCollector
{

  /**
   * the logger for AbstractChangesetCollector
   */
  private static final Logger logger =
    LoggerFactory.getLogger(AbstractChangesetCollector.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param repositoryServiceFactory
   * @param repository
   */
  protected AbstractChangesetCollector(
    RepositoryServiceFactory repositoryServiceFactory, Repository repository)
  {
    this.repositoryServiceFactory = repositoryServiceFactory;
    this.repository = repository;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repositoryService
   * @param data
   * @param pageSize
   *
   * @throws IOException
   * @throws RepositoryException
   */
  protected abstract void collectChangesets(
    RepositoryService repositoryService, StatisticData data, int pageSize)
    throws IOException, RepositoryException;

  /**
   * Method description
   *
   *
   * @param data
   * @param pageSize
   */
  @Override
  public void collect(StatisticData data, int pageSize)
  {
    RepositoryService repositoryService = null;

    try
    {
      repositoryService = repositoryServiceFactory.create(repository);
      collectChangesets(repositoryService, data, pageSize);
    }
    catch (Exception ex)
    {
      logger.error(
        "could retrieve changesets for repository ".concat(
          repository.getName()), ex);
    }
    finally
    {
      Closeables.closeQuietly(repositoryService);
    }
  }

  /**
   * Method description
   *
   *
   * @param logCommand
   * @param data
   * @param pageSize
   *
   * @throws IOException
   * @throws RepositoryException
   */
  protected void append(LogCommandBuilder logCommand, StatisticData data,
    int pageSize)
    throws IOException, RepositoryException
  {

    // do not cache the whole changesets of a repository
    //J-
    ChangesetPagingResult result = logCommand.setDisableCache(true)
      .setPagingStart(0)
      .setPagingLimit(pageSize)
      .getChangesets();
    //J+

    append(data, result);

    int total = result.getTotal();

    for (int i = pageSize; i < total; i = i + pageSize)
    {
      //J-
      result = logCommand.setPagingStart(i)
        .setPagingLimit(pageSize)
        .getChangesets();
      //J+
      append(data, result);
    }
  }

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

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private Repository repository;

  /** Field description */
  private RepositoryServiceFactory repositoryServiceFactory;
}
