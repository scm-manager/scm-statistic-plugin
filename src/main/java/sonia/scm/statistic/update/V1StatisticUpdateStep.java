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

package sonia.scm.statistic.update;

import sonia.scm.migration.UpdateStep;
import sonia.scm.plugin.Extension;
import sonia.scm.update.PropertyFileAccess;
import sonia.scm.version.Version;

import jakarta.inject.Inject;

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
