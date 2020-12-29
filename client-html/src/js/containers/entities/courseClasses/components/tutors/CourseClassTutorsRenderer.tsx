/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { WrappedFieldArrayProps } from "redux-form";
import { ClassCostExtended } from "../../../../../model/entities/CourseClass";
import CourseClassTutorItem from "./CourseClassTutorItem";

interface Props extends WrappedFieldArrayProps {
  setExpanded: any;
  expanded: any;
  onDeleteTutor: any;
  onTutorIdChangeBase: any;
  onRoleIdChangeBase: any;
  activeTutorRoles: any;
  addTutorWage: any;
  currencySymbol: any;
  latestSession: any;
  tutorNamesWarnings: any;
  budget?: ClassCostExtended[];
}

const CourseClassTutorsRenderer = React.memo<Props>(props => {
  const {
    fields,
    tutorNamesWarnings,
    setExpanded,
    expanded,
    onDeleteTutor,
    onTutorIdChangeBase,
    onRoleIdChangeBase,
    activeTutorRoles,
    addTutorWage,
    latestSession,
    budget
  } = props;

  return (
    <>
      {fields.map((t, i) => {
        const onChange = () => setExpanded(expanded === i ? null : i);
        const onTutorIdChange = val => onTutorIdChangeBase(i, val);
        const onRoleIdChange = val => onRoleIdChangeBase(i, val);
        const tutor = fields.get(i);
        const onDelete = () => onDeleteTutor(i, tutor, fields);
        const nameWarning = tutorNamesWarnings[tutor.contactId];
        const wage = budget.find(b => b.flowType === "Wages"
          && (b.courseClassTutorId === tutor.id
            || (b.temporaryTutorId && b.temporaryTutorId === tutor.temporaryId)));

        const openTutorWage = () => addTutorWage(tutor, wage);

        return (
          <CourseClassTutorItem
            key={tutor.id || tutor.temporaryId}
            tutor={tutor}
            index={i}
            onChange={onChange}
            onDelete={onDelete}
            onTutorIdChange={onTutorIdChange}
            onRoleIdChange={onRoleIdChange}
            tutorRoles={activeTutorRoles}
            openTutorWage={openTutorWage}
            expandedIndex={expanded}
            latestSession={latestSession}
            nameWarning={nameWarning}
            hasWage={Boolean(wage)}
          />
        );
      })}
    </>
  );
});

export default CourseClassTutorsRenderer;
