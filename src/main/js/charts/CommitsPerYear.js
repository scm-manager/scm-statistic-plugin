// @flow
import React from "react";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import { getCommitsPerYear } from "./../statistics";
import { Bar } from "react-chartjs-2";

type Props = {
  url: string,
  t: string => string
};

type State = {
  loading: boolean,
  commitsPerYear?: [],
  error?: boolean
};

class CommitsPerYear extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  getStatistics() {
    const { url } = this.props;
    getCommitsPerYear(url).then(result => {
      if (result.error) {
        this.setState({
          loading: false,
          error: result.error
        });
      } else {
        this.setState({
          commitsPerYear: result,
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
    const { loading, error, commitsPerYear } = this.state;

    if (error) {
      return <ErrorNotification error={error} />;
    }

    if (loading || !commitsPerYear) {
      return <Loading />;
    }

    let labels = [];
    let datas = [];
    const options = {
      maintainAspectRatio: false,	// Don't maintain w/h ratio
      legend: {
        position: 'bottom'
      }
    };
    for (let singleCommitsPerYear of commitsPerYear) {
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
        <Bar data={data} options={options}/>
      </>
    );
  }
}

export default translate("plugins")(CommitsPerYear);
