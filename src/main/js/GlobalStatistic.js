// @flow
import React from "react";
import type { Repository } from "@scm-manager/ui-types";
import { Title, Button, ErrorNotification } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import { getCommitsPerAuthor } from "./statistics";
import { Bar, Pie, Line, Doughnut } from "react-chartjs-2";

type Props = {
  repository: Repository,
  t: string => string
};

type State = {
  loading: boolean,
  commitsPerAuthor: string[],
  error?: boolean
};

class GlobalStatistic extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      commitsPerAuthor: [],
      loading: false
    };
  }

  componentDidMount() {
    const { repository } = this.props;

    this.setState({ ...this.state, loading: true });
    getCommitsPerAuthor(
      "http://localhost:8081/scm/api/v2/plugins/statistic/scmadmin/scm-cas-plugin/commits-per-author"
    ).then(result => {
      if (result.error) {
        this.setState({
          loading: false,
          error: result.error
        });
      } else {
        this.setState({
          commitsPerAuthor: result,
          loading: false
        });
      }
    });
  }

  render() {
    const { t } = this.props;
    const { loading, error, commitsPerAuthor } = this.state;

    if (error) {
      return <ErrorNotification error={error} />;
    }

    const data = {
      labels: ["January", "February", "March", "April", "May", "June", "July"],
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerMonth"),
          backgroundColor: "rgb(255, 99, 132)",
          data: [0, 10, 5, 2, 20, 30, 45]
        }
      ]
    };

    const data2 = {
      labels: ["Max", "yop190", "dominik-s", "unknown", "Sebastian Sdorra"],
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerAuthor"),
          backgroundColor: [
            "rgb(255, 99, 132)",
            "green",
            "blue",
            "yellow",
            "orange"
          ],
          data: [0, 10, 27, 30, 45]
        }
      ]
    };

    const data3 = {
      labels: [
        t("scm-statistic-plugin.weekdays.monday"),
        t("scm-statistic-plugin.weekdays.tuesday"),
        t("scm-statistic-plugin.weekdays.wednesday"),
        t("scm-statistic-plugin.weekdays.thursday"),
        t("scm-statistic-plugin.weekdays.friday"),
        t("scm-statistic-plugin.weekdays.saturday"),
        t("scm-statistic-plugin.weekdays.sunday")
      ],
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerWeekday"),
          data: [0, 10, 5, 2, 20, 30, 45]
        }
      ]
    };

    const data4 = {
      labels: ["8", "12", "20", "21", "1", "4", "6"],
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerHour"),
          data: [0, 10, 5, 2, 20, 30, 45]
        }
      ]
    };

    return (
      <>
        <Title title={t("scm-statistic-plugin.title")} />
        <div>
          <div className="columns">
            <div className="column is-half">
              {t("scm-statistic-plugin.charts.commitsPerAuthor")}
              <Pie data={data2} />
            </div>
            <div className="column is-half">
              {t("scm-statistic-plugin.charts.commitsPerHour")}
              <Line data={data4} />
            </div>
          </div>
          <div className="columns">
            <div className="column is-half">
              {t("scm-statistic-plugin.charts.commitsPerMonth")}
              <Bar data={data} />
            </div>
            <div className="column is-half">
              {t("scm-statistic-plugin.charts.commitsPerYear")}
              <Bar data={data3} />
            </div>
          </div>
          <div className="columns">
            <div className="column is-half">
              {t("scm-statistic-plugin.charts.commitsPerWeekday")}
              <Bar data={data3} />
            </div>
            <div className="column is-half">
              {t("scm-statistic-plugin.charts.fileModificationCount")}
              <Doughnut data={data2} />
            </div>
          </div>
          <div className="columns">
            <div className="column is-half">
              {t("scm-statistic-plugin.charts.topModifiedFiles")}
              <Bar data={data3} />
            </div>
            <div className="column is-half">
              {t("scm-statistic-plugin.charts.topWords")}
              <Bar data={data2} />
            </div>
          </div>
        </div>
        <Button label={t("scm-statistic-plugin.rebuildButton")} />
      </>
    );
  }
}

export default translate("plugins")(GlobalStatistic);
