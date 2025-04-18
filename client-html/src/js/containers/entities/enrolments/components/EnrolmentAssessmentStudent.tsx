/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { GradingItem, GradingType } from '@api/model';
import Grid from '@mui/material/Grid';
import $t from '@t';
import clsx from 'clsx';
import { D_MMM_YYYY, EditInPlaceDateTimeField, EditInPlaceSearchSelect, stubFunction } from 'ish-ui';
import React, { useEffect, useMemo, useState } from 'react';
import { Dispatch } from 'redux';
import { IAction } from '../../../../common/actions/IshAction';
import { EnrolmentAssessmentExtended, EnrolmentExtended } from '../../../../model/entities/Enrolment';
import AssessmentSubmissionIconButton from '../../courseClasses/components/assessments/AssessmentSubmissionIconButton';
import GradeContent from '../../courseClasses/components/assessments/GradeContent';
import { useGradeErrors } from '../../courseClasses/components/assessments/utils/hooks';

interface Props {
  elem: EnrolmentAssessmentExtended;
  values: EnrolmentExtended;
  onChangeStatus: any;
  classes: any;
  hasGrades: boolean;
  index: number;
  dispatch: Dispatch<IAction>
  onToggleGrade: (elem: EnrolmentAssessmentExtended, grade: GradingItem) => void;
  onChangeGrade: (value: number, elem: EnrolmentAssessmentExtended) => void;
  handleGradeMenuOpen: any;
  gradeType: GradingType;
  gradeItems: GradingItem[];
  triggerAsyncChange: (newValue: any, field: string, index: number) => void;
}

const EnrolmentAssessmentStudent: React.FC<Props> = (
  {
    elem,
    values,
    onChangeStatus,
    classes,
    hasGrades,
    index,
    onToggleGrade,
    onChangeGrade,
    handleGradeMenuOpen,
    gradeType,
    gradeItems,
    triggerAsyncChange,
    dispatch
  }
) => {
  const [gradeVal, setGradeVal] = useState<number>(null);

  const submissionIndex = values.submissions.findIndex(s => s.assessmentId === elem.id);
  const submission = submissionIndex !== -1 && values.submissions[submissionIndex];
  const submitStatus = submission && submission.submittedOn ? "Submitted" : "Not submitted";
  const markedStatus = submission && submission.markedOn ? "Submitted" : "Not submitted";

  useEffect(() => {
    setGradeVal(submission?.grade);
  }, [submission?.grade]);

  const submittedContent = (
    <div className="d-flex relative">
      <AssessmentSubmissionIconButton
        status={submitStatus}
        onClick={() => onChangeStatus("Submitted", submissionIndex, submitStatus, elem)}
      />
    </div>
  );

  const markedContent = (
    <div className="d-flex relative">
      <AssessmentSubmissionIconButton
        status={markedStatus}
        onClick={() => onChangeStatus("Marked", submissionIndex, markedStatus, elem)}
      />
    </div>
  );

  const currentGrade = useMemo(() => (typeof submission?.grade === "number"
    ? gradeType?.entryType === "choice list"
      ? gradeItems?.find(g => g.lowerBound === submission.grade)
      : gradeItems?.find(g => g.lowerBound < submission.grade || (submission.grade === 0 && g.lowerBound === 0))
    : null), [submission?.grade, gradeItems]);

  const gradeErrors = useGradeErrors(submission?.grade, gradeType);

  return (
    <Grid container key={index} className={clsx(classes.rowWrapper, "align-items-center d-inline-flex-center")}>
      <Grid item xs={3} className="d-inline-flex-center pl-1">
        {elem.name}
      </Grid>
      <Grid item xs={hasGrades ? 3 : 6} className={classes.center}>
        {submitStatus === "Submitted"
          ? (
            <div className="pl-3">
              <EditInPlaceDateTimeField
                meta={{
                  dispatch
                }}
                input={{
                onChange: value => triggerAsyncChange(value, "submittedOn", submissionIndex),
                onFocus: stubFunction,
                onBlur: stubFunction,
                value: submission.submittedOn
              }}
                type="datetime"
                inline
                formatDate={D_MMM_YYYY}
              />
            </div>
          )
          : submittedContent}
      </Grid>

      {Boolean(gradeType) && (
      <>
        <Grid item xs={3} className={classes.center}>
          {markedStatus === "Submitted" ? (
            <div className="pt-0-5">
              <div className="pl-3">
                <EditInPlaceDateTimeField
                  meta={{
                    dispatch
                  }}
                  input={{
                    onChange: value => triggerAsyncChange(value, "markedOn", submissionIndex),
                    onFocus: stubFunction,
                    onBlur: stubFunction,
                    value: submission.markedOn
                  }}
                  type="datetime"
                  inline
                  formatDate={D_MMM_YYYY}
                />
              </div>
              <div className="pl-3">
                <EditInPlaceSearchSelect
                  meta={{}}
                  selectValueMark="contactId"
                  selectLabelMark="tutorName"
                  input={{
                    onChange: value => triggerAsyncChange(value, "markedById", submissionIndex),
                    onFocus: stubFunction,
                    onBlur: stubFunction,
                    value: submission.markedById
                  }}
                  placeholder={$t('no_assessor')}
                  items={elem.tutors || []}
                  allowEmpty
                  inline
                />
              </div>
            </div>
          ) : markedContent}
        </Grid>
        <Grid item xs={3} className={classes.center}>
          <GradeContent
            handleGradeMenuOpen={handleGradeMenuOpen}
            onToggleGrade={onToggleGrade}
            onChangeGrade={onChangeGrade}
            currentGrade={currentGrade}
            gradeErrors={gradeErrors}
            setGradeVal={setGradeVal}
            gradeType={gradeType}
            gradeVal={gradeVal}
            classes={classes}
            index={index}
            elem={elem}
          />
        </Grid>
      </>
    )}
    </Grid>
  );
};

export default EnrolmentAssessmentStudent;
