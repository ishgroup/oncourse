import React from "react";
import AppBar from "@mui/material/AppBar";
import { withStyles } from "@mui/styles";
import useMediaQuery from '@mui/material/useMediaQuery';
import clsx from "clsx";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import MenuIcon from "@mui/icons-material/Menu";
import { APPLICATION_THEME_STORAGE_NAME, DRAWER_WIDTH } from "../../../constants/Config";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { openDrawer } from "../../actions";
import { LSGetItem } from "../../utils/storage";

const styles: any = theme => ({
  appBar: {
    width: "100%"
  },
  "appBar-left": {
    marginLeft: DRAWER_WIDTH
  },
  "appBar-right": {
    marginRight: DRAWER_WIDTH
  },
  drawerToggle: {
    marginRight: theme.spacing(1),
    marginLeft: theme.spacing(-1)
  },
  hiddenContainer: {
    display: "none"
  }
});

class Bar extends React.Component<any, any> {
  render() {
    const { classes, title, openDrawer, noDrawer } = this.props;

    const isSmallScreen = useMediaQuery('(max-width:992px)');

    return (
      <AppBar
        classes={{ root: classes.appBar }}
        position="absolute"
        className={clsx(LSGetItem(APPLICATION_THEME_STORAGE_NAME) === "christmas" && "christmasHeader")}
      >
        <Toolbar>
          <Typography className="appHeaderFontSize" color="inherit" noWrap>
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

            {title}
          </Typography>
        </Toolbar>
      </AppBar>
    );
  }
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    openDrawer: () => dispatch(openDrawer())
  };
};

export default connect<any, any, any>(
  null,
  mapDispatchToProps
)(withStyles(styles)(Bar));
