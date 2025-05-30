/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Script } from '@api/model';
import Close from '@mui/icons-material/Close';
import { Grid, IconButton, List, Typography } from '@mui/material';
import Divider from '@mui/material/Divider';
import $t from '@t';
import clsx from 'clsx';
import { useHoverShowStyles } from 'ish-ui';
import React, { useMemo } from 'react';
import { connect } from 'react-redux';
import { withStyles } from 'tss-react/mui';
import { DashboardItem } from '../../../../model/dashboard';
import { State } from '../../../../reducers/state';
import FavoriteItem from './FavoriteItem';
import FavoriteScriptItem from './FavoriteScriptItem';

const styles = theme => ({
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
  updateFavorites: (key: string, type: "category" | "automation") => void;
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
    updateFavorites
  } = props;

  const { classes: hoverClasses } = useHoverShowStyles();

  const renderFavorites = useMemo(() => groupedSortedItems
    .filter(c => (favorites.includes(c.category)
        || c.category === "quickEnrol")
        || favoriteScripts.includes(String(c.id)))
    .map(v => (
      isCategoryType(v) ? (
        <div className={clsx("centeredFlex", hoverClasses.container)} key={v.category}>
          <FavoriteItem
            item={v}
          />
          {v.category !== "quickEnrol" &&
            <IconButton
              onMouseDown={e => e.stopPropagation()}
              onClick={() => updateFavorites(v.category, "category")}
              className={clsx("p-0-5", hoverClasses.target)}
              size="small"
            >
              <Close fontSize="inherit" color="primary" />
            </IconButton>
          }
        </div>
      )
      : (
          <div className={clsx("centeredFlex", hoverClasses.container)} key={v.id}>
            <FavoriteScriptItem
              item={v}
              setScriptIdSelected={setScriptIdSelected}
              setExecMenuOpened={setExecMenuOpened}
            />
            <IconButton
              onMouseDown={e => e.stopPropagation()}
              onClick={() => updateFavorites(String(v.id), "automation")}
              className={clsx("p-0-5", hoverClasses.target)}
              size="small"
            >
              <Close fontSize="inherit" color="primary" />
            </IconButton>
          </div>
      )
  )), [groupedSortedItems, scripts, favoriteScripts, favorites, hoverClasses]);

  return (
    <Grid container alignItems="center">
      <Grid item xs={12} className={classes.topBar}>
        <Typography className="heading">{$t('favourites')}</Typography>
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

export default connect<any, any, any>(mapStateToProps)(withStyles(Favorites, styles));
