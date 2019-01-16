// @flow
import React from "react";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import { getCommitsPerAuthor } from "./../statistics";
import { Pie } from "react-chartjs-2";

type Props = {
  url: string,
  t: string => string
};

type State = {
  loading: boolean,
  commitsPerAuthor?: [],
  error?: boolean
};

class CommitsPerAuthor extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  getStatistics() {
    const { url } = this.props;
    getCommitsPerAuthor(url).then(result => {
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

  componentDidMount() {
    this.setState({ ...this.state, loading: true });
    this.getStatistics();
  }

  render() {
    const { t } = this.props;
    const { loading, error, commitsPerAuthor } = this.state;

    if (error) {
      return <ErrorNotification error={error} />;
    }

    if (loading || !commitsPerAuthor) {
      return <Loading />;
    }

    if (commitsPerAuthor.length <= 0) {
      return (
        <div className="notification is-warning">
          {t("scm-statistic-plugin.noData")}
        </div>
      );
    }

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

    const options = {
      maintainAspectRatio: false, // Don't maintain w/h ratio
      legend: {
        position: "bottom"
      }
    };

    for (let singleCommitsPerAuthor of commitsPerAuthor) {
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
