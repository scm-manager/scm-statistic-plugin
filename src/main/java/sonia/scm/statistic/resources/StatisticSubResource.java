/**
 * Copyright (c) 2010, Sebastian Sdorra All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of SCM-Manager;
 * nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */



package sonia.scm.statistic.resources;

//~--- non-JDK imports --------------------------------------------------------

import sonia.scm.statistic.StatisticCollector;
import sonia.scm.statistic.StatisticData;
import sonia.scm.statistic.dto.CommitsPerAuthor;
import sonia.scm.statistic.dto.CommitsPerHour;
import sonia.scm.statistic.dto.CommitsPerMonth;

//~--- JDK imports ------------------------------------------------------------

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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
   * @param data
   */
  public StatisticSubResource(StatisticData data)
  {
    this.data = data;
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
    return new CommitsPerHour(data.getCommitsPerHour());
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
  @Path("raw")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public StatisticData getRaw()
  {
    return data;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private StatisticData data;
}
