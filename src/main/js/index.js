// @flow
import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import StatisticsNavLink from "./StatisticsNavLink";
import Statistics from "./Statistics";
import { Route } from "react-router-dom";

const StatisticRoute = ({ url }) => {
  return <StatisticsNavLink url={url} />;
};

binder.bind("repository.navigation", StatisticRoute); // TODO: rename StatisticRoute

const StatisticRoute2 = ({ url, repository }) => {
  return (
    <Route
      path={`${url}/statistic`}
      render={() => <Statistics repository={repository} />}
      exact
    />
  );
};

binder.bind("repository.route", StatisticRoute2); // TODO: rename StatisticRoute2
