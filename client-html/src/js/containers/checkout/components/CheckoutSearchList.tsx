/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";

interface Props {
  Title?: any;
  type: string;
  items: any[];
  primaryText: (item: any) => any;
  secondaryText?: (item: any) => any;
  onClick?: (item: any, type: string) => void;
  onDoubleClick?: (item: any, type: string, singleClick: boolean) => void;
  selected: (item: any, type: string) => boolean;
}

const CheckoutSearchList = React.memo<Props>(props => {
  const {
    Title, type, items, primaryText, secondaryText, selected, onClick, onDoubleClick
  } = props;

  return (
    items.length > 0 && (
      <>
        {Title}
        <List disablePadding component="nav">
          {items.map((item, i) => (
            <React.Fragment key={i}>
              <ListItem
                button
                disableGutters
                dense
                className="justify-content-space-between p-0-5"
                alignItems="flex-start"
                onClick={onClick && (e => onClick(item, type))}
                onDoubleClick={onDoubleClick && (e => onDoubleClick(item, type, false))}
                selected={selected(item, type)}
              >
                <ListItemText
                  classes={{
                    multiline: "mt-0 mb-0"
                  }}
                  primary={primaryText(item)}
                  secondary={secondaryText && secondaryText(item)}
                  disableTypography
                />
              </ListItem>
            </React.Fragment>
          ))}
        </List>
      </>
    )
  );
});

export default CheckoutSearchList;
