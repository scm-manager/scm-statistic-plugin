import React from "react";
import { Repository } from "@scm-manager/ui-types";
import { Loading, ErrorNotification, Subtitle, Level, SubmitButton, confirmAlert } from "@scm-manager/ui-components";
import { withTranslation, WithTranslation } from "react-i18next";
import {
  Chart,
  CommitsPerAuthor,
  CommitsPerHour,
  CommitsPerMonth,
  CommitsPerYear,
  CommitsPerWeekday,
  FileModificationCount,
  TopModifiedFiles,
  TopWords
} from "./charts";
import {
  getLinksForStatistics,
  getCommitsPerAuthor,
  getTopModifiedFiles,
  getTopWords,
  getFileModificationCount,
  getCommitsPerWeekday,
  getCommitsPerYear,
  getCommitsPerMonth,
  getCommitsPerHour,
  rebuildStatistics
} from "./statistics";
import { StatisticLinks } from "./DataTypes";

type Props = WithTranslation & {
  repository: Repository;
};

type State = {
  loading: boolean;
  error?: boolean;
  statisticsLinks?: StatisticLinks;
};

class GlobalStatistic extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: true
    };
  }

  componentDidMount() {
    const { repository } = this.props;

    getLinksForStatistics(repository._links.statistics.href).then(result => {
      if (result.error) {
        this.setState({
          error: result.error,
          loading: false
        });
      } else {
        this.setState({
          statisticsLinks: result,
          loading: false
        });
      }
    });
  }

  rebuildStatistics = () => {
    const { statisticsLinks } = this.state;
    this.setState({
      ...this.state,
      loading: true
    });

    rebuildStatistics(statisticsLinks.rebuild.href).then(result => {
      if (result.error) {
        this.setState({
          error: result.error,
          loading: false
        });
      } else {
        this.setState({
          loading: false
        });
      }
    });
  };

  confirmRebuildStatistics = () => {
    const { t } = this.props;
    confirmAlert({
      title: t("scm-statistic-plugin.confirmRebuildStatistics.title"),
      message: t("scm-statistic-plugin.confirmRebuildStatistics.message"),
      buttons: [
        {
          label: t("scm-statistic-plugin.confirmRebuildStatistics.submit"),
          onClick: () => this.rebuildStatistics()
        },
        {
          label: t("scm-statistic-plugin.confirmRebuildStatistics.cancel"),
          onClick: () => null
        }
      ]
    });
  };

  render() {
    const { t } = this.props;
    const { loading, error, statisticsLinks } = this.state;

    if (error) {
      return <ErrorNotification error={error} />;
    }

    if (loading || !statisticsLinks) {
      return <Loading />;
    }

    return (
      <>
        <Subtitle subtitle={t("scm-statistic-plugin.title")} />
        <div className="columns is-multiline is-vcentered">
          <Chart
            render={props => <CommitsPerAuthor {...props} />}
            getData={() => getCommitsPerAuthor(statisticsLinks.commitsPerAuthor.href)}
            title={t("scm-statistic-plugin.charts.commitsPerAuthor")}
          />
          <Chart
            render={props => <CommitsPerHour {...props} />}
            getData={() => getCommitsPerHour(statisticsLinks.commitsPerHour.href)}
            title={t("scm-statistic-plugin.charts.commitsPerHour")}
          />
          <Chart
            render={props => <CommitsPerMonth {...props} />}
            getData={() => getCommitsPerMonth(statisticsLinks.commitsPerMonth.href)}
            title={t("scm-statistic-plugin.charts.commitsPerMonth")}
          />
          <Chart
            render={props => <CommitsPerYear {...props} />}
            getData={() => getCommitsPerYear(statisticsLinks.commitsPerYear.href)}
            title={t("scm-statistic-plugin.charts.commitsPerYear")}
          />
          <Chart
            render={props => <CommitsPerWeekday {...props} />}
            getData={() => getCommitsPerWeekday(statisticsLinks.commitsPerWeekday.href)}
            title={t("scm-statistic-plugin.charts.commitsPerWeekday")}
          />
          <Chart
            render={props => <FileModificationCount {...props} />}
            getData={() => getFileModificationCount(statisticsLinks.fileModificationCount.href)}
            title={t("scm-statistic-plugin.charts.fileModificationCount")}
          />
          <Chart
            render={props => <TopWords {...props} />}
            getData={() => getTopWords(statisticsLinks.topWords.href)}
            title={t("scm-statistic-plugin.charts.topWords")}
          />
          <Chart
            render={props => <TopModifiedFiles {...props} />}
            getData={() => getTopModifiedFiles(statisticsLinks.topModifiedFiles.href)}
            title={t("scm-statistic-plugin.charts.topModifiedFiles")}
          />
        </div>
        <Level
          right={
            <SubmitButton
              label={t("scm-statistic-plugin.rebuildButton")}
              action={this.confirmRebuildStatistics}
              disabled={!statisticsLinks.rebuild}
              color="warning"
            />
          }
        />
      </>
    );
  }
}

export default withTranslation("plugins")(GlobalStatistic);
