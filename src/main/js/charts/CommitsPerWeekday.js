// @flow
import React from "react";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import { getCommitsPerWeekday } from "./../statistics";
import { Bar } from "react-chartjs-2";

type Props = {
  url: string,
  t: string => string
};

type State = {
  loading: boolean,
  commitsPerWeekday?: [],
  error?: boolean
};

class CommitsPerWeekday extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  getStatistics() {
    const { url } = this.props;
    getCommitsPerWeekday(url).then(result => {
      if (result.error) {
        this.setState({
          loading: false,
          error: result.error
        });
      } else {
        this.setState({
          commitsPerWeekday: result,
          loading: false
        });
      }
    });
  }

  componentDidMount() {
    this.setState({ ...this.state, loading: true });
    this.getStatistics();
  }

  render() {
    const { t } = this.props;
    const { loading, error, commitsPerWeekday } = this.state;

    if (error) {
      return <ErrorNotification error={error} />;
    }

    if (loading || !commitsPerWeekday) {
      return <Loading />;
    }

    if (commitsPerWeekday.length <= 0) {
      return (
        <div className="notification is-warning">
          {t("scm-statistic-plugin.noData")}
        </div>
      );
    }

    let labels = [];
    let datas = [];
    const options = {
      maintainAspectRatio: false, // Don't maintain w/h ratio
      legend: {
        position: "bottom"
      }
    };
    for (let singleCommitPerWeekday of commitsPerWeekday) {
      labels.push(singleCommitPerWeekday.value);
      datas.push(singleCommitPerWeekday.count);
    }

    const data = {
      labels: labels,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.commitsPerWeekday"),
          backgroundColor: "#33b2e8",
          data: datas
        }
      ]
    };

    return (
      <>
        {t("scm-statistic-plugin.charts.commitsPerWeekday")}
        <Bar data={data} options={options} />
      </>
    );
  }
}

export default translate("plugins")(CommitsPerWeekday);
