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

package sonia.scm.statistic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.Modifications;
import sonia.scm.repository.Repository;
import sonia.scm.repository.api.RepositoryService;

import java.io.IOException;

public class Statistics implements AutoCloseable {

  private static final Logger LOG = LoggerFactory.getLogger(StatisticData.class);

  private StatisticManager statisticManager;
  private RepositoryService repositoryService;
  private StatisticData statisticData;

  Statistics(StatisticManager statisticManager, RepositoryService repositoryService, StatisticData statisticData) {
    this.statisticManager = statisticManager;
    this.repositoryService = repositoryService;
    this.statisticData = statisticData;
  }

  public RepositoryService getRepositoryService() {
    return repositoryService;
  }

  public Repository getRepository() {
    return repositoryService.getRepository();
  }

  public StatisticData getStatisticData() {
    return statisticData;
  }

  public void add(Changeset c) throws IOException {
    Modifications modifications = repositoryService.getModificationsCommand().revision(c.getId()).getModifications();
    statisticData = statisticData.add(c, modifications);
  }

  public void commit() throws IOException {
    statisticManager.store(this);
  }

  @Override
  public void close() {
    repositoryService.close();
  }
}
