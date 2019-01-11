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
  componentDidMount() {}

  render() {
    const { children, classes } = this.props;
    return (
      <div className={classNames("column is-half", classes.higherMaxColumn)}>
        {children}
      </div>
    );
  }
}

export default injectSheet(styles)(translate("plugins")(Chart));
