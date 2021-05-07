/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import clsx from "clsx";
import { Grid } from "@material-ui/core";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { PreferenceEnum } from "@api/model";
import { State } from "../../reducers/state";
import DashboardHeader from "./components/DashboardHeader";
import ActionBody from "./components/action-body/ActionBody";
import { getUserPreferences, showConfirm, setUserPreference } from "../../common/actions";
import {
  DASHBOARD_NEWS_LATEST_READ,
  DASHBOARD_CATEGORY_WIDTH_KEY,
  DASHBOARD_THEME_KEY,
  APPLICATION_THEME_STORAGE_NAME
} from "../../constants/Config";
import { AppTheme, ThemeValues } from "../../model/common/Theme";
import { toggleSwipeableDrawer } from "../../common/components/layout/swipeable-sidebar/actions";
import { VARIANTS } from "../../common/components/layout/swipeable-sidebar/utils";
import { SWIPEABLE_SIDEBAR_WIDTH } from "../../common/components/layout/swipeable-sidebar/SwipeableSidebar";
import { LSGetItem } from "../../common/utils/storage";

const styles = (theme: AppTheme) =>
  createStyles({
    container: {
      minWidth: "1024px",
      overflowX: "auto",
      overflowY: "hidden",
      marginLeft: "auto",
      transition: theme.transitions.create(["width"], {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen
      })
    },
    containerHeight: {
      height: "100vh"
    },
    drawerOpenedContainer: {
      minWidth: `calc(1024px - ${SWIPEABLE_SIDEBAR_WIDTH}px)`,
      width: `calc(100% - ${SWIPEABLE_SIDEBAR_WIDTH}px)`,
      transition: theme.transitions.create(["width"], {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen
      })
    }
  });

class Dashboard extends React.PureComponent<any, any> {
  componentDidMount() {
    this.props.getDashboardPreferences();
    this.props.toggleSwipeableDrawer();
  }

  render() {
    const {
      classes,
      preferences,
      setDashboardColumnWidth,
      setPreferencesTheme,
      upgradePlanLink,
      openConfirm,
      drawerOpened,
      dispatch,
      setDashboardNewsLatestReadDate
    } = this.props;

    return (
      <Grid
        container
        className={clsx(
          classes.container,
          {
            [classes.drawerOpenedContainer]: drawerOpened
          },
          LSGetItem(APPLICATION_THEME_STORAGE_NAME) === "christmas" && "christmasBody"
        )}
      >
        <Grid item xs={12}>
          <DashboardHeader
            dispatch={dispatch}
            upgradePlanLink={upgradePlanLink}
            setPreferencesTheme={setPreferencesTheme}
            openConfirm={openConfirm}
            drawerOpened={drawerOpened}
          />
        </Grid>

        <Grid item xs={12} className={classes.containerHeight}>
          <ActionBody
            preferencesCategoryWidth={preferences[DASHBOARD_CATEGORY_WIDTH_KEY]}
            preferencesNewsLatestReadDate={preferences[DASHBOARD_NEWS_LATEST_READ]}
            setDashboardColumnWidth={setDashboardColumnWidth}
            setDashboardNewsLatestReadDate={setDashboardNewsLatestReadDate}
            drawerOpened={drawerOpened}
          />
        </Grid>
      </Grid>
    );
  }
}

const mapStateToProps = (state: State) => ({
  drawerOpened: state.swipeableDrawer.opened,
  preferences: state.userPreferences,
  upgradePlanLink: state.dashboard.upgradePlanLink
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  getDashboardPreferences: () => dispatch(getUserPreferences([DASHBOARD_CATEGORY_WIDTH_KEY, DASHBOARD_NEWS_LATEST_READ])),
  setDashboardColumnWidth: (key: PreferenceEnum, value: string) => dispatch(setUserPreference({ key, value })),
  setPreferencesTheme: (value: ThemeValues) => dispatch(setUserPreference({ key: DASHBOARD_THEME_KEY, value })),
  openConfirm: props => dispatch(showConfirm(props)),
  toggleSwipeableDrawer: () => dispatch(toggleSwipeableDrawer(VARIANTS.persistent)),
  setDashboardNewsLatestReadDate: (value: string) => dispatch(setUserPreference({ key: DASHBOARD_NEWS_LATEST_READ, value }))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(Dashboard));
