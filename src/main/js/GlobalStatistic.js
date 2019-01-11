// @flow
import React from "react";
import type { Repository } from "@scm-manager/ui-types";
import { Title, Button, ErrorNotification } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import {
  getCommitsPerAuthor,
  getCommitsPerHour,
  getCommitsPerMonth,
  getCommitsPerYear,
  getCommitsPerWeekday,
  getFileModificationCount,
  getTopModifiedFiles,
  getTopWords
} from "./statistics";
import { Bar, Pie, Line, Doughnut } from "react-chartjs-2";

type Props = {
  repository: Repository,
  t: string => string
};

type State = {
  loading: boolean,
  commitsPerAuthor?: [], //Todo
  commitsPerHour?: [],
  commitsPerMonth?: [],
  commitsPerYear?: [],
  commitsPerWeekday?: [],
  fileModificationCount?: [],
  topModifiedFiles?: [],
  topWords?: [],
  error?: boolean
};

class GlobalStatistic extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  getDataFromApi(func: any, url: string, name: string) {
    const { repository } = this.props;

    func(
      "http://localhost:8081/scm/api/v2/plugins/statistic/" +
        repository.namespace +
        "/" +
        repository.name +
        "/" +
        url
    ).then(result => {
      if (result.error) {
        this.setState({
          loading: false,
          error: result.error
        });
      } else {
        this.setState({
          [name]: result,
          loading: false
        });
      }
    });
  }

  componentDidMount() {
    const { repository } = this.props;

    this.setState({ ...this.state, loading: true });

    this.getDataFromApi(
      getCommitsPerAuthor,
      "commits-per-author",
      "commitsPerAuthor"
    );
    this.getDataFromApi(
      getCommitsPerHour,
      "commits-per-hour",
      "commitsPerHour"
    );
    this.getDataFromApi(
      getCommitsPerMonth,
      "commits-per-month",
      "commitsPerMonth"
    );
    this.getDataFromApi(
      getCommitsPerYear,
      "commits-per-year",
      "commitsPerYear"
    );
    this.getDataFromApi(
      getCommitsPerWeekday,
      "commits-per-weekday",
      "commitsPerWeekday"
    );
    this.getDataFromApi(
      getFileModificationCount,
      "file-modification-count",
      "fileModificationCount"
    );
    this.getDataFromApi(
      getTopModifiedFiles,
      "top-modified-files",
      "topModifiedFiles"
    );
    this.getDataFromApi(getTopWords, "top-words", "topWords");
  }

  getRandomColor() {
    var letters = "0123456789ABCDEF".split("");
    var color = "#";
    for (var i = 0; i < 5; i++) {
      color += letters[Math.floor(Math.random() * 16)];
    }
    color += "f";
    return color;
  }

  showCommitsPerAuthor() {
    const { t } = this.props;
    const { commitsPerAuthor } = this.state;

    if (commitsPerAuthor) {
      let labels = [];
      let datas = [];
      let colors = [];
      for (let v of commitsPerAuthor) {
        labels.push(v.value);
        datas.push(v.count);
        colors.push(this.getRandomColor());
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
        <div className="column is-half">
          {t("scm-statistic-plugin.charts.commitsPerAuthor")}
          <Pie data={data} />
        </div>
      );
    } else {
      return null;
    }
  }

  showCommitsPerHour() {
    const { t } = this.props;
    const { commitsPerHour } = this.state;

    if (commitsPerHour) {
      let labels = [];
      let datas = [];
      for (let v of commitsPerHour) {
        labels.push(v.value);
        datas.push(v.count);
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
        <div className="column is-half">
          {t("scm-statistic-plugin.charts.commitsPerHour")}
          <Line data={data} />
        </div>
      );
    } else {
      return null;
    }
  }

  showCommitsPerMonth() {
    const { t } = this.props;
    const { commitsPerMonth } = this.state;

    if (commitsPerMonth) {
      let labels = [];
      let datas = [];
      for (let v of commitsPerMonth) {
        labels.push(v.value);
        datas.push(v.count);
      }

      const data = {
        labels: labels,
        datasets: [
          {
            label: t("scm-statistic-plugin.charts.commitsPerMonth"),
            backgroundColor: "#33b2e8",
            data: datas
          }
        ]
      };

      return (
        <div className="column is-half">
          {t("scm-statistic-plugin.charts.commitsPerMonth")}
          <Line data={data} />
        </div>
      );
    } else {
      return null;
    }
  }

  showCommitsPerYear() {
    const { t } = this.props;
    const { commitsPerYear } = this.state;

    if (commitsPerYear) {
      let labels = [];
      let datas = [];
      for (let v of commitsPerYear) {
        labels.push(v.value);
        datas.push(v.count);
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
        <div className="column is-half">
          {t("scm-statistic-plugin.charts.commitsPerYear")}
          <Bar data={data} />
        </div>
      );
    } else {
      return null;
    }
  }

  showCommitsPerWeekday() {
    const { t } = this.props;
    const { commitsPerWeekday } = this.state;

    if (commitsPerWeekday) {
      let labels = [];
      let datas = [];
      for (let v of commitsPerWeekday) {
        labels.push(v.value);
        datas.push(v.count);
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
        <div className="column is-half">
          {t("scm-statistic-plugin.charts.commitsPerWeekday")}
          <Bar data={data} />
        </div>
      );
    } else {
      return null;
    }
  }

  showFileModificationCount() {
    const { t } = this.props;
    const { fileModificationCount } = this.state;

    if (fileModificationCount) {
      let labels = [];
      let datas = [];
      for (let v of fileModificationCount) {
        labels.push(v.value);
        datas.push(v.count);
      }

      let colors = [];
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
        <div className="column is-half">
          {t("scm-statistic-plugin.charts.fileModificationCount")}
          <Doughnut data={data} />
        </div>
      );
    } else {
      return null;
    }
  }

  showTopModifiedFiles() {
    const { t } = this.props;
    const { topModifiedFiles } = this.state;

    if (topModifiedFiles) {
      let labels = [];
      let datas = [];
      let colors = [];
      for (let v of topModifiedFiles) {
        labels.push(v.value);
        datas.push(v.count);
        colors.push(this.getRandomColor());
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
        <div className="column is-half">
          {t("scm-statistic-plugin.charts.topModifiedFiles")}
          <Doughnut data={data} />
        </div>
      );
    } else {
      return null;
    }
  }

  showTopWords() {
    const { t } = this.props;
    const { topWords } = this.state;

    if (topWords) {
      let labels = [];
      let datas = [];
      for (let v of topWords) {
        labels.push(v.value);
        datas.push(v.count);
      }

      const data = {
        labels: labels,
        datasets: [
          {
            label: t("scm-statistic-plugin.charts.topWords"),
            backgroundColor: "#33b2e8",
            data: datas
          }
        ]
      };

      return (
        <div className="column is-half">
          {t("scm-statistic-plugin.charts.topWords")}
          <Bar data={data} />
        </div>
      );
    } else {
      return null;
    }
  }

  render() {
    const { t } = this.props;
    const { loading, error } = this.state;

    if (error) {
      return <ErrorNotification error={error} />;
    }

    return (
      <>
        <Title title={t("scm-statistic-plugin.title")} />
        <div>
          <div className="columns">
            {this.showCommitsPerAuthor()}
            {this.showCommitsPerHour()}
          </div>
          <div className="columns">
            {this.showCommitsPerMonth()}
            {this.showCommitsPerYear()}
          </div>
          <div className="columns">
            {this.showCommitsPerWeekday()}
            {this.showFileModificationCount()}
          </div>
          <div className="columns">
            {this.showTopModifiedFiles()}
            {this.showTopWords()}
          </div>
        </div>
        <Button label={t("scm-statistic-plugin.rebuildButton")} />
      </>
    );
  }
}

export default translate("plugins")(GlobalStatistic);
