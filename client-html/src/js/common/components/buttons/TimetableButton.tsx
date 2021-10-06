/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import DateRange from "@mui/icons-material/DateRange";
import Button from "@mui/material/Button";

const TimetableButton: React.FC<any> = ({ title = "Timetable", onClick }) => (
  <Button variant="contained" size="small" onClick={onClick}>
    <DateRange className="mr-1" />
    {title}
  </Button>
);

export default TimetableButton;
