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
          backgroundColor: "#33b2e8",
          data: statisticData.count
        }
      ]
    };

    return (
      <>
        {t("scm-statistic-plugin.charts.commitsPerHour")}
        <Line data={data} options={options} />
      </>
    );
  }
}

export default translate("plugins")(CommitsPerHour);
