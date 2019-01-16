//@flow
import React from "react";
import { translate } from "react-i18next";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import classNames from "classnames";
import injectSheet from "react-jss";
import type StatisticData from "./../DataTypes";

type RenderProps = {
  statisticData: StatisticData,
  options: any
};

type Props = {
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
  statisticData?: StatisticData
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
    const { getData } = this.props;
    getData().then(result => {
      if (result.error) {
        this.setState({
          loading: false,
          error: result.error
        });
      } else {

        let count = [];
        let value = [];

        for (let statisticData of result) {
          value.push(statisticData.value);
          count.push(statisticData.count);
        }
        this.setState({
          statisticData: {
            value: value,
            count: count
          },
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

  createChartsObject() {
    const {classes} = this.props;
    console.log(this.state.statisticData);

    const renderProps: RenderProps = {
      statisticData: this.state.statisticData,
      options: {
        maintainAspectRatio: false, // Don't maintain w/h ratio
        legend: {
          position: "bottom"
        }
      }
    };

    return (
      <article className={classNames(classes.canvasContainer)}>
        {this.props.render(renderProps)}
      </article>
    );
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

    if (statisticData.value <= 0 || statisticData.label <= 0) {
      return (
        <div className="notification is-warning">
          {t("scm-statistic-plugin.noData")}
        </div>
      );
    }



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
                {this.createChartsObject()}
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
          {this.createChartsObject()}
        </div>
      );
    }
}

export default injectSheet(styles)(translate("plugins")(Chart));
