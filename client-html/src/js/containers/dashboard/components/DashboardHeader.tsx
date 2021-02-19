import React from "react";
import clsx from "clsx";
import { AppBar } from "@material-ui/core";
import Toolbar from "@material-ui/core/Toolbar";
import Grid from "@material-ui/core/Grid";
import withStyles from "@material-ui/core/styles/withStyles";
import IconButton from "@material-ui/core/IconButton";
import PaletteIcon from "@material-ui/icons/Palette";
import LogoutIcon from "@material-ui/icons/PowerSettingsNew";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import Tooltip from "@material-ui/core/Tooltip";
import Button from "@material-ui/core/Button";
import { darken } from "@material-ui/core/styles";
import instantFetchErrorHandler from "../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import { ThemeContext } from "../../ThemeContext";
import { APPLICATION_THEME_STORAGE_NAME, DASHBOARD_ACTIVITY_STORAGE_NAME } from "../../../constants/Config";
import onCourseLogoDark from "../../../../images/onCourseLogoDark.png";
import onCourseLogoLight from "../../../../images/onCourseLogoLight.png";
import onCourseLogoChristmas from "../../../../images/onCourseLogoChristmas.png";
import HamburgerMenu from "../../../common/components/layout/swipeable-sidebar/components/HamburgerMenu";
import { VARIANTS } from "../../../common/components/layout/swipeable-sidebar/utils";
import DashboardService from "../services/DashboardService";
import { LSGetItem, LSRemoveItem } from "../../../common/utils/storage";

const styles = theme => ({
  appBar: {
    backgroundColor:
      theme.palette.type === "light" ? theme.palette.primary.main : darken(theme.palette.background.default, 0.4),
    zIndex: theme.zIndex.drawer + 1
  },
  toolBarGutters: {
    padding: "0 16px"
  },
  whiteTextColor: {
    color: theme.palette.primary.contrastText
  },
  upgradeButton: {
    color: theme.palette.primary.contrastText,
    borderColor: theme.palette.primary.contrastText,
    margin: theme.spacing(0, 2)
  },
  logo: { height: "36px", width: "auto" }
});

class DashboardHeader extends React.PureComponent<any, any> {
  state = {
    themeMenu: null
  };

  themeMenuOpen = e => {
    this.setState({ themeMenu: e.currentTarget });
  };

  themeMenuClose = () => {
    this.setState({ themeMenu: null });
  };

  logout = () => {
    LSRemoveItem(APPLICATION_THEME_STORAGE_NAME);
    LSRemoveItem(DASHBOARD_ACTIVITY_STORAGE_NAME);

    DashboardService.logout()
      .then(() => {
        window.open("/login", "_self");
      })
      .catch(err => instantFetchErrorHandler(this.props.dispatch, err));
  };

  toggleConfirm = () => {
    this.props.openConfirm(this.logout, "Do you want to logout?", "Yes");
  };

  render() {
    const { themeMenu } = this.state;
    const {
      classes, theme, upgradePlanLink, setPreferencesTheme
    } = this.props;
    const isChristmas = LSGetItem(APPLICATION_THEME_STORAGE_NAME) === "christmas";
    return (
      <AppBar className={clsx(classes.appBar, isChristmas && "christmasHeaderDashboard")}>
        <Toolbar
          classes={{
            gutters: classes.toolBarGutters
          }}
        >
          <HamburgerMenu variant={VARIANTS.persistent} />
          <Grid container alignItems="center">
            <Grid item xs={6}>
              <Grid container spacing={3} wrap="nowrap">
                <Grid item>
                  {isChristmas ? (
                    <img src={onCourseLogoChristmas} className={classes.logo} alt="Logo" />
                  ) : (
                    <img
                      src={theme.palette.type === "dark" ? onCourseLogoDark : onCourseLogoLight}
                      className={classes.logo}
                      alt="Logo"
                    />
                  )}
                </Grid>
              </Grid>
            </Grid>
            <Grid item xs={6}>
              <Grid container justify="flex-end">
                <Grid item>
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
                      className={classes.whiteTextColor}
                      onClick={this.themeMenuOpen}
                      aria-owns={themeMenu ? "theme-menu" : null}
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
                        onClose={this.themeMenuClose}
                      >
                        <MenuItem
                          id="default"
                          onClick={() => {
                            this.themeMenuClose();
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
                            this.themeMenuClose();
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
                            this.themeMenuClose();
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
                            this.themeMenuClose();
                            themeHandler("highcontrast");
                            setPreferencesTheme("highcontrast");
                          }}
                          selected={themeName === "highcontrast"}
                        >
                          High Contrast Theme
                        </MenuItem>
                        {/* <MenuItem */}
                        {/*  id="christmas" */}
                        {/*  onClick={() => { */}
                        {/*    this.themeMenuClose(); */}
                        {/*    themeHandler("christmas"); */}
                        {/*    setPreferencesTheme("christmas"); */}
                        {/*  }} */}
                        {/*  selected={themeName === "christmas"} */}
                        {/* > */}
                        {/*  Christmas Theme */}
                        {/* </MenuItem> */}
                      </Menu>
                    )}
                  </ThemeContext.Consumer>
                  <Tooltip title="Logout">
                    <IconButton onClick={this.toggleConfirm} className={classes.whiteTextColor}>
                      <LogoutIcon />
                    </IconButton>
                  </Tooltip>
                </Grid>
              </Grid>
            </Grid>
          </Grid>
        </Toolbar>
      </AppBar>
    );
  }
}

export default withStyles(styles, { withTheme: true })(DashboardHeader) as any;
