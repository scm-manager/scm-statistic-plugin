/**
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
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
import sonia.scm.security.Role;
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
    return getStore(repository).get(repository.getId()) != null;
  }

  public void createStatistic(Repository repository) throws IOException {
    RepositoryPermissions.modify(repository).check();

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
    LOG.warn("rebuild statistic for repository {}", repository.getId());

    Subject subject = SecurityUtils.getSubject();

    subject.checkRole(Role.ADMIN);
    remove(repository);

    Runnable worker = new StatisticBootstrapWorker(this, repository);

    worker = subject.associateWith(worker);

    Thread thread = new Thread(worker, StatisticBootstrapWorker.THREADNAME);

    thread.start();
  }

  public void remove(Repository repository) {
    LOG.debug("try to remove statistic for repository {}",
      repository.getId());

    RepositoryPermissions.modify(repository).check();
    getStore(repository).remove(repository.getId());
  }

  void store(Statistics statistics)
    throws IOException {
    Repository repository = statistics.getRepository();
    RepositoryPermissions.modify(repository).check();

    if (LOG.isDebugEnabled()) {
      LOG.debug("update statistic for repository {}", repository.getName());
    }

    getStore(repository).put(repository.getId(), statistics.getStatisticData());
  }

  public Statistics get(Repository repository) throws IOException {
    StatisticData data = getData(repository);
    return new Statistics(this, serviceFactory.create(repository), data);
  }

  public StatisticData getData(Repository repository) {
    RepositoryPermissions.read(repository).check();

    StatisticData data = getStore(repository).get(repository.getId());

    if (data == null) {
      data = new StatisticData();
    }
    return data;
  }

  private void createBootstrapStatistic(Repository repository) throws IOException {
    RepositoryPermissions.modify(repository).check();

    if (LOG.isDebugEnabled()) {
      LOG.debug("create bootstrap statistic for repository {}",
        repository.getId());
    }

    try (Statistics statistics = new Statistics(this, serviceFactory.create(repository), new StatisticData())) {
      ChangesetCollector collector = ChangesetCollectorFactory.createCollector(statistics);

      collector.collect(statistics, PAGE_SIZE);
    }
  }

}
