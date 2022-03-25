/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
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
  showConfirm?: (onConfirm: any) => void;
}

const FavoriteItem = (props: Props) => {
  const {
    showConfirm, classes, item, isEditing
  } = props;

  const openLink = () => showConfirm(() => openInternalLink(getPrivisioningLink(item.url)));

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
