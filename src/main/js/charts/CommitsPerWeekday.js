// @flow
import React from "react";
import { translate } from "react-i18next";
import { Bar } from "react-chartjs-2";
import type StatisticData from "./../DataTypes";

type Props = {
  statisticData: StatisticData,
  options: any,
  t: string => string
};


class CommitsPerWeekday extends React.Component<Props> {


  render() {
    const { t, statisticData, options } = this.props;


    const data = {
      labels: statisticData.value,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerWeekday"),
          backgroundColor: "#33b2e8",
          data: statisticData.count
        }
      ]
    };

    return (
      <>
        {t("scm-statistic-plugin.charts.commitsPerWeekday")}
        <Bar data={data} options={options} />
      </>
    );
  }
}

export default translate("plugins")(CommitsPerWeekday);
