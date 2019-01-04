// @flow
import React from "react";
import { Title } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import Button from "@scm-manager/ui-components/src/buttons/Button";

type Props = {
  link: string,
  t: string => string
};

class GlobalStatistic extends React.Component<Props> {
  render() {
    const { t, link } = this.props;
    return (
      <>
        <Title title={t("scm-statistic-plugin.title")} />
        <div>
          <div className="columns">
            <div className="column is-half">
              {t("scm-statistic-plugin.charts.commitsPerAuthor")}
            </div>
            <div className="column is-half">
              {t("scm-statistic-plugin.charts.commitsPerMonth")}
            </div>
          </div>
          <div className="columns">
            <div className="column is-half">
              {t("scm-statistic-plugin.charts.commitsPerHour")}
            </div>
            <div className="column is-half">
              {t("scm-statistic-plugin.charts.commitsPerWeekday")}
            </div>
          </div>
          <div className="columns">
            <div className="column is-half">
              {t("scm-statistic-plugin.charts.fileModificationCount")}
            </div>
            <div className="column is-half">
              {t("scm-statistic-plugin.charts.modifiedFiles")}
            </div>
          </div>
          <div className="columns">
            <div className="column is-half">
              {t("scm-statistic-plugin.charts.wordCount")}
            </div>
          </div>
        </div>
        <Button label={t("scm-statistic-plugin.charts.rebuildButton")} />
      </>
    );
  }
}

export default translate("plugins")(GlobalStatistic);
