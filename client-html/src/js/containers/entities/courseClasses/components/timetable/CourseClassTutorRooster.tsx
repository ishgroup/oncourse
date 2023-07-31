/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo, useRef, useState } from "react";
import {
  FormHelperText,
  Menu,
  MenuItem,
} from "@mui/material";
import clsx from "clsx";
import { WrappedFieldProps } from "redux-form";
import { ClashType, CourseClassTutor, SessionWarning } from "@api/model";
import { TimetableSession } from "../../../../../model/timetable";
import { ClassCostExtended, CourseClassTutorExtended } from "../../../../../model/entities/CourseClass";
import { AddButton, preventEventDefault, NumberArgFunction } from  "ish-ui";
import CourseClassTutorRoosterItem from "./CourseClassTutorRoosterItem";

interface TutorRoosterProps extends WrappedFieldProps {
  warningTypes: { [P in ClashType]: SessionWarning[] },
  session: TimetableSession;
  tutors: CourseClassTutorExtended[];
  onDeleteTutor: NumberArgFunction;
  onAddTutor: (tutor: CourseClassTutorExtended) => void;
  sessionDuration: number;
  budget: ClassCostExtended[];
  addTutorWage: (tutor: CourseClassTutor, wage?: ClassCostExtended) => void;
  disableExpand?: boolean;
}

const CourseClassTutorRooster = (
  {
    meta: {
     invalid, error, dispatch, form
    },
    input: { name },
    warningTypes,
    session,
    tutors,
    onDeleteTutor,
    onAddTutor,
    sessionDuration,
    addTutorWage,
    budget,
    disableExpand
  }: TutorRoosterProps
) => {
  const [tutorsMenuOpened, setTutorsMenuOpened] = useState(false);
  const [expanded, setExpanded] = useState(null);

  const tutorsRef = useRef(null);

  const filteredTutors = useMemo<CourseClassTutorExtended[]>(() => tutors
    .filter(t => t.contactId
      && t.roleName
      && !session.tutorAttendances?.some(ta => (ta.courseClassTutorId && t.id === ta.courseClassTutorId)
          || (ta.temporaryTutorId && t.temporaryId === ta.temporaryTutorId))),
    [tutors, session.tutorAttendances]);

  return (
    <div id={name} className="w-100">
      <div className="centeredFlex">
        <div className={clsx("secondaryHeading mb-1 mt-1", (invalid || warningTypes?.Tutor.length) && "errorColor")}>
          TUTOR  TIME & ATTENDANCE
        </div>
        <div>
          {Boolean(filteredTutors.length) && (
          <AddButton className="p-1" ref={tutorsRef} onClick={() => setTutorsMenuOpened(true)} />
        )}
          <Menu
            anchorOrigin={{ vertical: "top", horizontal: "right" }}
            anchorEl={tutorsRef.current}
            open={tutorsMenuOpened}
            onClose={() => setTutorsMenuOpened(false)}
          >
            {filteredTutors.map(t => (
              <MenuItem
                key={t.id}
                onClick={() => {
                onAddTutor(t);
                setTutorsMenuOpened(false);
              }}
              >
                {`${t.tutorName} (${t.roleName})`}
              </MenuItem>
              ))}
          </Menu>
        </div>
      </div>

      <div onClick={preventEventDefault}>
        {session?.tutorAttendances?.map((t, index) => (
          <CourseClassTutorRoosterItem
            key={t.courseClassTutorId || t.temporaryTutorId}
            tutorAttendance={t}
            warningTypes={warningTypes}
            name={name}
            index={index}
            expanded={expanded}
            session={session}
            sessionDuration={sessionDuration}
            dispatch={dispatch}
            form={form}
            tutors={tutors}
            budget={budget}
            addTutorWage={addTutorWage}
            onDeleteTutor={onDeleteTutor}
            setExpanded={setExpanded}
            disableExpand={disableExpand}
          />
        ))}
      </div>
      {invalid && <FormHelperText className="shakingError">{error}</FormHelperText>}
    </div>
);
};

export default CourseClassTutorRooster;
