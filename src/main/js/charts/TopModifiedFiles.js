// @flow
import React from "react";
import { Doughnut } from "react-chartjs-2";
import type { StatisticData } from "../DataTypes";
import { translate } from "react-i18next";

type Props = {
  statisticData: StatisticData,
  options: any,
  t: string => string
};

class TopModifiedFiles extends React.Component<Props> {
  render() {
    const { t, options, statisticData } = this.props;

    let colors = [
      "yellow",
      "blue",
      "green",
      "orange",
      "red",
      "purple",
      "cyan",
      "magenta",
      "teal",
      "maroon",
      "navy"
    ];

    const data = {
      labels: statisticData.value,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.topModifiedFiles"),
          backgroundColor: colors,
          data: statisticData.count
        }
      ]
    };

    return (
      <>
        {t("scm-statistic-plugin.charts.topModifiedFiles")}
        <Doughnut data={data} options={options} />
      </>
    );
  }
}

export default translate("plugins")(TopModifiedFiles);
