import React from "react";
import AppBar from "@material-ui/core/AppBar";
import { withStyles } from "@material-ui/core/styles";
import withWidth, { isWidthUp } from "@material-ui/core/withWidth";
import IconButton from "@material-ui/core/IconButton";
import MenuIcon from "@material-ui/icons/Menu";
import clsx from "clsx";
import Toolbar from "@material-ui/core/Toolbar";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { openDrawer } from "../../actions";
import { LSGetItem } from "../../utils/storage";
import { APPLICATION_THEME_STORAGE_NAME } from "../../../constants/Config";

const styles: any = theme => ({
  appBar: {
    width: "100%",
    zIndex: theme.zIndex.drawer + 1
  },
  hiddenContainer: {
    display: "none"
  },
  drawerToggle: {
    marginRight: theme.spacing(1),
    marginLeft: theme.spacing(-1)
  }
});

const CustomAppBar = props => {
  const {
 classes, children, noDrawer, width, openDrawer 
} = props;

  const isSmallScreen = !isWidthUp("md", width);

  return (
    <AppBar
      className={clsx(classes.appBar, LSGetItem(APPLICATION_THEME_STORAGE_NAME) === "christmas" && "christmasHeader")}
      position="absolute"
    >
      <Toolbar>
        {!noDrawer && (
          <IconButton
            color="inherit"
            aria-label="Open drawer"
            onClick={openDrawer}
            className={clsx(!isSmallScreen && classes.hiddenContainer, classes.drawerToggle)}
          >
            <MenuIcon />
          </IconButton>
        )}
        {children}
      </Toolbar>
    </AppBar>
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    openDrawer: () => dispatch(openDrawer())
  });

export default connect<any, any, any>(
  null,
  mapDispatchToProps
)(withWidth()(withStyles(styles)(CustomAppBar)));
