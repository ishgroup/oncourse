/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import MenuIcon from "@mui/icons-material/Menu";
import CloseIcon from "@mui/icons-material/Close";
import IconButton from "@mui/material/IconButton";
import { State } from "../../../../../reducers/state";
import { toggleSwipeableDrawer } from "../actions";
import Divider from "@mui/material/Divider";
import { getTheme } from "../../../../../../ish-ui/themes/ishTheme";
import onCourseLogoDark from "../../../../../../images/onCourseLogoDark.png";
import onCourseLogoLight from "../../../../../../images/onCourseLogoLight.png";

interface Props {
  opened?: boolean;
  variant?: string;
  toggleSwipeableDrawer?: () => void;
}

const HamburgerMenu = React.memo<Props>(props => {
  const { opened, toggleSwipeableDrawer } = props;

  const theme = getTheme();

  return (
    <div className="centeredFlex mr-3">
      <IconButton
        color="inherit"
        aria-label="Open drawer"
        edge="start"
        onClick={toggleSwipeableDrawer}
        size="large"
        className="relative zIndex2"
      >
        {opened ? <CloseIcon /> : <MenuIcon />}
      </IconButton>
      <Divider orientation="vertical" variant="middle" flexItem />
      <img
        src={theme.palette.mode === "dark" ? onCourseLogoLight : onCourseLogoDark}
        alt="Logo"
        height={36}
      />
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  opened: state.swipeableDrawer.opened
});

const mapStateToDispatch = (dispatch: Dispatch, props: Props) => ({
  toggleSwipeableDrawer: () => dispatch(toggleSwipeableDrawer(props.variant))
});

export default connect<any, any, any>(mapStateToProps, mapStateToDispatch)(HamburgerMenu);
