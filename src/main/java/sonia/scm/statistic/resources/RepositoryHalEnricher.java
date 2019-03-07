package sonia.scm.statistic.resources;

import sonia.scm.api.v2.resources.Enrich;
import sonia.scm.api.v2.resources.HalAppender;
import sonia.scm.api.v2.resources.HalEnricher;
import sonia.scm.api.v2.resources.HalEnricherContext;
import sonia.scm.api.v2.resources.LinkBuilder;
import sonia.scm.api.v2.resources.ScmPathInfoStore;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;

import javax.inject.Inject;
import javax.inject.Provider;

import static sonia.scm.statistic.StatisticsPermissions.isReadPermitted;

@Extension
@Enrich(Repository.class)
public class RepositoryHalEnricher implements HalEnricher {

  private final Provider<ScmPathInfoStore> scmPathInfoStore;

  @Inject
  public RepositoryHalEnricher(Provider<ScmPathInfoStore> scmPathInfoStore) {
    this.scmPathInfoStore = scmPathInfoStore;
  }

  @Override
  public void enrich(HalEnricherContext context, HalAppender appender) {
    Repository repository = context.oneRequireByType(Repository.class);
    if (isReadPermitted(repository)) {
      String statisticsIndexUrl = new LinkBuilder(scmPathInfoStore.get().get(), StatisticResource.class)
        .method("getStatisticsIndex")
        .parameters(repository.getNamespace(), repository.getName())
        .href();
      appender.appendLink("statistics", statisticsIndexUrl);
    }
  }
}
