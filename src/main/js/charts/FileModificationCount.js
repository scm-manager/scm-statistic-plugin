// @flow
import React from "react";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import { getFileModificationCount } from "./../statistics";
import { Pie } from "react-chartjs-2";

type Props = {
  statisticData: [],
  options: any,
  t: string => string
};


class FileModificationCount extends React.Component<Props> {


  render() {
    const { t, statisticData, options } = this.props;


    let labels = [];
    let datas = [];
    for (let singleFileModificationCount of statisticData) {
      labels.push(singleFileModificationCount.value);
      datas.push(singleFileModificationCount.count);
    }

    let colors = [];

    for (let fileModification of statisticData) {
      switch (fileModification.value) {
        case "removed":
          colors.push("#ff3860");
          break;
        case "added":
          colors.push("#23d160");
          break;
        case "modified":
          colors.push("#ffdd57");
          break;
      }
    }

    const data = {
      labels: labels,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.fileModificationCount"),
          backgroundColor: colors,
          data: datas
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
