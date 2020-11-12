/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import clsx from "clsx";
import withStyles from "@material-ui/core/styles/withStyles";
import Typography from "@material-ui/core/Typography";
import ListItem from "@material-ui/core/ListItem";
import Favorite from "@material-ui/icons/Favorite";
import FavoriteBorder from "@material-ui/icons/FavoriteBorder";
import { CategoryItem } from "@api/model";
import { AnyArgFunction } from "../../../../../../model/common/CommonFunctions";
import { openInternalLink } from "../../../../../utils/links";
import itemStyles from "./itemStyles";

interface Props {
  item: CategoryItem;
  toggleFavorite: AnyArgFunction;
  isEditing?: boolean;
  favorite?: boolean;
  classes?: any;
  showConfirm?: (onConfirm: any) => void;
}

const FavoriteItem = (props: Props) => {
  const {
    showConfirm, classes, item, isEditing, toggleFavorite, favorite
  } = props;

  const openLink = () => showConfirm(() => openInternalLink(item.url));

  const handleIconClick = () => toggleFavorite(item.category);

  const isQuickEnroll = item.category === "Checkout (Quick Enrol)";

  return (
    <ListItem
      dense
      disableGutters
      className={clsx(classes.listItem, {
        [classes.listItemEditing]: isEditing,
        "d-none": isEditing && isQuickEnroll
      })}
    >
      {isEditing ? (
        favorite ? (
          <Favorite onClick={handleIconClick} className={clsx(classes.listItemIcon, classes.listItemIconActive)} />
        ) : (
          <FavoriteBorder onClick={handleIconClick} className={clsx(classes.listItemIcon, "invisible")} />
        )
      ) : null}

      <Typography
        variant={!isEditing && isQuickEnroll ? "subtitle2" : "body2"}
        onClick={openLink}
        className={clsx("linkDecoration", classes.listItemContent, { [classes.quickEnrollItem]: isQuickEnroll })}
      >
        {item.category}
      </Typography>
    </ListItem>
  );
};

export default withStyles(itemStyles)(FavoriteItem);
