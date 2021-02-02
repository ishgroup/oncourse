import React from "react";
import clsx from "clsx";
import { AppBar } from "@material-ui/core";
import Toolbar from "@material-ui/core/Toolbar";
import Grid from "@material-ui/core/Grid";
import withStyles from "@material-ui/core/styles/withStyles";
import { darken } from "@material-ui/core/styles";
import { onCourseLogoDark, onCourseLogoLight, onCourseLogoChristmas } from "../../../img"

const styles = (theme: any) => ({
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
  render() {
    const {
      classes, theme
    } = this.props;
    const isChristmas = localStorage.getItem("theme") === "christmas";
    return (
      <AppBar className={clsx(classes.appBar, isChristmas && "christmasHeaderDashboard")}>
        <Toolbar
          classes={{
            gutters: classes.toolBarGutters
          }}
        >
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
          </Grid>
        </Toolbar>
      </AppBar>
    );
  }
}

export default withStyles(styles, { withTheme: true })(DashboardHeader) as any;
