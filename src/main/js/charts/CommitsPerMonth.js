// @flow
import React from "react";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import { getCommitsPerMonth } from "./../statistics";
import { Line } from "react-chartjs-2";

type Props = {
  statisticData: [],
  options: any,
  t: string => string
};


class CommitsPerMonth extends React.Component<Props> {

  render() {
    const { t, statisticData, options } = this.props;

    let labels = [];
    let datas = [];

    for (let singleCommitsPerMonth of statisticData) {
      labels.push(singleCommitsPerMonth.value);
      datas.push(singleCommitsPerMonth.count);
    }

    const data = {
      labels: labels,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerMonth"),
          backgroundColor: "#33b2e8",
          data: datas
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
