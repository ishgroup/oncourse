/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import clsx from "clsx";
import withStyles from "@mui/styles/withStyles";
import createStyles from "@mui/styles/createStyles";
import List from "@mui/material/List";
import Collapse from "@mui/material/Collapse";
import Typography from "@mui/material/Typography";
import { openInternalLink } from "../../../../../utils/links";
import ListLinkItem from "./ListLinkItem";
import { getResultId } from "../../utils";
import navigation from "../../../../navigation/navigation.json";

const styles = theme =>
  createStyles({
    showMoreText: {
      color: theme.palette.primary.main,
      width: "min-content",
      margin: theme.spacing(2.5, 0),
      whiteSpace: "nowrap",
      maxWidth: 140,
      fontSize: theme.spacing(1.375),
      fontWeight: 600,
      padding: theme.spacing(0, 0.75),
    },
    heading: {
      margin: theme.spacing(4, 0, 2),
    },
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
      showConfirm, entityDisplayName, userSearch
    } = this.props;

    const category = navigation.features.find(c => c.title === entityDisplayName);

    if (category) {
      const url = category.link.indexOf("?") !== -1 ? category.link.slice(0, category.link.indexOf("?")) : category.link;

      showConfirm(() => openInternalLink(
        url + (userSearch ? `?search=~"${userSearch}"` : "")
      ));
    }
  };

  openLink = id => {
    const { showConfirm, entityDisplayName } = this.props;
    const category = navigation.features.find(c => c.title === entityDisplayName);

    if (category) {
      const url = category.link.indexOf("?") !== -1 ? category.link.slice(0, category.link.indexOf("?")) : category.link;

      showConfirm(() => openInternalLink(
        !id || !Number.isNaN(Number(id)) ? url + (id ? `/${id}` : "") : id
      ));
    }
  };

  getListItems = items => {
    const {
      entity, entityDisplayName, checkSelectedResult
    } = this.props;
    return items.map((v, i) => (
      <ListLinkItem
        key={i}
        item={v}
        openLink={this.openLink}
        entity={entityDisplayName}
        selected={checkSelectedResult(entity, "id", v.id)}
        id={getResultId(i, `${entity}-${v.id}`)}
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
          <Typography
            onClick={() => this.openEntity()}
            className={clsx("heading cursor-pointer mr-1", classes.heading)}
          >
            {entityDisplayName}
          </Typography>
          {showFirst && Boolean(lastItems.length) && (
            <>
              <div className="flex-fill" />
              <Typography
                onClick={this.toggleCollapsed}
                className={clsx("cursor-pointer", classes.showMoreText)}
              >
                {`View ${lastItems.length} ${collapsed ? "less" : "more"}`}
              </Typography>
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

export default withStyles(styles)(ListLinksGroup);
