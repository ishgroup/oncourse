/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
  useEffect, useMemo, useState
} from "react";
import { Grid, } from "@mui/material";
import clsx from "clsx";
import {
 CourseClassTutor, GradingItem, GradingType
} from "@api/model";
import AssessmentSubmissionIconButton from "./AssessmentSubmissionIconButton";
import { StudentForRender } from "./CourseClassAssessmentItems";
import { useGradeErrors } from "./utils/hooks";
import GradeContent from "./GradeContent";
import EditInPlaceDateTimeField from "../../../../../common/ish-ui/formFields/EditInPlaceDateTimeField";
import { stubFunction } from "../../../../../common/utils/common";
import EditInPlaceSearchSelect from "../../../../../common/ish-ui/formFields/EditInPlaceSearchSelect";
import { Dispatch } from "redux";

interface Props {
  elem: StudentForRender;
  onChangeStatus: any;
  classes: any;
  gradeType: GradingType;
  gradeItems: GradingItem[];
  onToggleGrade: (elem: StudentForRender, grade: GradingItem) => void;
  onChangeGrade: (value: number, elem: StudentForRender) => void;
  handleGradeMenuOpen: any;
  index: number;
  tutors: CourseClassTutor[];
  triggerAsyncChange: (newValue: any, field: string, index: number) => void;
  dispatch: Dispatch;
}

const CourseClassAssessmentStudent: React.FC<Props> = (
  {
    elem,
    onChangeStatus,
    classes,
    gradeType,
    gradeItems,
    onToggleGrade,
    onChangeGrade,
    handleGradeMenuOpen,
    index,
    tutors,
    triggerAsyncChange,
    dispatch
  }
) => {
  const [gradeVal, setGradeVal] = useState<number>(null);

  useEffect(() => {
    setGradeVal(elem?.submission?.grade);
  }, [elem?.submission?.grade]);

  const submittedContent = (
    <div className="d-flex relative">
      <AssessmentSubmissionIconButton
        status={elem.submittedValue}
        onClick={() => onChangeStatus("Submitted", elem)}
      />
    </div>
  );

  const markedContent = (
    <div className="d-flex relative">
      <AssessmentSubmissionIconButton
        status={elem.markedValue}
        onClick={() => onChangeStatus("Marked", elem)}
      />
    </div>
  );

  const currentGrade = useMemo(() => (typeof elem?.submission?.grade === "number"
    ? gradeType?.entryType === "choice list"
      ? gradeItems?.find(g => g.lowerBound === elem.submission.grade)
      : gradeItems?.find(g => g.lowerBound < elem.submission.grade || (elem.submission.grade === 0 && g.lowerBound === 0))
    : null), [elem?.submission?.grade, gradeItems]);

  const gradeErrors = useGradeErrors(elem?.submission?.grade, gradeType);

  return (
    <Grid container key={index} className={clsx(classes.rowWrapper, "align-items-center d-inline-flex-center")}>
      <Grid item xs={4} className="d-inline-flex-center pl-1">
        {elem.studentName}
      </Grid>
      <Grid item xs={Boolean(gradeType) ? 2 : 4} className={classes.center}>
        {elem.submittedValue === "Submitted"
          ? (
            <EditInPlaceDateTimeField
              meta={{
                dispatch
              }}
              input={{
                onChange: value => triggerAsyncChange(value, "submittedOn", elem.submissionIndex),
                onFocus: stubFunction,
                onBlur: stubFunction,
                value: elem.submission.submittedOn
              }}
              type="datetime"
              inline
            />
          )
          : submittedContent}
      </Grid>

      {Boolean(gradeType) && (
      <>
        <Grid item xs={2} className={classes.center}>
          {elem.markedValue === "Submitted" ? (
            <div className="pt-0-5">
              <div>
                <EditInPlaceDateTimeField
                  meta={{
                    dispatch
                  }}
                  input={{
                    onChange: value => triggerAsyncChange(value, "markedOn", elem.submissionIndex),
                    onFocus: stubFunction,
                    onBlur: stubFunction,
                    value: elem.submission.markedOn
                  }}
                  type="datetime"
                  inline
                />
              </div>
              <div>
                <EditInPlaceSearchSelect
                  meta={{}}
                  selectValueMark="contactId"
                  selectLabelMark="tutorName"
                  input={{
                    onChange: value => triggerAsyncChange(value, "markedById", elem.submissionIndex),
                    onFocus: stubFunction,
                    onBlur: stubFunction,
                    value: elem.submission.markedById
                  }}
                  placeholder="No assessor"
                  items={tutors}
                  allowEmpty
                  inline
                />
              </div>
            </div>
          ) : markedContent}
        </Grid>
        <Grid item xs={2} className={classes.center}>
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

export default CourseClassAssessmentStudent;