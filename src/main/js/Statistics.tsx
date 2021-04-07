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
import React, { FC, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Repository } from "@scm-manager/ui-types";
import { Loading, ErrorNotification, Subtitle } from "@scm-manager/ui-components";
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
  getCommitsPerHour
} from "./statistics";
import { StatisticLinks } from "./DataTypes";
import RebuildStatistics from "./RebuildStatistics";

type Props = {
  repository: Repository;
};

const Statistic: FC<Props> = ({ repository }) => {
  const [t] = useTranslation("plugins");
  const [isLoading, setLoading] = useState(true);
  const [statisticsLinks, setStatisticsLinks] = useState<StatisticLinks | null>(null);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    getLinksForStatistics(repository._links.statistics.href).then(result => {
      if (result.error) {
        setError(result.error);
        setLoading(false);
      } else {
        setStatisticsLinks(result);
        setLoading(false);
      }
    });
  }, [isLoading]);

  if (error) {
    return <ErrorNotification error={error} />;
  }

  if (isLoading || !statisticsLinks) {
    return <Loading />;
  }

  return (
    <>
      <Subtitle subtitle={t("scm-statistic-plugin.title")} />
      <div className="columns">
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
      {statisticsLinks?.rebuild && <RebuildStatistics statisticsLinks={statisticsLinks} />}
    </>
  );
};

export default Statistic;
