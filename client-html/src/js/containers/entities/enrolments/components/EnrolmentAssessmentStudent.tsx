/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useMemo, useState } from "react";
import Grid from "@material-ui/core/Grid";
import clsx from "clsx";
import { GradingItem, GradingType } from "@api/model";
import AssessmentSubmissionIconButton from "../../courseClasses/components/assessments/AssessmentSubmissionIconButton";
import { D_MMM_YYYY } from "../../../../common/utils/dates/format";
import { EnrolmentAssessmentExtended, EnrolmentExtended } from "../../../../model/entities/Enrolment";
import { useGradeErrors } from "../../courseClasses/components/assessments/utils/hooks";
import GradeContent from "../../courseClasses/components/assessments/GradeContent";
import { stubFunction } from "../../../../common/utils/common";
import EditInPlaceDateTimeField from "../../../../common/components/form/form-fields/EditInPlaceDateTimeField";
import EditInPlaceField from "../../../../common/components/form/form-fields/EditInPlaceField";

interface Props {
  elem: EnrolmentAssessmentExtended;
  values: EnrolmentExtended;
  onChangeStatus: any;
  classes: any;
  hasGrades: boolean;
  index: number;
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
    triggerAsyncChange
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
            <EditInPlaceDateTimeField
              meta={{}}
              input={{
                onChange: value => triggerAsyncChange(value, "submittedOn", submissionIndex),
                onFocus: stubFunction,
                onBlur: stubFunction,
                value: submission.submittedOn
              }}
              type="date"
              formatting="inline"
              formatDate={D_MMM_YYYY}
              inlineMargin
            />
          )
          : submittedContent}
      </Grid>

      {Boolean(gradeType) && (
      <>
        <Grid item xs={3} className={classes.center}>
          {markedStatus === "Submitted" ? (
            <div>
              <div>
                <EditInPlaceDateTimeField
                  meta={{}}
                  input={{
                    onChange: value => triggerAsyncChange(value, "markedOn", submissionIndex),
                    onFocus: stubFunction,
                    onBlur: stubFunction,
                    value: submission.markedOn
                  }}
                  type="date"
                  formatting="inline"
                  formatDate={D_MMM_YYYY}
                  inlineMargin
                />
              </div>
              <div>
                <EditInPlaceField
                  meta={{}}
                  selectValueMark="contactId"
                  selectLabelMark="tutorName"
                  input={{
                    onChange: value => triggerAsyncChange(value, "markedById", submissionIndex),
                    onFocus: stubFunction,
                    onBlur: stubFunction,
                    value: submission.markedById
                  }}
                  placeholder="No assessor"
                  formatting="inline"
                  items={elem.tutors || []}
                  allowEmpty
                  select
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
