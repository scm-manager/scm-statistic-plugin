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

class FileModificationCount extends React.Component<Props> {
  render() {
    const { t, statisticData, options } = this.props;

    let colors = [];

    for (let fileModification of statisticData.value) {
      switch (fileModification) {
        case "removed":
          colors.push("#ff92a8");
          break;
        case "added":
          colors.push("#00d1df");
          break;
        case "modified":
          colors.push("#ffdd65");
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
        <Pie data={data} options={options} />
      </>
    );
  }
}

export default translate("plugins")(FileModificationCount);
