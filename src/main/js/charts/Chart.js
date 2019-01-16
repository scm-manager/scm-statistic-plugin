// @flow
import * as React from "react";
import { translate } from "react-i18next";
import injectSheet from "react-jss";
import classNames from "classnames";

type Props = {
  t: string => string,
  classes: any,
  children: React.Node
};

type State = {
  showModal: boolean
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
    height: "30vh"
  }
};

class Chart extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      showModal: false
    };
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

  render() {
    const { children, classes, t } = this.props;
    const { showModal } = this.state;

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
                  {children}
                </article>
              </div>
            </section>
          </div>
        </div>
      );
    }

    return (
      <>
        {modal}
        <div className={classNames("column is-half", classes.columnSettings)}>
          <div
            className={classNames(classes.detailedViewButton)}
            onClick={this.showModal}
          >
            <span className="icon is-small">
              <i className="fas fa-search-plus" />
            </span>
          </div>
          <article className={classNames(classes.canvasContainer)}>
            {children}
          </article>
        </div>
      </>
    );
  }
}

export default injectSheet(styles)(translate("plugins")(Chart));
