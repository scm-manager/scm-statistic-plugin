// @flow
import React from "react";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import { getTopModifiedFiles } from "./../statistics";
import { Doughnut } from "react-chartjs-2";

type Props = {
  statisticData: [],
  options: any,
  t: string => string
};

class TopModifiedFiles extends React.Component<Props> {

  render() {
    const { t, options, statisticData } = this.props;


    let labels = [];
    let datas = [];
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


    for (let singleModifiedFiles of statisticData) {
      labels.push(singleModifiedFiles.value);
      datas.push(singleModifiedFiles.count);
    }

    const data = {
      labels: labels,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.topModifiedFiles"),
          backgroundColor: colors,
          data: datas
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
