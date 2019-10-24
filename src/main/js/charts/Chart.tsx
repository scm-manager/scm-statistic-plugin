import React from "react";
import { withTranslation, WithTranslation } from "react-i18next";
import { ErrorNotification, Loading } from "@scm-manager/ui-components";
import styled from "styled-components";
import { StatisticData } from "../DataTypes";
import NoDataFound from "../NoDataFound";

type RenderProps = {
  statisticData: StatisticData;
  options: any;
};

type Props = WithTranslation & {
  render: (props: RenderProps) => any;
  getData: (p: void) => Promise<any>;
  title: string;
};

type State = {
  error?: Error;
  loading: boolean;
  showModal: boolean;
  statisticData?: StatisticData;
};

const ColumnSettings = styled.div`
  max-height: none !important;
  height: unset !important;
  margin-bottom: 1.5rem;
`;

const DetailedViewButton = styled.div`
  float: right;
  cursor: pointer;
`;

const CanvasContainerDiv = styled.div`
  margin-bottom: 5px;
  height: 30vh;
`;

const CanvasContainerArticle = styled.article`
  margin-bottom: 5px;
  height: 30vh;
`;
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
        const count = [];
        const value = [];

        for (const statisticData of result) {
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
    this.setState({
      ...this.state,
      loading: true
    });
    this.getStatistics();
  }

  createChartsObject() {
    if (!this.state.statisticData) {
      return null;
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

    return <CanvasContainerArticle>{this.props.render(renderProps)}</CanvasContainerArticle>;
  }

  render() {
    const { t } = this.props;
    const { error, loading, statisticData, showModal } = this.state;

    let content = null;
    let modal = null;

    if (error) {
      content = <ErrorNotification error={error} />;
    } else if (loading || !statisticData) {
      content = <Loading />;
    } else if (statisticData.value.length === 0 || statisticData.count.length === 0) {
      content = <NoDataFound />;
    } else {
      if (showModal) {
        modal = (
          <div className="modal is-active">
            <div className="modal-background" />
            <div className="modal-card">
              <header className="modal-card-head">
                <p className="modal-card-title">{t("scm-statistic-plugin.charts.detailedView")}</p>
                <button className="delete" aria-label="close" onClick={() => this.onClose()} />
              </header>
              <section className="modal-card-body">
                <CanvasContainerDiv className="content">{this.createChartsObject()}</CanvasContainerDiv>
              </section>
            </div>
          </div>
        );
      }

      content = (
        <>
          {modal}
          <DetailedViewButton onClick={this.showModal}>
            <span className="icon is-small">
              <i className="fas fa-search-plus" />
            </span>
          </DetailedViewButton>
          {this.createChartsObject()}
        </>
      );
    }

    return (
      <ColumnSettings className="column is-half">
        {this.props.title}
        {content}
      </ColumnSettings>
    );
  }
}

export default withTranslation("plugins")(Chart);
