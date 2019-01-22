// @flow
import React from "react";
import { Pie } from "react-chartjs-2";
import type { StatisticData } from "../DataTypes";
import { translate } from "react-i18next";

type Props = {
  statisticData: StatisticData,
  options: any,
  t: string => string
};

class CommitsPerAuthor extends React.Component<Props> {
  render() {
    const { t, statisticData, options } = this.props;

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
          label: t("scm-statistic-plugin.charts.commitsPerAuthor"),
          backgroundColor: colors,
          data: statisticData.count
        }
      ]
    };

    return (
      <>
        {t("scm-statistic-plugin.charts.commitsPerAuthor")}
        <Pie data={data} options={options} />
      </>
    );
  }
}

export default translate("plugins")(CommitsPerAuthor);
