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

  showModal = () => { //TODO: does not work yet - guess because this component is called in column environment
    this.setState({
      showModal: true
    })
  };

  render() {
    const {children, classes} = this.props;
    const {showModal} = this.state;
    //TODO: add link and Title to return! - ?

    let modal = null;
    if (showModal) {
      modal = (
        <div className="modal">
          <div className="modal-background"></div>
          <div className="modal-content">
            {children}
          </div>
          <button className="modal-close is-large" aria-label="close"></button>
        </div>);
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
