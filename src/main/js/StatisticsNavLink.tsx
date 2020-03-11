import React from "react";
import { SecondaryNavigationItem } from "@scm-manager/ui-components";
import { withTranslation, WithTranslation } from "react-i18next";

type Props = WithTranslation & {
  url: string;
};

class StatisticsNavLink extends React.Component<Props> {
  render() {
    const { url, t } = this.props;

    return (
      <SecondaryNavigationItem
        to={`${url}/statistic`}
        icon="fas fa-chart-pie"
        label={t("scm-statistic-plugin.navLink")}
        title={t("scm-statistic-plugin.navLink")}
      />
    );
  }
}

export default withTranslation("plugins")(StatisticsNavLink);
