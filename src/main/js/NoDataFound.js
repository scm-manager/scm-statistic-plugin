// @flow
import React from "react";
import { translate } from "react-i18next";

type Props = { t: string => string };

class NoDataFound extends React.Component<Props> {
  render() {
    const { t } = this.props;
    return (
      <div className="notification is-info">
        {t("scm-statistic-plugin.noData")}
      </div>
    );
  }
}

export default translate("plugins")(NoDataFound);
