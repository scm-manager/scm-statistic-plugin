// @flow
import React from "react";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import { getFileModificationCount } from "./../statistics";
import { Pie } from "react-chartjs-2";

type Props = {
  url: string,
  t: string => string
};

type State = {
  loading: boolean,
  fileModificationCount?: [],
  error?: boolean
};

class FileModificationCount extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  getStatistics() {
    const { url } = this.props;
    getFileModificationCount(url).then(result => {
      if (result.error) {
        this.setState({
          loading: false,
          error: result.error
        });
      } else {
        this.setState({
          fileModificationCount: result,
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
    const { loading, error, fileModificationCount } = this.state;

    if (error) {
      return <ErrorNotification error={error} />;
    }

    if (loading || !fileModificationCount) {
      return <Loading />;
    }

    if (fileModificationCount.length <= 0) {
      return (
        <div className="notification is-warning">
          {t("scm-statistic-plugin.noData")}
        </div>
      );
    }

    let labels = [];
    let datas = [];
    for (let singleFileModificationCount of fileModificationCount) {
      labels.push(singleFileModificationCount.value);
      datas.push(singleFileModificationCount.count);
    }

    let colors = [];
    const options = {
      maintainAspectRatio: false, // Don't maintain w/h ratio
      legend: {
        position: "bottom"
      }
    };
    for (let fileModification of fileModificationCount) {
      switch (fileModification.value) {
        case "removed":
          colors.push("#ff3860");
          break;
        case "added":
          colors.push("#23d160");
          break;
        case "modified":
          colors.push("#ffdd57");
          break;
      }
    }

    const data = {
      labels: labels,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.fileModificationCount"),
          backgroundColor: colors,
          data: datas
        }
      ]
    };

    return (
      <>
        {t("scm-statistic-plugin.charts.fileModificationCount")}
        <Pie data={data} options={options} />
      </>
    );
  }
}

export default translate("plugins")(FileModificationCount);
