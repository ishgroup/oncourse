/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import clsx from "clsx";
import {
 Typography, Grid, List, createStyles
} from "@material-ui/core";
import withStyles from "@material-ui/core/styles/withStyles";
import ButtonBase from "@material-ui/core/ButtonBase";
import { Category, CategoryItem, Script } from "@api/model";
import ExecuteScriptModal from "../../../../../../containers/automation/containers/scripts/components/ExecuteScriptModal";
import { State } from "../../../../../../reducers/state";
import { IAction } from "../../../../../actions/IshAction";
import { AnyArgFunction } from "../../../../../../model/common/CommonFunctions";
import { setDashboardFavorites, setFavoriteScripts } from "../../../../../../containers/dashboard/actions";
import FavoriteItem from "./FavoriteItem";
import FavoriteScriptItem from "./FavoriteScriptItem";

const styles = theme => createStyles({
  root: {
    padding: theme.spacing(1, 2),
    columnCount: 2
  },
  tabsRoot: {
    marginTop: "2px"
  },
  tabRoot: {},
  tabsFlex: {
    justifyContent: "space-around"
  },
  topBar: {
    display: "flex",
    padding: theme.spacing(2, 2, 0, 2),
    position: "relative",
    alignItems: "center"
  },
  quickEnrollItem: {
    lineHeight: "1.46429em"
  },
  favouriteSelect: {
    border: `1px solid ${theme.palette.text.disabled}`,
    padding: theme.spacing(0.25, 1),
    cursor: "pointer",
    "&:first-child": {
      borderRadius: theme.spacing(0.5, 0, 0, 0.5)
    },
    "&:last-child": {
      borderRadius: theme.spacing(0, 0.5, 0.5, 0)
    }
  },
  favouriteHighlightSelect: {
    border: `1px solid ${theme.palette.text.primary}`
  },
  favouriteSelectTypography: {
    color: theme.palette.text.secondary,
    fontSize: theme.spacing(1.5),
    fontWeight: 600
  },
  favouriteHighlightedSelectTypography: {
    color: theme.palette.text.primary
  }
});

interface Props {
  categories?: CategoryItem[];
  scripts?: Script[];
  favoriteScripts?: number[];
  hasScriptsPermissions?: boolean;
  getCategories?: AnyArgFunction;
  setUpdateIndicator?: AnyArgFunction;
  setDashboardFavorites?: (categories: Category[]) => void;
  setFavoriteScripts?: (ids: number[]) => void;
  classes?: any;
  showConfirm?: (onConfirm: any) => void;
  isFormDirty?: boolean;
  scriptIdSelected?: any;
  setScriptIdSelected?: any;
  execMenuOpened?: any;
  setExecMenuOpened?: any;
}

interface DashboardItem extends CategoryItem {
  name?: string;
  id?: number;
}

const sortItems = (a, b) => {
  if (a.category === "Checkout (Quick Enrol)") return -1;
  if (b.category === "Checkout (Quick Enrol)") return 1;
  const aName = (a.category || a.name).toUpperCase();
  const bName = (b.category || b.name).toUpperCase();

  return aName.localeCompare(bName);
};

const isCategoryType = item => !!item.category;

const Favorites: React.FC<Props> = props => {
  const {
    isFormDirty,
    showConfirm,
    classes,
    categories,
    scripts,
    favoriteScripts,
    setDashboardFavorites,
    setFavoriteScripts,
    hasScriptsPermissions,
    scriptIdSelected,
    setScriptIdSelected,
    execMenuOpened,
    setExecMenuOpened,
  } = props;

  const [favorites, setFavorites] = React.useState([]);
  const [localFavoriteScripts, setLocalFavoriteScripts] = React.useState([...favoriteScripts]);
  const [isEditing, setIsEditing] = React.useState(false);

  const groupedSortedItems: DashboardItem[] = useMemo(() => [...categories, ...((hasScriptsPermissions && scripts) || [])].sort(sortItems),
    [categories, scripts]);

  React.useEffect(() => {
    setFavorites(() => {
      const updated = [];
      categories.forEach(c => {
        if (c.favorite) {
          updated.push(c.category);
        }
      });
      return updated;
    });
  }, [categories, isFormDirty]);

  React.useEffect(() => {
    setLocalFavoriteScripts([...favoriteScripts]);
  }, [favoriteScripts, isFormDirty]);

  const toggleFavorite = React.useCallback(
    (category: string) => {
      const favoriteIndex = favorites.findIndex(i => i === category);

      setFavorites(prev => {
        const updated = [...prev];
        if (favoriteIndex === -1) {
          updated.push(category);
        } else {
          updated.splice(favoriteIndex, 1);
        }
        return updated;
      });
    },
    [favorites, localFavoriteScripts]
  );

  const toggleFavoriteScript = React.useCallback(
    (id: number) => {
      const favoriteScriptIndex = localFavoriteScripts.findIndex(i => i === id);

      setLocalFavoriteScripts(prev => {
        const updated = [...prev];
        if (favoriteScriptIndex === -1) {
          updated.push(id);
        } else {
          updated.splice(favoriteScriptIndex, 1);
        }
        return updated;
      });
    },
    [localFavoriteScripts]
  );

  const filterForExistingScripts = React.useCallback(
    localScriptIds => {
      const existingScriptIds = scripts.map(s => s.id);

      return localScriptIds.filter(id => existingScriptIds.includes(id));
    },
    [scripts, localFavoriteScripts]
  );

  const renderFavorites = React.useCallback(() => groupedSortedItems
    .filter(c => (favorites.includes(c.category)
      || c.category === "Checkout (Quick Enrol)")
      || localFavoriteScripts.includes(c.id))
    .map(v => (
      isCategoryType(v) ? (
        <FavoriteItem
          key={v.category}
          toggleFavorite={toggleFavorite}
          item={v}
          showConfirm={showConfirm}
        />
      )
      : (
        <FavoriteScriptItem
          key={v.id}
          toggleFavorite={toggleFavoriteScript}
          item={v}
          setScriptIdSelected={setScriptIdSelected}
          setExecMenuOpened={setExecMenuOpened}
        />
      )
  )), [groupedSortedItems, scripts, favoriteScripts, favorites, localFavoriteScripts]);

  const renderAll = React.useCallback(() => groupedSortedItems.map(v => (
    isCategoryType(v) ? (
      <FavoriteItem
        favorite={favorites.includes(v.category)}
        key={v.category}
        toggleFavorite={toggleFavorite}
        item={v}
        isEditing
        showConfirm={showConfirm}
      />
    )
    : (
      <FavoriteScriptItem
        favorite={localFavoriteScripts.includes(v.id)}
        key={v.id}
        toggleFavorite={toggleFavoriteScript}
        item={v}
        isEditing
        setScriptIdSelected={setScriptIdSelected}
        setExecMenuOpened={setExecMenuOpened}
      />
    )
  )), [groupedSortedItems, scripts, favorites, localFavoriteScripts]);

  const toggleEditing = React.useCallback(editing => {
    setIsEditing(editing);
  }, []);

  React.useEffect(() => {
    if (isEditing) {
      setDashboardFavorites(categories.filter(c => favorites.includes(c.category)).map(i => i.category));
      setFavoriteScripts(filterForExistingScripts([...localFavoriteScripts]));
    }
  }, [isEditing, categories, favorites, localFavoriteScripts]);

  return (
    <>
      <ExecuteScriptModal
        opened={execMenuOpened}
        onClose={() => {
          setExecMenuOpened(false);
          setScriptIdSelected(null);
        }}
        scriptId={scriptIdSelected}
      />
      <Grid container justify="space-between" alignItems="center">
        <Grid item xs={12} className={classes.topBar}>
          <Typography className="heading">{isEditing ? "" : "Navigation"}</Typography>
          <div className="flex-fill" />
          <div className="d-flex">
            <ButtonBase
              className={clsx(classes.favouriteSelect, { [classes.favouriteHighlightSelect]: !isEditing })}
              onClick={() => toggleEditing(false)}
            >
              <Typography
                className={clsx(classes.favouriteSelectTypography, { [classes.favouriteHighlightedSelectTypography]: !isEditing })}
              >
                Favourites
              </Typography>
            </ButtonBase>
            <ButtonBase
              className={clsx(classes.favouriteSelect, { [classes.favouriteHighlightSelect]: isEditing })}
              onClick={() => toggleEditing(true)}
            >
              <Typography
                className={clsx(classes.favouriteSelectTypography, { [classes.favouriteHighlightedSelectTypography]: isEditing })}
              >
                All
              </Typography>
            </ButtonBase>
          </div>
        </Grid>

        <Grid item xs={12}>
          <List classes={{ root: classes.root }}>{isEditing ? renderAll() : renderFavorites()}</List>
        </Grid>
      </Grid>
    </>
  );
};

const mapStateToProps = (state: State) => ({
  categories: state.dashboard.categories,
  scripts: state.dashboard.scripts,
  favoriteScripts: state.dashboard.favoriteScripts,
  hasScriptsPermissions: state.access["ADMIN"]
});

const mapDispatchToProps = (dispatch: Dispatch<IAction<any>>) => ({
  setDashboardFavorites: (categories: Category[]) => dispatch(setDashboardFavorites(categories)),
  setFavoriteScripts: (scripts: number[]) => dispatch((setFavoriteScripts(scripts)))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(Favorites));
