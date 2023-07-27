/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback } from "react";
import { Divider, ListItem, ListItemText } from "@mui/material";
import clsx from "clsx";
import { CatalogItemType } from "../../../../model/common/Catalog";
import { makeAppStyles } from  "ish-ui";
import { NumberArgFunction } from  "ish-ui";
import { useHoverShowStyles } from  "ish-ui";
import InfoPill from  "ish-ui";
import { InfoPill } from "ish-ui";

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
  root: {
    paddingRight: theme.spacing(7.5),
    "&.disabled $dot": {
      backgroundColor: theme.palette.text.primary
    }
  }
}));

interface Props {
  item: Partial<CatalogItemType>;
  onOpen?: NumberArgFunction;
  secondaryAction?: React.ReactNode;
  hoverSecondary?: boolean;
  disabled?: boolean;
  showDot?: boolean;
  grayOut?: boolean;
}

const CatalogItem = (
  {
    item,
    onOpen,
    secondaryAction,
    hoverSecondary,
    grayOut,
    showDot,
    disabled
  }: Props
) => {
  const {
    id,
    tags,
    title,
    titleAdornment,
    shortDescription,
    hideShortDescription
  } = item;

  const classes = useStyles();
  const hoverClasses = useHoverShowStyles();

  const onItemClick = useCallback(() => {
    if (onOpen) onOpen(id);
  }, [id]);

  return (
    <>
      <ListItem
        button
        className={clsx(hideShortDescription ? "pl-0 pr-0" : "p-0", hoverClasses.container)}
        onClick={onItemClick}
        secondaryAction={<span className={clsx(hoverSecondary && hoverClasses.target)}>{secondaryAction}</span>}
        disabled={disabled}
      >
        <ListItemText
          classes={{
            root: clsx(classes.root, grayOut && "disabled"),
            primary: classes.primaryText,
            secondary: classes.secondaryText
          }}
          primary={(
            <div className="centeredFlex">
              {showDot && <span className={classes.dot}/>}
              {title}
              {titleAdornment}
              {tags?.split(",").map(t => <InfoPill key={t} label={t}/>)}
            </div>
          )}
          secondary={!hideShortDescription && (shortDescription || "No description")}
        />
      </ListItem>
      <Divider light/>
    </>
  );
};

export default CatalogItem;