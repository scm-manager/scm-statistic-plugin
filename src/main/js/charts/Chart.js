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

const styles = {
  higherMaxColumn: {
    maxHeight: "225px !important"
  }
};

class Chart extends React.Component<Props> {
  render() {
    const { children, classes } = this.props;
    //TODO: add link and Title to return!
    return (
      <div className={classNames("column is-half", classes.higherMaxColumn)}>
        <div className="media">
          <div className="media-content">{children}</div>
          <div className="media-right">
            <span className="icon is-small">
              <a href="">
                <i className="fas fa-search-plus" />
              </a>
            </span>
          </div>
        </div>
      </div>
    );
  }
}

export default injectSheet(styles)(translate("plugins")(Chart));
