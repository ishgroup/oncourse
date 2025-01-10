/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Collapse, Typography } from '@mui/material';
import List from '@mui/material/List';
import clsx from 'clsx';
import { openInternalLink } from 'ish-ui';
import React from 'react';
import { withStyles } from 'tss-react/mui';
import navigation from '../../../../navigation/data/navigation.json';
import { getResultId } from '../../utils';
import ListLinkItem from './ListLinkItem';

const styles = theme =>
  ({
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
      entityDisplayName, userSearch
    } = this.props;

    const category = navigation.features.find(c => c.title === entityDisplayName);

    if (category) {
      const url = category.link.indexOf("?") !== -1 ? category.link.slice(0, category.link.indexOf("?")) : category.link;

      openInternalLink(
        url + (userSearch ? `?search=~"${userSearch}"` : "")
      );
    }
  };

  openLink = id => {
    const {
      entityDisplayName, entity, setSelected
    } = this.props;

    if (entity === "Contact" && typeof setSelected === "function") {
      setSelected(id);
      return;
    }

    const category = navigation.features.find(c => c.title === entityDisplayName);

    if (category) {
      const url = category.link.indexOf("?") !== -1 ? category.link.slice(0, category.link.indexOf("?")) : category.link;

      openInternalLink(
        !id || !Number.isNaN(Number(id)) ? url + (id ? `/${id}` : "") : id
      );
    }
  };

  render() {
    const {
      classes, entityDisplayName, items, showFirst, checkSelectedResult, entity
    } = this.props;
    const {collapsed} = this.state;

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
              <div className="flex-fill"/>
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
              <Collapse in={collapsed} mountOnEnter unmountOnExit>
                {lastItems.map((v, i) => (
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

export default withStyles(ListLinksGroup, styles);
