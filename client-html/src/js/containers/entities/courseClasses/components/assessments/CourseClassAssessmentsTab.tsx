/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AssessmentClass, GradingType } from '@api/model';
import { Divider, Grid, Typography } from '@mui/material';
import $t from '@t';
import React, { useCallback, useMemo } from 'react';
import { connect } from 'react-redux';
import { arrayInsert, arrayRemove } from 'redux-form';
import { addActionToQueue, removeActionsFromQueue } from '../../../../../common/actions';
import instantFetchErrorHandler from '../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import MinifiedEntitiesList from '../../../../../common/components/form/minifiedEntitiesList/MinifiedEntitiesList';
import uniqid from '../../../../../common/utils/uniqid';
import { EditViewProps } from '../../../../../model/common/ListView';
import { AssessmentClassExtended, CourseClassExtended } from '../../../../../model/entities/CourseClass';
import { State } from '../../../../../reducers/state';
import { deleteCourseClassAssessment } from './actions';
import CourseClassAssessmentItems from './CourseClassAssessmentItems';
import CourseClassAssessmentService from './services/CourseClassAssessmentService';

const assessmentInitial: AssessmentClass = {
  id: null,
  assessmentId: null,
  assessmentCode: null,
  assessmentName: null,
  contactIds: [],
  moduleIds: [],
  submissions: [],
  releaseDate: null,
  dueDate: null
};

interface Props {
  courseClassEnrolments?: any[];
  gradingTypes?: GradingType[];
}

const CourseClassAssessmentsTab: React.FC<Partial<EditViewProps<CourseClassExtended> & Props>> = (
  {
    values,
    form,
    dispatch,
    isNew,
    twoColumn,
    syncErrors,
    showConfirm,
    courseClassEnrolments,
    gradingTypes
}
) => {
  const validateAssesments = useCallback((value: AssessmentClass[]) => {
    let error;

    if (Array.isArray(value) && value.length) {
      value.forEach(a => {
        if (!a.assessmentId || !a.assessmentCode || !a.assessmentName || !a.dueDate) {
          error = "Some assessments are missing required fields";
        }
        const gradeType: GradingType = gradingTypes?.find(g => g.id === a.gradingTypeId);

        if (gradeType && a.submissions.some(s => typeof s.grade === "number"
          && (s.grade > gradeType.maxValue || s.grade < gradeType.minValue))) {
          error = "Some assessments grades are invalid";
        }
      });
    }

    return error;
  }, [gradingTypes]);

  const AssessmentItemsComponent = useCallback(
    ({ classes, item, ...rest }) => (
      <CourseClassAssessmentItems
        {...rest}
        item={item}
        dispatch={dispatch}
        form={form}
        courseClassEnrolments={courseClassEnrolments}
        gradingTypes={gradingTypes}
        tutors={values.tutors.filter(t => t.id)}
      />
      ),
    [values.tutors, form, gradingTypes, courseClassEnrolments]
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
      showConfirm({
          onConfirm: () => {
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
          confirmButtonText: "Delete",
          confirmMessage: "Assessment will be deleted permanently"
        }
      );
    },
    [values.assessments && values.assessments.length]
  );

  return (
    <Grid container>
      <Grid item xs={12} className="pl-3 pr-3 pb-1">
        <Divider className="mb-1" />
        {isNew ? (
          <div>
            <div className="heading pb-1">{$t('assessments2')}</div>
            <Typography variant="caption" color="textSecondary">
              {$t('please_save_your_new_class_before_editing_assessme')}
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

const mapStateToProps = (state: State) => ({
  courseClassEnrolments: state.courseClass.enrolments,
  gradingTypes: state.preferences.gradingTypes
});

export default connect(mapStateToProps)(CourseClassAssessmentsTab);
