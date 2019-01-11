// @flow
import * as React from "react";
import { translate } from "react-i18next";

type Props = {
  t: string => string,
  children: React.Node
};

class Chart extends React.Component<Props> {
  componentDidMount() {}

  render() {
    const { children } = this.props;
    return <div className="column is-half">{children}</div>;
  }
}

export default translate("plugins")(Chart);
