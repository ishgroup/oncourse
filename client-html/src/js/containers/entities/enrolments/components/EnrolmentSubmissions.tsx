/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useState } from "react";
import { AssessmentSubmission, GradingItem, GradingType } from "@api/model";
import Grid from "@material-ui/core/Grid";
import IconButton from "@material-ui/core/IconButton";
import DateRange from "@material-ui/icons/DateRange";
import { format } from "date-fns";
import { withStyles } from "@material-ui/core/styles";
import { Dispatch } from "redux";
import {
 arrayInsert, arrayRemove, change, WrappedFieldArrayProps
} from "redux-form";
import Typography from "@material-ui/core/Typography";
import { YYYY_MM_DD_MINUSED } from "../../../../common/utils/dates/format";
import styles from "../../courseClasses/components/assessments/styles";
import SubmissionModal from "../../courseClasses/components/assessments/SubmissionModal";
import { EnrolmentAssessmentExtended, EnrolmentExtended } from "../../../../model/entities/Enrolment";
import EnrolmentAssessmentStudent from "./EnrolmentAssessmentStudent";
import GradeModal from "../../courseClasses/components/assessments/GradeModal";
import { normalizeNumber } from "../../../../common/utils/numbers/numbersNormalizing";

interface Props {
  classes?: any;
  values: EnrolmentExtended;
  dispatch: Dispatch;
  gradingTypes: GradingType[];
  twoColumn: boolean;
}

const today = format(new Date(), YYYY_MM_DD_MINUSED);

const EnrolmentSubmissions: React.FC<Props & WrappedFieldArrayProps> = props => {
  const {
    classes, values, dispatch, fields: { name }, meta: { error, form }, gradingTypes = [], twoColumn
  } = props;

  const [modalOpenedBy, setModalOpenedBy] = useState<string>(null);
  const [modalProps, setModalProps] = useState<string[]>([]);
  const [gradeMenuAnchorEl, setGradeMenuAnchorEl] = useState(null);

  useEffect(() => {
    setModalProps(modalOpenedBy ? modalOpenedBy.split("-") : []);
  }, [modalOpenedBy]);

  const handleGradeMenuClose = () => {
    setGradeMenuAnchorEl(null);
  };

  const handleGradeMenuOpen = e => {
    setGradeMenuAnchorEl(e.currentTarget);
  };

  const onChangeAllGrades = (value: number) => {
    dispatch(change(form, "submissions", values.assessments.map(a => {
      const submission = values.submissions.find(s => s.assessmentId === a.id);
      return {
        id: submission ? submission.id : null,
        submittedOn: submission ? submission.submittedOn : null,
        markedById: submission ? submission.markedById : null,
        markedOn: submission ? submission.markedOn : null,
        enrolmentId: values.id,
        studentId: values.studentContactId,
        studentName: values.studentName,
        assessmentId: a.id,
        grade: value,
        classId: values.courseClassId
      };
    }).filter(s => s)));
  };

  const onChangeGrade = (value: number, elem: EnrolmentAssessmentExtended) => {
    const grade = normalizeNumber(value);
    const submissionIndex = values.submissions.findIndex(s => s.assessmentId === elem.id);
    if (submissionIndex === -1) {
      const newSubmission: AssessmentSubmission = {
        id: null,
        submittedOn: today,
        markedById: elem.tutors?.length ? elem.tutors[0].contactId : null,
        markedOn: today,
        enrolmentId: values.id,
        studentId: values.studentContactId,
        studentName: values.studentName,
        assessmentId: elem.id,
        classId: values.courseClassId,
        grade
      };
      dispatch(change(form, "submissions", [newSubmission, ...values.submissions]));
    } else {
      dispatch(change(form, "submissions", values.submissions.map((s, index) => {
        if (submissionIndex === index) {
          return { ...s, grade };
        }
        return s;
      })));
    }
  };

  const onToggleGrade = (elem: EnrolmentAssessmentExtended, grade: GradingItem) => {
    const submissionIndex = values.submissions.findIndex(s => s.assessmentId === elem.id);
    const gradeItems: GradingItem[] = gradingTypes?.find(g => g.id === elem.gradingTypeId)?.gradingItems;

    if (submissionIndex === -1) {
      const newSubmission: AssessmentSubmission = {
        id: null,
        submittedOn: today,
        markedById: elem.tutors?.length ? elem.tutors[0].contactId : null,
        markedOn: today,
        enrolmentId: values.id,
        studentId: values.studentContactId,
        studentName: values.studentName,
        assessmentId: elem.id,
        grade: gradeItems ? gradeItems[0]?.lowerBound : null,
        classId: values.courseClassId
      };
      dispatch(change(form, "submissions", [newSubmission, ...values.submissions]));
    } else {
      dispatch(change(form, "submissions", values.submissions.map((s, index) => {
        if (submissionIndex === index) {
          const gradeIndex = grade ? gradeItems?.findIndex(g => g.lowerBound === grade.lowerBound) : -1;
          return { ...s, grade: gradeItems[gradeIndex + 1]?.lowerBound };
        }
        return s;
      })));
    }
  };

  const onChangeStatus = (type, submissionIndex, prevStatus, assessment) => {
    let pathIndex = submissionIndex;

    if (prevStatus !== "Submitted") {
      if (submissionIndex === -1) {
        pathIndex = 0;
        const newSubmission: AssessmentSubmission = {
          id: null,
          submittedOn: today,
          markedById: null,
          markedOn: type === "Marked" ? today : null,
          enrolmentId: values.id,
          studentId: values.studentContactId,
          studentName: values.studentName,
          assessmentId: assessment.id,
          classId: values.courseClassId,
          grade: null
        };
        dispatch(arrayInsert(form, "submissions", pathIndex, newSubmission));
      }
    } else if (submissionIndex !== -1 && type === "Submitted") {
      dispatch(arrayRemove(form, "submissions", pathIndex));
    }
    if (type === "Marked" && prevStatus === "Submitted") {
      dispatch(change(form, `submissions[${pathIndex}].markedOn`, null));
      dispatch(change(form, `submissions[${pathIndex}].markedById`, null));
    }
    if (type === "Marked" && prevStatus !== "Submitted" && submissionIndex !== -1) {
      dispatch(change(form, `submissions[${submissionIndex}].markedOn`, today));
    }
  };

  const triggerAsyncChange = (newValue, field, index) => {
    const updatedSubmissions = values.submissions.map((s, sInd) => ({
      ...s,
      [field]: sInd === index ? newValue : s[field]
    }));

    if (field === "submittedOn" && modalProps.length && modalProps[2] !== "all") {
      if (!newValue) {
        updatedSubmissions.splice(index, 1);
      } else {
        const assessment = values.assessments[modalProps[3]];
        const submission = values.submissions.find(s => s.assessmentId === assessment?.id);
        if (!submission && newValue && modalProps[2] !== "all") {
          updatedSubmissions.unshift({
            id: null,
            submittedOn: newValue,
            markedById: null,
            markedOn: null,
            enrolmentId: values.id,
            studentId: values.studentContactId,
            studentName: values.studentName,
            assessmentId: assessment?.id,
            classId: values.courseClassId,
            grade: null
          });
        }
      }
    }
    dispatch(change(form, "submissions", updatedSubmissions.filter(s => s.hasOwnProperty("assessmentId"))));
  };

  const onPickerClose = (dateVal, selectVal) => {
    setModalOpenedBy(null);
    if (modalProps[2] !== "all") {
      const index = Number(modalProps[1]);
      if (modalProps[0] === "Submitted") {
        triggerAsyncChange(dateVal, "submittedOn", index);
      }
      if (modalProps[0] === "Marked") {
        triggerAsyncChange(dateVal, "markedOn", index);
        triggerAsyncChange(selectVal, "markedById", index);
      }
    }

    if (modalProps[2] === "all") {
      dispatch(change(form, "submissions", values.assessments.map(a => {
        const submission = values.submissions.find(s => s.assessmentId === a.id);
        return !dateVal && modalProps[0] === "Submitted" ? null : {
          id: submission?.id,
          submittedOn: modalProps[0] === "Submitted" ? dateVal : submission ? submission.submittedOn : dateVal,
          markedById: submission?.markedById,
          markedOn: modalProps[0] === "Marked" ? dateVal : submission ? submission.markedOn : null,
          enrolmentId: values.id,
          studentId: values.studentContactId,
          studentName: values.studentName,
          assessmentId: a.id,
          grade: submission?.grade,
          classId: values.courseClassId
        };
      }).filter(s => s)));
    }
  };

  const titlePostfix = modalProps[0] === "Marked" ? " and assessor" : "";

  const title = modalProps[0] && (modalProps[2] === "all"
    ? `All assessments ${modalProps[0].toLowerCase()} date`
    : `${modalProps[2]} ${modalProps[0].toLowerCase()} date${titlePostfix}`);

  const modalGradeType = gradingTypes.find(g =>
    g.id === values.assessments[gradeMenuAnchorEl?.attributes?.id?.value?.replace("grade", "")]?.gradingTypeId);

  const modalGradeItems = modalGradeType?.gradingItems;

  return values.assessments && values.assessments.length ? (
    <Grid item={true} xs={12} id={name} container>
      <GradeModal
        gradeMenuAnchorEl={gradeMenuAnchorEl}
        handleGradeMenuClose={handleGradeMenuClose}
        gradedItems={values.assessments}
        onChangeGrade={onChangeGrade}
        onChangeAllGrades={onChangeAllGrades}
        gradeType={modalGradeType}
        gradeItems={modalGradeItems}
      />
      <SubmissionModal
        modalProps={modalProps}
        tutors={values.assessments[modalProps[3]]?.tutors || []}
        title={title}
        onSave={onPickerClose}
        onClose={() => setModalOpenedBy(null)}
        selectDefault={modalProps[2] === "all" ? today : modalProps[0] === "Submitted"
          ? null
          : values.submissions[modalProps[1]]?.markedById}
        dateDefault={modalProps[2] === "all" ? today : modalProps[0] === "Submitted"
          ? values.submissions[modalProps[1]]?.submittedOn || today
          : values.submissions[modalProps[1]]?.markedOn || today}
      />

      <Grid item xs={12} className="mb-2">
        <div className="heading">Assessments Submissions</div>
        <Typography variant="caption" color="error" className="mt-1 shakingError">
          {error}
        </Typography>
      </Grid>

      <Grid container item={true} xs={twoColumn ? 8 : 12} className={classes.tableHeader}>
        <Grid item xs={3} />
        <Grid item xs={3} className={classes.center}>
          <span className="relative">
            Submitted
            <IconButton
              size="small"
              className={classes.hiddenTitleIcon}
              onClick={() => {
                setModalOpenedBy(`Submitted-0-all`);
              }}
            >
              <DateRange color="disabled" fontSize="small" />
            </IconButton>
          </span>
        </Grid>
        <Grid xs={3} className={classes.center}>
          <span className="relative">
            Marked
            <IconButton
              size="small"
              className={classes.hiddenTitleIcon}
              onClick={() => {
                setModalOpenedBy(`Marked-0-all`);
              }}
            >
              <DateRange color="disabled" fontSize="small" />
            </IconButton>
          </span>
        </Grid>
        <Grid xs={3} className={classes.center}>
          Grade
        </Grid>
      </Grid>
      <Grid container item={true} xs={twoColumn ? 8 : 12} className={classes.items}>
        {values.assessments.map((elem, index) => {
          const elemGradeType = gradingTypes?.find(g => g.id === elem.gradingTypeId);
          return (
            <EnrolmentAssessmentStudent
              elem={elem}
              values={values}
              gradeType={elemGradeType}
              gradeItems={elemGradeType?.gradingItems}
              onChangeStatus={onChangeStatus}
              onToggleGrade={onToggleGrade}
              onChangeGrade={onChangeGrade}
              handleGradeMenuOpen={handleGradeMenuOpen}
              triggerAsyncChange={triggerAsyncChange}
              classes={classes}
              index={index}
            />
          );
        })}
      </Grid>
    </Grid>
  ) : null;
};

export default withStyles(styles)(EnrolmentSubmissions) as React.FC<Props>;
