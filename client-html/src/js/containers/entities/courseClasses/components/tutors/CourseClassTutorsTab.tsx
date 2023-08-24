/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CourseClassTutor, DefinedTutorRole } from "@api/model";
import Grid from "@mui/material/Grid";
import { AddButton, StringKeyAndValueObject } from "ish-ui";
import React, { useCallback, useMemo, useState } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { arrayInsert, change, FieldArray, WrappedFieldArrayProps } from "redux-form";
import { addActionToQueue, removeActionsFromQueue } from "../../../../../common/actions";
import instantFetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import uniqid from "../../../../../common/utils/uniqid";
import { EditViewProps } from "../../../../../model/common/ListView";
import {
  ClassCostExtended,
  CourseClassExtended,
  CourseClassTutorExtended
} from "../../../../../model/entities/CourseClass";
import { State } from "../../../../../reducers/state";
import { getContactFullName } from "../../../contacts/utils";
import { deleteCourseClassTutor, postCourseClassTutor, setCourseClassTutorNamesWarnings } from "./actions";
import CourseClassTutorsRenderer from "./CourseClassTutorsRenderer";
import CourseClassTutorService from "./services/CourseClassTutorService";
import { getTutorNameWarning, isTutorWageExist } from "./utils";

export interface CourseClassTutorsTabProps extends Partial<EditViewProps> {
  values?: CourseClassExtended;
  setTutorNamesWarnings?: (warnings: StringKeyAndValueObject) => void;
  tutorRoles?: DefinedTutorRole[];
  addTutorWage?: (tutor: CourseClassTutor, wage?: ClassCostExtended) => void;
  tutorNamesWarnings?: StringKeyAndValueObject;
  currencySymbol?: string;
  latestSession?: Date;
}

const TutorInitial: CourseClassTutor = {
  id: null,
  classId: null,
  contactId: null,
  roleId: null,
  tutorName: null,
  roleName: null,
  confirmedOn: null,
  isInPublicity: true
};

const CourseClassTutorsTab = React.memo<CourseClassTutorsTabProps>(
  ({
    values,
    tutorRoles,
    twoColumn,
    dispatch,
    form,
    showConfirm,
    currencySymbol,
    latestSession,
    setTutorNamesWarnings,
    tutorNamesWarnings,
    addTutorWage
  }) => {
    const [expanded, setExpanded] = useState(null);

    const activeTutorRoles = useMemo(() => (tutorRoles ? tutorRoles.filter(t => t.active) : []), [
      tutorRoles
    ]);

    const onTutorIdChangeBase = useCallback(
      (index: number, value) => {
        const warningsUpdated = { ...tutorNamesWarnings };
        const warning = getTutorNameWarning(value, latestSession);

        if (warning) {
          warningsUpdated[value.id] = warning;
          setTutorNamesWarnings(warningsUpdated);
        } else if (tutorNamesWarnings[value.id]) {
          warningsUpdated[value.id] = null;
          setTutorNamesWarnings(warningsUpdated);
        }
        dispatch(change(form, `tutors[${index}].tutorName`, getContactFullName(value)));
      },
      [tutorNamesWarnings]
    );

    const onDeleteTutor = (index: number, tutor: CourseClassTutorExtended, fields: WrappedFieldArrayProps["fields"]) => {
        const hasWages = isTutorWageExist(values.budget, tutor);

        showConfirm({
          onConfirm: () => {
            const upadted = [...fields.getAll()];
            upadted.splice(index, 1);

            const onDeleteConfirm = () => {
              if (expanded === index) {
                setExpanded(null);
              }
              dispatch(change(form, "tutors", upadted));

              if (hasWages) {
                dispatch(
                  change(
                    form,
                    "budget",
                    values.budget.filter(
                      b => !(
                        b.flowType === "Wages"
                        && (tutor.id ? b.courseClassTutorId === tutor.id : b.temporaryTutorId === tutor.temporaryId)
                      )
                    )
                  )
                );
              }
              
              if (values.sessions.some(s => s.tutorAttendances.some(ta => (ta.courseClassTutorId && ta.courseClassTutorId === tutor.id) || (ta.temporaryTutorId && ta.temporaryTutorId === tutor.temporaryId)))) {
                dispatch(change(form, 'sessions', values.sessions.map(s => ({
                  ...s,
                  tutorAttendances: s.tutorAttendances.filter(sta => (sta.courseClassTutorId ? sta.courseClassTutorId !== tutor.id : sta.temporaryTutorId ? sta.temporaryTutorId !== tutor.temporaryId : true))
                }))));
              }
            };

            if (tutor.id) {
              CourseClassTutorService.validateDelete(tutor.id)
                .then(() => {
                  onDeleteConfirm();
                  dispatch(addActionToQueue(deleteCourseClassTutor(tutor.id), "DELETE", "CourseClassTutor", tutor.id));
                })
                .catch(response => instantFetchErrorHandler(dispatch, response));
              return;
            }
            dispatch(removeActionsFromQueue([{ entity: "CourseClassTutor", id: tutor.temporaryId }]));

            onDeleteConfirm();
          },
          confirmMessage: hasWages
            ? `Wages for ${tutor.tutorName} will be removed too, do you really want to continue?`
            : "Tutor will be deleted permanently",
          confirmButtonText: "Delete"
        });
      };

    const onAddTutor = useCallback(() => {
      const newTutor = { ...TutorInitial, classId: values.id };
      const temporaryId = uniqid();
      newTutor["temporaryId"] = temporaryId;

      dispatch(arrayInsert(form, "tutors", 0, newTutor));
      setExpanded(0);

      const postTutor = { ...newTutor };
      delete postTutor["temporaryId"];

      dispatch(
        addActionToQueue(
          postCourseClassTutor(postTutor),
          "POST",
          "CourseClassTutor",
          temporaryId,
          values.id ? null : temporaryId
        )
      );
    }, [values.tutors, values.id]);

    const onRoleIdChangeBase = useCallback((index: number, value: DefinedTutorRole) => {
      dispatch(change(form, `tutors[${index}].roleName`, value.name));
    }, []);

    return (
      <Grid container columnSpacing={3} className="pl-3 pr-3 pb-2">
        <Grid item xs={12} className="centeredFlex">
          <div className="heading">Tutors</div>
          <AddButton onClick={onAddTutor} />
        </Grid>

        <Grid item xs={twoColumn ? 6 : 12}>
          <FieldArray
            name="tutors"
            component={CourseClassTutorsRenderer}
            setExpanded={setExpanded}
            expanded={expanded}
            onDeleteTutor={onDeleteTutor}
            onTutorIdChangeBase={onTutorIdChangeBase}
            onRoleIdChangeBase={onRoleIdChangeBase}
            activeTutorRoles={activeTutorRoles}
            addTutorWage={addTutorWage}
            tutorNamesWarnings={tutorNamesWarnings}
            currencySymbol={currencySymbol}
            latestSession={latestSession}
            budget={values.budget}
          />
        </Grid>
      </Grid>
    );
  }
);

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setTutorNamesWarnings: warnings => dispatch(setCourseClassTutorNamesWarnings(warnings)),
});

const mapStateToProps = (state: State) => ({
  tutorNamesWarnings: state.courseClass.tutorNamesWarnings,
  latestSession: state.courseClass.latestSession,
  tutorRoles: state.preferences.tutorRoles,
  queuedActions: state.actionsQueue.queuedActions
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(CourseClassTutorsTab);
