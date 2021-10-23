/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Launch from "@mui/icons-material/Launch";
import IconButton from "@mui/material/IconButton";

const TimetableButton: React.FC<any> = ({ title = "Timetable", onClick }) => (
  <div className="centeredFlex">
    <div className="heading">{title}</div>
    <IconButton
      color="primary"
      onClick={onClick}
    >
      <Launch className="inputAdornmentIcon" />
    </IconButton>
  </div>
);

export default TimetableButton;
