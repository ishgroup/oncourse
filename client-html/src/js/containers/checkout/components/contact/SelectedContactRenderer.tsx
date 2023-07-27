/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { memo, useEffect, useState } from "react";
import clsx from "clsx";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import { alpha } from '@mui/material/styles';
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import Delete from "@mui/icons-material/Delete";
import { AppTheme } from  "ish-ui";
import CheckoutAlertTextMessage from "../CheckoutAlertTextMessage";
import { getContactFullName } from "../../../entities/contacts/utils";

const styles = (theme: AppTheme) =>
  createStyles({
    root: {
      "&:hover $deleteIcon": {
        visibility: "visible"
      }
    },
    deleteIcon: {
      color: alpha(theme.palette.text.primary, 0.2),
      padding: 5,
      fontSize: 16,
      visibility: "hidden"
    }
  });

const Item = ({
 classes, selected, item, index, openRow, onDelete, disabledDeleteId
}) => {
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setTimeout(() => {
      setMounted(true);
    }, 500);
  }, []);

  return (
    <div
      className={clsx(
      "relative",
      classes.root,
      selected && selected.id === item.id && "selectedItemArrow",
      `${mounted ? "" : "bounceInRight animated"}`
    )}
    >
      <div className="centeredFlex">
        <Typography
          variant="body2"
          className={clsx("flex-fill text-truncate cursor-pointer", { "fontWeight600": selected && selected.id === item.id })}
          noWrap
          onClick={() => openRow(item)}
        >
          {getContactFullName(item)}
        </Typography>
        {
          disabledDeleteId !== item.id && (
            <IconButton
              className={classes.deleteIcon}
              color="secondary"
              onClick={e => onDelete(e, index, item)}
            >
              <Delete fontSize="inherit" color="inherit" />
            </IconButton>
          )
        }
      </div>
      {mounted && item.message && (
      <CheckoutAlertTextMessage message={item.message} />
    )}
    </div>
  );
};

const SelectedContactRenderer = memo<any>(({ items, ...rest }) => (
  <div className="relative">
    {items && items.map((item, i) => <Item key={i} item={item} index={i} {...rest as any} />)}
  </div>
));

export default withStyles(styles)(SelectedContactRenderer);
