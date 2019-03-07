// @flow
import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import StatisticsNavLink from "./StatisticsNavLink";
import Statistics from "./Statistics";
import { Route } from "react-router-dom";

const statisticPredicate = ({ url, repository }) => {
  return repository._links && repository._links.statistics && repository._links.statistics .href;
};

const StatisticNavLink = ({ url }) => {
  return <StatisticsNavLink url={url} />;
};

binder.bind("repository.navigation", StatisticNavLink, statisticPredicate);

const StatisticRoute = ({ url, repository }) => {
  return (
    <Route
      path={`${url}/statistic`}
      render={() => <Statistics repository={repository} />}
      exact
    />
  );
};

binder.bind("repository.route", StatisticRoute, statisticPredicate);
