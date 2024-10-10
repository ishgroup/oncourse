/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { PreferenceEnum, SearchQuery } from '@api/model';
import { Backdrop, Collapse, Divider } from '@mui/material';
import { darken } from '@mui/material/styles';
import SwipeableDrawer from '@mui/material/SwipeableDrawer';
import Typography from '@mui/material/Typography';
import clsx from 'clsx';
import { AnyArgFunction, AppTheme, openInternalLink } from 'ish-ui';
import * as React from 'react';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { withStyles } from 'tss-react/mui';
import { DASHBOARD_FAVORITES_KEY, FAVORITE_SCRIPTS_KEY, SPECIAL_TYPES_DISPLAY_KEY } from '../../../../constants/Config';
import ExecuteScriptModal from '../../../../containers/automation/containers/scripts/components/ExecuteScriptModal';
import { getDashboardSearch } from '../../../../containers/dashboard/actions';
import ContactInsight from '../../../../containers/entities/contacts/components/contact-insight/ContactInsight';
import SendMessageEditView from '../../../../containers/entities/messages/components/SendMessageEditView';
import { DashboardItem } from '../../../../model/dashboard';
import { State } from '../../../../reducers/state';
import { checkPermissions, getOnDemandScripts, getUserPreferences, setUserPreference } from '../../../actions';
import { getEntityDisplayName } from '../../../utils/getEntityDisplayName';
import { useAppSelector } from '../../../utils/hooks';
import navigation from '../../navigation/data/navigation.json';
import Favorites from '../../navigation/favorites/Favorites';
import Navigation from '../../navigation/Navigation';
import NavigationCategory from '../../navigation/NavigationCategory';
import { setSwipeableDrawerSelection, toggleSwipeableDrawer } from './actions';
import HamburgerMenu from './components/HamburgerMenu';
import SearchResults from './components/searchResults/SearchResults';
import SidebarLatestActivity from './components/SidebarLatestActivity';
import UserSearch from './components/UserSearch';
import { getResultId, VARIANTS } from './utils';

export const SWIPEABLE_SIDEBAR_WIDTH: number = 350;

export const CATEGORY_SIDEBAR_WIDTH: number = 850;

const styles = (theme: AppTheme) =>
  ({
    drawerRoot: {
      zIndex: theme.zIndex.modal + 2,
    },
    drawerPaper: {
      overflowX: "hidden"
    },
    drawerWidth: {
      width: SWIPEABLE_SIDEBAR_WIDTH,
      maxWidth: SWIPEABLE_SIDEBAR_WIDTH,
      flex: 1,
      zIndex: 2,
      position: "relative",
      background: theme.palette.background.paper,
    },
    appBar: {
      backgroundColor:
        theme.palette.mode === "light" ? theme.palette.primary.main : darken(theme.palette.background.default, 0.4)
    },
    searchInput: {
      backgroundColor: theme.palette.background.default,
      padding: "10px 15px",
      borderRadius: "4px"
    },
    toolBarGutters: {
      padding: "0 16px"
    },
    searchResultsRoot: {
      padding: theme.spacing(2),
      transition: theme.transitions.create("all", {
        duration: theme.transitions.duration.shorter,
        easing: theme.transitions.easing.easeInOut
      })
    },
    favoritesTopBar: {
      background: "none"
    },
    toolbar: {
      ...theme.mixins.toolbar,
      display: "flex",
      alignItems: "center"
    },
    searchResultsWrapper: {
      overflowY: "auto",
      maxHeight: "calc(100vh - 64px - 60px)",
      transition: "all 0.5s ease-in"
    },
    categoryRoot: {
      top: 0,
      zIndex: 1,
      minWidth: `${CATEGORY_SIDEBAR_WIDTH}px`,
      width: `calc(100vw - ${SWIPEABLE_SIDEBAR_WIDTH}px - 20%)`,
      height: "100%",
      position: "fixed",
      display: "flex",
      background: theme.palette.background.default,
      transition: "transform 225ms cubic-bezier(0, 0, 0.2, 1) 0ms",
      left: `${SWIPEABLE_SIDEBAR_WIDTH}px`,
      transform: "translateX(-100%)"
    },
    categoryVisible: {
      transform: "translateX(1px)"
    },
    paperBorder: {
      borderRight: `1px solid ${theme.palette.divider}`
    },
    logo: { height: "36px", width: "auto" },
  });

interface Props {
  form: string;
  resetEditView: any;
  classes: any;
  opened: boolean;
  toggleSwipeableDrawer: any;
  getScripts: any;
  getFavoriteScripts: any;
  getFavorites: any;
  getSearchResults: any;
  userSearch: any;
  searchResults: any;
  variant: keyof typeof VARIANTS;
  getScriptsPermissions: any;
  scripts: any;
  hasScriptsPermissions: any;
  dispatch?: Dispatch;
  selected?: number | string;
  setSelected?: AnyArgFunction;
  listEntity?: string;
  listSelection?: string[];
  listFilteredCount?: number;
  listSearchQuery?: SearchQuery;
}

const sortItems = (a, b) => {
  if (a.category === "quickEnrol") return -1;
  if (b.category === "quickEnrol") return 1;
  const aName = (a.category || a.name).toUpperCase();
  const bName = (b.category || b.name).toUpperCase();

  return aName.localeCompare(bName);
};

const SwipeableSidebar: React.FC<Props> = props => {
  const {
    form,
    dispatch,
    classes,
    opened,
    toggleSwipeableDrawer,
    getScripts,
    getFavorites,
    getFavoriteScripts,
    getSearchResults,
    userSearch,
    searchResults,
    variant,
    getScriptsPermissions,
    scripts,
    hasScriptsPermissions,
    selected,
    setSelected,
    listSelection,
    listFilteredCount,
    listEntity,
    listSearchQuery
  } = props;

  const [controlResults, setControlResults] = useState([]);
  const [resultIndex, setResultIndex] = useState(-1);
  const [scriptIdSelected, setScriptIdSelected] = useState(null);
  const [execMenuOpened, setExecMenuOpened] = useState(false);
  const [focusOnSearchInput, setFocusOnSearchInput] = useState<boolean>(false);
  const [disabled, setDisabled] = useState({});

  const favoritesString = useAppSelector(state => state.userPreferences[PreferenceEnum[DASHBOARD_FAVORITES_KEY]]);
  const favoriteScriptsString = useAppSelector(state => state.userPreferences[PreferenceEnum[FAVORITE_SCRIPTS_KEY]]);

  const favorites = useMemo<string[]>(() => (favoritesString ? favoritesString.split(",") : []), [favoritesString]);
  const favoriteScripts = useMemo<string[]>(() => (favoriteScriptsString ? favoriteScriptsString.split(",") : []), [favoriteScriptsString]);

  const accessTypes = useAppSelector(state => state.userPreferences[SPECIAL_TYPES_DISPLAY_KEY] === 'true');

  useEffect(() => {
    setDisabled({
      faculties: !accessTypes,
      subjects: !accessTypes
    });
  }, [accessTypes]);

  useEffect(() => {
    getFavorites();
    getScriptsPermissions();
  }, []);

  useEffect(() => {
    if (hasScriptsPermissions) {
      getScripts();
      getFavoriteScripts();
    }
  }, [hasScriptsPermissions]);

  useEffect(() => {
    if (!opened) {
      setSelected(null);
    }
  }, [opened]);

  const toggleDrawer = open => event => {
    if (event && event.type === "keydown" && (event.key === "Tab" || event.key === "Shift")) {
      return;
    }

    toggleSwipeableDrawer();

    if (!open) {
      getSearchResults("");
    }
  };

  const formatSearchControlResult = React.useCallback(
    (searchResults, updateCustomIndex = false, selectedItem = null, selectedEntity = null) => {
      const results = [];

      if (userSearch) {
        navigation.features
          .filter(c => c.title.toLowerCase().includes(userSearch.toLowerCase()))
          .forEach((c, i) => results.push(Object.assign(c, { entity: "category", htmlId: getResultId(i, c.key) })));
      }

      if (userSearch && scripts && hasScriptsPermissions) {
        scripts
          .filter(s => s.name.toLowerCase().includes(userSearch.toLowerCase()))
          .forEach((s, i) => results.push(Object.assign(s, { entity: "script", htmlId: getResultId(i, s.name) })));
      }

      searchResults.map(res => {
        res.items.some((item, index) => {
          if (index > 2 && !res.showAll) {
            return true;
          }
          results.push(
            Object.assign(item, { entity: res.entity, htmlId: getResultId(index, `${res.entity}-${item.id}`) })
          );
          return false;
        });
      });

      setControlResults(() => results);

      if (results.length > 0 && !updateCustomIndex) {
        setResultIndex(-1);
      }

      if (results.length > 0 && updateCustomIndex && selectedItem !== null) {
        const selectedIndex = results.findIndex(r => r.id === selectedItem.id && r.entity === selectedEntity);
        if (resultIndex > selectedIndex + 2) setResultIndex(selectedIndex);
      }
    },
    [userSearch, resultIndex]
  );

  useEffect(() => {
    if (searchResults) {
      const updated = [...searchResults];

      updated.map(res => Object.assign(res, { showAll: false }));

      formatSearchControlResult(updated);
    }
  }, [searchResults]);

  const showUserSearch = userSearch && userSearch.length > 0;
  const getUserSearchField: any = document.getElementsByName("sidebar_user_search");

  useEffect(() => {
    if (getUserSearchField.length > 0) {
      if (resultIndex > -1) {
        getUserSearchField[0].blur();
      } else if (resultIndex <= -1) {
        getUserSearchField[0].focus();
      }
    }
  }, [resultIndex, getUserSearchField]);

  const setItemScrollPosition = React.useCallback((index, controlResults) => {
    const item = controlResults[index];
    if (item) {
      const id = item.htmlId;
      if (id) {
        const searchResultsWrapper: any = document.getElementById("search-results-wrapper");
        if (searchResultsWrapper) {
          const node: any = document.getElementById(id);
          if (node) {
            const windowHeight = window.innerHeight / 2 + searchResultsWrapper.offsetTop;
            const parentOffset = searchResultsWrapper.offsetTop * 3;
            const reduceOffset = windowHeight > parentOffset ? parentOffset : windowHeight;
            const offsetY = node.offsetTop + node.offsetParent.offsetTop + node.offsetParent.offsetParent.offsetTop - reduceOffset;

            if (offsetY > 0) {
              searchResultsWrapper.scrollTo(0, offsetY);
            } else {
              searchResultsWrapper.scrollTo(0, 0);
            }
          }
        }
      }
    }
  }, []);

  const onKeyDown = React.useCallback(
    e => {
      if (controlResults.length > 0) {
        switch (e.keyCode) {
          case 40: {
            setResultIndex(prev => {
              const nextIndex = prev + 1;
              if (nextIndex >= controlResults.length) {
                return prev;
              }
              setItemScrollPosition(nextIndex, controlResults);
              return nextIndex;
            });
            break;
          }
          case 38: {
            setResultIndex(prev => {
              const prevIndex = prev - 1;
              if (prevIndex < -1) {
                return prev;
              }
              setItemScrollPosition(prevIndex, controlResults);
              return prevIndex;
            });
            break;
          }
          case 13: {
            const openItem = controlResults[resultIndex];
            if (openItem !== undefined) {
              if (openItem.entity === "category") {
                openInternalLink(openItem.url);
              } else {
                openInternalLink(
                  navigation.features.find(c => c.title === getEntityDisplayName(openItem.entity)).link + "/" + openItem.id
                );
              }
            }
            break;
          }
        }
      }
    },
    [controlResults, resultIndex]
  );

  useEffect(() => {
    document.addEventListener("keydown", onKeyDown);

    return () => {
      document.removeEventListener("keydown", onKeyDown);
    };
  }, [controlResults, resultIndex]);

  const checkSelectedResult = useCallback(
    (type, field, value) => {
      if (controlResults && resultIndex >= 0) {
        const selectedResult = controlResults[resultIndex];
        return selectedResult.entity === type && selectedResult[field] === value;
      }
      return false;
    },
    [controlResults, resultIndex]
  );

  const groupedSortedItems = useMemo<DashboardItem[]>(() => [
      ...navigation.features.map(f => ({
        category: f.key, url: f.link, name: f.title, id: null
      })),
      ...((hasScriptsPermissions && scripts as DashboardItem[]) || [])]
      .sort(sortItems),
    [navigation.features, scripts]);

  const isContactIdSelected = selected && typeof selected === "number";

  const updateFavorites = (key, type: "category" | "automation") => {
    if (type === "category") {
      dispatch(setUserPreference({
        key: DASHBOARD_FAVORITES_KEY,
        value: favorites.includes(key)
          ? favorites.filter(v => v !== key).toString()
          : [...favorites, key].toString()
      }));
    }
    if (type === "automation") {
      dispatch(setUserPreference({
        key: FAVORITE_SCRIPTS_KEY,
        value: favoriteScripts.includes(key)
          ? favoriteScripts.filter(v => v !== key).toString()
          : [...favoriteScripts, key].toString()
      }));
    }
  };

  return (
    <>
      <SwipeableDrawer
        variant={variant}
        open={opened}
        onClose={toggleDrawer(false)}
        onOpen={toggleDrawer(true)}
        classes={{
          paper: classes.drawerPaper,
          root: classes.drawerRoot,
        }}
        PaperProps={{
          classes: {
            root: variant === "temporary" && opened && selected !== null && classes.paperBorder
          }
        }}
      >
        <div className={classes.drawerWidth}>
          <div className={clsx("pl-2", classes.toolbar)}>
            <HamburgerMenu variant={variant} form={form}/>
          </div>
          <UserSearch
            getSearchResults={getSearchResults}
            setFocusOnSearchInput={setFocusOnSearchInput}
          />
          <div>
            <Collapse in={(focusOnSearchInput && !showUserSearch)}>
              <div className="p-2">
                <Typography className="mb-1" component="div" variant="body2" color="textSecondary">
                  Navigate to an onCourse feature by typing the action you want to perform.
                </Typography>
                <Typography className="mb-1" component="div" variant="body2" color="textSecondary">
                  Search for contacts by phone, email or name. Find courses, invoices and much more.
                </Typography>
              </div>
            </Collapse>
            <Collapse in={!focusOnSearchInput || showUserSearch}>
              <div
                id="search-results-wrapper"
                className={clsx(classes.searchResultsWrapper, !showUserSearch ? "d-none" : "")}
              >
                <SearchResults
                  classes={{ root: classes.searchResultsRoot }}
                  userSearch={userSearch}
                  checkSelectedResult={checkSelectedResult}
                  setExecMenuOpened={setExecMenuOpened}
                  setScriptIdSelected={setScriptIdSelected}
                  groupedSortedItems={groupedSortedItems}
                  setSelected={setSelected}
                  favorites={favorites}
                  favoriteScripts={favoriteScripts}
                  updateFavorites={updateFavorites}
                />
              </div>
              <div className={showUserSearch ? "d-none" : ""}>
                <ExecuteScriptModal
                  opened={execMenuOpened}
                  onClose={() => {
                    setExecMenuOpened(false);
                    setScriptIdSelected(null);
                  }}
                  scriptId={scriptIdSelected}
                />
                <Favorites
                  classes={{ topBar: classes.favoritesTopBar }}
                  groupedSortedItems={groupedSortedItems}
                  setScriptIdSelected={setScriptIdSelected}
                  execMenuOpened={execMenuOpened}
                  setExecMenuOpened={setExecMenuOpened}
                  scriptIdSelected={scriptIdSelected}
                  favoriteScripts={favoriteScripts}
                  favorites={favorites}
                  updateFavorites={updateFavorites}
                />
                <Navigation
                  selected={selected}
                  setSelected={setSelected}
                  scriptIdSelected={scriptIdSelected}
                  setScriptIdSelected={setScriptIdSelected}
                  execMenuOpened={execMenuOpened}
                  setExecMenuOpened={setExecMenuOpened}
                />
                <Divider variant="middle" className="mb-2"/>
                <SidebarLatestActivity checkSelectedResult={checkSelectedResult}/>
              </div>
            </Collapse>
          </div>
        </div>

        <div className={
          clsx(
            classes.categoryRoot,
            opened && selected !== null && classes.categoryVisible
          )
        }
        >
          {isContactIdSelected ? (
            <ContactInsight id={selected as number} onClose={() => setSelected(null)}/>
          ) : (
            <NavigationCategory
              selected={selected as string}
              favorites={favorites}
              disabled={disabled}
              favoriteScripts={favoriteScripts}
              onClose={() => setSelected(null)}
              setScriptIdSelected={setScriptIdSelected}
              setExecMenuOpened={setExecMenuOpened}
              updateFavorites={updateFavorites}
            />
          )}
        </div>
      </SwipeableDrawer>

      {variant !== "temporary" && (
        <Backdrop
          open={opened && selected !== null}
          onClick={toggleDrawer(false)}
        />
      )}

      <SendMessageEditView
        selection={isContactIdSelected ? [String(selected)] : listSelection}
        filteredCount={isContactIdSelected ? 1 : listFilteredCount}
        listEntity={isContactIdSelected ? "Contact" : listEntity}
        listSearchQuery={isContactIdSelected ? {} : listSearchQuery}
        selectionOnly={isContactIdSelected}
      />
    </>
  );
};

const mapsStateToProps = (state: State) => ({
  listFilteredCount: state.list.records.filteredCount,
  listSelection: state.list.selection,
  listEntity: state.list.records.entity,
  listSearchQuery: state.list.searchQuery,
  selected: state.swipeableDrawer.selected,
  resetEditView: state.swipeableDrawer.resetEditView,
  opened: state.swipeableDrawer.opened,
  variant: state.swipeableDrawer.variant,
  userSearch: state.dashboard.userSearch,
  searchResults: state.dashboard.searchResults.results,
  scripts: state.dashboard.scripts,
  hasScriptsPermissions: state.access["ADMIN"]
});

const mapStateToDispatch = (dispatch: Dispatch<any>) => ({
  dispatch,
  setSelected: selection => dispatch(setSwipeableDrawerSelection(selection)),
  toggleSwipeableDrawer: () => dispatch(toggleSwipeableDrawer()),
  getSearchResults: (search: string) => dispatch(getDashboardSearch(search)),
  getScripts: () => dispatch(getOnDemandScripts()),
  getFavoriteScripts: () => dispatch(getUserPreferences([FAVORITE_SCRIPTS_KEY])),
  getFavorites: () => dispatch(getUserPreferences([DASHBOARD_FAVORITES_KEY])),
  getScriptsPermissions: () => dispatch(checkPermissions({ keyCode: "ADMIN" })),
});

export default connect<any, any, any>(mapsStateToProps, mapStateToDispatch)(withStyles(SwipeableSidebar, styles));