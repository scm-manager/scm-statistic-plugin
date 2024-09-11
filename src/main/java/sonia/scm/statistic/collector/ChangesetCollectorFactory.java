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

package sonia.scm.statistic.collector;

import sonia.scm.repository.Feature;
import sonia.scm.statistic.Statistics;

/**
 * @author Sebastian Sdorra
 */
public final class ChangesetCollectorFactory {

  private ChangesetCollectorFactory() {
  }

  public static ChangesetCollector createCollector(Statistics statistics) {
    if (statistics.getRepositoryService().isSupported(Feature.COMBINED_DEFAULT_BRANCH)) {
      return new CombinedBranchCollector(statistics);
    }
    return new NonCombinedBranchCollector(statistics);
  }

}
