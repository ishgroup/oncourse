/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useState } from "react";
import { createStyles, withStyles } from "@mui/styles";
import Drawer from "@mui/material/Drawer";
import Hidden from "@mui/material/Hidden";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { State } from "../../../reducers/state";
import { closeDrawer } from "../../actions";
import { AppTheme } from  "ish-ui";

const styles = (theme: AppTheme) =>
  createStyles({
    drawerPaper: {
      position: "relative",
      height: "100%",
      width: "100%",
      borderRight: "none",
      paddingBottom: theme.spacing(3)
    },
    drawerHeader: {
      ...theme.mixins.toolbar
    },
    activeList: {
      "text-decoration": "none",
      "background-color": "rgba(0, 0, 0, 0.12)"
    },
    listItemPadding: {
      padding: theme.spacing(0.75, 3)
    },
    listHeadingPadding: {
      padding: `${theme.spacing(1) + 4} ${theme.spacing(3)}`
    },
    listPadding: {
      padding: `12px 0px ${theme.spacing(2)} 0px`
    }
  });

const appDrawer = React.memo<any>(({
                                     classes, children, opened, closeDrawer
                                   }) => {
  const [open, setOpen] = useState(false);

  useEffect(() => {
    if (opened !== open) {
      setOpen(opened);
    }
  }, [opened]);

  const handleDrawerClose = useCallback(() => {
    closeDrawer();
  }, []);

  return (
    <>
      <Hidden smDown>
        <Drawer
          variant="permanent"
          classes={{
            paper: classes.drawerPaper,
            docked: "h-100"
          }}
          anchor="left"
        >
          {children}
        </Drawer>
      </Hidden>
      <Hidden mdUp>
        <Drawer variant="temporary" anchor="left" onClose={handleDrawerClose} open={open}>
          <div className={classes.drawerPaper}>{children}</div>
        </Drawer>
      </Hidden>
    </>
  );
});

const mapStateToProps = (state: State) => ({
  opened: state.drawer.opened
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  closeDrawer: () => dispatch(closeDrawer())
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(appDrawer));
