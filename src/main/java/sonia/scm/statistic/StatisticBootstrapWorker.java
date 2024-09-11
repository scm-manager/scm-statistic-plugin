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
import sonia.scm.repository.Repository;

import java.io.IOException;

/**
 * @author Sebastian Sdorra
 */
public class StatisticBootstrapWorker implements Runnable {

  static final String THREADNAME = "StatisticWorker";
  static final String THREADNAME_PATTERN = THREADNAME.concat("-%s");
  private static final Logger LOG = LoggerFactory.getLogger(StatisticBootstrapWorker.class);

  public StatisticBootstrapWorker(StatisticManager manager,
                                  Repository repository) {
    this.manager = manager;
    this.repository = repository;
  }

  @Override
  public void run() {
    try {

      manager.createStatistic(repository);
    } catch (IOException ex) {
      LOG.error(
        "could not create statistic for repository ".concat(
          repository.getName()), ex);
    }
  }

  private StatisticManager manager;
  private Repository repository;
}
