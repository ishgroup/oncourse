/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { AssessmentSubmission, GradingItem, GradingType } from '@api/model';
import DateRange from '@mui/icons-material/DateRange';
import { Grid, IconButton, Typography } from '@mui/material';
import $t from '@t';
import { normalizeNumber } from 'ish-ui';
import React, { useEffect, useMemo, useState } from 'react';
import { arrayInsert, arrayRemove, change, WrappedFieldArrayProps } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { EditViewProps } from '../../../../model/common/ListView';
import { EnrolmentAssessmentExtended, EnrolmentExtended } from '../../../../model/entities/Enrolment';
import GradeModal from '../../courseClasses/components/assessments/GradeModal';
import styles from '../../courseClasses/components/assessments/styles';
import SubmissionModal from '../../courseClasses/components/assessments/SubmissionModal';
import EnrolmentAssessmentStudent from './EnrolmentAssessmentStudent';

interface Props extends EditViewProps<EnrolmentExtended> {
  classes?: any;
  gradingTypes: GradingType[];
  namePrefix?: string;
}

const today = new Date().toISOString();

const EnrolmentSubmissions: React.FC<Props & WrappedFieldArrayProps> = props => {
  const {
    classes, namePrefix = "", values, dispatch, fields: { name }, meta: { error, form }, gradingTypes = []
  } = props;

  const [modalOpenedBy, setModalOpenedBy] = useState<string>(null);
  const [modalProps, setModalProps] = useState<string[]>([]);
  const [gradeMenuAnchorEl, setGradeMenuAnchorEl] = useState(null);

  const filedName = useMemo(() => namePrefix ? `${namePrefix}.submissions` : "submissions", [namePrefix]);

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
    dispatch(change(form, filedName, values.assessments.map(a => {
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
    let updatedSubmissions;

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
      updatedSubmissions = [newSubmission, ...values.submissions];
    } else {
      updatedSubmissions = values.submissions.map((s, index) => {
        if (submissionIndex === index) {
          return { ...s, grade, ...(grade === "" ? { markedById: null, markedOn: null } : { markedOn: s.markedOn || today }) };
        }
        return s;
      });
    }
    dispatch(change(form, filedName, updatedSubmissions));
  };

  const onToggleGrade = (elem: EnrolmentAssessmentExtended, prevGrade: GradingItem) => {
    const submissionIndex = values.submissions.findIndex(s => s.assessmentId === elem.id);
    const gradeItems: GradingItem[] = gradingTypes?.find(g => g.id === elem.gradingTypeId)?.gradingItems;
    let updatedSubmissions;

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
      updatedSubmissions = [newSubmission, ...values.submissions];
    } else {
      updatedSubmissions = values.submissions.map((s, index) => {
        if (submissionIndex === index) {
          const gradeIndex = prevGrade ? gradeItems?.findIndex(g => g.lowerBound === prevGrade.lowerBound) : -1;
          const grade = gradeItems[gradeIndex + 1]?.lowerBound;
          return { ...s, grade, ...(typeof grade === "number" ? { markedOn: s.markedOn || today } : { markedById: null, markedOn: null }) };
        }
        return s;
      });
    }
    dispatch(change(form, filedName, updatedSubmissions));
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
        dispatch(arrayInsert(form, filedName, pathIndex, newSubmission));
      }
    } else if (submissionIndex !== -1 && type === "Submitted") {
      dispatch(arrayRemove(form, filedName, pathIndex));
    }
    if (type === "Marked" && prevStatus === "Submitted") {
      dispatch(change(form, `${filedName}[${pathIndex}].markedOn`, null));
      dispatch(change(form, `${filedName}[${pathIndex}].markedById`, null));
    }
    if (type === "Marked" && prevStatus !== "Submitted" && submissionIndex !== -1) {
      dispatch(change(form, `${filedName}[${submissionIndex}].markedOn`, today));
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
    dispatch(change(form, filedName, updatedSubmissions.filter(s => s.hasOwnProperty("assessmentId"))));
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
      dispatch(change(form, filedName, values.assessments.map(a => {
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
    g.id === values?.assessments[gradeMenuAnchorEl?.attributes?.id?.value?.replace("grade", "")]?.gradingTypeId);

  const modalGradeItems = modalGradeType?.gradingItems;

  const hasGrades = Boolean(values.assessments?.some(a => gradingTypes.some(g => g.id === a.gradingTypeId)));

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
        dispatch={dispatch}
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
        <Typography variant="caption" color="error" className="mt-1 shakingError">
          {error}
        </Typography>
      </Grid>

      <Grid container item xs={12} className={classes.tableHeader}>
        <Grid item xs={3} />
        <Grid item xs={hasGrades ? 3 : 6} className={classes.center}>
          <span className="relative">
            {$t('submitted')}
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

        {hasGrades
          && (
          <>
            <Grid xs={3} className={classes.center}>
              <span className="relative">
                {$t('marked')}
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
              {$t('grade')}
            </Grid>
          </>
      )}

      </Grid>
      <Grid container item xs={12} className={classes.items}>
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
              hasGrades={hasGrades}
              dispatch={dispatch}
              index={index}
            />
          );
        })}
      </Grid>
    </Grid>
  ) : null;
};

export default withStyles(EnrolmentSubmissions, styles) as React.FC<Props>;