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

import com.google.inject.Inject;
import sonia.scm.plugin.Extension;
import sonia.scm.web.security.AdministrationContext;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * @author Sebastian Sdorra
 */
@Extension
public class StatisticBootstrapListener implements ServletContextListener {

  @Inject
  public StatisticBootstrapListener(AdministrationContext adminContext) {
    this.adminContext = adminContext;
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
  }

  @Override
  public void contextInitialized(ServletContextEvent event) {
    adminContext.runAsAdmin(StatisticBootstrapAction.class);
  }

  private AdministrationContext adminContext;
}
