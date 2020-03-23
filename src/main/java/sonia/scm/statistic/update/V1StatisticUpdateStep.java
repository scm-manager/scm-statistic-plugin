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
