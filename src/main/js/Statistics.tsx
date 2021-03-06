/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
        <div className={"columns"}>
          <div className="column">
            <Chart
              render={props => <CommitsPerAuthor {...props} />}
              getData={() => getCommitsPerAuthor(statisticsLinks.commitsPerAuthor.href)}
              title={t("scm-statistic-plugin.charts.commitsPerAuthor")}
            />
            <Chart
              render={props => <CommitsPerWeekday {...props} />}
              getData={() => getCommitsPerWeekday(statisticsLinks.commitsPerWeekday.href)}
              title={t("scm-statistic-plugin.charts.commitsPerWeekday")}
            />
            <Chart
              render={props => <TopModifiedFiles {...props} />}
              getData={() => getTopModifiedFiles(statisticsLinks.topModifiedFiles.href)}
              title={t("scm-statistic-plugin.charts.topModifiedFiles")}
            />
            <Chart
              render={props => <CommitsPerYear {...props} />}
              getData={() => getCommitsPerYear(statisticsLinks.commitsPerYear.href)}
              title={t("scm-statistic-plugin.charts.commitsPerYear")}
            />
          </div>
          <div className="column">
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
              render={props => <FileModificationCount {...props} />}
              getData={() => getFileModificationCount(statisticsLinks.fileModificationCount.href)}
              title={t("scm-statistic-plugin.charts.fileModificationCount")}
            />
            <Chart
              render={props => <TopWords {...props} />}
              getData={() => getTopWords(statisticsLinks.topWords.href)}
              title={t("scm-statistic-plugin.charts.topWords")}
            />
          </div>
        </div>
        {statisticsLinks?.rebuild && (
          <Level
            right={
              <SubmitButton
                label={t("scm-statistic-plugin.rebuildButton")}
                action={this.confirmRebuildStatistics}
                color="warning"
              />
            }
          />
        )}
      </>
    );
  }
}

export default withTranslation("plugins")(GlobalStatistic);
