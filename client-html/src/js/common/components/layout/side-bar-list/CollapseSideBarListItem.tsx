/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Tooltip from "@material-ui/core/Tooltip";
import MenuItem from "@material-ui/core/MenuItem";
import Typography from "@material-ui/core/Typography";
import clsx from "clsx";
import { NavLink } from "react-router-dom";

const CollapseSideBarListItem: React.FC<any> = ({
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
  <NavLink to={to} className="link" isActive={(match, location) => isActiveLink(location, to, index, item.name)}>
    <Tooltip
      title={item.name}
      disableTouchListener
      disableFocusListener
      open={openedTooltip === index}
      placement="bottom"
    >
      <MenuItem button className={classes.listItemPadding} selected={activeLink === index.toString()}>
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
        {ItemIconRenderer ? <ItemIconRenderer className={classes.itemIcon} item={item} /> : (item.hasIcon && <ItemIcon className={classes.itemIcon} item={item} />)}
      </MenuItem>
    </Tooltip>
  </NavLink>
  );

export default CollapseSideBarListItem;
