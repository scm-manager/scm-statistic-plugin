// @flow
import React from "react";
import { Line } from "react-chartjs-2";
import type { StatisticData } from "../DataTypes";
import { translate } from "react-i18next";

type Props = {
  statisticData: StatisticData,
  options: any,
  t: string => string
};

class CommitsPerHour extends React.Component<Props> {
  render() {
    const { t, statisticData, options } = this.props;

    const data = {
      labels: statisticData.value,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerHour"),
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

export default translate("plugins")(CommitsPerHour);
