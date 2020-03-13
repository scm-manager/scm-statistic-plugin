import React from "react";
import { Pie } from "react-chartjs-2";
import { StatisticData } from "../DataTypes";
import { withTranslation, WithTranslation } from "react-i18next";

type Props = WithTranslation & {
  statisticData: StatisticData;
  options: any;
};

class CommitsPerAuthor extends React.Component<Props> {
  render() {
    const { t, statisticData, options } = this.props;

    const colors = [
      "#ffc3cf",
      "#ff92a8",
      "#ffdd65",
      "#ffeeae",
      "#1689b2",
      "#33b2e8",
      "#8dd7f1",
      "#00737a",
      "#00a9b3",
      "#00d1df",
      "#b2f1f4"
    ];

    const data = {
      labels: statisticData.value,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerAuthor"),
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

export default withTranslation("plugins")(CommitsPerAuthor);