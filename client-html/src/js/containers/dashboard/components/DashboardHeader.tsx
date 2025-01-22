import PaletteIcon from '@mui/icons-material/PaletteOutlined';
import LogoutIcon from '@mui/icons-material/PowerSettingsNewOutlined';
import { Grid, MenuItem, Typography } from '@mui/material';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import Menu from '@mui/material/Menu';
import Tooltip from '@mui/material/Tooltip';
import { makeAppStyles, ShowConfirmCaller } from 'ish-ui';
import React, { useEffect, useState } from 'react';
import { Dispatch } from 'redux';
import { IAction } from '../../../common/actions/IshAction';
import instantFetchErrorHandler from '../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import HamburgerMenu from '../../../common/components/layout/swipeable-sidebar/components/HamburgerMenu';
import { VARIANTS } from '../../../common/components/layout/swipeable-sidebar/utils';
import { useAppSelector } from '../../../common/utils/hooks';
import { LSRemoveItem } from '../../../common/utils/storage';
import {
  APP_BAR_HEIGHT,
  APPLICATION_THEME_STORAGE_NAME,
  DASHBOARD_ACTIVITY_STORAGE_NAME
} from '../../../constants/Config';
import { getSystemUserData } from '../../login/actions';
import { ThemeContext } from '../../ThemeContext';
import DashboardService from '../services/DashboardService';

const useStyles = makeAppStyles()(theme => ({
  appBar: {
    backgroundColor: theme.palette.background.default,
    height: APP_BAR_HEIGHT,
    padding: theme.spacing(0, 3),
    position: "absolute",
    width: "100%",
    display: "flex",
    flexDirection: "column",
    left: "auto",
    right: 0,
  },
  toolBar: {
    display: "flex",
    alignItems: "center",
    flex: 1,
    borderBottom: `1px solid ${theme.palette.divider}`
  },
  toolBarButton: {
    color: theme.addButtonColor.color
  },
  upgradeButton: {
    color: theme.palette.primary.contrastText,
    borderColor: theme.palette.primary.contrastText,
    margin: theme.spacing(0, 2)
  }
}));

interface Props {
  upgradePlanLink: any;
  setPreferencesTheme: any;
  openConfirm: ShowConfirmCaller;
  dispatch: Dispatch<IAction>
  drawerOpened: boolean;
}

const DashboardHeader = (
  {
    upgradePlanLink,
    setPreferencesTheme,
    openConfirm,
    dispatch,
    drawerOpened,
  }: Props
) => {
  const { classes } = useStyles();

  const [themeMenu, setThemeMenu] = useState();

  const themeMenuOpen = e => {
    setThemeMenu(e.currentTarget);
  };

  const themeMenuClose = () => {
    setThemeMenu(null);
  };

  const logout = () => {
    LSRemoveItem(APPLICATION_THEME_STORAGE_NAME);
    LSRemoveItem(DASHBOARD_ACTIVITY_STORAGE_NAME);

    DashboardService.logout()
      .then(() => {
        window.open("/login", "_self");
      })
      .catch(err => instantFetchErrorHandler(dispatch, err));
  };

  const toggleConfirm = () => {
    openConfirm({ onConfirm: logout, confirmMessage: "Do you want to logout?", confirmButtonText: "Yes" });
  };

  const systemUser = useAppSelector(state => state.systemUser);

  useEffect(() => {
    dispatch(getSystemUserData());
  }, []);
    
  return (
    <header className={classes.appBar}>
      <div className={classes.toolBar}>
        {!drawerOpened && (
          <HamburgerMenu variant={VARIANTS.persistent} />
        )}
        <Grid container className="justify-content-end" alignContent="center">
          <Grid item xs={6} container justifyContent="start" alignContent="center">
            <Typography variant="subtitle2" color="textSecondary" noWrap>
              Welcome back
              {" "}
              {systemUser?.firstName}
            </Typography>
          </Grid>
          <Grid item xs={6} container justifyContent="end" alignContent="center">
            {upgradePlanLink && (
            <Button
              target="_blank"
              variant="outlined"
              href={upgradePlanLink}
              className={classes.upgradeButton}
              classes={{
                      root: classes.upgradeButton
                    }}
            >
              Upgrade to onCourse Pro
            </Button>
                )}

            <Tooltip title="Change Theme" disableFocusListener>
              <IconButton
                onClick={themeMenuOpen}
                aria-owns={themeMenu ? "theme-menu" : null}
                className={classes.toolBarButton}
              >
                <PaletteIcon />
              </IconButton>
            </Tooltip>
            <ThemeContext.Consumer>
              {({ themeHandler, themeName }) => (
                <Menu
                  id="theme-menu"
                  anchorEl={themeMenu}
                  open={Boolean(themeMenu)}
                  onClose={themeMenuClose}
                >
                  <MenuItem
                    id="default"
                    onClick={() => {
                      themeMenuClose();
                      themeHandler("default");
                      setPreferencesTheme("default");
                    }}
                    selected={themeName === "default"}
                  >
                    Light Theme
                  </MenuItem>
                  <MenuItem
                    id="dark"
                    onClick={() => {
                          themeMenuClose();
                          themeHandler("dark");
                          setPreferencesTheme("dark");
                        }}
                    selected={themeName === "dark"}
                  >
                    Dark Theme
                  </MenuItem>
                  <MenuItem
                    id="monochrome"
                    onClick={() => {
                          themeMenuClose();
                          themeHandler("monochrome");
                          setPreferencesTheme("monochrome");
                        }}
                    selected={themeName === "monochrome"}
                  >
                    Monochrome Theme
                  </MenuItem>
                  <MenuItem
                    id="highcontrast"
                    onClick={() => {
                          themeMenuClose();
                          themeHandler("highcontrast");
                          setPreferencesTheme("highcontrast");
                        }}
                    selected={themeName === "highcontrast"}
                  >
                    High Contrast Theme
                  </MenuItem>
                </Menu>
                  )}
            </ThemeContext.Consumer>
            <Tooltip title="Logout">
              <IconButton
                onClick={toggleConfirm}
                className={classes.toolBarButton}
              >
                <LogoutIcon />
              </IconButton>
            </Tooltip>
          </Grid>
        </Grid>
      </div>
    </header>
  );
};

export default DashboardHeader;
