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
import styled from "styled-components";
import { ErrorNotification, Level, Loading, Notification, OpenInFullscreenButton } from "@scm-manager/ui-components";
import { StatisticData } from "../DataTypes";

type RenderProps = {
  statisticData: StatisticData;
  options: any;
};

type Props = {
  render: (props: RenderProps) => any;
  getData: (p: void) => Promise<any>;
  title: string;
};

const ColumnSettings = styled.div`
  max-height: none !important;
  height: unset !important;
`;

const CanvasModalContainer = styled.div`
  height: 60vh;
`;

const CanvasContainerArticle = styled.article`
  height: 30vh;
`;

type ChartObjectProps = {
  statisticData: StatisticData;
  render: (props: RenderProps) => any;
};

const ChartsObject: FC<ChartObjectProps> = ({ statisticData, render }) => {
  const renderProps: RenderProps = {
    statisticData: statisticData,
    options: {
      maintainAspectRatio: false, // Don't maintain w/h ratio
      legend: {
        position: "bottom"
      }
    }
  };

  return render(renderProps);
};

const Chart: FC<Props> = ({ render, getData, title }) => {
  const [t] = useTranslation("plugins");
  const [isLoading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);
  const [statisticData, setStatisticData] = useState<StatisticData | null>(null);

  useEffect(() => {
    getStatistics();
  }, []);

  const getStatistics = () => {
    getData().then(result => {
      if (result.error) {
        setLoading(false);
        setError(result.error);
      } else {
        const count = [];
        const value = [];

        for (const statsData of result) {
          value.push(statsData.value);
          count.push(statsData.count);
        }
        setStatisticData({
          value: value,
          count: count
        });
        setLoading(false);
      }
    });
  };

  let content = null;

  if (isLoading || !statisticData) {
    content = <Loading />;
  } else if (error) {
    content = <ErrorNotification error={error} />;
  } else if (statisticData.value.length === 0 || statisticData.count.length === 0) {
    content = <Notification type="info">{t("scm-statistic-plugin.noData")}</Notification>;
  } else {
    return (
      <ColumnSettings className="column mb-5">
        <Level
          left={title}
          right={
            <OpenInFullscreenButton
              modalTitle={t("scm-statistic-plugin.charts.detailedView")}
              modalBody={
                <CanvasModalContainer className="content">
                  <ChartsObject statisticData={statisticData} render={render} />
                </CanvasModalContainer>
              }
            />
          }
        />
        <CanvasContainerArticle className="mb-2">
          <ChartsObject render={render} statisticData={statisticData} />
        </CanvasContainerArticle>
      </ColumnSettings>
    );
  }

  return (
    <ColumnSettings className="column">
      <h3 className="mb-2">{title}</h3>
      {content}
    </ColumnSettings>
  );
};

export default Chart;
