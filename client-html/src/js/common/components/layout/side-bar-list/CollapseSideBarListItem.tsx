/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import MenuItem from "@mui/material/MenuItem";
import Tooltip from "@mui/material/Tooltip";
import Typography from "@mui/material/Typography";
import clsx from "clsx";
import React from "react";
import { NavLink } from "react-router-dom";

const CollapseSideBarListItem: React.FC<any> = (
  {
    to,
    index,
    item,
    isActiveLink,
    activeLink,
    ItemIcon,
    classes,
    handleOpenTooltip,
    handleCloseTooltip,
    openedTooltip,
    ItemIconRenderer
}) => (
  <NavLink to={to} className={clsx("link", item.disabled && "pointer-events-none")} isActive={(match, location) => isActiveLink(location, to, index, item.name)}>
    <Tooltip
      title={item.name}
      disableTouchListener
      disableFocusListener
      open={openedTooltip === index}
      placement="bottom"
    >
      <MenuItem disabled={item.disabled} className={classes.listItemPadding} selected={activeLink === index.toString()}>
        <Typography
          variant="body2"
          onMouseOver={() => handleOpenTooltip(index)}
          onMouseLeave={handleCloseTooltip}
          className={clsx(classes.truncateLabel, {
            [classes.inactiveText]: item.grayOut
          })}
        >
          {item.name}
        </Typography>
        {ItemIconRenderer ? <ItemIconRenderer className={classes.itemIcon} item={item}/> : (item.hasIcon &&
          <ItemIcon className={classes.itemIcon} item={item}/>)}
      </MenuItem>
    </Tooltip>
  </NavLink>
);

export default CollapseSideBarListItem;
