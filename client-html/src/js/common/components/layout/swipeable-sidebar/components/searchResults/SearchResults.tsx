/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { connect } from "react-redux";
import withStyles from "@mui/styles/withStyles";
import createStyles from "@mui/styles/createStyles";
import List from "@mui/material/List";
import CircularProgress from "@mui/material/CircularProgress";
import { getHighlightedPartLabel } from "../../../../../utils/formatting";
import { getEntityDisplayName } from "../../../../../utils/getEntityDisplayName";
import { State } from "../../../../../../reducers/state";
import ListLinkItem from "./ListLinkItem";
import ListLinksGroup from "./ListLinksGroup";
import { getResultId } from "../../utils";
import navigation from "../../../../navigation/data/navigation.json";
import FavoriteBorderIcon from "@mui/icons-material/FavoriteBorder";
import { IconButton } from "@mui/material";
import { useHoverShowStyles } from  "ish-ui";
import clsx from "clsx";
import IconButton from "@mui/material/IconButton";

const styles = theme => createStyles({
  root: {
    padding: `${theme.spacing(2)} ${theme.spacing(2)} 228px ${theme.spacing(2)}`
  },
  divider: {
    margin: theme.spacing(2, 0, 1.25),
  }
});

const SearchResults = props => {
  const {
    classes,
    userSearch,
    scripts,
    hasScriptsPermissions,
    updating,
    searchResults,
    checkSelectedResult,
    setExecMenuOpened,
    setScriptIdSelected,
    setSelected,
    favorites,
    favoriteScripts,
    updateFavorites
  } = props;

  const hoverClasses = useHoverShowStyles();

  return (
    <List disablePadding className={classes.root}>
      {userSearch
        && navigation.features
          .filter(c => c.title.toLowerCase().includes(userSearch.toLowerCase()))
          .map((c, i) => {

            const isFavorite = favorites.includes(c.key);

            return <div key={i} className={clsx(hoverClasses.container, "centeredFlex")}>
              <ListLinkItem
                url={c.link}
                selected={checkSelectedResult("category", "url", c.link)}
                item={{
                  name: getHighlightedPartLabel(c.title, userSearch)
                }}
                id={getResultId(i, c.title)}
              />
              <IconButton
                onMouseDown={e => e.stopPropagation()}
                onClick={() => updateFavorites(c.key, "category")}
                className={clsx("p-0-5 lightGrayColor", !isFavorite && hoverClasses.target)}
                size="small"
              >
                <FavoriteBorderIcon fontSize="inherit" color={isFavorite ? "primary" : "inherit"}/>
              </IconButton>
            </div>;
          })}
      {userSearch
        && hasScriptsPermissions
        && scripts
          .filter(s => s.name.toLowerCase().includes(userSearch.toLowerCase()))
          .map((s, i) => {

            const isFavorite = favoriteScripts.includes(String(s.id));

            return <div key={i} className={clsx(hoverClasses.container, "centeredFlex")}>
              <ListLinkItem
                key={i}
                openLink={id => {
                  setScriptIdSelected(id);
                  setExecMenuOpened(true);
                }}
                item={{
                  name: getHighlightedPartLabel(s.name, userSearch),
                  id: s.id,
                  type: "script"
                }}
                id={getResultId(i, s.name)}
              />
              <IconButton
                onMouseDown={e => e.stopPropagation()}
                onClick={() => updateFavorites(String(s.id), "automation")}
                className={clsx("p-0-5 lightGrayColor", !isFavorite && hoverClasses.target)}
                size="small"
              >
                <FavoriteBorderIcon fontSize="inherit" color={isFavorite ? "primary" : "inherit"}/>
              </IconButton>
            </div>;
          })}
      {updating && <CircularProgress size={32} thickness={5}/>}
      {!updating
        && searchResults
        && searchResults.map((r, index) => (
          <div className="relative" key={index}>
            <ListLinksGroup
              showFirst={3}
              entity={r.entity}
              entityDisplayName={getEntityDisplayName(r.entity)}
              checkSelectedResult={checkSelectedResult}
              items={r.items.map(item => {
                const name = getHighlightedPartLabel(item.name, userSearch);
                return {...item, name};
              })}
              userSearch={userSearch}
              setSelected={setSelected}
            />
          </div>
        ))}
    </List>
  );
};

const mapStateToProps = (state: State) => ({
  scripts: state.dashboard.scripts,
  hasScriptsPermissions: state.access["ADMIN"],
  searchResults: state.dashboard.searchResults.results,
  updating: state.dashboard.searchResults.updating
});

export default connect(mapStateToProps)(withStyles(styles)(SearchResults));