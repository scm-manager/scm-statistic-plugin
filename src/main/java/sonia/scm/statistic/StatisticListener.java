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

import com.github.legman.Subscribe;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sonia.scm.EagerSingleton;
import sonia.scm.HandlerEventType;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Changeset;
import sonia.scm.repository.PostReceiveRepositoryHookEvent;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryEvent;
import sonia.scm.web.security.AdministrationContext;

import java.io.IOException;

/**
 * @author Sebastian Sdorra
 */
@Extension
@EagerSingleton
public class StatisticListener {
  private static final Logger LOG = LoggerFactory.getLogger(StatisticListener.class);

  private final StatisticManager statisticManager;

  private final AdministrationContext administrationContext;

  @Inject
  public StatisticListener(AdministrationContext administrationContext, StatisticManager statisticManager) {
    this.administrationContext = administrationContext;
    this.statisticManager = statisticManager;
  }

  @Subscribe
  public void onHookEvent(PostReceiveRepositoryHookEvent event) {
    LOG.debug("update statistic of repository {}", event.getRepository().getName());
    administrationContext.runAsAdmin(() -> {
      try (Statistics statistics = statisticManager.get(event.getRepository())) {
        for (Changeset c : event.getContext().getChangesetProvider().getChangesets()) {
          statistics.add(c);
        }
        statistics.commit();
      } catch (IOException ex) {
        LOG.error("could not update statistic", ex);
      }
    });
  }
}
