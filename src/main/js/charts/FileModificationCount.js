// @flow
import React from "react";
import { Pie } from "react-chartjs-2";
import type StatisticData from "./../DataTypes";
import { translate } from "react-i18next";

type Props = {
  statisticData: StatisticData,
  options: any,
  t: string => string
};

class FileModificationCount extends React.Component<Props> {
  render() {
    const { t, statisticData, options } = this.props;

    let colors = [];

    for (let fileModification of statisticData.value) {
      switch (fileModification) {
        case "removed":
          colors.push("#ff3860");
          break;
        case "added":
          colors.push("#23d160");
          break;
        case "modified":
          colors.push("#ffdd57");
          break;
        default:
          colors.push("#bbb");
          break;
      }
    }

    const data = {
      labels: statisticData.value,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.fileModificationCount"),
          backgroundColor: colors,
          data: statisticData.count
        }
      ]
    };

    return (
      <>
        {t("scm-statistic-plugin.charts.fileModificationCount")}
        <Pie data={data} options={options} />
      </>
    );
  }
}

export default translate("plugins")(FileModificationCount);
