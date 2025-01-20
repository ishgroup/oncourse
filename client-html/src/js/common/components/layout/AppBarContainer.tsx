/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import AddIcon from '@mui/icons-material/Add';
import MenuIcon from '@mui/icons-material/Menu';
import AppBar from '@mui/material/AppBar';
import Button from '@mui/material/Button';
import Fab from '@mui/material/Fab';
import IconButton from '@mui/material/IconButton';
import Toolbar from '@mui/material/Toolbar';
import useMediaQuery from '@mui/material/useMediaQuery';
import { AppBarHelpMenu, AppTheme } from 'ish-ui';
import React, { useCallback, useState } from 'react';
import { makeStyles } from 'tss-react/mui';
import { APP_BAR_HEIGHT, APPLICATION_THEME_STORAGE_NAME } from '../../../constants/Config';
import { openDrawer } from '../../actions';
import { useAppDispatch } from '../../utils/hooks';
import { LSGetItem } from '../../utils/storage';
import FormSubmitButton from '../form/FormSubmitButton';
import FullScreenStickyHeader from '../list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import HamburgerMenu from './swipeable-sidebar/components/HamburgerMenu';
import { VARIANTS } from './swipeable-sidebar/utils';

const useStyles = makeStyles<void, 'submitButtonAlternate' | 'closeButtonAlternate' | 'actionsWrapper'>()((theme: AppTheme, _params, classes) => ({
  header: {
    width: "100%",
    background: theme.appBar.header.background,
    color: theme.appBar.header.color,
    zIndex: theme.zIndex.appBar,
    height: `${APP_BAR_HEIGHT}px`,
    [`& .${classes.submitButtonAlternate}`]: {
      background: `${theme.appBar.headerAlternate.color}`,
      color: `${theme.appBar.headerAlternate.background}`,
    },
    [`& .${classes.closeButtonAlternate}`]: {
      color: `${theme.appBar.headerAlternate.color}`,
    }
  },
  headerHighContrast: {
    background: theme.palette.background.default,
  },
  hiddenContainer: {
    display: "none"
  },
  drawerToggle: {
    marginRight: theme.spacing(1),
    marginLeft: theme.spacing(-1)
  },
  container: {
    padding: theme.spacing(4, 3, 3, 3),
    background: theme.appBar.header.background,
  },
  headerAlternate: {
    background: `${theme.appBar.headerAlternate.background}`,
    color: `${theme.appBar.headerAlternate.color}`,
    [`& .${classes.actionsWrapper} svg`]: {
      color: `${theme.appBar.headerAlternate.color}`,
    }
  },
  submitButtonAlternate: {},
  closeButtonAlternate: {},
  fullScreenTitleItem: {
    position: "relative",
    maxWidth: "100%",
    zIndex: 1,
    marginTop: 8,
  },
  scriptAddMenu: {
    position: "absolute",
    zIndex: theme.zIndex.drawer + 2,
    left: 26,
    top: 43,
    "& > .appBarFab": {
      top: 0,
      left: 0,
    }
  },
  headerFabOffset: {
    paddingLeft: theme.spacing(8.75)
  },
  actionsWrapper: {
    display: "flex",
    alignItems: "center",
    zIndex: theme.zIndex.appBar + 1
  }
}));

interface Props {
  title?: any;
  actions?: any;
  hideHelpMenu?: boolean;
  noDrawer?: boolean;
  noScrollSpy?: boolean;
  noTitle?: boolean;
  children?: any;
  values?: any;
  getAuditsUrl?: string | ((id: number) => string);
  manualUrl?: string;
  containerClass?: string;
  disabled?: boolean;
  invalid?: boolean;
  fields?: any;
  opened?: boolean;
  disableInteraction?: boolean;
  hideSubmitButton?: boolean;
  disabledScrolling?: boolean;
  createdOn?: (values: any) => Date;
  modifiedOn?: (values: any) => Date;
  onAddMenu?: () => void;
  customAddMenu?: any;
  submitButtonText?: string;
  closeButtonText?: string;
  onCloseClick?: () => void;
  hamburgerMenu?: boolean;
  Avatar?: React.FC<{
    avatarSize: number,
    disabled: boolean
  }>
}

const AppBarContainer = (props: Props) => {
  const {
    title,
    actions,
    hideHelpMenu,
    children,
    noDrawer,
    noTitle,
    noScrollSpy,
    values,
    manualUrl,
    getAuditsUrl,
    disabled,
    invalid,
    fields,
    disableInteraction,
    hideSubmitButton,
    disabledScrolling,
    createdOn,
    modifiedOn,
    onAddMenu,
    customAddMenu,
    submitButtonText,
    onCloseClick,
    hamburgerMenu,
    opened,
    containerClass,
    closeButtonText,
    Avatar
  } = props;

  const dispatch = useAppDispatch();

  const { classes, cx } = useStyles();

  const [hasScrolling, setScrolling] = useState<boolean>(false);

  const onScroll = useCallback(e => {
    if (e.target.scrollTop > 0 && !hasScrolling) {
      setScrolling(true);
    }
    if (e.target.scrollTop <= 0 && hasScrolling) {
      setScrolling(false);
    }
  }, [hasScrolling]);

  const drawerHandler = () => dispatch(openDrawer());

  const isSmallScreen = useMediaQuery('(max-width:992px)');
  const isDarkTheme = LSGetItem(APPLICATION_THEME_STORAGE_NAME) === "dark";
  const isHighcontrastTheme = LSGetItem(APPLICATION_THEME_STORAGE_NAME) === "highcontrast";
  const hasFab = onAddMenu || customAddMenu;

  const childrenWithProps = React.Children.map(children, child => {
    if (React.isValidElement(child)) {
      return React.cloneElement<any>(child, {onScroll});
    }
    return child;
  });

  return (<>
    <AppBar
      elevation={0}
      position="absolute"
      className={cx(
        classes.header,
        hasFab && classes.headerFabOffset,
        opened && "pt-2",
        {[classes.headerAlternate]: hasScrolling},
        {[classes.headerHighContrast]: isHighcontrastTheme}
      )}
    >
      <Toolbar>
        {!noDrawer && !hamburgerMenu && (
          <IconButton
            color="inherit"
            aria-label="Open drawer"
            onClick={drawerHandler}
            className={cx(!isSmallScreen && classes.hiddenContainer, classes.drawerToggle)}
          >
            <MenuIcon/>
          </IconButton>
        )}
        {hamburgerMenu && (
          <HamburgerMenu variant={VARIANTS.temporary}/>
        )}
        {
          !noTitle && (
            <FullScreenStickyHeader
              opened={opened}
              title={title}
              fields={fields}
              Avatar={Avatar}
              disableInteraction={disableInteraction}
              customStuck={hasScrolling}
              twoColumn
            />
          )
        }
        <div className="flex-fill"/>
        <div className={classes.actionsWrapper}>
          {actions}
          {!hideHelpMenu && (
            <AppBarHelpMenu
              created={values && (createdOn ? createdOn(values) : (values.createdOn ? new Date(values.createdOn) : null))}
              modified={values && (modifiedOn ? modifiedOn(values) : (values.modifiedOn ? new Date(values.modifiedOn) : null))}
              manualUrl={manualUrl}
              auditsUrl={values && getAuditsUrl && (typeof getAuditsUrl === "string" ? getAuditsUrl : getAuditsUrl(values.id))}
            />
          )}

          {onCloseClick && (
            <Button
              onClick={onCloseClick}
              className={cx("closeAppBarButton", hasScrolling && classes.closeButtonAlternate)}
            >
              {closeButtonText || "Close"}
            </Button>
          )}
          {!hideSubmitButton && (
            <FormSubmitButton
              disabled={disabled}
              invalid={invalid}
              className={isDarkTheme && classes.submitButtonAlternate}
              text={submitButtonText || "Save"}
              fab
            />
          )}
        </div>
      </Toolbar>
    </AppBar>
    <div className={cx("w-100", {"appBarContainer": !disabledScrolling}, classes.container, containerClass)}
         onScroll={noScrollSpy ? null : onScroll}>
      {hasFab && (
        <div className={classes.scriptAddMenu}>
          {customAddMenu || (
            <Fab
              type="button"
              size="small"
              color="primary"
              classes={{
                sizeSmall: "appBarFab"
              }}
              onClick={onAddMenu}
            >
              <AddIcon/>
            </Fab>
          )}
        </div>
      )}
      {childrenWithProps}
    </div>
  </>);
};

export default AppBarContainer;