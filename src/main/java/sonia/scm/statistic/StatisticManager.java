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

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;
import sonia.scm.repository.api.RepositoryServiceFactory;
import sonia.scm.statistic.collector.ChangesetCollector;
import sonia.scm.statistic.collector.ChangesetCollectorFactory;
import sonia.scm.store.DataStore;
import sonia.scm.store.DataStoreFactory;

import java.io.IOException;

/**
 * @author Sebastian Sdorra
 */
@Extension
@Singleton
public class StatisticManager {

  public static final String ACTION_COMPUTE_STATISTICS = "computeStatistics";
  public static final String ACTION_READ_STATISTICS = "readStatistics";

  private static final int PAGE_SIZE = 100;
  private static final String STORE = "statistic";
  private static final Logger LOG =
    LoggerFactory.getLogger(StatisticManager.class);

  private RepositoryServiceFactory serviceFactory;
  private DataStoreFactory storeFactory;

  @Inject
  public StatisticManager(RepositoryServiceFactory serviceFactory, DataStoreFactory storeFactory) {
    this.serviceFactory = serviceFactory;
    this.storeFactory = storeFactory;
  }

  private DataStore<StatisticData> getStore(Repository repository) {
    return storeFactory.withType(StatisticData.class).withName(STORE).forRepository(repository).build();
  }

  public boolean contains(Repository repository) {
    return getStore(repository).get(STORE) != null;
  }

  public void createStatistic(Repository repository) throws IOException {
    RepositoryPermissions.custom(ACTION_COMPUTE_STATISTICS, repository).check();

    try {
      createBootstrapStatistic(repository);
    } catch (Exception ex) {

      Throwables.propagateIfPossible(ex, IOException.class);

      throw new StatisticException(
        "could not create statistic for repository ".concat(
          repository.getName()));
    }
  }

  public void rebuild(Repository repository) {
    RepositoryPermissions.custom(ACTION_COMPUTE_STATISTICS, repository).check();
    LOG.warn("rebuild statistic for repository {}", repository.getId());

    Subject subject = SecurityUtils.getSubject();

    remove(repository);

    Runnable worker = new StatisticBootstrapWorker(this, repository);

    worker = subject.associateWith(worker);

    Thread thread = new Thread(worker, StatisticBootstrapWorker.THREADNAME);

    thread.start();
  }

  public void remove(Repository repository) {
    LOG.debug("try to remove statistic for repository {}",
      repository.getId());

    RepositoryPermissions.custom(ACTION_COMPUTE_STATISTICS, repository).check();
    getStore(repository).remove(STORE);
  }

  void store(Statistics statistics) {
    Repository repository = statistics.getRepository();
    RepositoryPermissions.custom(ACTION_COMPUTE_STATISTICS, repository).check();

    if (LOG.isDebugEnabled()) {
      LOG.debug("update statistic for repository {}", repository.getName());
    }

    getStore(repository).put(STORE, statistics.getStatisticData());
  }

  public Statistics get(Repository repository) {
    StatisticData data = getData(repository);
    return new Statistics(this, serviceFactory.create(repository), data);
  }

  public StatisticData getData(Repository repository) {
    RepositoryPermissions.custom(ACTION_READ_STATISTICS, repository).check();

    StatisticData data = getStore(repository).get(STORE);

    if (data == null) {
      data = new StatisticData();
    }
    return data;
  }

  private void createBootstrapStatistic(Repository repository) throws IOException {
    RepositoryPermissions.custom(ACTION_COMPUTE_STATISTICS, repository).check();

    if (LOG.isDebugEnabled()) {
      LOG.debug("create bootstrap statistic for repository {}",
        repository.getId());
    }

    try (Statistics statistics = new Statistics(this, serviceFactory.create(repository), new StatisticData())) {
      ChangesetCollector collector = ChangesetCollectorFactory.createCollector(statistics);

      collector.collect(statistics, PAGE_SIZE);
      statistics.commit();
    }
  }

}
