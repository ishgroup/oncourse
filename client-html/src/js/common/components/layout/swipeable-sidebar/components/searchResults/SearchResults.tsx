/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import withStyles from "@mui/styles/withStyles";
import createStyles from "@mui/styles/createStyles";
import List from "@mui/material/List";
import Divider from "@mui/material/Divider";
import CircularProgress from "@mui/material/CircularProgress";
import { getHighlightedPartLabel } from "../../../../../utils/formatting";
import { getEntityDisplayName } from "../../../../../utils/getEntityDisplayName";
import { State } from "../../../../../../reducers/state";
import { IAction } from "../../../../../actions/IshAction";
import { getDashboardCategories } from "../../../../../../containers/dashboard/actions";
import ListLinkItem from "./ListLinkItem";
import ListLinksGroup from "./ListLinksGroup";
import { getResultId } from "../../utils";

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
    showConfirm,
    classes,
    userSearch,
    categories,
    scripts,
    hasScriptsPermissions,
    updating,
    searchResults,
    checkSelectedResult,
    setExecMenuOpened,
    setScriptIdSelected
  } = props;

  return (
    <List disablePadding className={classes.root}>
      {userSearch
        && categories
          .filter(c => c.category.toLowerCase().includes(userSearch.toLowerCase()))
          .map((c, i) => (
            <ListLinkItem
              key={i}
              url={c.url}
              selected={checkSelectedResult("category", "url", c.url)}
              item={{
                name: getHighlightedPartLabel(c.category, userSearch)
              }}
              id={getResultId(i, c.category)}
              showConfirm={showConfirm}
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
              showConfirm={showConfirm}
            />
            {((searchResults.length - 1) > index) && <Divider className={classes.divider} />}
          </div>
        ))}
    </List>
  );
};

const mapStateToProps = (state: State) => ({
  categories: state.dashboard.categories,
  scripts: state.dashboard.scripts,
  hasScriptsPermissions: state.access["ADMIN"],
  searchResults: state.dashboard.searchResults.results,
  updating: state.dashboard.searchResults.updating
});

const mapDispatchToProps = (dispatch: Dispatch<IAction<any>>) => ({
  getCategories: () => dispatch(getDashboardCategories())
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(SearchResults));
