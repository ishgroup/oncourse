/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import Grid from "@mui/material/Grid";
import { PreferenceEnum } from "@api/model";
import { AppTheme } from "../../../../model/common/Theme";
import ResizableWrapper from "../../../../common/components/layout/resizable/ResizableWrapper";
import { SWIPEABLE_SIDEBAR_WIDTH } from "../../../../common/components/layout/swipeable-sidebar/SwipeableSidebar";
import { DASHBOARD_CATEGORY_WIDTH_KEY } from "../../../../constants/Config";
import Statistics from "./components/Statistics";
import NewsRender from "../../../../common/components/news/NewsRender";
import TutorialPanel from "./components/TutorialPanel";

const styles = (theme: AppTheme) => createStyles({
  root: {
    marginTop: "64px",
    height: "calc(100% - 64px)"
  },
  rightSideBar: {
    display: "flex",
    backgroundColor: theme.palette.background.default,
    overflowY: "auto",
    minWidth: 370,
    padding: theme.spacing(3)
  }
});

interface Props {
  classes?: any;
  setDashboardColumnWidth: (key: PreferenceEnum, value: string) => void;
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
        : window.screen.width - dashboardFeedWidth
    };
  }

  UNSAFE_componentWillReceiveProps(nextProps: Readonly<Props>): void {
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

  componentDidUpdate(prevProps: Readonly<Props>) {
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
    const { classes } = this.props;
    const { statisticsColumnWidth } = this.state;

    return (
      <Grid container wrap="nowrap" className={classes.root}>
        <ResizableWrapper
          minWidth="20%"
          maxWidth="60%"
          onResizeStop={this.handleStatisticsResizeStop}
          onResize={this.handleStatisticsResize}
          sidebarWidth={statisticsColumnWidth}
          ignoreScreenWidth
          // showDotsBackground
        >
          <Grid item xs>
            {/*<TutorialPanel
              tutorial={{
                "entity": "Course",
                "title": "Getting starting with onCourse",
                "documentation": "/manual/#courses",
                "link": "/course/new",
                "canSkip": false,
                "video": "q8m9kIYW1Cw",
                "content": "<p>Let's get started by creating our first course. For now, your new course will need a name and a code as well as a data collection rule detailing the questions asked of people enrolling. Choose one of the built-in rules for now.</p><p>Under the marketing section, go ahead and add a web description for your course. You don't need to mention pricing, schedules or trainers... we'll do all that later. Just put in a description to get people excited about your offering.</p><p>Courses are the product you offer, not the specific event that students or customers are purchasing. We'll set that up later.</p><p>When you are done with your course, hit save at the top right.</p>",
              }}
            />*/}
            <Statistics setUpdateChart={this.setUpdateChart} />
          </Grid>
        </ResizableWrapper>
        <Grid
          item
          xs
          className={classes.rightSideBar}
        >
          <NewsRender showPlaceholder />
        </Grid>
      </Grid>
    );
  }
}

export default withStyles(styles)(ActionBody);
