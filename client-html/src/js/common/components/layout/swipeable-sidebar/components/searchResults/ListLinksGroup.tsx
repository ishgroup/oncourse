/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { connect } from "react-redux";
import clsx from "clsx";
import withStyles from "@material-ui/core/styles/withStyles";
import createStyles from "@material-ui/core/styles/createStyles";
import Chip from "@material-ui/core/Chip";
import List from "@material-ui/core/List";
import Collapse from "@material-ui/core/Collapse";
import { openInternalLink } from "../../../../../utils/links";
import ListLinkItem from "./ListLinkItem";
import { State } from "../../../../../../reducers/state";
import { getResultId } from "../../utils";

const styles = theme =>
  createStyles({
    chip: {
      color: theme.palette.text.primary,
      height: "18px",
      width: "min-content",
      marginTop: "4px",
      justifySelf: "end",
      whiteSpace: "nowrap",
      maxWidth: 140
    },
    chipWrapper: {
      position: "absolute",
      left: 0
    },
    chipOffset: {
      position: "relative",
      left: "-100%"
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

  render() {
    const {
      entity, classes, entityDisplayName, items, showFirst, withOffset, checkSelectedResult
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
        <div className={clsx("d-grid", { [classes.chipWrapper]: withOffset })}>
          <Chip
            onClick={() => this.openEntity()}
            label={entityDisplayName}
            className={clsx("mr-1", classes.chip, { [classes.chipOffset]: withOffset })}
          />
          {showFirst && Boolean(lastItems.length) && (
            <Chip
              onClick={this.toggleCollapsed}
              className={clsx("mr-1", classes.chip, classes.chipOffset)}
              label={`${lastItems.length} ${collapsed ? "less" : "more"}`}
              classes={{ clickable: classes.collapseChip, label: classes.collapseChipLabel }}
            />
          )}
        </div>

        <List disablePadding>
          {showFirst ? (
            <>
              <Collapse in={collapsed}>
                {items.map((v, i) => (
                  <ListLinkItem
                    key={i}
                    item={v}
                    openLink={this.openLink}
                    entity={entityDisplayName}
                    selected={checkSelectedResult(entity, "id", v.id)}
                    id={getResultId(i, `${entity}-${v.id}`)}
                  />
                ))}
              </Collapse>
              <Collapse in={!collapsed}>
                {firstItems.map((v, i) => (
                  <ListLinkItem
                    key={i}
                    item={v}
                    openLink={this.openLink}
                    entity={entityDisplayName}
                    selected={checkSelectedResult(entity, "id", v.id)}
                    id={getResultId(i, `${entity}-${v.id}`)}
                  />
                ))}
              </Collapse>
            </>
          ) : (
            items.map((v, i) => (
              <ListLinkItem
                key={i}
                item={v}
                openLink={this.openLink}
                entity={entityDisplayName}
                selected={checkSelectedResult(entity, "id", v.id)}
                id={getResultId(i, `${entity}-${v.id}`)}
              />
            ))
          )}
        </List>
      </>
    );
  }
}

const mapStateToProps = (state: State) => ({
  categories: state.dashboard.categories
});

export default connect<any, any, any>(mapStateToProps, null)(withStyles(styles)(ListLinksGroup));
