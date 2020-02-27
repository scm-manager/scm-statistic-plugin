import React from "react";
import { NavLink } from "@scm-manager/ui-components";
import { withTranslation, WithTranslation } from "react-i18next";

type Props = WithTranslation & {
  url: string;
  collapsed?: boolean;
};

class StatisticsNavLink extends React.Component<Props> {
  render() {
    const { url, collapsed, t } = this.props;

    return <NavLink
      to={`${url}/statistic`}
      icon="fas fa-chart-pie"
      label={t("scm-statistic-plugin.navLink")}
      title={t("scm-statistic-plugin.navLink")}
      collapsed={collapsed}
    />;
  }
}

export default withTranslation("plugins")(StatisticsNavLink);
