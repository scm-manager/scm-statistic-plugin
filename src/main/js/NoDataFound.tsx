import React from "react";
import { withTranslation, WithTranslation } from "react-i18next";

class NoDataFound extends React.Component<WithTranslation> {
  render() {
    const { t } = this.props;
    return <div className="notification is-info">{t("scm-statistic-plugin.noData")}</div>;
  }
}

export default withTranslation("plugins")(NoDataFound);
