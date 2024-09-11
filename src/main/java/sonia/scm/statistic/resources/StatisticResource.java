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

package sonia.scm.statistic.resources;

import com.google.inject.Inject;
import de.otto.edison.hal.Link;
import de.otto.edison.hal.Links;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import sonia.scm.api.v2.resources.ErrorDto;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.repository.NamespaceAndName;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryManager;
import sonia.scm.statistic.StatisticData;
import sonia.scm.statistic.StatisticManager;
import sonia.scm.web.VndMediaType;

import jakarta.inject.Provider;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static sonia.scm.statistic.StatisticsPermissions.isComputePermitted;
import static sonia.scm.statistic.StatisticsPermissions.isReadPermitted;

/**
 * @author Sebastian Sdorra
 */
@OpenAPIDefinition(tags = {
  @Tag(name = "Statistic Plugin", description = "Statistic plugin provided endpoints")
})
@Path("v2/plugins/statistic")
public class StatisticResource {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;

  @Inject
  public StatisticResource(Provider<ScmPathInfoStore> scmPathInfoStore, RepositoryManager repositoryManager,
                           StatisticManager statisticManager) {
    this.scmPathInfoStore = scmPathInfoStore;
    this.repositoryManager = repositoryManager;
    this.statisticManager = statisticManager;
  }

  @GET
  @Path("{namespace}/{name}")
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "Index of all statistics", description = "Returns an index of all statistics.", tags = "Statistic Plugin")
  @ApiResponse(
    responseCode = "200",
    description = "success",
    content = @Content(
      mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = IndexDto.class)
    )
  )
  @ApiResponse(responseCode = "401", description = "not authenticated / invalid credentials")
  @ApiResponse(responseCode = "403", description = "not authorized, the current user has no privileges to read the repository")
  @ApiResponse(
    responseCode = "500",
    description = "internal server error",
    content = @Content(
      mediaType = VndMediaType.ERROR_TYPE,
      schema = @Schema(implementation = ErrorDto.class)
    )
  )
  public Response getStatisticsIndex(@PathParam("namespace") String namespace, @PathParam("name") String name) {
    Repository repository = repositoryManager.get(new NamespaceAndName(namespace, name));
    if (!isReadPermitted(repository)) {
      throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).build());
    }
    LinkBuilder subResourceLinkBuilder = new LinkBuilder(scmPathInfoStore.get().get(), StatisticResource.class, StatisticSubResource.class)
      .method("getSubResource")
      .parameters(namespace, name);
    Links.Builder statisticLinks = Links.linkingTo().single(
      Link.link("commitsPerAuthor", subResourceLinkBuilder.method("getCommitPerAuthor").parameters().href()),
      Link.link("commitsPerHour", subResourceLinkBuilder.method("getCommitPerHour").parameters().href()),
      Link.link("commitsPerMonth", subResourceLinkBuilder.method("getCommitPerMonth").parameters().href()),
      Link.link("commitsPerYear", subResourceLinkBuilder.method("getCommitPerYear").parameters().href()),
      Link.link("commitsPerWeekday", subResourceLinkBuilder.method("getCommitPerWeekday").parameters().href()),
      Link.link("fileModificationCount", subResourceLinkBuilder.method("getFileModificationCount").parameters().href()),
      Link.link("raw", subResourceLinkBuilder.method("getRaw").parameters().href()),
      Link.link("topModifiedFiles", subResourceLinkBuilder.method("getTopModifiedFiles").parameters().href()),
      Link.link("topWords", subResourceLinkBuilder.method("getTopWords").parameters().href())
    );
    if (isComputePermitted(repository)) {
      statisticLinks.single(Link.link("rebuild", subResourceLinkBuilder.method("rebuild").parameters().href()));
    }
    return Response.ok(new IndexDto(
      statisticLinks.build())).build();
  }

  @Path("{namespace}/{name}")
  public StatisticSubResource getSubResource(@PathParam("namespace") String namespace, @PathParam("name") String name) {
    NamespaceAndName namespaceAndName = new NamespaceAndName(namespace, name);
    Repository repository = repositoryManager.get(namespaceAndName);
    StatisticData data = statisticManager.getData(repository);

    return new StatisticSubResource(statisticManager, repository, data);
  }

  private RepositoryManager repositoryManager;
  private StatisticManager statisticManager;
}
