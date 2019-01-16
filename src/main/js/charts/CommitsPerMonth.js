// @flow
import React from "react";
import { translate } from "react-i18next";
import { Line } from "react-chartjs-2";
import type StatisticData from "./../DataTypes";

type Props = {
  statisticData: StatisticData,
  options: any,
  t: string => string
};


class CommitsPerMonth extends React.Component<Props> {

  render() {
    const { t, statisticData, options } = this.props;



    const data = {
      labels: statisticData.value,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerMonth"),
          backgroundColor: "#33b2e8",
          data: statisticData.count
        }
      ]
    };

    return (
      <>
        {t("scm-statistic-plugin.charts.commitsPerMonth")}
        <Line data={data} options={options} />
      </>
    );
  }
}

export default translate("plugins")(CommitsPerMonth);
