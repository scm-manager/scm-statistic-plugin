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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.ChangesetPagingResult;
import sonia.scm.repository.InternalRepositoryException;
import sonia.scm.repository.api.LogCommandBuilder;
import sonia.scm.statistic.Statistics;

import java.io.IOException;

/**
 * @author Sebastian Sdorra
 */
public abstract class AbstractChangesetCollector implements ChangesetCollector {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractChangesetCollector.class);
  private final Statistics statistics;

  protected AbstractChangesetCollector(Statistics statistics) {
    this.statistics = statistics;
  }

  protected void append(LogCommandBuilder logCommand, Statistics statistics, int pageSize)
    throws IOException, InternalRepositoryException {

    // do not cache the whole changesets of a repository
    ChangesetPagingResult result = logCommand.setDisableCache(true)
      .setPagingStart(0)
      .setPagingLimit(pageSize)
      .getChangesets();

    append(statistics, result);

    int total = result.getTotal();

    for (int i = pageSize; i < total; i = i + pageSize) {
      result = logCommand.setPagingStart(i)
        .setPagingLimit(pageSize)
        .getChangesets();

      append(statistics, result);
    }
  }

  private void append(Statistics statistics, ChangesetPagingResult result) throws IOException {
    for (Changeset c : result) {
      statistics.add(c);
    }
  }
}
