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
package sonia.scm.statistic.resources;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.repository.Repository;
import sonia.scm.statistic.StatisticCollector;
import sonia.scm.statistic.StatisticData;
import sonia.scm.statistic.StatisticManager;
import sonia.scm.statistic.dto.CommitsPerAuthor;
import sonia.scm.statistic.dto.CommitsPerHour;
import sonia.scm.statistic.dto.CommitsPerMonth;
import sonia.scm.statistic.dto.CommitsPerWeekday;
import sonia.scm.statistic.dto.CommitsPerYear;
import sonia.scm.statistic.dto.FileModificationCount;
import sonia.scm.statistic.dto.TopModifiedFiles;
import sonia.scm.statistic.dto.TopWords;
import sonia.scm.web.VndMediaType;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.Set;

import static sonia.scm.statistic.StatisticsPermissions.isComputePermitted;

/**
 * @author Sebastian Sdorra
 */
public class StatisticSubResource {

  public StatisticSubResource(StatisticManager manager, Repository repository,
                              StatisticData data) {
    this.manager = manager;
    this.repository = repository;
    this.data = data;
  }

  @POST
  @Path("rebuild")
  @Operation(summary = "Rebuild all statistics", description = "Rebuilds all statistics of a specific repository.", tags = "Statistic Plugin")
  @ApiResponse(responseCode = "204", description = "no content")
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user does not have the \"repository:computeStatistics\" privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public void rebuild() {
    if (!isComputePermitted(repository)) {
      throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).build());
    }
    manager.rebuild(repository);
  }

  @GET
  @Path("commits-per-author")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get commits per author statistic", description = "Returns the commits per author statistic of a specific repository.", tags = "Statistic Plugin")
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = CommitsPerAuthor.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user does not have the \"repository:readStatistics\" privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public CommitsPerAuthor getCommitPerAuthor(@QueryParam("limit")
                                             @DefaultValue("10") int limit) {
    return StatisticCollector.collectCommitsPerAuthor(data, limit);
  }

  @GET
  @Path("commits-per-hour")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get commits per hour statistic", description = "Returns the commits per hour statistic of a specific repository.", tags = "Statistic Plugin")
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = CommitsPerHour.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user does not have the \"repository:readStatistics\" privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public CommitsPerHour getCommitPerHour() {
    return StatisticCollector.collectCommitsPerHour(data);
  }

  @GET
  @Path("commits-per-month")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get commits per month statistic", description = "Returns the commits per month statistic of a specific repository.", tags = "Statistic Plugin")
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = CommitsPerMonth.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user does not have the \"repository:readStatistics\" privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public CommitsPerMonth getCommitPerMonth() {
    return StatisticCollector.collectCommitsPerMonth(data);
  }

  @GET
  @Path("commits-per-year")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get commits per year statistic", description = "Returns the commits per year statistic of a specific repository.", tags = "Statistic Plugin")
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = CommitsPerYear.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user does not have the \"repository:readStatistics\" privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public CommitsPerYear getCommitPerYear() {
    return StatisticCollector.collectCommitsPerYear(data);
  }

  @GET
  @Path("commits-per-weekday")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get commits per weekday statistic", description = "Returns the commits per weekday statistic of a specific repository.", tags = "Statistic Plugin")
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = CommitsPerWeekday.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user does not have the \"repository:readStatistics\" privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public CommitsPerWeekday getCommitPerWeekday() {
    return StatisticCollector.collectCommitsPerWeekday(data);
  }

  @GET
  @Path("file-modification-count")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get file modification count statistic", description = "Returns the file modification count statistic of a specific repository.", tags = "Statistic Plugin")
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = FileModificationCount.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user does not have the \"repository:readStatistics\" privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public FileModificationCount getFileModificationCount() {
    return StatisticCollector.collectFileModificationCount(data);
  }

  @GET
  @Path("raw")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get raw statistics", description = "Returns all raw statistics of a specific repository.", hidden = true)
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = StatisticData.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user does not have the \"repository:readStatistics\" privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public StatisticData getRaw() {
    return data;
  }

  @GET
  @Path("top-modified-files")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get top modified files statistic", description = "Returns the most frequently changed files statistic of a specific repository.", tags = "Statistic Plugin")
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = TopModifiedFiles.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user does not have the \"repository:readStatistics\" privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public TopModifiedFiles getTopModifiedFiles(@QueryParam("limit")
                                              @DefaultValue("10") int limit) {
    return StatisticCollector.collectTopModifiedFiles(data, limit);
  }

  @GET
  @Path("top-words")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Get top words statistic", description = "Returns the most used words statistic of a specific repository.", tags = "Statistic Plugin")
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = TopWords.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user does not have the \"repository:readStatistics\" privilege")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public TopWords getTopWords(@QueryParam("limit")
                              @DefaultValue("10") int limit, @QueryParam("excludes") String excludes) {
    TopWords words;

    if (Strings.isNullOrEmpty(excludes)) {
      words = StatisticCollector.collectTopWords(data, limit);
    } else {
      Predicate<String> excludePredicate =
        Predicates.not(Predicates.in(createExcludes(excludes)));

      words = StatisticCollector.collectTopWords(data, excludePredicate, limit);
    }

    return words;
  }

  private Set<String> createExcludes(String excludes) {
    Iterator<String> wordIterator =
      Splitter.on(",").trimResults().omitEmptyStrings().split(
        excludes).iterator();

    return ImmutableSet.copyOf(wordIterator);
  }

  private StatisticData data;
  private StatisticManager manager;
  private Repository repository;

}
