// @flow
import React from "react";
import { NavLink } from "@scm-manager/ui-components";
import { translate } from "react-i18next";

type Props = {
  url: string,
  t: string => string
};

class GlobalStatisticNavLink extends React.Component<Props> {
  render() {
    const { url, t } = this.props;

    return (
      <NavLink
        to={`${url}/statistic`}
        icon="fas fa-chart-pie"
        label={t("scm-statistic-plugin.navLink")}
      />
    );
  }
}

export default translate("plugins")(GlobalStatisticNavLink);
