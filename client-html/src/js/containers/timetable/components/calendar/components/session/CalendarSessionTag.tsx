/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Theme } from '@mui/material';
import React from 'react';
import { withStyles } from 'tss-react/mui';
import { CalendarTagsState } from '../../../../../../model/timetable';

const styles = (theme: Theme) => ({
  tagColorDot: {
    isolate: false,
    width: theme.spacing(2),
    height: theme.spacing(2),
    borderRadius: "100%",
    marginRight: theme.spacing(0.5),
    transition: theme.transitions.create("transform", {
      duration: theme.transitions.duration.shorter,
      easing: theme.transitions.easing.easeInOut
    })
  },
  tagName: {
    transition: theme.transitions.create("width", {
      duration: theme.transitions.duration.shorter,
      easing: theme.transitions.easing.easeInOut
    }),
    whiteSpace: "nowrap",
    overflow: "hidden",
    minWidth: "0px"
  }
});

interface SessionTagProps {
  classes?: any;
  color: string;
  tagsState?: CalendarTagsState;
  name: string;
}

const CalendarSessionTag: React.FC<SessionTagProps> = ({
  classes, color, tagsState, name
}) => (
  <span className="d-inline-flex align-items-center mr-1">
    <span className={classes.tagColorDot} style={{ background: color }} />
    <span className={classes.tagName} style={{ width: tagsState === "Tag names" ? "calc(100% - 20px)" : "0px" }}>
      #
      {name}
    </span>
  </span>
);

export default withStyles(CalendarSessionTag, styles as any);
