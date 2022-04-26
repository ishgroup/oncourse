/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo, useState } from "react";
import Collapse from "@mui/material/Collapse";
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import ExpandIcon from "@mui/icons-material/ExpandMore";
import clsx from "clsx";
import { Session } from "@api/model";
import CalendarSession from "./CalendarSession";
import { CalendarTagsState } from "../../../../../../model/timetable";

const numberOfClasses = (length: number) => (length > 1 ? `${length} classes` : `${length} class`);

interface Props {
  classes: any;
  sessions: Session[];
  tagsState?: CalendarTagsState;
}

const onCodeClick = classId => window.open(`/class?search=id is ${classId}`, "_self");

const CalendarSessionHour = React.memo<Props>(({
 classes, sessions, tagsState
}) => {
  const [open, toggleHour] = useState(false);

  const sessionCodes = useMemo(
    () => sessions.map((s, i) => (
      <span key={s.id + i} onClick={() => onCodeClick(s.classId)} className="linkDecoration">
        {s.code}
        {i !== sessions.length - 1 && ", "}
      </span>
      )),
    [sessions]
  );

  const renderedSessions = useMemo(
    () => sessions.map(s => (
      <CalendarSession key={s.id} tagsState={tagsState} {...s} />
      )),
    [sessions, tagsState]
  );

  return useMemo(
    () => (
      <div className={classes.root}>
        <div className={classes.codeLine}>
          <Typography noWrap>{sessionCodes}</Typography>
          <Typography variant="caption" color="textSecondary">
            (
            {numberOfClasses(sessions.length)}
            )
          </Typography>
          <IconButton className={classes.expandButton} onClick={() => toggleHour(!open)}>
            <ExpandIcon
              className={clsx({
                [classes.expandIcon]: true,
                [classes.rotateIcon]: open
              })}
            />
          </IconButton>
        </div>

        <Collapse in={open} classes={{ wrapperInner: classes.sessions }} mountOnEnter unmountOnExit>
          {renderedSessions}
        </Collapse>
      </div>
    ),
    [open, renderedSessions]
  );
});

export default CalendarSessionHour;
