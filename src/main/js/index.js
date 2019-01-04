// @flow
import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import { NavLink } from "@scm-manager/ui-components";
import GlobalStatistic from "./GlobalStatistic";
import { Route } from "react-router-dom";

const StatisticRoute = ({ url }) => {
  return (
    <NavLink to={`${url}/statistic`} label="scm-statistic-plugin.nav-link" />
  );
};

binder.bind("repository.navigation", StatisticRoute); // TODO: rename StatisticRoute

const StatisticRoute2 = ({ url }) => {
  return (
    <Route path={`${url}/statistic`} render={() => <GlobalStatistic />} exact />
  );
};

binder.bind("repository.route", StatisticRoute2); // TODO: rename StatisticRoute2
