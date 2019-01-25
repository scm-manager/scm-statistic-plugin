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
      "#33b2e8",
      "#66c5ee",
      "#99d8f3",
      "#ccecf9",
      "#00d1df",
      "#40dde7",
      "#7fe8ef",
      "#bff3f7",
      "#ffdd57",
      "#ffe681",
      "#ffeeab"
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
