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
import { Doughnut } from "react-chartjs-2";
import { StatisticData } from "../DataTypes";
import { withTranslation, WithTranslation } from "react-i18next";

type Props = WithTranslation & {
  statisticData: StatisticData;
  options: any;
};

class TopModifiedFiles extends React.Component<Props> {
  render() {
    const { t, options, statisticData } = this.props;

    const colors = [
      "#ffc3cf",
      "#ff92a8",
      "#ffdd65",
      "#ffeeae",
      "#1689b2",
      "#33b2e8",
      "#8dd7f1",
      "#00737a",
      "#00a9b3",
      "#00d1df",
      "#b2f1f4"
    ];

    const data = {
      labels: statisticData.value,
      datasets: [
        {
          label: t("scm-statistic-plugin.charts.topModifiedFiles"),
          backgroundColor: colors,
          data: statisticData.count
        }
      ]
    };

    return (
      <>
        <Doughnut data={data} options={options} />
      </>
    );
  }
}

export default withTranslation("plugins")(TopModifiedFiles);
