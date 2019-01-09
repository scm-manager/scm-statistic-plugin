// @flow
import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import GlobalStatisticNavLink from "./GlobalStatisticNavLink";
import GlobalStatistic from "./GlobalStatistic";
import { Route } from "react-router-dom";

const StatisticRoute = ({ url }) => {
  return <GlobalStatisticNavLink url={url} />;
};

binder.bind("repository.navigation", StatisticRoute); // TODO: rename StatisticRoute

const StatisticRoute2 = ({ url, repository }) => {
  return (
    <Route
      path={`${url}/statistic`}
      render={() => <GlobalStatistic repository={repository} />}
      exact
    />
  );
};

binder.bind("repository.route", StatisticRoute2); // TODO: rename StatisticRoute2
