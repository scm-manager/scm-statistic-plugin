// @flow
import * as React from "react";
import {translate} from "react-i18next";
import injectSheet from "react-jss";
import classNames from "classnames";

type Props = {
  t: string => string,
  classes: any,
  children: React.Node
};

type State = {
  showModal: boolean
}

const styles = {
  higherMaxColumn: {
    maxHeight: "225px !important" // ?
  },
  pointer: {
    cursor: "pointer"
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
    })
  };

  onClose = () => {
    this.setState({
      showModal: false
    })
  };

  render() {
    const {children, classes, t} = this.props;
    const {showModal} = this.state;

    let modal = null;
    if (showModal) {
      modal = (
        <div className="modal is-active">
          <div className="modal-background" />
          <div className="modal-card">
            <header className="modal-card-head">
              <p className="modal-card-title">{t("scm-statistic-plugin.charts.detailedView")}</p>
              <button
                className="delete"
                aria-label="close"
                onClick={() => this.onClose()}
              />
            </header>
            <section className="modal-card-body">
              <div className="content">
                {children}
              </div>
            </section>
          </div>
        </div>
        );
    }

    return (
      <>
        {modal}
        <div className={classNames("column is-half", classes.higherMaxColumn)}>
          <div className="media">
            <div className="media-content">{children}</div>
            <div className={classNames(classes.pointer, "media-right")} onClick={this.showModal}>
            <span className="icon is-small">
                <i className="fas fa-search-plus"/>
            </span>
            </div>
          </div>
        </div>
      </>
    );
  }
}

export default injectSheet(styles)(translate("plugins")(Chart));
