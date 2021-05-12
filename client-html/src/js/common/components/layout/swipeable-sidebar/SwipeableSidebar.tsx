/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import clsx from "clsx";
import { darken, createStyles, withStyles } from "@material-ui/core/styles";
import SwipeableDrawer from "@material-ui/core/SwipeableDrawer";
import Divider from "@material-ui/core/Divider";
import { AppTheme } from "../../../../model/common/Theme";
import { State } from "../../../../reducers/state";
import { getDashboardCategories, getDashboardSearch, getFavoriteScripts } from "../../../../containers/dashboard/actions";
import { openInternalLink } from "../../../utils/links";
import { getEntityDisplayName } from "../../../utils/getEntityDisplayName";
import { checkPermissions, getOnDemandScripts, showConfirm } from "../../../actions";
import { toggleSwipeableDrawer } from "./actions";
import UserSearch from "./components/UserSearch";
import SearchResults from "./components/searchResults/SearchResults";
import SidebarLatestActivity from "./components/SidebarLatestActivity";
import Favorites from "./components/favorites/Favorites";
import { getResultId } from "./utils";
import HamburgerMenu from "./components/HamburgerMenu";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";

export const SWIPEABLE_SIDEBAR_WIDTH: number = 350;

const styles = (theme: AppTheme) =>
  createStyles({
    drawerPaper: {
      overflowX: "hidden"
    },
    drawerWidth: {
      width: SWIPEABLE_SIDEBAR_WIDTH,
      maxWidth: SWIPEABLE_SIDEBAR_WIDTH,
      position: "relative"
    },
    appBar: {
      backgroundColor:
        theme.palette.type === "light" ? theme.palette.primary.main : darken(theme.palette.background.default, 0.4)
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
      padding: `${theme.spacing(2)}px ${theme.spacing(2)}px ${theme.spacing(2)}px 125px`,
      transition: "all 0.5s ease-in"
    },
    favoritesTopBar: {
      background: "none"
    },
    toolbar: {
      ...theme.mixins.toolbar
    },
    searchResultsWrapper: {
      overflowY: "auto",
      maxHeight: "calc(100vh - 64px - 60px)",
      transition: "all 0.5s ease-in"
    }
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
  variant: any;
  categories: any;
  getScriptsPermissions: any;
  scripts: any;
  hasScriptsPermissions: any;
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
    hasScriptsPermissions
  } = props;

  const [controlResults, setControlResults] = React.useState([]);
  const [resultIndex, setResultIndex] = React.useState(-1);
  const [scriptIdSelected, setScriptIdSelected] = React.useState(null);
  const [execMenuOpened, setExecMenuOpened] = React.useState(false);

  React.useEffect(() => {
    getCategories();
    getScriptsPermissions();
  }, []);

  React.useEffect(() => {
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

  React.useEffect(() => {
    if (searchResults) {
      const updated = [...searchResults];

      updated.map(res => Object.assign(res, { showAll: false }));

      formatSearchControlResult(updated);
    }
  }, [searchResults]);

  const showUserSearch = userSearch && userSearch.length > 0;
  const getUserSearchField: any = document.getElementsByName("sidebar_user_search");

  React.useEffect(() => {
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

  React.useEffect(() => {
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

  return (
    <>
      <SwipeableDrawer
        variant={variant}
        open={opened}
        onClose={toggleDrawer(false)}
        onOpen={toggleDrawer(true)}
        classes={{
          paper: classes.drawerPaper
        }}
      >
        <div className={classes.drawerWidth}>
          <div className={clsx("pl-2", classes.toolbar)}>
            <HamburgerMenu variant={variant} form={form} />
          </div>
          <UserSearch getSearchResults={getSearchResults} />
          <div>
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
              <Favorites
                classes={{ topBar: classes.favoritesTopBar }}
                showConfirm={showConfirmHandler}
                isFormDirty={isFormDirty}
                scriptIdSelected={scriptIdSelected}
                setScriptIdSelected={setScriptIdSelected}
                execMenuOpened={execMenuOpened}
                setExecMenuOpened={setExecMenuOpened}
              />
              <Divider variant="middle" />
              <SidebarLatestActivity showConfirm={showConfirmHandler} checkSelectedResult={checkSelectedResult} />
            </div>
          </div>
        </div>
      </SwipeableDrawer>
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
  hasScriptsPermissions: state.access["ADMIN"]
});

const mapStateToDispatch = (dispatch: Dispatch<any>) => ({
  toggleSwipeableDrawer: () => dispatch(toggleSwipeableDrawer()),
  getSearchResults: (search: string) => dispatch(getDashboardSearch(search)),
  getCategories: () => dispatch(getDashboardCategories()),
  getScripts: () => dispatch(getOnDemandScripts()),
  getFavoriteScripts: () => dispatch(getFavoriteScripts()),
  getScriptsPermissions: () => dispatch(checkPermissions({ keyCode: "ADMIN" })),
  showConfirm: props => dispatch(showConfirm(props))
});

export default connect<any, any, any>(mapsStateToProps, mapStateToDispatch)(withStyles(styles)(SwipeableSidebar));
