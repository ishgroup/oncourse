/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import DateRange from "@material-ui/icons/DateRange";
import Button from "@material-ui/core/Button";

const TimetableButton: React.FC<any> = ({ title = "Timetable", onClick }) => (
  <Button variant="contained" size="small" onClick={onClick}>
    <DateRange className="mr-1" />
    {title}
  </Button>
);

export default TimetableButton;
