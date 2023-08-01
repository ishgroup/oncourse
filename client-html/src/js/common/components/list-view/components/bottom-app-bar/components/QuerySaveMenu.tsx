/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import React from "react";

const QuerySaveMenu = props => {
  const { closeQuerySaveMenu, anchor, setSavingFilterState } = props;

  return (
    <Menu id="querySave" anchorEl={anchor} open={Boolean(anchor)} onClose={closeQuerySaveMenu} disableAutoFocusItem>
      <MenuItem
        classes={{
          root: "listItemPadding"
        }}
        onClick={() => setSavingFilterState(true)}
      >
        Add custom filter
      </MenuItem>
      <MenuItem
        classes={{
          root: "listItemPadding"
        }}
        onClick={() => setSavingFilterState(false)}
      >
        Add custom filter visible to everyone
      </MenuItem>
    </Menu>
  );
};

export default QuerySaveMenu;
