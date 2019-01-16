// @flow
import React from "react";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import { getCommitsPerHour } from "./../statistics";
import { Line } from "react-chartjs-2";

type Props = {
  url: string,
  t: string => string
};

type State = {
  loading: boolean,
  commitsPerHour?: [],
  error?: boolean
};

class CommitsPerHour extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  getStatistics() {
    const { url } = this.props;
    getCommitsPerHour(url).then(result => {
      if (result.error) {
        this.setState({
          loading: false,
          error: result.error
        });
      } else {
        this.setState({
          commitsPerHour: result,
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
    const { loading, error, commitsPerHour } = this.state;

    if (error) {
      return <ErrorNotification error={error} />;
    }

    if (loading || !commitsPerHour) {
      return <Loading />;
    }

    let labels = [];
    let datas = [];

    const options = {
      maintainAspectRatio: false, // Don't maintain w/h ratio
      legend: {
        position: "bottom"
      }
    };
    for (let singleCommitsPerHour of commitsPerHour) {
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
