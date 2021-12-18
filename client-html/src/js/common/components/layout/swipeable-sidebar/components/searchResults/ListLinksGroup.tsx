/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { connect } from "react-redux";
import clsx from "clsx";
import withStyles from "@mui/styles/withStyles";
import createStyles from "@mui/styles/createStyles";
import Chip from "@mui/material/Chip";
import List from "@mui/material/List";
import Collapse from "@mui/material/Collapse";
import { openInternalLink } from "../../../../../utils/links";
import ListLinkItem from "./ListLinkItem";
import { State } from "../../../../../../reducers/state";
import { getResultId } from "../../utils";

const styles = theme =>
  createStyles({
    chip: {
      color: theme.palette.text.primary,
      height: 30,
      width: "min-content",
      marginTop: 12,
      marginBottom: 12,
      whiteSpace: "nowrap",
      maxWidth: 140,
      fontSize: theme.spacing(2),
      fontWeight: 600,
    },
    collapseChip: {
      backgroundColor: "inherit",
      "&:hover": {
        backgroundColor: theme.palette.grey[200]
      },
      "&:active": {
        backgroundColor: "inherit"
      },
      "&:focus": {
        backgroundColor: "inherit"
      }
    },
    collapseChipLabel: {
      color: theme.palette.primary.main,
      fontWeight: "bolder"
    }
  });

class ListLinksGroup extends React.PureComponent<any, any> {
  state = {
    collapsed: false
  };

  toggleCollapsed = () => {
    this.setState(prev => ({
      collapsed: !prev.collapsed
    }));
  };

  openEntity = () => {
    const {
      showConfirm, categories, entityDisplayName, userSearch
    } = this.props;

    const category = categories.find(c => c.category === entityDisplayName);

    if (category) {
      const url = category.url.indexOf("?") !== -1 ? category.url.slice(0, category.url.indexOf("?")) : category.url;

      showConfirm(() => openInternalLink(
        url + (userSearch ? `?search=~"${userSearch}"` : "")
      ));
    }
  };

  openLink = id => {
    const { showConfirm, entityDisplayName, categories } = this.props;
    const category = categories.find(c => c.category === entityDisplayName);

    if (category) {
      const url = category.url.indexOf("?") !== -1 ? category.url.slice(0, category.url.indexOf("?")) : category.url;

      showConfirm(() => openInternalLink(
        !id || !isNaN(Number(id)) ? url + (id ? `/${id}` : "") : id
      ));
    }
  };

  getListItems = items => {
    const {
      entity, entityDisplayName, checkSelectedResult, shortenTime
    } = this.props;
    return items.map((v, i) => (
      <ListLinkItem
        key={i}
        item={v}
        openLink={this.openLink}
        entity={entityDisplayName}
        selected={checkSelectedResult(entity, "id", v.id)}
        id={getResultId(i, `${entity}-${v.id}`)}
        shortenTime={shortenTime}
      />
    ));
  };

  render() {
    const {
      classes, entityDisplayName, items, showFirst
    } = this.props;
    const { collapsed } = this.state;

    let firstItems;
    let lastItems;

    if (showFirst) {
      firstItems = items.slice(0, showFirst);
      lastItems = items.slice(showFirst);
    }
    return (
      <>
        <div className="d-flex align-items-center">
          <Chip
            onClick={() => this.openEntity()}
            label={entityDisplayName}
            className={clsx("mr-1", classes.chip)}
          />
          {showFirst && Boolean(lastItems.length) && (
            <>
              <div className="flex-fill" />
              <Chip
                onClick={this.toggleCollapsed}
                className={classes.chip}
                label={`${lastItems.length} ${collapsed ? "less" : "more"}`}
                classes={{ clickable: classes.collapseChip, label: classes.collapseChipLabel }}
              />
            </>
          )}
        </div>

        <List disablePadding>
          {showFirst ? (
            <>
              <Collapse in={collapsed}>
                {this.getListItems(items)}
              </Collapse>
              <Collapse in={!collapsed}>
                {this.getListItems(firstItems)}
              </Collapse>
            </>
          ) : this.getListItems(items)}
        </List>
      </>
    );
  }
}

const mapStateToProps = (state: State) => ({
  categories: state.dashboard.categories
});

export default connect<any, any, any>(mapStateToProps, null)(withStyles(styles)(ListLinksGroup));
