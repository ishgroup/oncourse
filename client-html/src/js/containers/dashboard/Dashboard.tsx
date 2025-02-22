/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PreferenceEnum } from '@api/model';
import { Grid } from '@mui/material';
import clsx from 'clsx';
import { AppTheme, ThemeValues } from 'ish-ui';
import React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { withStyles } from 'tss-react/mui';
import { checkPermissions, getUserPreferences, setUserPreference, showConfirm } from '../../common/actions';
import { toggleSwipeableDrawer } from '../../common/components/layout/swipeable-sidebar/actions';
import { SWIPEABLE_SIDEBAR_WIDTH } from '../../common/components/layout/swipeable-sidebar/SwipeableSidebar';
import { VARIANTS } from '../../common/components/layout/swipeable-sidebar/utils';
import { DASHBOARD_CATEGORY_WIDTH_KEY, DASHBOARD_THEME_KEY, SYSTEM_USER_TUTORIAL_SKIP, } from '../../constants/Config';
import { State } from '../../reducers/state';
import ActionBody from './components/action-body/ActionBody';
import DashboardHeader from './components/DashboardHeader';

const styles = (theme: AppTheme) =>
  ({
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
    this.props.checkPermissions();
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
      access
    } = this.props;

    return (
      <Grid
        container
        className={clsx(
          classes.container,
          {
            [classes.drawerOpenedContainer]: drawerOpened
          }
        )}
      >
        <Grid item xs={12} className="relative">
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
            access={access}
            dispatch={dispatch}
            preferencesCategoryWidth={preferences[DASHBOARD_CATEGORY_WIDTH_KEY]}
            setDashboardColumnWidth={setDashboardColumnWidth}
            drawerOpened={drawerOpened}
            skipSystemUser={Boolean(preferences[SYSTEM_USER_TUTORIAL_SKIP])}
          />
        </Grid>
      </Grid>
    );
  }
}

const mapStateToProps = (state: State) => ({
  drawerOpened: state.swipeableDrawer.opened,
  preferences: state.userPreferences,
  upgradePlanLink: state.dashboard.upgradePlanLink,
  access: state.access
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  getDashboardPreferences: () => dispatch(getUserPreferences([DASHBOARD_CATEGORY_WIDTH_KEY, SYSTEM_USER_TUTORIAL_SKIP])),
  setDashboardColumnWidth: (key: PreferenceEnum, value: string) => dispatch(setUserPreference({ key, value })),
  setPreferencesTheme: (value: ThemeValues) => dispatch(setUserPreference({ key: DASHBOARD_THEME_KEY, value })),
  openConfirm: props => dispatch(showConfirm(props)),
  toggleSwipeableDrawer: () => dispatch(toggleSwipeableDrawer(VARIANTS.persistent)),
  checkPermissions: () => {
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Course", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Site", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=Contact", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=CourseClass", method: "GET" }));
    dispatch(checkPermissions({ path: "/a/v1/list/plain?entity=SystemUser", method: "GET" }));
  }
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(Dashboard, styles));