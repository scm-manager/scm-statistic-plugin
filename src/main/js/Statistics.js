// @flow
import React from "react";
import type { Repository } from "@scm-manager/ui-types";
import {
  Title,
  Button,
  ErrorNotification,
  Loading
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
import { getLinksForStatistics } from "./statistics";

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

        <div className="columns">
          <Chart>
            <CommitsPerAuthor url={statisticsLinks.commitsPerAuthor.href} />
          </Chart>
          <Chart>
            <CommitsPerHour url={statisticsLinks.commitsPerHour.href} />
          </Chart>
        </div>
        <div className="columns">
          <Chart>
            <CommitsPerMonth url={statisticsLinks.commitsPerMonth.href} />
          </Chart>
          <Chart>
            <CommitsPerYear url={statisticsLinks.commitsPerYear.href} />
          </Chart>
        </div>
        <div className="columns">
          <Chart>
            <CommitsPerWeekday url={statisticsLinks.commitsPerWeekday.href} />
          </Chart>
          <Chart>
            <FileModificationCount
              url={statisticsLinks.fileModificationCount.href}
            />
          </Chart>
        </div>
        <div className="columns">
          <Chart>
            <TopModifiedFiles url={statisticsLinks.topModifiedFiles.href} />
          </Chart>
          <Chart>
            <TopWords url={statisticsLinks.topWords.href} />
          </Chart>
        </div>
        <Button
          label={t("scm-statistic-plugin.rebuildButton")}
          color="warning"
        />
      </>
    );
  }
}

export default translate("plugins")(GlobalStatistic);
