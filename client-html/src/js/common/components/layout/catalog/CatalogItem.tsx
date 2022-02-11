/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { ReactNode } from "react";
import {
 ListItem, ListItemText, Divider, Typography 
} from "@mui/material";
import clsx from "clsx";
import Chip from "@mui/material/Chip";
import { CatalogItemType } from "../../../../model/common/Catalog";
import { makeAppStyles } from "../../../styles/makeStyles";
import { NumberArgFunction } from "../../../../model/common/CommonFunctions";
import { alpha } from "@mui/material/styles";

const useStyles = makeAppStyles(theme => ({
  primaryText: {
    fontSize: "15px",
    fontWeight: 600
  },
  secondaryText: {
    fontSize: "13px",
  },
  dot: {
    height: "10px",
    width: "10px",
    borderRadius: "50%",
    marginRight: theme.spacing(1),
    backgroundColor: theme.palette.text.primary
  },
  chip: {
    textTransform: "uppercase",
    fontWeight: 600,
    fontSize: "12px",
    marginLeft: theme.spacing(1),
    "&$chipPrimary": {
      color: theme.palette.primary.main,
      backgroundColor: alpha(theme.palette.primary.main, 0.15)
    },
    "&$chipSuccess": {
      color: theme.palette.success.light,
      backgroundColor: alpha(theme.palette.success.light, 0.15)
    }
  },
  chipPrimary: {},
  chipSuccess: {},
}));

type Props = Partial<CatalogItemType> & {
  dotColor?: string;
  onOpen?: NumberArgFunction;
  showAdded?: boolean;
}

const getTagClass = (tag, classes) => {
  switch (tag) {
    case "New":
      return classes["chipSuccess"];
    default:
      return classes["chipPrimary"];
  }
};

const CatalogItem = (
  {
    id,
    title,
    category,
    installed,
    enabled,
    tag,
    shortDescription,
    dotColor,
    onOpen,
    showAdded,
  }: Props
) => {
  const classes = useStyles();

  return (
    <>
      <ListItem
        button
        className="p-0"
        onClick={() => onOpen(id)}
        secondaryAction={showAdded && !enabled && <Typography variant="caption" className="disabled">Added</Typography>}
      >
        <ListItemText
          classes={{
            root: !enabled && "disabled",
            primary: classes.primaryText,
            secondary: classes.secondaryText
          }}
          primary={(
            <div className="centeredFlex">
              {dotColor && <span className={classes.dot} style={{ backgroundColor: enabled ? dotColor : null }} />}
              {title}
              {!dotColor && tag && <Chip className={clsx(classes.chip, getTagClass(tag, classes))} label={tag} size="small" />}
            </div>
          )}
          secondary={shortDescription || "No description"}
        />
      </ListItem>
      <Divider light />
    </>
);
};

export default CatalogItem;