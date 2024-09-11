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
import { Line } from "react-chartjs-2";
import { StatisticData } from "../DataTypes";
import { withTranslation, WithTranslation } from "react-i18next";

type Props = WithTranslation & {
  statisticData: StatisticData;
  options: any;
};

class CommitsPerMonth extends React.Component<Props> {
  render() {
    const { t, statisticData, options } = this.props;

    const data = {
      labels: statisticData.value,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerMonth"),
          backgroundColor: "#66c5ee",
          borderColor: "#33b2e8",
          pointBackgroundColor: "#33b2e8",
          pointBorderColor: "#1689b2",
          data: statisticData.count
        }
      ]
    };

    return (
      <>
        <Line data={data} options={options} />
      </>
    );
  }
}

export default withTranslation("plugins")(CommitsPerMonth);
