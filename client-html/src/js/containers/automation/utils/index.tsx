/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import LockOutlined from "@mui/icons-material/LockOutlined";
import MenuItem from "@mui/material/MenuItem";
import React from "react";

export const validateKeycode = value =>
  (value && value.startsWith("ish.") ? "Custom automation key codes cannot start with 'ish.'" : undefined);

export const renderAutomationItems = ( content, data, search, parentProps ) => <MenuItem {...parentProps || {}}>
  {data?.hasIcon ? <span>
        {content}
    {' '}
    <LockOutlined className="selectItmeIcon" />
      </span> : content}
</MenuItem>;

export const validateNameForQuotes = name => {
  if (name.includes("\"")) {
    return "Quotation marks not allowed";
  }
  return undefined;
};
