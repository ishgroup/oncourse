/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo } from "react";
import { Button } from "@mui/material";
import { isSameDay } from "date-fns";
import clsx from "clsx";
import { alpha } from "@mui/material/styles";
import { makeAppStyles } from "../../../../../../../common/styles/makeStyles";
import { useAppTheme } from "../../../../../../../common/themes/ishTheme";

interface Props {
  day: number;
  isToday?: boolean;
  today?: any;
  date: Date;
  status: string;
  selectedMonthSessionDays: number[];
  disabled: boolean;
  theme?: any;
  targetDay?: any;
  setTargetDay?: any;
}

const useStyles = makeAppStyles(theme => ({
  root: {
    padding: 0,
    border: "2px solid transparent",
    borderRadius: 4,
    minWidth: "initial",
    boxShadow: "none",
    fontWeight: "normal",
    color: theme.palette.text.primary,
    "&:active": {
      boxShadow: "none"
    },
    "&:focus": {
      boxShadow: "none"
    },
    "&::after": {
      content: "''",
      position: "absolute",
      width: 4,
      height: 4,
      top: 4,
      borderRadius: "100%"
    },
    "&$same": {
      border: `2px solid ${theme.palette.text.primary}`
    }
  },
  same: {}
}));

const CalendarDay = React.memo((props: Props) => {
  const {
   day, status, date, disabled, targetDay, setTargetDay, selectedMonthSessionDays
  } = props;

  const classes = useStyles();

  const theme = useAppTheme();
  const isSame = isSameDay(targetDay, date);
  const isToday = isSameDay(new Date(), date);
  
  const style = useMemo(() => {
    let result;

    const dayIndexValue = selectedMonthSessionDays[day - 1];
    
    if (selectedMonthSessionDays.length && status === "current" && dayIndexValue !== 0) {
      const backgroundColor = alpha(theme.palette.primary.main, dayIndexValue);

      result = {
        ...result || {},
        backgroundColor,
        color: theme.palette.getContrastText(backgroundColor)
      };
    }
    
    if (isToday) {
      result = {
        ...result || {},
        fontWeight: "900"
      };
    }
    
    return result;
  }, [isToday, status, selectedMonthSessionDays, day, theme]);

  return (
    <Button
      className={clsx(classes.root, isSame && classes.same)}
      onClick={() => setTargetDay(date)}
      disabled={disabled}
      style={style}
    >
      {day}
    </Button>
  );
});

export default CalendarDay;
