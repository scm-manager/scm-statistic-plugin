// @flow
import React from "react";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import { getCommitsPerHour } from "./../statistics";
import { Line } from "react-chartjs-2";

type Props = {
  statisticData: [],
  options: any,
  t: string => string
};


class CommitsPerHour extends React.Component<Props, State> {


  render() {
    const { t, statisticData, options } = this.props;

    let labels = [];
    let datas = [];

    for (let singleCommitsPerHour of statisticData) {
      labels.push(singleCommitsPerHour.value);
      datas.push(singleCommitsPerHour.count);
    }

    const data = {
      labels: labels,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerHour"),
          backgroundColor: "#33b2e8",
          data: datas
        }
      ]
    };

    return (
      <>
        {t("scm-statistic-plugin.charts.commitsPerHour")}
        <Line data={data} options={options} />
      </>
    );
  }
}

export default translate("plugins")(CommitsPerHour);
