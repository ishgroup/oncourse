/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import OpenInNew from "@mui/icons-material/OpenInNew";
import IconButton from "@mui/material/IconButton";

const TimetableButton: React.FC<any> = ({ title = "Timetable", onClick }) => (
  <div className="centeredFlex">
    <div className="heading">{title}</div>
    <IconButton size="small" color="primary" onClick={onClick}>
      <OpenInNew fontSize="inherit" />
    </IconButton>
  </div>
);

export default TimetableButton;
