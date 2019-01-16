// @flow
import React from "react";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import { getTopModifiedFiles } from "./../statistics";
import { Doughnut } from "react-chartjs-2";

type Props = {
  url: string,
  t: string => string
};

type State = {
  loading: boolean,
  topModifiedFiles?: [],
  error?: boolean
};

class TopModifiedFiles extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  getStatistics() {
    const { url } = this.props;
    getTopModifiedFiles(url).then(result => {
      if (result.error) {
        this.setState({
          loading: false,
          error: result.error
        });
      } else {
        this.setState({
          topModifiedFiles: result,
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
    const { loading, error, topModifiedFiles } = this.state;

    if (error) {
      return <ErrorNotification error={error} />;
    }

    if (loading || !topModifiedFiles) {
      return <Loading />;
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
      maintainAspectRatio: false,	// Don't maintain w/h ratio
      legend: {
        position: 'bottom'
      }
    };
    for (let singleModifiedFiles of topModifiedFiles) {
      labels.push(singleModifiedFiles.value);
      datas.push(singleModifiedFiles.count);
    }

    const data = {
      labels: labels,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.topModifiedFiles"),
          backgroundColor: colors,
          data: datas
        }
      ]
    };

    return (
      <>
        {t("scm-statistic-plugin.charts.topModifiedFiles")}
        <Doughnut data={data} options={options}/>
      </>
    );
  }
}

export default translate("plugins")(TopModifiedFiles);
