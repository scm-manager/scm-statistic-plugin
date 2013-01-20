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



package sonia.scm.statistic.resources;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import sonia.scm.repository.Repository;
import sonia.scm.statistic.StatisticCollector;
import sonia.scm.statistic.StatisticData;
import sonia.scm.statistic.StatisticManager;
import sonia.scm.statistic.dto.CommitsPerAuthor;
import sonia.scm.statistic.dto.CommitsPerHour;
import sonia.scm.statistic.dto.CommitsPerMonth;
import sonia.scm.statistic.dto.CommitsPerWeekday;
import sonia.scm.statistic.dto.FileModificationCount;
import sonia.scm.statistic.dto.TopModifiedFiles;
import sonia.scm.statistic.dto.TopWords;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.Iterator;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Sebastian Sdorra
 */
public class StatisticSubResource
{

  /**
   * Constructs ...
   *
   *
   *
   * @param manager
   * @param repository
   * @param data
   */
  public StatisticSubResource(StatisticManager manager, Repository repository,
    StatisticData data)
  {
    this.manager = manager;
    this.repository = repository;
    this.data = data;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @throws IOException
   */
  @POST
  @Path("rebuild")
  public void rebuild() throws IOException
  {
    manager.rebuild(repository);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param repositoryId
   *
   * @param limit
   *
   * @return
   *
   * @throws Exception
   */
  @GET
  @Path("commits-per-author")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public CommitsPerAuthor getCommitPerAuthor(@QueryParam("limit")
  @DefaultValue("10") int limit)
  {
    return StatisticCollector.collectCommitsPerAuthor(data, limit);
  }

  /**
   *  Method description
   *
   *
   *  @return
   */
  @GET
  @Path("commits-per-hour")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public CommitsPerHour getCommitPerHour()
  {
    return StatisticCollector.collectCommitsPerHour(data);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @GET
  @Path("commits-per-month")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public CommitsPerMonth getCommitPerMonth()
  {
    return StatisticCollector.collectCommitsPerMonth(data);
  }

  /**
   *  Method description
   *
   *
   *  @return
   */
  @GET
  @Path("commits-per-weekday")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public CommitsPerWeekday getCommitPerWeekday()
  {
    return StatisticCollector.collectCommitsPerWeekday(data);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @GET
  @Path("commits-per-year")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public CommitsPerMonth getCommitPerYear()
  {
    return StatisticCollector.collectCommitsPerYear(data);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @GET
  @Path("file-modification-count")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public FileModificationCount getFileModificationCount()
  {
    return StatisticCollector.collectFileModificationCount(data);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @GET
  @Path("raw")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public StatisticData getRaw()
  {
    return data;
  }

  /**
   * Method description
   *
   *
   * @param limit
   *
   * @return
   */
  @GET
  @Path("top-modified-files")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public TopModifiedFiles getTopModifiedFiles(@QueryParam("limit")
  @DefaultValue("10") int limit)
  {
    return StatisticCollector.collectTopModifiedFiles(data, limit);
  }

  /**
   * Method description
   *
   *
   * @param limit
   * @param excludes
   *
   * @return
   */
  @GET
  @Path("top-words")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public TopWords getTopWords(@QueryParam("limit")
  @DefaultValue("10") int limit, @QueryParam("excludes") String excludes)
  {
    TopWords words;

    if (Strings.isNullOrEmpty(excludes))
    {
      words = StatisticCollector.collectTopWords(data, limit);
    }
    else
    {
      Predicate<String> excludePredicate =
        Predicates.not(Predicates.in(createExcludes(excludes)));

      words = StatisticCollector.collectTopWords(data, excludePredicate, limit);
    }

    return words;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param excludes
   *
   * @return
   */
  private Set<String> createExcludes(String excludes)
  {
    Iterator<String> wordIterator =
      Splitter.on(",").trimResults().omitEmptyStrings().split(
        excludes).iterator();

    return ImmutableSet.copyOf(wordIterator);
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private StatisticData data;

  /** Field description */
  private StatisticManager manager;

  /** Field description */
  private Repository repository;
}
