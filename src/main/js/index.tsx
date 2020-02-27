import React from "react";
import { Route } from "react-router-dom";
import { binder } from "@scm-manager/ui-extensions";
import StatisticsNavLink from "./StatisticsNavLink";
import Statistics from "./Statistics";

const statisticPredicate = ({ repository }) => {
  return repository._links && repository._links.statistics && repository._links.statistics.href;
};

const StatisticNavLink = ({ url, collapsedRepositoryMenu  }) => {
  return <StatisticsNavLink url={url} collapsed={collapsedRepositoryMenu} />;
};

binder.bind("repository.navigation", StatisticNavLink, statisticPredicate);

const StatisticRoute = ({ url, repository }) => {
  return <Route path={`${url}/statistic`} render={() => <Statistics repository={repository} />} exact />;
};

binder.bind("repository.route", StatisticRoute, statisticPredicate);
