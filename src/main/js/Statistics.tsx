/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
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
} from "./hook/statistics";
import { StatisticLinks } from "./DataTypes";
import RebuildStatistics from "./RebuildStatistics";
import { useDocumentTitleForRepository } from "@scm-manager/ui-core";

type Props = {
  repository: Repository;
};

const Statistic: FC<Props> = ({ repository }) => {
  const [t] = useTranslation("plugins");
  const [isLoading, setLoading] = useState(true);
  const [statisticsLinks, setStatisticsLinks] = useState<StatisticLinks | null>(null);
  const [error, setError] = useState<Error | null>(null);
  useDocumentTitleForRepository(repository, t("scm-statistic-plugin.navLink"));

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
