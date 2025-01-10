/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import DescriptionOutlinedIcon from '@mui/icons-material/DescriptionOutlined';
import { ListItemButton, Typography } from '@mui/material';
import { formatDistanceStrict } from 'date-fns';
import { openInternalLink } from 'ish-ui';
import React from 'react';
import { withStyles } from 'tss-react/mui';
import { getPrivisioningLink } from '../../../../../../routes/routesMapping';

const styles = theme => ({
  chips: {
    color: theme.palette.text.primary,
    height: "18px",
    width: "min-content",
    marginTop: "4px",
    justifySelf: "end"
  },
  listItem: {
    justifyContent: "space-between",
    padding: "4px 6px 4px 0",
    "&:hover": {
      backgroundColor: 'inherit'
    }
  },
  favoriteScriptIcon: {
    fontSize: theme.spacing(2),
    verticalAlign: "text-bottom"
  },
  itemName: {
    wordBreak: "break-all"
  }
});

const ListLinkItem = props => {
  const {
    classes, item, url, openLink, selected, id
  } = props;

  return (
    <ListItemButton
      onClick={openLink ? () => openLink(item.id) : () => openInternalLink(getPrivisioningLink(url))}
      disableGutters
      className={classes.listItem}
      dense
      selected={selected}
      id={id}
    >
      <Typography variant="body2" className={`linkDecoration ${classes.itemName}`}>
        {item.name}
        {' '}
        {item.type === "script" ? <DescriptionOutlinedIcon className={classes.favoriteScriptIcon}/> : ""}
      </Typography>
      {item.date && (
        <Typography className="graySmallFont12" align="right">
          {formatDistanceStrict(new Date(item.date), new Date(), { addSuffix: true })}
        </Typography>
      )}
    </ListItemButton>
  );
};

export default withStyles(ListLinkItem, styles);
