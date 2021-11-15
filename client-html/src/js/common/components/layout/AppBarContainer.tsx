/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
  useState, useCallback
} from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { InjectedFormProps } from "redux-form";
import clsx from "clsx";
import { createStyles, withStyles } from "@mui/styles";
import useMediaQuery from "@mui/material/useMediaQuery";
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton";
import MenuIcon from "@mui/icons-material/Menu";
import { APPLICATION_THEME_STORAGE_NAME } from "../../../constants/Config";
import { LSGetItem } from "../../utils/storage";
import { openDrawer } from "../../actions";
import AppBarHelpMenu from "../form/AppBarHelpMenu";
import FormSubmitButton from "../form/FormSubmitButton";
import FullScreenStickyHeader from "../list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import { useStickyScrollSpy } from "../../utils/hooks";

const styles: any = theme => createStyles({
  header: {
    width: "100%",
    background: theme.appBar.header.background,
    color: theme.appBar.header.color,
    zIndex: theme.zIndex.drawer + 1,
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
  container: {},
  headerAlternate: {
    background: `${theme.appBar.headerAlternate.background} !important`,
    color: `${theme.appBar.headerAlternate.color} !important`,
  },
  submitButtonAlternate: {
    background: `${theme.appBar.headerAlternate.color} !important`,
    color: `${theme.appBar.headerAlternate.background} !important`,
  },
  fullScreenTitleItem: {
    position: "relative",
    maxWidth: "100%",
    zIndex: 1,
    marginTop: 8,
  },
});

interface Props extends InjectedFormProps {
  classes?: any,
  title?: any;
  actions?: any;
  hideHelpMenu?: boolean;
  noDrawer?: boolean;
  drawerHandler?: () => void;
  children?: any;
  values?: any;
  getAuditsUrl?: string | ((id: number) => string);
  manualUrl?: string;
  disabled?: boolean;
  fields?: any;
  disableInteraction?: boolean;
  hideSubmitButton?: boolean;
  disabledScrolling?: boolean;
  createdOn?: (values: any) => string;
  modifiedOn?: (values: any) => string;
}

const AppBarContainer: React.FC<Props> = props => {
  const {
    classes, title, actions, hideHelpMenu, children, noDrawer, drawerHandler, values, manualUrl, getAuditsUrl, disabled, invalid, fields,
    disableInteraction, hideSubmitButton, disabledScrolling, createdOn, modifiedOn
  } = props;

  const { scrollSpy } = useStickyScrollSpy();

  const [hasScrolling, setScrolling] = useState<boolean>(false);

  console.log(typeof getAuditsUrl)
  console.log(typeof getAuditsUrl === "string")

  const onScroll = useCallback(
    e => {
      scrollSpy(e);
      if (e.target) {
        const isScrolling = e.target.scrollTop > 20;
        setScrolling(isScrolling);
      }
    },
    []
  );

  const isSmallScreen = useMediaQuery('(max-width:992px)');
  const isDarkTheme = LSGetItem(APPLICATION_THEME_STORAGE_NAME) === "dark";
  const isHighcontrastTheme = LSGetItem(APPLICATION_THEME_STORAGE_NAME) === "highcontrast";

  return (
    <>
      <AppBar
        elevation={0}
        position="absolute"
        className={clsx(
          classes.header,
          LSGetItem(APPLICATION_THEME_STORAGE_NAME) === "christmas" && "christmasHeader",
          { [classes.headerAlternate]: hasScrolling },
          { [classes.headerHighContrast]: isHighcontrastTheme }
        )}
      >
        <Toolbar>
          {!noDrawer && (
            <IconButton
              color="inherit"
              aria-label="Open drawer"
              onClick={drawerHandler}
              className={clsx(!isSmallScreen && classes.hiddenContainer, classes.drawerToggle)}
            >
              <MenuIcon />
            </IconButton>
          )}
          <FullScreenStickyHeader
            title={title}
            fields={fields}
            otherClasses={{
              fullScreenTitleItem: classes.fullScreenTitleItem,
            }}
            disableInteraction={disableInteraction}
            twoColumn
            truncateTitle
          />
          <div className="flex-fill" />
          {actions}

          {!hideHelpMenu && values && (
            <AppBarHelpMenu
              created={values && (createdOn ? createdOn(values) : (values.createdOn ? new Date(values.createdOn) : null))}
              modified={values && (modifiedOn ? modifiedOn(values) : (values.modifiedOn ? new Date(values.modifiedOn) : null))}
              manualUrl={manualUrl}
              auditsUrl={values && getAuditsUrl && (typeof getAuditsUrl === "string" ? getAuditsUrl : getAuditsUrl(values.id))}
              classes={{ buttonAlternate: hasScrolling && classes.headerAlternate }}
            />
          )}

          {!hideSubmitButton && (
            <FormSubmitButton
              disabled={disabled}
              invalid={invalid}
              fab
              className={isDarkTheme && classes.submitButtonAlternate}
            />
          )}
        </Toolbar>
      </AppBar>
      <div className={clsx("w-100", { "appBarContainer p-3": !disabledScrolling }, classes.container)} onScroll={onScroll}>
        {children}
      </div>
    </>
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  drawerHandler: () => dispatch(openDrawer())
});

export default connect<any, any, any>(
  null,
  mapDispatchToProps
)(withStyles(styles)(AppBarContainer));
