/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import { connect } from "react-redux";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import List from "@mui/material/List";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import Divider from "@mui/material/Divider";
import { Script } from "@api/model";
import { State } from "../../../../reducers/state";
import { DashboardItem } from "../../../../model/dashboard";
import FavoriteItem from "./FavoriteItem";
import FavoriteScriptItem from "./FavoriteScriptItem";

const styles = theme => createStyles({
  root: {
    padding: theme.spacing(2),
    columnCount: 1
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
    padding: theme.spacing(3, 2, 0, 2),
    position: "relative",
    alignItems: "center"
  },
  quickEnrollItem: {
    lineHeight: "1.46429em"
  },
  favouriteSelect: {
    padding: theme.spacing(0.5, 0),
    margin: theme.spacing(0, 1),
    cursor: "pointer",
    "&::after": {
      content: `" "`,
      position: "absolute",
      left: 0,
      right: 0,
      bottom: 0,
      borderBottom: `2px solid transparent`,
      transform: "scaleX(0)",
      transition: theme.transitions.create("transform", {
        duration: theme.transitions.duration.shorter,
        easing: theme.transitions.easing.easeInOut
      })
    }
  },
  favouriteSelectTypography: {
    color: theme.palette.text.secondary,
    fontSize: theme.spacing(1.625),
    fontWeight: 600
  },
  favouriteHighlightedSelectTypography: {
    color: theme.palette.text.primary
  }
});

interface Props {
  scripts: Script[];
  favorites: string[];
  favoriteScripts: string[];
  groupedSortedItems: DashboardItem[];
  classes?: any;
  setScriptIdSelected?: any;
  setExecMenuOpened?: any;
}

const isCategoryType = item => !!item.category;

const Favorites: React.FC<Props> = props => {
  const {
    classes,
    scripts,
    favorites,
    favoriteScripts,
    groupedSortedItems,
    setScriptIdSelected,
    setExecMenuOpened,
  } = props;

  const renderFavorites = useMemo(() => groupedSortedItems
    .filter(c => (favorites.includes(c.category)
        || c.category === "quickEnrol")
        || favoriteScripts.includes(String(c.id)))
    .map(v => (
      isCategoryType(v) ? (
        <FavoriteItem
          key={v.category}
          item={v}
        />
      )
      : (
        <FavoriteScriptItem
          key={v.id}
          item={v}
          setScriptIdSelected={setScriptIdSelected}
          setExecMenuOpened={setExecMenuOpened}
        />
      )
  )), [groupedSortedItems, scripts, favoriteScripts, favorites]);

  return (
    <Grid container alignItems="center">
      <Grid item xs={12} className={classes.topBar}>
        <Typography className="heading">Favourites</Typography>
      </Grid>

      <Grid item xs={12}>
        <List classes={{ root: classes.root }}>{renderFavorites}</List>
      </Grid>

      <Grid item xs={12} className="mt-1 mb-2">
        <Divider variant="middle" />
      </Grid>
    </Grid>
  );
};

const mapStateToProps = (state: State) => ({
  scripts: state.dashboard.scripts,
  hasScriptsPermissions: state.access["ADMIN"]
});

export default connect<any, any, any>(mapStateToProps)(withStyles(styles)(Favorites));
