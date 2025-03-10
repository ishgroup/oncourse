/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Divider, ListItem, ListItemButton, ListItemText } from '@mui/material';
import clsx from 'clsx';
import { InfoPill, makeAppStyles, NumberArgFunction, useHoverShowStyles } from 'ish-ui';
import React, { useCallback } from 'react';
import { CatalogItemType } from '../../../../model/common/Catalog';

const useStyles = makeAppStyles<void, 'dot'>()((theme, p, classes) => ({
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
    [`&.disabled .${classes.dot}`]: {
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

  const { classes } = useStyles();
  const { classes: hoverClasses } = useHoverShowStyles();

  const onItemClick = useCallback(() => {
    if (onOpen) onOpen(id);
  }, [id]);

  return (
    <>
      <ListItem
        className={clsx("p-0", hoverClasses.container)}
        secondaryAction={<span className={clsx(hoverSecondary && hoverClasses.target)}>{secondaryAction}</span>}
      >
        <ListItemButton className={hideShortDescription ? "pl-0 pr-0" : "p-0"} onClick={onItemClick} disabled={disabled}>
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
            secondary={!hideShortDescription && (shortDescription || "No short description")}
          />
        </ListItemButton>

      </ListItem>
      <Divider light/>
    </>
  );
};

export default CatalogItem;