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
import { Grid, IconButton, Tooltip } from "@material-ui/core";
import { DateRange } from "@material-ui/icons";
import clsx from "clsx";
import { format } from "date-fns";
import {
 CourseClassTutor, GradingItem, GradingType
} from "@api/model";
import AssessmentSubmissionIconButton from "./AssessmentSubmissionIconButton";
import { III_DD_MMM_YYYY } from "../../../../../common/utils/dates/format";
import { StudentForRender } from "./CourseClassAssessmentItem";
import { StringArgFunction } from "../../../../../model/common/CommonFunctions";
import { useGradeErrors } from "./utils/hooks";
import GradeContent from "./GradeContent";

interface Props {
  elem: StudentForRender;
  onChangeStatus: any;
  classes: any;
  setModalOpenedBy: StringArgFunction;
  gradeType: GradingType;
  gradeItems: GradingItem[];
  onToggleGrade: (elem: StudentForRender, grade: GradingItem) => void;
  onChangeGrade: (value: number, elem: StudentForRender) => void;
  handleGradeMenuOpen: any;
  index: number;
  tutors: CourseClassTutor[];
}

const CourseClassAssessmentStudent: React.FC<Props> = (
  {
    elem,
    onChangeStatus,
    classes,
    setModalOpenedBy,
    gradeType,
    gradeItems,
    onToggleGrade,
    onChangeGrade,
    handleGradeMenuOpen,
    index,
    tutors
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
      {elem.submittedValue === "Submitted" && (
        <IconButton
          size="small"
          className={classes.hiddenIcon}
          onClick={() => setModalOpenedBy(`Submitted-${elem.submissionIndex}-${elem.studentName}-${index}`)}
        >
          <DateRange color="disabled" fontSize="small" />
        </IconButton>
      )}
    </div>
  );

  const markedContent = (
    <div className="d-flex relative">
      <AssessmentSubmissionIconButton
        status={elem.markedValue}
        onClick={() => onChangeStatus("Marked", elem)}
      />
      {elem.markedValue === "Submitted" && (
        <IconButton
          size="small"
          className={classes.hiddenIcon}
          onClick={() => setModalOpenedBy(`Marked-${elem.submissionIndex}-${elem.studentName}-${index}`)}
        >
          <DateRange color="disabled" fontSize="small" />
        </IconButton>
      )}
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
            <Tooltip
              title={(
                <span>
                  Submitted date:
                  {" "}
                  {elem.submission && format(new Date(elem.submission.submittedOn), III_DD_MMM_YYYY)}
                </span>
              )}
              placement="top"
              disableFocusListener
              disableTouchListener
            >
              {submittedContent}
            </Tooltip>
          )
          : submittedContent}
      </Grid>
      <Grid item xs={2} className={classes.center}>
        {elem.markedValue === "Submitted" ? (
          <Tooltip
            title={(
              <span>
                Marked date:
                {" "}
                {elem.submission && format(new Date(elem.submission.markedOn), III_DD_MMM_YYYY)}
                <br />
                {elem?.submission?.markedById && tutors && (
                  <span>
                    Assessor:
                    {" "}
                    {tutors.find(t => t.contactId === elem.submission.markedById)?.tutorName}
                  </span>
                )}
              </span>
            )}
            placement="top"
            disableFocusListener
            disableTouchListener
          >
            {markedContent}
          </Tooltip>
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
