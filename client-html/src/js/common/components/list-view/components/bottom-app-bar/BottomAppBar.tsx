/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import PlusIcon from '@mui/icons-material/Add';
import FindInPage from '@mui/icons-material/FindInPage';
import Settings from '@mui/icons-material/Settings';
import Share from '@mui/icons-material/Share';
import IconButton from '@mui/material/IconButton';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import { alpha, darken } from '@mui/material/styles';
import Tooltip from '@mui/material/Tooltip';
import $t from '@t';
import clsx from 'clsx';
import { makeAppStyles, openInternalLink } from 'ish-ui';
import React, { useEffect, useMemo, useState } from 'react';
import { APP_BAR_HEIGHT, PLAIN_LIST_MAX_PAGE_SIZE } from '../../../../../constants/Config';
import ExecuteScriptModal from '../../../../../containers/automation/containers/scripts/components/ExecuteScriptModal';
import { FindRelatedItem } from '../../../../../model/common/ListView';
import instantFetchErrorHandler from '../../../../api/fetch-errors-handlers/InstantFetchErrorHandler';
import EntityService from '../../../../services/EntityService';
import FindRelatedMenu from './components/FindRelatedMenu';
import ScriptsMenu from './components/ScriptsMenu';
import SearchInput from './components/SearchInput';
import SendMessageMenu from './components/SendMessageMenu';
import ViewSwitcher from './components/ViewSwitcher';

const SendMessageEntities = [
  "AbstractInvoice",
  "Invoice",
  "Application",
  "Contact",
  "Enrolment",
  "CourseClass",
  "PaymentIn",
  "PaymentOut",
  "Payslip",
  "ProductItem",
  "WaitingList",
  "Lead"
];

const useStyles = makeAppStyles<void, 'buttonDisabledOpacity' | 'buttonDisabledFade'>()((theme, p, classes) => ({
  root: {
    backgroundColor:
      theme.palette.mode === "light" ? theme.palette.primary.main : darken(theme.palette.background.default, 0.4),
    height: `${APP_BAR_HEIGHT}px`,
    bottom: 0,
    width: "100%",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: theme.spacing(2),
    position: "relative"
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
    [`&.${classes.buttonDisabledOpacity}`]: {
      opacity: 0.5
    },
    [`&.${classes.buttonDisabledFade}`]: {
      color: alpha(theme.palette.primary.contrastText, 0.5)
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
  customIconButton: {
    padding: `${theme.spacing(1)} 2.5px`,
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
    color: alpha(theme.palette.primary.contrastText, 0.3)
  },
  findRelated: {
    display: "flex",
    flex: 1,
    justifyContent: "flex-end"
  }
}));

const BottomAppBar = (
  {
    rootEntity,
    scripts,
    getScripts,
    selection,
    onDelete,
    fetch,
    toggleExportDrawer,
    showExportDrawer,
    toggleBulkEditDrawer,
    showBulkEditDrawer,
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
    filteredCount,
    emailTemplatesWithKeyCode,
    findRelatedByFilter,
    scriptsFilterColumn,
    dispatch
  }) => {
  const [filterScriptsBy, setFilterScriptsBy] = useState(null);
  const [scriptsMenuOpen, setScriptsMenuOpen] = useState(null);
  const [showSettingsMenu, setShowSettingsMenu] = useState(null);
  const [scriptIdSelected, setScriptIdSelected] = useState(null);
  const [showFindRelatedMenu, setShowFindRelatedMenu] = useState(null);
  const [execScriptsMenuOpen, setExecScriptsMenuOpen] = useState(null);
  
  const { classes } = useStyles();
  
  useEffect(() => {
    if (rootEntity && !scripts) {
      getScripts(rootEntity);
    }
  }, [rootEntity, scripts]);

  useEffect(() => {
    setFilterScriptsBy(null);
  }, [rootEntity]);

  // Workaround to trigger menu resize
  useEffect(() => {
    window.dispatchEvent(new CustomEvent('resize'));
  }, [filterScriptsBy]);

  const getFilteredBy = async () => {
    try {
      let filterGroups = null;
      
      if (selection.length && selection[0] !== "new") {
        filterGroups = {};
        
        const records = await EntityService.getPlainRecords(rootEntity, scriptsFilterColumn, `id in (${selection})`);

        records.rows.forEach(r => {
          if (filterGroups[r.values[0]]) {
            filterGroups[r.values[0]].ids.push(r.id);
          } else {
            filterGroups[r.values[0]] = {
              ids: [r.id],
              scripts: []
            };
          }
        });
        
        scripts?.forEach(s => {
          filterGroups[s.entity]?.scripts.push(s);
        });
      }

      setFilterScriptsBy(filterGroups);
    } catch (e) {
      instantFetchErrorHandler(dispatch, e);
    }
  };

  useEffect(() => {
    if (scriptsFilterColumn) {
      getFilteredBy();
    }
  }, [scriptsFilterColumn, selection]);
  
  const openScriptModal = scriptId => {
    setScriptIdSelected(scriptId);
    setExecScriptsMenuOpen(true);
  };

  const onExecuteScriptDialogClose = () => {
    setScriptIdSelected(null);
    setExecScriptsMenuOpen(false);
  };

  const handleClickSettings = e => setShowSettingsMenu(e.currentTarget);

  const handleClickRelatedMenu = e => setShowFindRelatedMenu(e.currentTarget);

  const handleClose = () => {
    setShowSettingsMenu(null);
    setShowFindRelatedMenu(null);
  };

  const handleRelatedLinkClick = (item: FindRelatedItem) => {
    if (item.list) {
      let searchParam = "";

      if (item.expression) {
        searchParam = item.expression;
      }

      if (item.customExpression) {
        searchParam = item.customExpression(selection.join(", "));
      }

      if (selection.length) {
        searchParam += ` in (${selection.join(", ")})`;
      } else {
        findRelatedByFilter(item.customExpression || searchParam, item.list);
        return;
      }

      openInternalLink(searchParam ? `/${item.list}?search=${searchParam}` : `/${item.list}`);
    } else {
      window.location.href = `/find/related?destList=${item.destination}&sourceList=${rootEntity}&ids=${selection.join(
        ","
      )}`;
    }

    handleClose();
  };

  const handleDeleteClick = () => {
    onDelete(selection[0]);
    handleClose();
  };
  
  const existingRecordSelected = Boolean(selection.length) && selection[0] !== "NEW";

  const isSendMessageAvailable = SendMessageEntities.includes(rootEntity) && Array.isArray(emailTemplatesWithKeyCode) && emailTemplatesWithKeyCode.length;

  const settingsItems = [
    (selection.length === 0 || existingRecordSelected) && scripts?.length && (
      <ScriptsMenu
        key="ScriptsMenu"
        classes={classes}
        scripts={scriptsMenuOpen?.entity ? filterScriptsBy[scriptsMenuOpen?.entity]?.scripts : scripts}
        entity={rootEntity}
        filterScriptsBy={filterScriptsBy}
        closeAll={handleClose}
        openScriptModal={openScriptModal}
        scriptsMenuOpen={scriptsMenuOpen?.anchor}
        setScriptsMenuOpen={setScriptsMenuOpen}
      />
    ),
    isSendMessageAvailable
    && <SendMessageMenu key="SendMessageMenu" selection={selection} entity={rootEntity} closeAll={handleClose} />,
    CogwheelAdornment && (
      <CogwheelAdornment
        key="CogwheelAdornment"
        closeMenu={handleClose}
        menuItemClass="listItemPadding"
        searchQuery={searchQuery}
        selection={selection}
        showConfirm={showConfirm}
        onCreate={onCreate}
        entity={rootEntity}
        showBulkEditDrawer={showBulkEditDrawer}
        toggleBulkEditDrawer={toggleBulkEditDrawer}
        records={records}
      />
    ),
    deleteEnabled
    && (
      <MenuItem
        key="DeleteRecord"
        disabled={selection.length !== 1 || !existingRecordSelected}
        onClick={handleDeleteClick}
        classes={{
          root: "listItemPadding errorColor"
        }}
      >
        {$t('delete_record')}
      </MenuItem>
    )].filter(i => i);

    const findRelatedTitle = useMemo(() => {
      switch (true) {
        case (selection.length && selection.length < PLAIN_LIST_MAX_PAGE_SIZE):
        default:
          return "Find Related";
        case (!findRelated):
          return "No find related filters found";
        case (fetch.pending):
          return "Loading...";
        case (records.filteredCount > PLAIN_LIST_MAX_PAGE_SIZE):
          return `Not available for greater than ${PLAIN_LIST_MAX_PAGE_SIZE} records`;
      }
    }, [findRelated, selection, fetch.pending, records.filteredCount]);

    return (
      <>
        <ExecuteScriptModal
          opened={Boolean(execScriptsMenuOpen)}
          onClose={onExecuteScriptDialogClose}
          scriptId={scriptIdSelected}
          filteredCount={filteredCount}
          filteredSelection={filterScriptsBy && filterScriptsBy[scriptsMenuOpen?.entity]?.ids}
        />

        <div className={classes.root}>
          <SearchInput
            innerRef={searchComponentNode}
            onQuerySearch={onQuerySearch}
            querySearch={querySearch}
            rootEntity={rootEntity}
            changeQueryView={changeQueryView}
            searchMenuItemsRenderer={searchMenuItemsRenderer}
            placeholder={$t('find2')}
          />

          <div className={clsx("centeredFlex", !querySearch && "flex-fill")}>
            {!querySearch && (
              <Tooltip title={findRelatedTitle} disableFocusListener>
                <div className={clsx(querySearch && classes.findRelated)}>
                  <IconButton
                    classes={{
                      root: clsx(classes.actionsBarButton, classes.customIconButton),
                      disabled: classes.buttonDisabledOpacity
                    }}
                    disabled={(selection.length > 0 && selection.length < PLAIN_LIST_MAX_PAGE_SIZE) ? false : (!findRelated || fetch.pending || records.filteredCount > PLAIN_LIST_MAX_PAGE_SIZE)}
                    className="ml-1"
                    aria-owns={showFindRelatedMenu ? "related" : undefined}
                    aria-haspopup="true"
                    onClick={handleClickRelatedMenu}
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
              onClose={handleClose}
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
                {$t('find_related_to_record',[selection.length])}
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
                <FindRelatedMenu findRelated={findRelated} handleRelatedLinkClick={handleRelatedLinkClick} />
              )}
            </Menu>

            <div className={clsx("flex-fill text-center", { "d-none": querySearch })} />
            <div className={classes.actionArea}>
              <Tooltip title={$t('add_record')} disableFocusListener>
                <div>
                  <IconButton
                    color="inherit"
                    onClick={onCreate}
                    disabled={!onCreate || fetch.pending || createButtonDisabled}
                    classes={{
                      root: classes.actionsBarButton,
                      disabled: classes.buttonDisabledFade
                    }}
                    size="large"
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

              <Tooltip title={$t('share')} disableFocusListener>
                <div>
                  <IconButton
                    onClick={toggleExportDrawer}
                    disabled={!hasShareTypes}
                    classes={{
                      root: showExportDrawer ? classes.shareOnBackdrop : classes.actionsBarButton,
                      disabled: classes.buttonDisabledFade
                    }}
                    size="large"
                  >
                    <Share />
                  </IconButton>
                </div>
              </Tooltip>
              <Tooltip title={$t('more_options')} disableFocusListener>
                <div>
                  <IconButton
                    classes={{
                      root: classes.actionsBarButton,
                      disabled: classes.buttonDisabledFade
                    }}
                    disabled={fetch.pending || !settingsItems.length}
                    color="inherit"
                    aria-owns={showSettingsMenu ? "settings" : undefined}
                    aria-haspopup="true"
                    onClick={handleClickSettings}
                    size="large"
                  >
                    <Settings />
                  </IconButton>
                </div>
              </Tooltip>
              <Menu
                id="settings"
                anchorEl={showSettingsMenu}
                open={Boolean(showSettingsMenu)}
                onClose={handleClose}
                classes={{
                  paper: classes.cogWheelMenuOffset
                }}
                disableAutoFocusItem

              >
                {settingsItems}
              </Menu>
            </div>
          </div>
        </div>
      </>
  );
};

export default BottomAppBar;