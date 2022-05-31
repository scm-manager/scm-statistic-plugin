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
import React, { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { ConfirmAlert, ErrorNotification, Level, SubmitButton } from "@scm-manager/ui-components";
import { rebuildStatistics } from "./statistics";
import { StatisticLinks } from "./DataTypes";

type Props = {
  statisticsLinks: StatisticLinks;
};

const RebuildStatistics: FC<Props> = ({ statisticsLinks }) => {
  const [t] = useTranslation("plugins");
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState<Error | undefined>(undefined);
  const [showConfirmAlert, setShowConfirmAlert] = useState(false);

  const rebuildStatistic = () => {
    setLoading(true);

    rebuildStatistics(statisticsLinks.rebuild.href).then(result => {
      if (result.error) {
        setError(result.error);
      }
      setLoading(false);
    });
  };

  let confirmAlert = null;
  if (showConfirmAlert) {
    confirmAlert = (
      <ConfirmAlert
        title={t("scm-statistic-plugin.confirmRebuildStatistics.title")}
        message={t("scm-statistic-plugin.confirmRebuildStatistics.message")}
        buttons={[
          {
            label: t("scm-statistic-plugin.confirmRebuildStatistics.submit"),
            onClick: () => rebuildStatistic()
          },
          {
            className: "is-info",
            label: t("scm-statistic-plugin.confirmRebuildStatistics.cancel"),
            onClick: () => null
          }
        ]}
        close={() => setShowConfirmAlert(false)}
      />
    );
  }

  return (
    <>
      <ErrorNotification error={error} />
      {showConfirmAlert && confirmAlert}
      <Level
        right={
          <SubmitButton
            label={t("scm-statistic-plugin.rebuildButton")}
            action={() => setShowConfirmAlert(true)}
            color="warning"
            loading={isLoading || !statisticsLinks}
          />
        }
      />
    </>
  );
};

export default RebuildStatistics;
