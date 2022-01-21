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

        for (const statisticData of result) {
          value.push(statisticData.value);
          count.push(statisticData.count);
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
