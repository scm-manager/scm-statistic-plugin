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
import {getLinksForStatistics, rebuildStatistics} from "./statistics";

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
    const {statisticsLinks} = this.state;
    this.setState({ ...this.state, loading: true });

    rebuildStatistics(statisticsLinks.rebuild.href).then(result => {
      if(result.error){
        this.setState({
          error: result.error,
          loading: false
        });
      }
      else {
        //rebuild
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

        <div className="columns is-multiline">
          <Chart>
            <CommitsPerAuthor url={statisticsLinks.commitsPerAuthor.href} />
          </Chart>
          <Chart>
            <CommitsPerHour url={statisticsLinks.commitsPerHour.href} />
          </Chart>
          <Chart>
            <CommitsPerMonth url={statisticsLinks.commitsPerMonth.href} />
          </Chart>
          <Chart>
            <CommitsPerYear url={statisticsLinks.commitsPerYear.href} />
          </Chart>
          <Chart>
            <CommitsPerWeekday url={statisticsLinks.commitsPerWeekday.href} />
          </Chart>
          <Chart>
            <FileModificationCount
              url={statisticsLinks.fileModificationCount.href}
            />
          </Chart>
          <Chart>
            <TopModifiedFiles url={statisticsLinks.topModifiedFiles.href} />
          </Chart>
          <Chart>
            <TopWords url={statisticsLinks.topWords.href} />
          </Chart>
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
