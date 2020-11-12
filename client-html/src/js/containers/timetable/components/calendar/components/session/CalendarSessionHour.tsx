/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo, useState } from "react";
import Collapse from "@material-ui/core/Collapse";
import Typography from "@material-ui/core/Typography";
import IconButton from "@material-ui/core/IconButton";
import ExpandIcon from "@material-ui/icons/ExpandMore";
import clsx from "clsx";
import { Session } from "@api/model";
import CalendarSession from "./CalendarSession";
import { Classes } from "../../../../../../model/entities/CourseClass";

const numberOfClasses = (length: number) => (length > 1 ? `${length} classes` : `${length} class`);

interface Props {
  classes: any;
  sessions: Session[];
  tagsExpanded: any;
  setTagsExpanded: any;
}

const onCodeClick = classId => window.open(`/${Classes.path}?search=id is ${classId}`, "_self");

const CalendarSessionHour = React.memo<Props>(({
 classes, sessions, tagsExpanded, setTagsExpanded
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
      <CalendarSession key={s.id} tagsExpanded={tagsExpanded} setTagsExpanded={setTagsExpanded} inView {...s} />
      )),
    [sessions, tagsExpanded]
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
