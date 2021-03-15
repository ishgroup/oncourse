/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import Grid from "@material-ui/core/Grid/Grid";
import { arrayInsert, arrayRemove } from "redux-form";
import Typography from "@material-ui/core/Typography";
import { AssessmentClass } from "@api/model";

import MinifiedEntitiesList from "../../../../../common/components/form/minifiedEntitiesList/MinifiedEntitiesList";
import CourseClassAssessmentItems from "./CourseClassAssessmentItem";
import { EditViewProps } from "../../../../../model/common/ListView";
import { AssessmentClassExtended, CourseClassExtended } from "../../../../../model/entities/CourseClass";
import { addActionToQueue, removeActionsFromQueue } from "../../../../../common/actions";
import instantFetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import CourseClassAssessmentService from "./services/CourseClassAssessmentService";
import { deleteCourseClassAssessment } from "./actions";
import uniqid from "../../../../../common/utils/uniqid";

const validateAssesments = (value: AssessmentClass[]) => {
  let error;

  if (Array.isArray(value) && value.length) {
    value.forEach(a => {
      if (!a.assessmentId || !a.assessmentCode || !a.assessmentName || !a.dueDate) {
        error = "Some assessments are missing required fields";
      }
    });
  }

  return error;
};

const assessmentInitial: AssessmentClass = {
  id: null,
  assessmentId: null,
  assessmentCode: null,
  assessmentName: null,
  contactIds: [],
  moduleIds: [],
  releaseDate: null,
  dueDate: null
};

const CourseClassAssessmentsTab: React.FC<Partial<EditViewProps<CourseClassExtended>>> = ({
  values,
  form,
  dispatch,
  isNew,
  twoColumn,
  syncErrors,
  showConfirm
}) => {
  const AssessmentItemsComponent = useCallback(
    ({ classes, ...rest }) => (
      <CourseClassAssessmentItems
        {...rest}
        dispatch={dispatch}
        form={form}
        tutors={values.tutors.filter(t => t.id)}
      />
      ),
    [values.tutors, form]
  );

  const AssessmentHeader = useCallback(
    ({ row, item }) => (
      <Typography variant="subtitle2" id={item} noWrap>
        {row.assessmentName}
      </Typography>
    ),
    []
  );

  const assessmentsCount = useMemo(() => (values && values.assessments ? values.assessments.length : 0), [
    values.assessments
  ]);

  const addAssessmentItem = useCallback(() => {
    const created: AssessmentClassExtended = {
      ...assessmentInitial,
      contactIds: Array.from(new Set(values.tutors.map(t => t.contactId)).values()),
      temporaryId: uniqid(),
      courseClassId: values.id
    };

    dispatch(arrayInsert(form, "assessments", 0, created));
  }, [form, isNew, values.tutors, values.id]);

  const deleteAssessmentItem = useCallback(
    (index: number) => {
      showConfirm(
        () => {
          const onDeleteConfirm = () => {
            dispatch(arrayRemove(form, "assessments", index));
          };

          const assessment = values.assessments[index];

          if (assessment.id) {
            CourseClassAssessmentService.validateDelete(assessment.id)
              .then(() => {
                onDeleteConfirm();
                dispatch(
                  addActionToQueue(
                    deleteCourseClassAssessment(assessment.id),
                    "DELETE",
                    "AssessmentClass",
                    assessment.id
                  )
                );
              })
              .catch(response => instantFetchErrorHandler(dispatch, response));
            return;
          }
          dispatch(removeActionsFromQueue([{ entity: "AssessmentClass", id: assessment.temporaryId }]));

          onDeleteConfirm();
        },
        "Assessment will be deleted permanently",
        "Delete"
      );
    },
    [values.assessments && values.assessments.length]
  );

  return (
    <Grid container>
      <Grid item xs={12} className="pl-3 pr-3 pb-2">
        {isNew ? (
          <div>
            <div className="heading pb-1">Assessments</div>
            <Typography variant="caption" color="textSecondary">
              Please save your new class before editing assessments
            </Typography>
          </div>
        ) : (
          <MinifiedEntitiesList
            name="assessments"
            header="Assessments"
            oneItemHeader="Assessment"
            count={assessmentsCount}
            FieldsContent={AssessmentItemsComponent}
            HeaderContent={AssessmentHeader}
            onAdd={addAssessmentItem}
            onDelete={deleteAssessmentItem}
            twoColumn={twoColumn}
            syncErrors={syncErrors}
            validate={validateAssesments}
            accordion
          />
        )}
      </Grid>
    </Grid>
  );
};

export default CourseClassAssessmentsTab;
