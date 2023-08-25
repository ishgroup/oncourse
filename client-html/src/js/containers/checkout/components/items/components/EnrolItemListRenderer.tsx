/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Chip from "@mui/material/Chip";
import CircularProgress from "@mui/material/CircularProgress";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import clsx from "clsx";
import { AppTheme } from "ish-ui";
import React, { useCallback, useMemo, useState } from "react";
import CheckoutSearchList from "../../CheckoutSearchList";

const styles = (theme: AppTheme) =>
  createStyles({
    listRoot: {
      width: "85%"
    },
    showAllChip: {
      height: "25px",
      marginLeft: 100
    },
    showMoreChip: {
      height: "18px"
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
    headingChip: {
      height: "25px"
    }
  });

const EnrolItemListRenderer = React.memo<any>(props => {
  const {
    classes,
    title,
    items,
    type,
    primaryText,
    secondaryText,
    onChangeHandler,
    disabledHandler,
    showAllItems,
    selectedItems,
    showFirst,
    searchString,
    itemsLoading
  } = props;

  const [showMore, setShowMore] = useState(false);

  const toggleCollapsed = useCallback(() => {
    setShowMore(!showMore);
  }, [showMore]);

  const filteredItem = useMemo(() => items.filter(i => !disabledHandler(i, type)), [items, selectedItems, type]);

  const itemShown = useMemo(() => {
    let totalItem;
    if (showMore) {
      totalItem = filteredItem;
    } else {
      totalItem = showFirst ? filteredItem.slice(0, showFirst) : filteredItem;
    }
    return totalItem;
  }, [showMore, filteredItem, showFirst]);

  const lastItems = useMemo(() => {
    let totalLastItem = [];
    if (showFirst) {
      totalLastItem = filteredItem.slice(showFirst);
    }
    return totalLastItem;
  }, [filteredItem, showFirst]);

  const showCategory = useMemo(() => filteredItem.length > 0, [filteredItem, searchString]);

  return (
    showCategory && (
      <div className="pt-1 pb-1">
        <div className="d-flex">
          <div className="flex-fill text-end pr-1 pb-0-5">
            <Chip clickable={false} className={clsx("textPrimaryColor", classes.headingChip)} label={title} />
            {!itemsLoading && lastItems.length > 0 && (
              <>
                <br />
                <Chip
                  onClick={toggleCollapsed}
                  className={clsx("textPrimaryColor mt-0-5", classes.showMoreChip)}
                  label={`${lastItems.length} ${showMore ? "less" : "more"}`}
                  classes={{ clickable: classes.collapseChip, label: "primaryColor text-bolder" }}
                />
              </>
            )}
          </div>
          {filteredItem.length > 0 ? (
            <div className={classes.listRoot}>
              {!itemsLoading && (
                <CheckoutSearchList
                  items={itemShown}
                  type={type}
                  primaryText={primaryText}
                  secondaryText={secondaryText}
                  onClick={onChangeHandler}
                  selected={disabledHandler}
                />
              )}
              <CircularProgress classes={{ root: clsx(!itemsLoading && "d-none") }} size={32} thickness={5} />
            </div>
          ) : (
            showAllItems && (
              <Chip
                onClick={() => showAllItems(type)}
                className={clsx("textPrimaryColor", classes.showAllChip)}
                label="Show all"
              />
            )
          )}
        </div>
      </div>
    )
  );
});

export default withStyles(styles)(EnrolItemListRenderer);
