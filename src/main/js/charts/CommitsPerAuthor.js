// @flow
import React from "react";
import { translate } from "react-i18next";
import { Pie } from "react-chartjs-2";

type Props = {
  statisticData: [],
  options: any,
  t: string => string
};


class CommitsPerAuthor extends React.Component<Props> {


  render() {
    const { t, statisticData, options } = this.props;

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

    for (let singleCommitsPerAuthor of statisticData) {
      labels.push(singleCommitsPerAuthor.value);
      datas.push(singleCommitsPerAuthor.count);
    }

    const data = {
      labels: labels,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerAuthor"),
          backgroundColor: colors,
          data: datas
        }
      ]
    };

    return (
      <>
        {t("scm-statistic-plugin.charts.commitsPerAuthor")}
        <Pie data={data} options={options} />
      </>
    );
  }
}

export default translate("plugins")(CommitsPerAuthor);
