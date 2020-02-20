/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * <p>
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
 * <p>
 * http://bitbucket.org/sdorra/scm-manager
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

import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
