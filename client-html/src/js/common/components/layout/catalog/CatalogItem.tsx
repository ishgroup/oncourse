/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import {
 ListItem, ListItemText, Divider, Typography 
} from "@mui/material";
import clsx from "clsx";
import Chip from "@mui/material/Chip";
import { alpha } from "@mui/material/styles";
import { CatalogItemType } from "../../../../model/common/Catalog";
import { makeAppStyles } from "../../../styles/makeStyles";
import { NumberArgFunction } from "../../../../model/common/CommonFunctions";

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
    backgroundColor: theme.palette.success.light
  },
  chip: {
    textTransform: "uppercase",
    fontWeight: 600,
    fontSize: "12px",
    marginLeft: theme.spacing(1),
    color: theme.palette.primary.light,
    backgroundColor: alpha(theme.palette.primary.light, 0.15)
  },
  root: {
    "&.disabled $dot": {
      backgroundColor: theme.palette.text.primary
    }
  }
}));

type Props = Partial<CatalogItemType> & {
  onOpen?: NumberArgFunction;
  showAdded?: boolean;
}

const CatalogItem = (
  {
    id,
    title,
    titleAdornment,
    installed,
    enabled,
    tags,
    shortDescription,
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
        secondaryAction={showAdded && installed && <Typography variant="caption">Added</Typography>}
        disabled={showAdded && installed}
      >
        <ListItemText
          classes={{
            root: clsx(classes.root, !showAdded && !enabled && "disabled"),
            primary: classes.primaryText,
            secondary: classes.secondaryText
          }}
          primary={(
            <div className="centeredFlex">
              {!showAdded && installed && <span className={classes.dot} />}
              {title}
              {titleAdornment}
              {tags?.split(",").map(t => <Chip className={classes.chip} key={t} label={t} size="small" />)}
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