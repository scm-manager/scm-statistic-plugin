package sonia.scm.statistic.update;

import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.update.PropertyFileAccess;
import sonia.scm.version.Version;

import javax.inject.Inject;

import java.io.IOException;

import static sonia.scm.version.Version.parse;

@Extension
public class V1StatisticUpdateStep implements UpdateStep {

  private final PropertyFileAccess propertyFileAccess;

  @Inject
  public V1StatisticUpdateStep(PropertyFileAccess propertyFileAccess) {
    this.propertyFileAccess = propertyFileAccess;
  }

  @Override
  public void doUpdate() throws IOException {
    PropertyFileAccess.StoreFileTools statisticStoreAccess = propertyFileAccess.forStoreName("statistic");
    statisticStoreAccess.forStoreFiles(statisticStoreAccess::moveAsRepositoryStore);
  }

  @Override
  public Version getTargetVersion() {
    return parse("2.0.0");
  }

  @Override
  public String getAffectedDataType() {
    return "sonia.scm.statistic.data.xml";
  }
}
