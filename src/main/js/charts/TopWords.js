// @flow
import React from "react";
import { Bar } from "react-chartjs-2";
import type { StatisticData } from "../DataTypes";
import { translate } from "react-i18next";

type Props = {
  statisticData: StatisticData,
  options: any,
  t: string => string
};

class TopWords extends React.Component<Props> {
  render() {
    const { t, statisticData, options } = this.props;

    const data = {
      labels: statisticData.value,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.topWords"),
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

export default translate("plugins")(TopWords);
