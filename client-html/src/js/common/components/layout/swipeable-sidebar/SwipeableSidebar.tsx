/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import clsx from "clsx";
import { createStyles, withStyles } from "@mui/styles";
import { darken } from "@mui/material/styles";
import SwipeableDrawer from "@mui/material/SwipeableDrawer";
import Divider from "@mui/material/Divider";
import { useCallback, useEffect, useState } from "react";
import { Backdrop } from "@mui/material";
import Collapse from "@mui/material/Collapse";
import Typography from "@mui/material/Typography";
import { AppTheme } from "../../../../model/common/Theme";
import { State } from "../../../../reducers/state";
import { getDashboardCategories, getDashboardSearch, getFavoriteScripts } from "../../../../containers/dashboard/actions";
import { openInternalLink } from "../../../utils/links";
import { getEntityDisplayName } from "../../../utils/getEntityDisplayName";
import { checkPermissions, getOnDemandScripts, showConfirm } from "../../../actions";
import { clearSelectedCategoryItem, toggleSwipeableDrawer } from "./actions";
import UserSearch from "./components/UserSearch";
import SearchResults from "./components/searchResults/SearchResults";
import SidebarLatestActivity from "./components/SidebarLatestActivity";
import Favorites from "./components/favorites/Favorites";
import { getResultId, VARIANTS } from "./utils";
import HamburgerMenu from "./components/HamburgerMenu";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";
import Navigation from "../../navigation/Navigation";
import NavigationCategory from "../../navigation/NavigationCategory";
import { NoArgFunction } from "../../../../model/common/CommonFunctions";
import { SelectedCategoryItem } from "../../../../model/common/drawer/SwipeableDrawerModel";

export const SWIPEABLE_SIDEBAR_WIDTH: number = 350;

export const CATEGORY_SIDEBAR_WIDTH: number = 850;

const styles = (theme: AppTheme) =>
  createStyles({
    drawerRoot: {
      zIndex: theme.zIndex.modal + 2,
    },
    drawerPaper: {
      overflowX: "hidden",
      zIndex: theme.zIndex.modal,
    },
    drawerWidth: {
      width: SWIPEABLE_SIDEBAR_WIDTH,
      maxWidth: SWIPEABLE_SIDEBAR_WIDTH,
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
      width: `100%`,
      height: "100%",
      position: "fixed",
      display: "flex",
      background: theme.palette.background.default,
      transition: "transform 225ms cubic-bezier(0, 0, 0.2, 1) 0ms",
      left: `${SWIPEABLE_SIDEBAR_WIDTH}px`,
      transform: `translateX(-180%)`,
      maxWidth: `calc(90% - ${SWIPEABLE_SIDEBAR_WIDTH}px)`
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
  isFormDirty: boolean;
  resetEditView: any;
  showConfirm: ShowConfirmCaller;
  classes: any;
  opened: boolean;
  toggleSwipeableDrawer: any;
  getCategories: any;
  getScripts: any;
  getFavoriteScripts: any;
  getSearchResults: any;
  userSearch: any;
  searchResults: any;
  variant: keyof typeof VARIANTS;
  categories: any;
  getScriptsPermissions: any;
  scripts: any;
  hasScriptsPermissions: any;
  theme?: AppTheme;
  onClearSelectedCategoryItem?: NoArgFunction;
  selectedCategoryItem?: SelectedCategoryItem;
}

const SwipeableSidebar: React.FC<Props> = props => {
  const {
    form,
    isFormDirty,
    resetEditView,
    showConfirm,
    classes,
    opened,
    toggleSwipeableDrawer,
    getCategories,
    getScripts,
    getFavoriteScripts,
    getSearchResults,
    userSearch,
    searchResults,
    variant,
    categories,
    getScriptsPermissions,
    scripts,
    hasScriptsPermissions,
    onClearSelectedCategoryItem,
    selectedCategoryItem
  } = props;

  const [controlResults, setControlResults] = useState([]);
  const [resultIndex, setResultIndex] = useState(-1);
  const [scriptIdSelected, setScriptIdSelected] = useState(null);
  const [execMenuOpened, setExecMenuOpened] = useState(false);
  const [selected, setSelected] = useState(null);
  const [focusOnSearchInput, setFocusOnSearchInput] = React.useState<boolean>(false);

  useEffect(() => {
    getCategories();
    getScriptsPermissions();
  }, []);

  useEffect(() => {
    if (hasScriptsPermissions) {
      getScripts();
      getFavoriteScripts();
    }
  }, [hasScriptsPermissions]);

  const toggleDrawer = open => event => {
    if (event && event.type === "keydown" && (event.key === "Tab" || event.key === "Shift")) {
      return;
    }

    toggleSwipeableDrawer();

    if (!open) {
      getSearchResults("");
      onClearSelectedCategoryItem();
    }
  };

  const formatSearchControlResult = React.useCallback(
    (searchResults, updateCustomIndex = false, selectedItem = null, selectedEntity = null) => {
      const results = [];

      if (userSearch && categories) {
        categories
          .filter(c => c.category.toLowerCase().includes(userSearch.toLowerCase()))
          .map((c, i) => results.push(Object.assign(c, { entity: "category", htmlId: getResultId(i, c.category) })));
      }

      if (userSearch && scripts && hasScriptsPermissions) {
        scripts
          .filter(s => s.name.toLowerCase().includes(userSearch.toLowerCase()))
          .map((s, i) => results.push(Object.assign(s, { entity: "script", htmlId: getResultId(i, s.name) })));
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
    [userSearch, categories, resultIndex]
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
                  categories.find(c => c.category === getEntityDisplayName(openItem.entity)).url + "/" + openItem.id
                );
              }
            }
            break;
          }
        }
      }
    },
    [controlResults, resultIndex, categories]
  );

  useEffect(() => {
    document.addEventListener("keydown", onKeyDown);

    return () => {
      document.removeEventListener("keydown", onKeyDown);
    };
  }, [controlResults, resultIndex, categories]);

  const checkSelectedResult = React.useCallback(
    (type, field, value) => {
      if (controlResults && resultIndex >= 0) {
        const selectedResult = controlResults[resultIndex];
        return selectedResult.entity === type && selectedResult[field] === value;
      }
      return false;
    },
    [controlResults, resultIndex]
  );

  const showConfirmHandler = React.useCallback(onConfirm => {
    if (isFormDirty && resetEditView) {
      showConfirm(
        {
          onConfirm,
          cancelButtonText: "DISCARD CHANGES"
        }
      );
    } else {
      onConfirm();
    }
  }, [isFormDirty, resetEditView]);

  const clearNavigationSelection = useCallback(() => {
    setSelected(null);
    onClearSelectedCategoryItem();
  }, []);

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
            <HamburgerMenu variant={variant} form={form} />
          </div>
          <UserSearch
            getSearchResults={getSearchResults}
            setFocusOnSearchInput={setFocusOnSearchInput}
            focusOnSearchInput={(focusOnSearchInput || showUserSearch)}
          />
          <div>
            <Collapse in={(focusOnSearchInput && !showUserSearch)}>
              <div className="p-2">
                <Typography className="mb-1" component="div" variant="body2">
                  Navigate to an onCourse feature by typing the action you want to perform.
                </Typography>
                <Typography className="mb-1" component="div" variant="body2">
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
                  showConfirm={showConfirmHandler}
                  setExecMenuOpened={setExecMenuOpened}
                  setScriptIdSelected={setScriptIdSelected}
                />
              </div>
              <div className={showUserSearch ? "d-none" : ""}>
                {/* <Favorites */}
                {/*  classes={{ topBar: classes.favoritesTopBar }} */}
                {/*  showConfirm={showConfirmHandler} */}
                {/*  isFormDirty={isFormDirty} */}
                {/*  scriptIdSelected={scriptIdSelected} */}
                {/*  setScriptIdSelected={setScriptIdSelected} */}
                {/*  execMenuOpened={execMenuOpened} */}
                {/*  setExecMenuOpened={setExecMenuOpened} */}
                {/* /> */}

                <Navigation
                  selected={selected}
                  setSelected={setSelected}
                  scriptIdSelected={scriptIdSelected}
                  setScriptIdSelected={setScriptIdSelected}
                  execMenuOpened={execMenuOpened}
                  setExecMenuOpened={setExecMenuOpened}
                />
                <Divider variant="middle" />
                <SidebarLatestActivity showConfirm={showConfirmHandler} checkSelectedResult={checkSelectedResult} />
              </div>
            </Collapse>
          </div>
        </div>

        <div className={
          clsx(
            classes.categoryRoot,
            opened && (selected !== null || selectedCategoryItem.id !== null) && classes.categoryVisible
          )}
        >
          <NavigationCategory
            selected={selected}
            onClose={clearNavigationSelection}
            selectedCategoryItem={selectedCategoryItem}
          />
        </div>
      </SwipeableDrawer>


      {variant !== "temporary" && (
        <Backdrop
          open={opened && (selected !== null || selectedCategoryItem.id !== null)}
          onClick={toggleDrawer(false)}
        />
      )}
    </>
);
};

const mapsStateToProps = (state: State) => ({
  isFormDirty: state.swipeableDrawer.isDirty,
  resetEditView: state.swipeableDrawer.resetEditView,
  opened: state.swipeableDrawer.opened,
  variant: state.swipeableDrawer.variant,
  userSearch: state.dashboard.userSearch,
  categories: state.dashboard.categories,
  searchResults: state.dashboard.searchResults.results,
  scripts: state.dashboard.scripts,
  hasScriptsPermissions: state.access["ADMIN"],
  selectedCategoryItem: state.swipeableDrawer.categoryItem,
});

const mapStateToDispatch = (dispatch: Dispatch<any>) => ({
  toggleSwipeableDrawer: () => dispatch(toggleSwipeableDrawer()),
  getSearchResults: (search: string) => dispatch(getDashboardSearch(search)),
  getCategories: () => dispatch(getDashboardCategories()),
  getScripts: () => dispatch(getOnDemandScripts()),
  getFavoriteScripts: () => dispatch(getFavoriteScripts()),
  getScriptsPermissions: () => dispatch(checkPermissions({ keyCode: "ADMIN" })),
  showConfirm: props => dispatch(showConfirm(props)),
  onClearSelectedCategoryItem: () => dispatch(clearSelectedCategoryItem()),
});

export default connect<any, any, any>(mapsStateToProps, mapStateToDispatch)(withStyles(styles, { withTheme: true })(SwipeableSidebar));
