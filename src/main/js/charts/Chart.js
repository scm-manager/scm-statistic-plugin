//@flow
import React from "react";
import { translate } from "react-i18next";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import classNames from "classnames";
import injectSheet from "react-jss";

type RenderProps = {
  statisticData: [],
  options: any
};

type Props = {
  url: string,
  render: (props: RenderProps) => any,
  getData: (url: string) => void,

  // context props
  t: string => string,
  classes: any
};

type State = {
  error?: Error,
  loading: boolean,
  showModal: boolean,
  statisticData?: []
};

const styles = {
  columnSettings: {
    maxHeight: "none !important",
    marginBottom: "1.5rem"
  },
  detailedViewButton: {
    cursor: "pointer",
    float: "right"
  },
  canvasContainer: {
    height: "30vh",
    marginBottom: "5px"
  }
};

class Chart extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      loading: true,
      showModal: false
    };
  }

  getStatistics() {
    const { url, getData } = this.props;
    getData(url).then(result => {
      if (result.error) {
        this.setState({
          loading: false,
          error: result.error
        });
      } else {
        this.setState({
          statisticData: result,
          loading: false
        });
      }
    });
  }

  showModal = () => {
    this.setState({
      showModal: true
    });
  };

  onClose = () => {
    this.setState({
      showModal: false
    });
  };

  componentDidMount() {
    this.setState({ ...this.state, loading: true });
    this.getStatistics();
  }

  render() {
    const { t, classes } = this.props;
    const { error, loading, statisticData, showModal } = this.state;

    if (error) {
      return <ErrorNotification error={error} />;
    }

    if (loading || !statisticData) {
      return <Loading />;
    }

    if (statisticData.length <= 0) {
      return (
        <div className="notification is-warning">
          {t("scm-statistic-plugin.noData")}
        </div>
      );
    }

    const renderProps: RenderProps = {
      statisticData: this.state.statisticData,
      options: {
        maintainAspectRatio: false, // Don't maintain w/h ratio
        legend: {
          position: "bottom"
        }
      }
    };

    let modal = null;
    if (showModal) {
      modal = (
        <div className="modal is-active">
          <div className="modal-background" />
          <div className="modal-card">
            <header className="modal-card-head">
              <p className="modal-card-title">
                {t("scm-statistic-plugin.charts.detailedView")}
              </p>
              <button
                className="delete"
                aria-label="close"
                onClick={() => this.onClose()}
              />
            </header>
            <section className="modal-card-body">
              <div className={classNames("content", classes.canvasContainer)}>
                <article className={classNames(classes.canvasContainer)}>
                  {this.props.render(renderProps)}
                </article>
              </div>
            </section>
          </div>
        </div>
      );
    }


    return (

        <div className={classNames("column is-half", classes.columnSettings)}>
          {modal}
          <div
            className={classNames(classes.detailedViewButton)}
            onClick={this.showModal}
          >
            <span className="icon is-small">
              <i className="fas fa-search-plus" />
            </span>
          </div>
          <article className={classNames(classes.canvasContainer)}>
            {this.props.render(renderProps)}
          </article>
        </div>
      );
    }
}

export default injectSheet(styles)(translate("plugins")(Chart));
