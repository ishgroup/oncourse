import React from "react";
import AppBar from "@mui/material/AppBar";
import { withStyles } from "@mui/styles";
import useMediaQuery from '@mui/material/useMediaQuery';
import IconButton from "@mui/material/IconButton";
import MenuIcon from "@mui/icons-material/Menu";
import clsx from "clsx";
import Toolbar from "@mui/material/Toolbar";
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
    classes, children, noDrawer, openDrawer
  } = props;

  const isSmallScreen = useMediaQuery('(max-width:992px)');

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
)(withStyles(styles)(CustomAppBar));
