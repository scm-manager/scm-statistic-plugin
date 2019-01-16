// @flow
import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import StatisticsNavLink from "./StatisticsNavLink";
import Statistics from "./Statistics";
import { Route } from "react-router-dom";

const StatisticNavLink = ({ url }) => {
  return <StatisticsNavLink url={url} />;
};

binder.bind("repository.navigation", StatisticNavLink);

const StatisticRoute = ({ url, repository }) => {
  return (
    <Route
      path={`${url}/statistic`}
      render={() => <Statistics repository={repository} />}
      exact
    />
  );
};

binder.bind("repository.route", StatisticRoute);
