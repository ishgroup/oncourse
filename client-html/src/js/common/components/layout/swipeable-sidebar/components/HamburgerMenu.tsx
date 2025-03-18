/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import CloseIcon from '@mui/icons-material/Close';
import MenuIcon from '@mui/icons-material/Menu';
import Divider from '@mui/material/Divider';
import IconButton from '@mui/material/IconButton';
import $t from '@t';
import React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { State } from '../../../../../reducers/state';
import { IAction } from '../../../../actions/IshAction';
import Logo from '../../Logo';
import { toggleSwipeableDrawer } from '../actions';

interface Props {
  opened?: boolean;
  variant?: string;
  toggleSwipeableDrawer?: () => void;
}

const HamburgerMenu = React.memo<Props>(props => {
  const { opened, toggleSwipeableDrawer } = props;

  return (
    <div className="centeredFlex mr-3">
      <IconButton
        color="inherit"
        aria-label={$t('open_drawer')}
        edge="start"
        onClick={toggleSwipeableDrawer}
        size="large"
        className="relative zIndex2"
      >
        {opened ? <CloseIcon/> : <MenuIcon/>}
      </IconButton>
      <Divider orientation="vertical" variant="middle" flexItem/>
      {/* <img*/}
      {/*  src={theme.palette.mode === "dark" ? onCourseLogoLight : onCourseLogoDark}*/}
      {/*  alt="Logo"*/}
      {/*  height={36}*/}
      {/* />*/}

      <Logo className="ml-2" whiteBackgound={opened}  />
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  opened: state.swipeableDrawer.opened
});

const mapStateToDispatch = (dispatch: Dispatch<IAction>, props: Props) => ({
  toggleSwipeableDrawer: () => dispatch(toggleSwipeableDrawer(props.variant))
});

export default connect<any, any, any>(mapStateToProps, mapStateToDispatch)(HamburgerMenu);
