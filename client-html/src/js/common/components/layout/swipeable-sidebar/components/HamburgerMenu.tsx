/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import MenuIcon from "@material-ui/icons/Menu";
import CloseIcon from "@material-ui/icons/Close";
import IconButton from "@material-ui/core/IconButton";
import { State } from "../../../../../reducers/state";
import { toggleSwipeableDrawer } from "../actions";

interface Props {
  opened?: boolean;
  variant?: string;
  toggleSwipeableDrawer?: () => void;
}

const HamburgerMenu = React.memo<Props>(props => {
  const { opened, toggleSwipeableDrawer } = props;
  return (
    <IconButton
      className="mr-2"
      color="inherit"
      aria-label="Open drawer"
      edge="start"
      onClick={toggleSwipeableDrawer}
    >
      {opened ? <CloseIcon /> : <MenuIcon />}
    </IconButton>
  );
});

const mapStateToProps = (state: State) => ({
  opened: state.swipeableDrawer.opened
});

const mapStateToDispatch = (dispatch: Dispatch, props: Props) => ({
  toggleSwipeableDrawer: () => dispatch(toggleSwipeableDrawer(props.variant))
});

export default connect<any, any, any>(mapStateToProps, mapStateToDispatch)(HamburgerMenu);
