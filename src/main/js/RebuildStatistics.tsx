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

import React, { FC, useState } from "react";
import { useTranslation } from "react-i18next";
import { ConfirmAlert, ErrorNotification, Level, SubmitButton } from "@scm-manager/ui-components";
import { rebuildStatistics } from "./hook/statistics";
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
