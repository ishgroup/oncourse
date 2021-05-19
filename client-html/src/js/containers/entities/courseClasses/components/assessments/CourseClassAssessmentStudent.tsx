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
import { Grid, } from "@material-ui/core";
import clsx from "clsx";
import {
 CourseClassTutor, GradingItem, GradingType
} from "@api/model";
import AssessmentSubmissionIconButton from "./AssessmentSubmissionIconButton";
import { D_MMM_YYYY } from "../../../../../common/utils/dates/format";
import { StudentForRender } from "./CourseClassAssessmentItems";
import { useGradeErrors } from "./utils/hooks";
import GradeContent from "./GradeContent";
import EditInPlaceDateTimeField from "../../../../../common/components/form/form-fields/EditInPlaceDateTimeField";
import { stubFunction } from "../../../../../common/utils/common";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";

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
    triggerAsyncChange
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
    ? gradeType.entryType === "choice list"
      ? gradeItems?.find(g => g.lowerBound === elem.submission.grade)
      : gradeItems?.find(g => g.lowerBound < elem.submission.grade || (elem.submission.grade === 0 && g.lowerBound === 0))
    : null), [elem?.submission?.grade, gradeItems]);

  const gradeErrors = useGradeErrors(elem?.submission?.grade, gradeType);

  return (
    <Grid container key={index} className={clsx(classes.rowWrapper, "align-items-center d-inline-flex-center")}>
      <Grid item xs={4} className="d-inline-flex-center pl-1">
        {elem.studentName}
      </Grid>
      <Grid item xs={2} className={classes.center}>
        {elem.submittedValue === "Submitted"
          ? (
            <EditInPlaceDateTimeField
              meta={{}}
              input={{
                onChange: value => triggerAsyncChange(value, "submittedOn", elem.submissionIndex),
                onFocus: stubFunction,
                onBlur: stubFunction,
                value: elem.submission.submittedOn
              }}
              type="date"
              formatting="inline"
              formatDate={D_MMM_YYYY}
              inlineMargin
            />
          )
          : submittedContent}
      </Grid>
      <Grid item xs={2} className={classes.center}>
        {elem.markedValue === "Submitted" ? (
          <div>
            <div>
              <EditInPlaceDateTimeField
                meta={{}}
                input={{
                  onChange: value => triggerAsyncChange(value, "markedOn", elem.submissionIndex),
                  onFocus: stubFunction,
                  onBlur: stubFunction,
                  value: elem.submission.markedOn
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
                  onChange: value => triggerAsyncChange(value, "markedById", elem.submissionIndex),
                  onFocus: stubFunction,
                  onBlur: stubFunction,
                  value: elem.submission.markedById
                }}
                placeholder="No assessor"
                formatting="inline"
                items={tutors}
                select
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
    </Grid>
  );
};

export default CourseClassAssessmentStudent;
