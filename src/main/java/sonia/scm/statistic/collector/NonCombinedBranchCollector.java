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

import sonia.scm.repository.Branch;
import sonia.scm.repository.Branches;
import sonia.scm.repository.InternalRepositoryException;
import sonia.scm.repository.api.Command;
import sonia.scm.repository.api.LogCommandBuilder;
import sonia.scm.repository.api.RepositoryService;
import sonia.scm.statistic.Statistics;

import java.io.IOException;

/**
 * @author Sebastian Sdorra
 */
public class NonCombinedBranchCollector extends AbstractChangesetCollector {

  NonCombinedBranchCollector(Statistics statistics) {
    super(statistics);
  }

  @Override
  public void collect(Statistics statistics, int pageSize)
    throws IOException, InternalRepositoryException {
    RepositoryService repositoryService = statistics.getRepositoryService();
    if (repositoryService.isSupported(Command.BRANCHES)) {
      Branches branches = repositoryService.getBranchesCommand().getBranches();

      if (branches != null) {
        LogCommandBuilder logCommand = repositoryService.getLogCommand();

        for (Branch branch : branches) {
          logCommand.setBranch(branch.getName());
          append(logCommand, statistics, pageSize);
        }
      }
    }
  }

}
