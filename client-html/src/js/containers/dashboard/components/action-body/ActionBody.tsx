/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import Grid from "@mui/material/Grid";
import { PreferenceEnum } from "@api/model";
import { AppTheme } from "../../../../../ish-ui/model/Theme";
import ResizableWrapper from "../../../../../ish-ui/layout/resizable/ResizableWrapper";
import { SWIPEABLE_SIDEBAR_WIDTH } from "../../../../common/components/layout/swipeable-sidebar/SwipeableSidebar";
import { DASHBOARD_CATEGORY_WIDTH_KEY } from "../../../../constants/Config";
import Statistics from "./components/Statistics";
import NewsRender from "../../../../common/components/news/NewsRender";
import TutorialPanel from "./components/TutorialPanel";
import tutorials from "./tutorials.json";
import EntityService from "../../../../common/services/EntityService";
import instantFetchErrorHandler from "../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import { Dispatch } from "redux";
import { AccessState } from "../../../../common/reducers/accessReducer";

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
  skipSystemUser?: boolean;
  dispatch?: Dispatch;
  access?: AccessState;
}

const dashboardFeedWidth = 370;

class ActionBody extends React.PureComponent<Props, any> {
  private updateChart;

  private drawerUpdated = true;

  private intervalIsSet = false;

  private interval = null;

  constructor(props) {
    super(props);

    this.state = {
      statisticsColumnWidth: props.preferencesCategoryWidth
        ? Number(props.preferencesCategoryWidth)
        : window.screen.width - dashboardFeedWidth,
      tutorialKey: null,
      customLink: null
    };
  }
  
  componentWillUnmount() {
    clearInterval(this.interval);
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
    const { preferencesCategoryWidth, access } = this.props;

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

    if (!this.intervalIsSet
      && access["/a/v1/list/plain?entity=Course"]
      && access["/a/v1/list/plain?entity=Site"]
      && access["/a/v1/list/plain?entity=Contact"]
      && access["/a/v1/list/plain?entity=CourseClass"]
      && access["/a/v1/list/plain?entity=SystemUser"]
    ) {
      this.intervalIsSet = true;
      this.interval = setInterval(this.checkTutorials, 10000);
      this.checkTutorials();
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

  getTutorial = async () => {
    const { access } = this.props;

    for (const tutorialKey in tutorials) {
      switch (tutorialKey) {
        case "course": {
          const courseAccess = access["/a/v1/list/plain?entity=Course"] && access["/a/v1/list/plain?entity=Course"]["GET"];
          if (!courseAccess) break;
          const courseResponse = await EntityService.getPlainRecords("Course", "id", "id not is null", 1);
          if (!courseResponse?.rows?.length) {
            return tutorialKey;
          }
          break;
        }
        case "site": {
          const siteAccess = access["/a/v1/list/plain?entity=Site"] && access["/a/v1/list/plain?entity=Site"]["GET"];
          if (!siteAccess) break;
          const siteResponse = await EntityService.getPlainRecords("Site", "id,name", "id not is null", 2);
          if (!siteResponse?.rows?.length) {
            return tutorialKey;
          }
          if (siteResponse?.rows?.length === 1 && siteResponse.rows[0].values[1] === "Default site") {
            this.setState({
              customLink: `/site/${siteResponse.rows[0].values[0]}`
            });
            return tutorialKey;
          }
          break;
        }
        case "tutor": {
          const tutorAccess = access["/a/v1/list/plain?entity=Contact"] && access["/a/v1/list/plain?entity=Contact"]["GET"];
          if (!tutorAccess) break;
          const tutorResponse = await EntityService.getPlainRecords("Contact", "id", "id not is null and isTutor is true", 1);
          if (!tutorResponse?.rows?.length) {
            return tutorialKey;
          }
          break;
        }
        case "courseclass": {
          const courseClassAccess = access["/a/v1/list/plain?entity=CourseClass"] && access["/a/v1/list/plain?entity=CourseClass"]["GET"];
          if (!courseClassAccess) break;
          const courseClassResponse = await EntityService.getPlainRecords("CourseClass", "id", "id not is null", 1);
          if (!courseClassResponse?.rows?.length) {
            return tutorialKey;
          }
          break;
        }
        case "systemuser": {
          const systemUserAccess = access["/a/v1/list/plain?entity=SystemUser"] && access["/a/v1/list/plain?entity=SystemUser"]["GET"];
          if (!systemUserAccess) break;
          const systemUserResponse = await EntityService.getPlainRecords("SystemUser", "id", "id not is null", 2);
          if (systemUserResponse?.rows?.length === 1) {
            return tutorialKey;
          }
          break;
        }
      }
    }
    return null;
  };

  checkTutorials = async () => {
    const { dispatch } = this.props;

    try {
      const tutorialKey = await this.getTutorial();

      if (!tutorialKey) {
        clearInterval(this.interval);
      }

      this.setState(prev => ({
        tutorialKey,
        customLink: tutorialKey === "site" ? prev.customLink : null
      }));
    } catch (e) {
      instantFetchErrorHandler(dispatch, e);
    }
  };

  render() {
    const { classes, skipSystemUser } = this.props;
    const { statisticsColumnWidth, tutorialKey, customLink } = this.state;

    const showTutorial = skipSystemUser && tutorialKey === "systemuser" ? false : Boolean(tutorialKey);

    return (
      <Grid container wrap="nowrap" className={classes.root}>
        <ResizableWrapper
          minWidth="20%"
          maxWidth="60%"
          onResizeStop={this.handleStatisticsResizeStop}
          onResize={this.handleStatisticsResize}
          sidebarWidth={statisticsColumnWidth}
          ignoreScreenWidth
        >
          <Grid item xs>
            {showTutorial && (
              <TutorialPanel
                tutorial={tutorials[tutorialKey]}
                customLink={customLink}
              />
            )}
            <Statistics setUpdateChart={this.setUpdateChart} hideChart={showTutorial} />
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