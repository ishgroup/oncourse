/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import clsx from "clsx";
import withStyles from "@mui/styles/withStyles";
import Typography from "@mui/material/Typography";
import ListItem from "@mui/material/ListItem";
import { openInternalLink } from "../../../utils/links";
import itemStyles from "./itemStyles";
import { DashboardItem } from "../../../../model/dashboard";
import { getPrivisioningLink } from "../../../../routes/routesMapping";

interface Props {
  item: DashboardItem;
  isEditing?: boolean;
  classes?: any;
}

const FavoriteItem = (props: Props) => {
  const {
    classes, item, isEditing
  } = props;

  const openLink = () => openInternalLink(getPrivisioningLink(item.url));

  const isQuickEnroll = item.category === "quickEnrol";

  return (
    <ListItem
      dense
      disableGutters
      className={clsx(classes.listItem, {
        [classes.listItemEditing]: isEditing,
        "d-none": isEditing && isQuickEnroll
      })}
    >
      <Typography
        variant={!isEditing && isQuickEnroll ? "subtitle2" : "body2"}
        onClick={openLink}
        className={clsx("linkDecoration", classes.listItemContent, { [classes.quickEnrollItem]: isQuickEnroll })}
      >
        {item.name}
      </Typography>
    </ListItem>
  );
};

export default withStyles(itemStyles)(FavoriteItem);