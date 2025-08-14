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
import { Bar } from "react-chartjs-2";
import { StatisticData } from "../DataTypes";
import { withTranslation, WithTranslation } from "react-i18next";

type Props = WithTranslation & {
  statisticData: StatisticData;
  options: any;
};

class CommitsPerYear extends React.Component<Props> {
  render() {
    const { t, statisticData, options } = this.props;

    const data = {
      labels: statisticData.value,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerYear"),
          backgroundColor: "#66c5ee",
          data: statisticData.count
        }
      ]
    };

    return (
      <>
        <Bar data={data} options={options} />
      </>
    );
  }
}

export default withTranslation("plugins")(CommitsPerYear);
