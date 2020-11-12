/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import DescriptionOutlinedIcon from "@material-ui/icons/DescriptionOutlined";
import React from "react";
import { formatDistanceStrict } from "date-fns";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import ListItem from "@material-ui/core/ListItem";
import Typography from "@material-ui/core/Typography";
import { openInternalLink } from "../../../../../utils/links";

const styles = theme => createStyles({
  chips: {
    color: theme.palette.text.primary,
    height: "18px",
    width: "min-content",
    marginTop: "4px",
    justifySelf: "end"
  },
  listItem: {
    justifyContent: "space-between",
    padding: "4px 6px"
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
    showConfirm, classes, item, url, openLink, selected, id
  } = props;

  return (
    <ListItem
      onClick={openLink ? () => openLink(item.id) : () => showConfirm(() => openInternalLink(url))}
      disableGutters
      className={classes.listItem}
      dense
      selected={selected}
      id={id}
    >
      <Typography variant="body2" className={`linkDecoration ${classes.itemName}`}>
        {item.name}
        {' '}
        {item.type === "script" ? <DescriptionOutlinedIcon className={classes.favoriteScriptIcon} /> : ""}
      </Typography>
      {item.date && (
        <Typography className="graySmallFont12" align="right">
          {formatDistanceStrict(new Date(item.date), new Date(), { addSuffix: true })}
        </Typography>
      )}
    </ListItem>
  );
};

export default withStyles(styles)(ListLinkItem);
