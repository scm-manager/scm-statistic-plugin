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

import React from "react";
import { Route } from "react-router-dom";
import { binder } from "@scm-manager/ui-extensions";
import StatisticsNavLink from "./StatisticsNavLink";
import Statistics from "./Statistics";

const statisticPredicate = ({ repository }) => {
  return repository._links && repository._links.statistics && repository._links.statistics.href;
};

const StatisticNavLink = ({ url }) => {
  return <StatisticsNavLink url={url}/>;
};

binder.bind("repository.navigation", StatisticNavLink, statisticPredicate);

const StatisticRoute = ({ url, repository }) => {
  return <Route path={`${url}/statistic`} render={() => <Statistics repository={repository} />} exact />;
};

binder.bind("repository.route", StatisticRoute, statisticPredicate);
