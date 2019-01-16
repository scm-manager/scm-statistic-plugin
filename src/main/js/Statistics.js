// @flow
import React from "react";
import type { Repository } from "@scm-manager/ui-types";
import {
  Loading,
  ErrorNotification,
  Title,
  SubmitButton,
  confirmAlert
} from "@scm-manager/ui-components";
import { translate } from "react-i18next";
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
  rebuildStatistics,
  getCommitsPerAuthor,
  getTopModifiedFiles,
  getTopWords,
  getFileModificationCount,
  getCommitsPerWeekday,
  getCommitsPerYear,
  getCommitsPerMonth,
  getCommitsPerHour
} from "./statistics";

type Props = {
  repository: Repository,
  t: string => string
};

type State = {
  loading: boolean,
  error?: boolean,
  statisticsLinks?: StatisticLinks
};

class GlobalStatistic extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: false
    };
  }

  componentDidMount() {
    const { repository } = this.props;

    this.setState({ ...this.state, loading: true });

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
    this.setState({ ...this.state, loading: true });

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
        <Title title={t("scm-statistic-plugin.title")} />
        <div className="columns is-multiline is-vcentered">
          <Chart
            render={props => <CommitsPerAuthor {...props} />}
            getData={() =>
              getCommitsPerAuthor(statisticsLinks.commitsPerAuthor.href)
            }
          />
          <Chart
            render={props => <CommitsPerHour {...props} />}
            getData={() =>
              getCommitsPerHour(statisticsLinks.commitsPerHour.href)
            }
          />
          <Chart
            render={props => <CommitsPerMonth {...props} />}
            getData={() =>
              getCommitsPerMonth(statisticsLinks.commitsPerMonth.href)
            }
          />
          <Chart
            render={props => <CommitsPerYear {...props} />}
            getData={() =>
              getCommitsPerYear(statisticsLinks.commitsPerYear.href)
            }
          />
          <Chart
            render={props => <CommitsPerWeekday {...props} />}
            getData={() =>
              getCommitsPerWeekday(statisticsLinks.commitsPerWeekday.href)
            }
          />
          <Chart
            render={props => <FileModificationCount {...props} />}
            getData={() =>
              getFileModificationCount(
                statisticsLinks.fileModificationCount.href
              )
            }
          />
          <Chart
            render={props => <TopWords {...props} />}
            getData={() => getTopWords(statisticsLinks.topWords.href)}
          />
          <Chart
            render={props => <TopModifiedFiles {...props} />}
            getData={() =>
              getTopModifiedFiles(statisticsLinks.topModifiedFiles.href)
            }
          />
        </div>
        <SubmitButton
          label={t("scm-statistic-plugin.rebuildButton")}
          action={this.confirmRebuildStatistics}
          color="warning"
        />
      </>
    );
  }
}

export default translate("plugins")(GlobalStatistic);
