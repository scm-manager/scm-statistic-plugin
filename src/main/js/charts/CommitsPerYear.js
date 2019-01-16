// @flow
import React from "react";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import { getCommitsPerYear } from "./../statistics";
import { Bar } from "react-chartjs-2";

type Props = {
  statisticData: [],
  options: any,
  t: string => string
};


class CommitsPerYear extends React.Component<Props> {


  render() {
    const { t, statisticData, options } = this.props;


    let labels = [];
    let datas = [];

    for (let singleCommitsPerYear of statisticData) {
      labels.push(singleCommitsPerYear.value);
      datas.push(singleCommitsPerYear.count);
    }

    const data = {
      labels: labels,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerYear"),
          backgroundColor: "#33b2e8",
          data: datas
        }
      ]
    };

    return (
      <>
        {t("scm-statistic-plugin.charts.commitsPerYear")}
        <Bar data={data} options={options} />
      </>
    );
  }
}

export default translate("plugins")(CommitsPerYear);
