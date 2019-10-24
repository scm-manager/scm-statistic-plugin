import React from "react";
import { Bar } from "react-chartjs-2";
import { StatisticData } from "../DataTypes";
import { withTranslation, WithTranslation } from "react-i18next";

type Props = WithTranslation & {
  statisticData: StatisticData;
  options: any;
};

class CommitsPerWeekday extends React.Component<Props> {
  render() {
    const { t, statisticData, options } = this.props;

    const data = {
      labels: statisticData.value,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerWeekday"),
          backgroundColor: "#66c5ee",
          data: statisticData.count
        }
      ]
    };

    return (
      <>
        <Bar data={data} options={options} />
      </>
    );
  }
}

export default withTranslation("plugins")(CommitsPerWeekday);
