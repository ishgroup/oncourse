/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import LockOutlined from "@material-ui/icons/LockOutlined";
import React from "react";

export const validateKeycode = value =>
  (value && value.startsWith("ish.") ? "Custom automation key codes cannot start with 'ish.'" : undefined);

export const renderAutomationItems = item => (
  item.hasIcon ? (
    <span>
      {item.label}
      {' '}
      <LockOutlined className="selectItmeIcon" />
    </span>
  ) : item.label );
