/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import clsx from "clsx";
import withStyles from "@material-ui/core/styles/withStyles";
import createStyles from "@material-ui/core/styles/createStyles";
import IconButton from "@material-ui/core/IconButton";
import PlusIcon from "@material-ui/icons/Add";
import Share from "@material-ui/icons/Share";
import Settings from "@material-ui/icons/Settings";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import Tooltip from "@material-ui/core/Tooltip";
import { fade, darken } from "@material-ui/core/styles/colorManipulator";
import FindInPage from "@material-ui/icons/FindInPage";
import ExecuteScriptModal from "../../../../../containers/automation/containers/scripts/components/ExecuteScriptModal";
import { openInternalLink } from "../../../../utils/links";
import SearchInput from "./components/SearchInput";
import ScriptsMenu from "./components/ScriptsMenu";
import SendMessageMenu from "./components/SendMessageMenu";
import ViewSwitcher from "./components/ViewSwitcher";
import { APP_BAR_HEIGHT } from "../../../../../constants/Config";
import FindRelatedMenu from "./components/FindRelatedMenu";
import { FindRelatedItem } from "../../../../../model/common/ListView";
import christmasBodyBackgroundStars from "../../../../../../images/christmas_header_background.gif";
import { vImage } from "../../../../utils/common";

const styles = theme => createStyles({
    root: {
      backgroundColor:
        theme.palette.type === "light" ? theme.palette.primary.main : darken(theme.palette.background.default, 0.4),
      height: `${APP_BAR_HEIGHT}px`,
      bottom: 0,
      width: "100%",
      display: "flex",
      justifyContent: "space-between",
      alignItems: "center",
      padding: theme.spacing(2),
      position: "relative",
      "&:before": localStorage.getItem("theme") === "christmas" ? {
        content: "''",
        backgroundImage: `url(${vImage(christmasBodyBackgroundStars)})`,
        position: "absolute",
        top: 0,
        left: 0,
        height: "100%",
        width: "100%"
      } : {}
    },
    mainLabel: {
      color: theme.palette.primary.contrastText
    },
    actionArea: {
      color: theme.palette.primary.contrastText,
      display: "flex"
    },
    actionsBarButton: {
      color: "inherit",
      "&$buttonDisabledOpacity": {
        opacity: 0.5
      },
      "&$buttonDisabledFade": {
        color: fade(theme.palette.primary.contrastText, 0.5)
      }
    },
    buttonDisabledOpacity: {},
    buttonDisabledFade: {},
    shareOnBackdrop: {
      color: "inherit",
      zIndex: theme.zIndex.modal + 1
    },
    relatedMenuOffset: {
      marginLeft: "45px"
    },
    cogWheelMenuOffset: {
      marginLeft: "-45px"
    },
    cogWheelMenuDelete: {
      color: theme.palette.error.main
    },
    customIconButton: {
      padding: `${theme.spacing(1)}px 2.5px`,
      height: theme.spacing(6),
      width: theme.spacing(6)
    },
    findInPage: {
      color: theme.palette.primary.contrastText
    },
    switcherActive: {
      color: theme.palette.primary.contrastText
    },
    switcherDisabled: {
      color: fade(theme.palette.primary.contrastText, 0.3)
    },
    findRelated: {
      display: "flex",
      flex: 1,
      justifyContent: "flex-end"
    }
  });

class BottomAppBar extends React.PureComponent<any, any> {
  constructor(props) {
    super(props);

    this.state = {
      showSettingsMenu: null,
      showFindRelatedMenu: null,
      execScriptsMenuOpen: null,
      scriptIdSelected: null
    };
  }

  setExecScriptsMenuOpen = (value: boolean) => {
    this.setState({
      execScriptsMenuOpen: value
    });
  };

  setScriptIdSelected = (id: number) => {
    this.setState({
      scriptIdSelected: id
    });
  };

  openScriptModal = scriptId => {
    this.setScriptIdSelected(scriptId);
    this.setExecScriptsMenuOpen(true);
  };

  onExecuteScriptDialogClose = () => {
    this.setScriptIdSelected(null);
    this.setExecScriptsMenuOpen(false);
  };

  handleClickSettings = event => {
    this.setState({
      showSettingsMenu: event.currentTarget
    });
  };

  handleClickRelatedMenu = event => {
    this.setState({
      showFindRelatedMenu: event.currentTarget
    });
  };

  handleClose = () => {
    this.setState({
      showSettingsMenu: null,
      showFindRelatedMenu: null
    });
  };

  handleRelatedLinkClick = (item: FindRelatedItem) => {
    const { rootEntity, selection } = this.props;
    this.handleClose();

    if (item.list) {
      let searchParam = "";

      if (item.expression) {
        searchParam = `${item.expression} in (${selection.join(", ")})`;
      }

      if (item.customExpression) {
        searchParam = item.customExpression;
      }

      openInternalLink(`/${item.list}?search=${searchParam}`);
    } else {
      window.location.href = `/find/related?destList=${item.destination}&sourceList=${rootEntity}&ids=${selection.join(
        ","
      )}`;
    }
  };

  handleDeleteClick = () => {
    const { onDelete, selection } = this.props;
    onDelete(selection[0]);
    this.handleClose();
  };

  render() {
    const {
      classes,
      fetch,
      toggleExportDrawer,
      showExportDrawer,
      toggleBulkEditDrawer,
      showBulkEditDrawer,
      selection,
      rootEntity,
      aqlEntity,
      querySearch,
      changeQueryView,
      onQuerySearch,
      switchLayout,
      threeColumn,
      deleteEnabled,
      hasShareTypes,
      onCreate,
      findRelated,
      CogwheelAdornment,
      showConfirm,
      CustomFindRelatedMenu,
      records,
      searchComponentNode,
      searchMenuItemsRenderer,
      createButtonDisabled,
      searchQuery,
      filteredCount
    } = this.props;

    const {
      showFindRelatedMenu,
      showSettingsMenu,
      execScriptsMenuOpen,
      scriptIdSelected
    } = this.state;

    const existingRecordSelected = Boolean(selection.length) && selection[0] !== "NEW";

    return (
      <>
        <ExecuteScriptModal
          opened={Boolean(execScriptsMenuOpen)}
          onClose={this.onExecuteScriptDialogClose}
          scriptId={scriptIdSelected}
          selection={selection}
          filteredCount={filteredCount}
        />

        <div className={classes.root}>
          <SearchInput
            innerRef={searchComponentNode}
            onQuerySearch={onQuerySearch}
            querySearch={querySearch}
            rootEntity={aqlEntity || rootEntity}
            changeQueryView={changeQueryView}
            searchMenuItemsRenderer={searchMenuItemsRenderer}
            placeholder="Find..."
          />

          <div className={clsx("centeredFlex", !querySearch && "flex-fill")}>
            {!querySearch && (
              <Tooltip title="Find Related" disableFocusListener>
                <div className={clsx(querySearch && classes.findRelated)}>
                  <IconButton
                    classes={{
                      root: clsx(classes.actionsBarButton, classes.customIconButton),
                      disabled: classes.buttonDisabledOpacity
                    }}
                    disabled={!selection.length || !findRelated || fetch.pending}
                    className="ml-1"
                    aria-owns={showFindRelatedMenu ? "related" : undefined}
                    aria-haspopup="true"
                    onClick={this.handleClickRelatedMenu}
                  >
                    <FindInPage className={classes.findInPage} />
                  </IconButton>
                </div>
              </Tooltip>
            )}

            <Menu
              id="related"
              anchorEl={showFindRelatedMenu}
              open={Boolean(showFindRelatedMenu)}
              onClose={this.handleClose}
              classes={{
                paper: classes.relatedMenuOffset
              }}
              disableAutoFocusItem
            >
              <MenuItem
                disabled
                classes={{
                  root: "listItemPadding"
                }}
              >
                Find related to
                {' '}
                {selection.length}
                {' '}
                record
                {selection.length > 1 ? "s" : ""}
              </MenuItem>

              {CustomFindRelatedMenu ? (
                <CustomFindRelatedMenu
                  findRelated={findRelated}
                  selection={selection}
                  rootEntity={rootEntity}
                  records={records}
                />
              ) : (
                <FindRelatedMenu findRelated={findRelated} handleRelatedLinkClick={this.handleRelatedLinkClick} />
              )}
            </Menu>

            <div className={clsx("flex-fill text-center", { "d-none": querySearch })} />
            <div className={classes.actionArea}>
              <Tooltip title="Add record" disableFocusListener>
                <div>
                  <IconButton
                    color="inherit"
                    onClick={onCreate}
                    disabled={!onCreate || fetch.pending || createButtonDisabled}
                    classes={{
                      root: classes.actionsBarButton,
                      disabled: classes.buttonDisabledFade
                    }}
                  >
                    <PlusIcon />
                  </IconButton>
                </div>
              </Tooltip>

              <ViewSwitcher
                switchLayout={switchLayout}
                threeColumn={threeColumn}
                classes={classes}
                disabled={fetch.pending}
              />

              <Tooltip title="Share" disableFocusListener>
                <div>
                  <IconButton
                    onClick={toggleExportDrawer}
                    disabled={!hasShareTypes}
                    classes={{
                      root: showExportDrawer ? classes.shareOnBackdrop : classes.actionsBarButton,
                      disabled: classes.buttonDisabledFade
                    }}
                  >
                    <Share />
                  </IconButton>
                </div>
              </Tooltip>
              <Tooltip title="More Options" disableFocusListener>
                <div>
                  <IconButton
                    classes={{
                      root: classes.actionsBarButton,
                      disabled: classes.buttonDisabledFade
                    }}
                    disabled={fetch.pending}
                    color="inherit"
                    aria-owns={showSettingsMenu ? "settings" : undefined}
                    aria-haspopup="true"
                    onClick={this.handleClickSettings}
                  >
                    <Settings />
                  </IconButton>
                </div>
              </Tooltip>
              <Menu
                id="settings"
                anchorEl={showSettingsMenu}
                open={Boolean(showSettingsMenu)}
                onClose={this.handleClose}
                classes={{
                  paper: classes.cogWheelMenuOffset
                }}
                disableAutoFocusItem
              >
                {(selection.length === 0 || existingRecordSelected) && (
                  <ScriptsMenu
                    classes={classes}
                    entity={rootEntity}
                    closeAll={this.handleClose}
                    openScriptModal={this.openScriptModal}
                  />
                )}

                <SendMessageMenu selection={selection} entity={rootEntity} closeAll={this.handleClose} />

                {CogwheelAdornment ? (
                  <CogwheelAdornment
                    closeMenu={this.handleClose}
                    menuItemClass="listItemPadding"
                    searchQuery={searchQuery}
                    selection={selection}
                    showConfirm={showConfirm}
                    onCreate={onCreate}
                    entity={rootEntity}
                    showBulkEditDrawer={showBulkEditDrawer}
                    toggleBulkEditDrawer={toggleBulkEditDrawer}
                  />
                ) : null}

                <MenuItem
                  disabled={selection.length !== 1 || !existingRecordSelected || !deleteEnabled}
                  onClick={this.handleDeleteClick}
                  classes={{
                    root: clsx("listItemPadding", classes.cogWheelMenuDelete)
                  }}
                >
                  Delete record
                </MenuItem>
              </Menu>
            </div>
          </div>
        </div>
      </>
    );
  }
}

export default withStyles(styles)(BottomAppBar);
