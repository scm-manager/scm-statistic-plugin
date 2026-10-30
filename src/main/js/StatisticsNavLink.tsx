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

import React, { FC } from "react";
import { useTranslation } from "react-i18next";
import { SecondaryNavigationItem } from "@scm-manager/ui-components";
import { useRepositoryFromParams } from "@scm-manager/ui-core";
import { resolveStatistic } from "./routes";

const StatisticsNavLink: FC = () => {
  const [t] = useTranslation("plugins");
  const repository = useRepositoryFromParams();
  const url = resolveStatistic(repository);

  return (
    <SecondaryNavigationItem
      to={url}
      icon="fas fa-chart-pie"
      label={t("scm-statistic-plugin.navLink")}
      title={t("scm-statistic-plugin.navLink")}
    />
  );
};

export default StatisticsNavLink;
