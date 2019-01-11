package sonia.scm.statistic.resources;

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.LinkAppender;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.LinkEnricher;
import sonia.scm.api.v2.resources.LinkEnricherContext;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

import javax.inject.Inject;
import javax.inject.Provider;

@Extension
@Enrich(Repository.class)
public class RepositoryLinkEnricher implements LinkEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;

  @Inject
  public RepositoryLinkEnricher(Provider<ScmPathInfoStore> scmPathInfoStore) {
    this.scmPathInfoStore = scmPathInfoStore;
  }

  @Override
  public void enrich(LinkEnricherContext context, LinkAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    if (RepositoryPermissions.read(repository).isPermitted()) {
      String statisticsIndexUrl = new LinkBuilder(scmPathInfoStore.get().get(), StatisticResource.class)
        .method("getStatisticsIndex")
        .parameters(repository.getNamespace(), repository.getName())
        .href();
      appender.appendOne("statistics", statisticsIndexUrl);
    }
  }
}
