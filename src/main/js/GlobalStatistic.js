// @flow
import React from "react";
import type { Repository } from "@scm-manager/ui-types";
import { Title, Button, ErrorNotification } from "@scm-manager/ui-components";
import { translate } from "react-i18next";
import { getCommitsPerAuthor } from "./statistics";

type Props = {
  repository: Repository,
  t: string => string
};

type State = {
  loading: boolean,
  commitsPerAuthor: string[],
  error?: boolean
};

class GlobalStatistic extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      commitsPerAuthor: [],
      loading: false
    };
  }

  componentDidMount() {
    const { repository } = this.props;

    this.setState({ ...this.state, loading: true });
    getCommitsPerAuthor(repository._links.branches.href).then(result => {
      // <--
      if (result.error) {
        this.setState({
          loading: false,
          error: result.error
        });
      } else {
        this.setState({
          commitsPerAuthor: result,
          loading: false
        });
      }
    });
  }

  render() {
    const { t } = this.props;
    const { loading, error } = this.state;

    if (error) {
      return <ErrorNotification error={error} />;
    }

    return (
      <>
        <Title title={t("scm-statistic-plugin.title")} />
        <div>
          <div className="columns">
            <div className="column is-half">
              <p>{t("scm-statistic-plugin.charts.commitsPerAuthor")}</p>
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
