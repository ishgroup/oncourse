/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import clsx from "clsx";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import Grid from "@material-ui/core/Grid";
import { PreferenceEnum } from "@api/model";
import { AppTheme } from "../../../../model/common/Theme";
import ResizableWrapper from "../../../../common/components/layout/resizable/ResizableWrapper";
import { SWIPEABLE_SIDEBAR_WIDTH } from "../../../../common/components/layout/swipeable-sidebar/SwipeableSidebar";
import { APPLICATION_THEME_STORAGE_NAME, DASHBOARD_CATEGORY_WIDTH_KEY } from "../../../../constants/Config";
import Statistics from "./components/Statistics";
import Blog from "./components/Blog";
import { LSGetItem } from "../../../../common/utils/storage";

const styles = (theme: AppTheme) =>
  createStyles({
    root: {
      marginTop: "64px",
      height: "calc(100% - 64px)"
    },
    rightSideBar: {
      backgroundColor:
        theme.palette.type === "light" ? theme.palette.background.paper : theme.palette.background.default,
      overflowY: "auto",
      minWidth: 370
    }
  });

interface Props {
  classes?: any;
  setDashboardColumnWidth: (key: PreferenceEnum, value: string) => void;
  setDashboardNewsLatestReadDate: (value: string) => void;
  preferencesCategoryWidth?: string;
  preferencesNewsLatestReadDate?: string;
  drawerOpened?: boolean;
}

const dashboardFeedWidth = 370;

class ActionBody extends React.PureComponent<Props, any> {
  private updateChart;
  private drawerUpdated = true;

  constructor(props) {
    super(props);

    this.state = {
      statisticsColumnWidth: props.preferencesCategoryWidth
        ? Number(props.preferencesCategoryWidth)
        : window.screen.width - dashboardFeedWidth,
      activityColumnWidth: props.preferencesActivityWidth ? Number(props.preferencesActivityWidth) : 425
    };
  }

  componentWillReceiveProps(nextProps: Readonly<Props>, nextContext: any): void {
    const { statisticsColumnWidth } = this.state;
    if (nextProps.drawerOpened === true) {
      if (this.drawerUpdated) {
        this.drawerUpdated = false;
        this.setState({
          statisticsColumnWidth: statisticsColumnWidth - SWIPEABLE_SIDEBAR_WIDTH
        });
      }
    } else if (nextProps.drawerOpened === false) {
      if (this.drawerUpdated === false) {
        this.drawerUpdated = true;
        this.setState({
          statisticsColumnWidth: statisticsColumnWidth + SWIPEABLE_SIDEBAR_WIDTH
        });
      }
    }
  }

  componentDidUpdate(prevProps: Readonly<Props>, prevState: Readonly<Props>) {
    const { preferencesCategoryWidth } = this.props;

    if (!prevProps.preferencesCategoryWidth && preferencesCategoryWidth) {
      const windowSize = window.screen.width;
      let newPreferencesCategoryWidth = Number(preferencesCategoryWidth);
      if (newPreferencesCategoryWidth + SWIPEABLE_SIDEBAR_WIDTH + dashboardFeedWidth > windowSize) {
        newPreferencesCategoryWidth = windowSize - SWIPEABLE_SIDEBAR_WIDTH - dashboardFeedWidth;
      }
      this.setState({
        statisticsColumnWidth: Number(newPreferencesCategoryWidth)
      });
    }
  }

  setUpdateChart = updateChart => {
    this.updateChart = updateChart;
  };

  handleStatisticsResizeStop = (...props) => {
    const statisticsColumnWidth = props[2].getClientRects()[0].width;
    this.setState({ statisticsColumnWidth });

    this.props.setDashboardColumnWidth(DASHBOARD_CATEGORY_WIDTH_KEY, String(statisticsColumnWidth));
  };

  handleStatisticsResize = () => {
    if (this.updateChart) {
      this.updateChart();
    }
  };

  render() {
    const { classes, preferencesNewsLatestReadDate, setDashboardNewsLatestReadDate } = this.props;
    const { statisticsColumnWidth } = this.state;

    return (
      <Grid container justify="flex-end" wrap="nowrap" className={classes.root}>
        <ResizableWrapper
          minWidth="20%"
          maxWidth="60%"
          onResizeStop={this.handleStatisticsResizeStop}
          onResize={this.handleStatisticsResize}
          sidebarWidth={statisticsColumnWidth}
          ignoreScreenWidth
        >
          <Grid item xs>
            <Statistics setUpdateChart={this.setUpdateChart} />
          </Grid>
        </ResizableWrapper>
        <Grid
          item
          xs
          className={clsx(classes.rightSideBar, LSGetItem(APPLICATION_THEME_STORAGE_NAME) === "christmas" && "christmasBackground")}
        >
          <Blog
            preferencesNewsLatestReadDate={preferencesNewsLatestReadDate}
            setDashboardNewsLatestReadDate={setDashboardNewsLatestReadDate}
          />
        </Grid>
      </Grid>
    );
  }
}

export default withStyles(styles)(ActionBody);
