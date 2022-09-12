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
import navigation from "../../../../navigation/navigation.json";

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
    setSelected
  } = props;

  return (
    <List disablePadding className={classes.root}>
      {userSearch
        && navigation.features
          .filter(c => c.title.toLowerCase().includes(userSearch.toLowerCase()))
          .map((c, i) => (
            <ListLinkItem
              key={i}
              url={c.link}
              selected={checkSelectedResult("category", "url", c.link)}
              item={{
                name: getHighlightedPartLabel(c.title, userSearch)
              }}
              id={getResultId(i, c.title)}
            />
          ))}
      {userSearch
        && hasScriptsPermissions
        && scripts
          .filter(s => s.name.toLowerCase().includes(userSearch.toLowerCase()))
          .map((s, i) => (
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
        ))}
      {updating && <CircularProgress size={32} thickness={5} />}
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
                return { ...item, name };
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
