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
  getTopWords, getFileModificationCount, getCommitsPerWeekday, getCommitsPerYear, getCommitsPerMonth, getCommitsPerHour
} from "./statistics";

type Props = {
  repository: Repository,
  t: string => string
};

type State = {
  loading: boolean,
  error?: boolean,
  statisticsLinks?: any //TODO
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
          <Chart url={statisticsLinks.commitsPerAuthor.href} render={props => <CommitsPerAuthor {...props} />} getData={(url: string) => getCommitsPerAuthor(url)}/>
          <Chart url={statisticsLinks.commitsPerHour.href} render={props => <CommitsPerHour {...props} />} getData={(url: string) => getCommitsPerHour(url)}/>
          <Chart url={statisticsLinks.commitsPerMonth.href} render={props => <CommitsPerMonth {...props} />} getData={(url: string) => getCommitsPerMonth(url)}/>
          <Chart url={statisticsLinks.commitsPerYear.href} render={props => <CommitsPerYear {...props} />} getData={(url: string) => getCommitsPerYear(url)}/>
          <Chart url={statisticsLinks.commitsPerWeekday.href} render={props => <CommitsPerWeekday {...props} />} getData={(url: string) => getCommitsPerWeekday(url)}/>
          <Chart url={statisticsLinks.fileModificationCount.href} render={props => <FileModificationCount {...props} />} getData={(url: string) => getFileModificationCount(url)}/>
          <Chart url={statisticsLinks.topWords.href} render={props => <TopWords {...props} />} getData={(url: string) => getTopWords(url)}/>
          <Chart url={statisticsLinks.topModifiedFiles.href} render={props => <TopModifiedFiles {...props} />} getData={(url: string) => getTopModifiedFiles(url)}/>
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
