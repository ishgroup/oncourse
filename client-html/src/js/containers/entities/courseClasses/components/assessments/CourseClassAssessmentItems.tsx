/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useMemo, useRef, useState
} from "react";
import { Dispatch } from "redux";
import { change, Field } from "redux-form";
import {
  Grid, FormControlLabel, IconButton, Typography
} from "@material-ui/core";
import { differenceInDays, format } from "date-fns";
import {
  AssessmentClass, AssessmentSubmission, CourseClassTutor, GradingItem, GradingType
} from "@api/model";
import { withStyles } from "@material-ui/core/styles";
import { DateRange, ExpandMore, Edit } from "@material-ui/icons";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { StyledCheckbox } from "../../../../../common/components/form/form-fields/CheckboxField";
import { validateSingleMandatoryField } from "../../../../../common/utils/validation";
import { stubComponent } from "../../../../../common/utils/common";
import { defaultContactName } from "../../../contacts/utils";
import { AssessmentsSubmissionType } from "./AssessmentSubmissionIconButton";
import SubmissionModal from "./SubmissionModal";
import { YYYY_MM_DD_MINUSED } from "../../../../../common/utils/dates/format";
import styles from "./styles";
import { normalizeNumber } from "../../../../../common/utils/numbers/numbersNormalizing";
import CourseClassAssessmentStudent from "./CourseClassAssessmentStudent";
import GradeModal from "./GradeModal";

interface Props {
  form: string;
  item: string;
  dispatch: Dispatch;
  tutors: CourseClassTutor[];
  classes,
  courseClassEnrolments?: any[],
  twoColumn?: boolean;
  row?: AssessmentClass;
  rows?: AssessmentClass[];
  gradingTypes?: GradingType[];
}

export interface StudentForRender {
  submittedValue: AssessmentsSubmissionType;
  markedValue: AssessmentsSubmissionType;
  studentName: string;
  studentId: string;
  submission: AssessmentSubmission;
  enrolmentId: number;
  submissionIndex: number;
}

type TickType = "Submitted" | "Marked";

const today = format(new Date(), YYYY_MM_DD_MINUSED);

const CourseClassAssessmentItems: React.FC<Props> = props => {
  const {
    form,
    row,
    rows,
    classes,
    item,
    tutors,
    twoColumn,
    courseClassEnrolments,
    gradingTypes,
    dispatch
  } = props;

  const [studentsForRender, setStudentsForRender] = useState<StudentForRender[]>([]);
  const [modalOpenedBy, setModalOpenedBy] = useState<string>(null);
  const [gradeMenuAnchorEl, setGradeMenuAnchorEl] = useState(null);

  const modalProps = modalOpenedBy ? modalOpenedBy.split("-") : [];

  const tutorsUpdater = useRef<any>();
  const submissionUpdater = useRef<(s: AssessmentSubmission[]) => void>();

  const submissionTutors = useMemo(() => tutors.filter(t => row.contactIds.includes(t.contactId)), [tutors, row.contactIds]);

  const gradeType: GradingType = useMemo(() =>
    gradingTypes?.find(g => g.id === row.gradingTypeId),
    [row.gradingTypeId, gradingTypes]);

  const gradeItems: GradingItem[] = useMemo(() =>
      gradeType?.gradingItems,
    [gradeType]);

  useEffect(() => {
    const result = courseClassEnrolments && courseClassEnrolments.reduce((acc, elem) => {
      const submissionIndex = row.submissions ? row.submissions.findIndex(s => s.enrolmentId === Number(elem.id)) : -1;
      const submission = submissionIndex !== -1 ? row.submissions[submissionIndex] : null;

      return [...acc, {
        studentName: elem.student,
        studentId: elem.contactId,
        submittedValue: submission && submission.submittedOn ? "Submitted" : "Not submitted",
        markedValue: submission && submission.markedOn ? "Submitted" : "Not submitted",
        enrolmentId: Number(elem.id),
        submissionIndex,
        submission
      } as StudentForRender];
    }, []);

    result.sort((a, b) => (a.studentName > b.studentName ? 1 : -1));

    setStudentsForRender(result);
  }, [courseClassEnrolments, row.submissions]);

  const handleGradeMenuClose = () => {
    setGradeMenuAnchorEl(null);
  };

  const handleGradeMenuOpen = e => {
    setGradeMenuAnchorEl(e.currentTarget);
  };

  const triggerAsyncChange = (newValue, field, index) => {
    const updatedSubmissions = row.submissions.map((s, sInd) => ({
      ...s,
      [field]: sInd === index ? newValue : s[field]
    }));
    if (field === "submittedOn") {
      if (!newValue) {
        updatedSubmissions.splice(index, 1);
      }
      const submission = row.submissions[index];
      if (!submission && newValue && modalProps.length && modalProps[2] !== "all") {
        const elem = studentsForRender[modalProps[3]];
        updatedSubmissions.unshift({
          id: null,
          submittedOn: newValue,
          markedById: null,
          markedOn: null,
          enrolmentId: Number(elem.enrolmentId),
          studentId: Number(elem.studentId),
          studentName: elem.studentName,
          assessmentId: row.assessmentId,
          grade: null
        });
      }
    }
    submissionUpdater.current(updatedSubmissions.filter(s => s.hasOwnProperty("enrolmentId")));
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
      submissionUpdater.current(courseClassEnrolments.map(elem => {
        const submission = row.submissions.find(s => s.enrolmentId === Number(elem.id));
        return !dateVal && modalProps[0] === "Submitted" ? null : {
          id: submission?.id,
          submittedOn: modalProps[0] === "Submitted" ? dateVal : submission ? submission.submittedOn : dateVal,
          markedById: submission?.markedById,
          markedOn: modalProps[0] === "Marked" ? dateVal : submission ? submission.markedOn : null,
          enrolmentId: Number(elem.id),
          studentId: Number(elem.contactId),
          studentName: elem.student,
          assessmentId: row.assessmentId,
          grade: submission?.grade
        };
      }).filter(s => s));
    }
  };

  const onChangeStatus = (type: TickType, student: StudentForRender) => {
    studentsForRender.forEach((elem => {
      if (student.studentId === elem.studentId) {
        const submissionIndex = row.submissions ? row.submissions.findIndex(s => s.enrolmentId === elem.enrolmentId) : -1;
        if (elem.submittedValue !== "Submitted") {
          if (submissionIndex === -1) {
            const newSubmission: AssessmentSubmission = {
              id: null,
              submittedOn: today,
              markedById: type === "Marked" && submissionTutors && submissionTutors.length ? submissionTutors[0].contactId : null,
              markedOn: type === "Marked" ? today : null,
              enrolmentId: Number(elem.enrolmentId),
              studentId: Number(elem.studentId),
              studentName: elem.studentName,
              assessmentId: row.assessmentId,
              grade: null
            };

            submissionUpdater.current([newSubmission, ...row.submissions]);
          }
        } else if (submissionIndex !== -1 && type === "Submitted") {
          const updatedSubmissions = [...row.submissions];
          updatedSubmissions.splice(submissionIndex, 1);
          submissionUpdater.current(updatedSubmissions);
        }
        if (type === "Marked" && elem.markedValue === "Submitted") {
          const updatedSubmissions = row.submissions.map((s, index) => {
            if (submissionIndex === index) {
              return { ...s, markedOn: null, markedById: null };
            }
            return s;
          });
          submissionUpdater.current(updatedSubmissions);
        }
        if (type === "Marked" && elem.markedValue !== "Submitted" && submissionIndex !== -1) {
          const updatedSubmissions = row.submissions.map((s, index) => {
            if (submissionIndex === index) {
              return { ...s, markedOn: today };
            }
            return s;
          });
          submissionUpdater.current(updatedSubmissions);
          dispatch(change(form, `${item}.submissions[${submissionIndex}].markedOn`, today));
        }
      }
    }));
  };

  const onChangeAllGrades = (value: number) => {
    submissionUpdater.current(courseClassEnrolments.map(elem => {
      const submission = row.submissions.find(s => s.enrolmentId === Number(elem.id));
      return {
        id: submission ? submission.id : null,
        submittedOn: submission ? submission.submittedOn : null,
        markedById: submission ? submission.markedById : null,
        markedOn: submission ? submission.markedOn : null,
        enrolmentId: Number(elem.id),
        studentId: Number(elem.contactId),
        studentName: elem.student,
        assessmentId: row.assessmentId,
        grade: value
      };
    }).filter(s => s));
  };

  const onChangeGrade = (value: number, elem: StudentForRender) => {
    const grade = normalizeNumber(value);
    if (elem.submissionIndex === -1) {
      const newSubmission: AssessmentSubmission = {
        id: null,
        submittedOn: today,
        markedById: submissionTutors && submissionTutors.length ? submissionTutors[0].contactId : null,
        markedOn: today,
        enrolmentId: Number(elem.enrolmentId),
        studentId: Number(elem.studentId),
        studentName: elem.studentName,
        assessmentId: row.assessmentId,
        grade
      };
      submissionUpdater.current([newSubmission, ...row.submissions]);
    } else {
      const updatedSubmissions = row.submissions.map((s, index) => {
        if (elem.submissionIndex === index) {
          return { ...s, grade };
        }
        return s;
      });
      submissionUpdater.current(updatedSubmissions);
    }
  };

  const onToggleGrade = (elem: StudentForRender, grade: GradingItem) => {
    if (elem.submissionIndex === -1) {
      const newSubmission: AssessmentSubmission = {
        id: null,
        submittedOn: today,
        markedById: submissionTutors && submissionTutors.length ? submissionTutors[0].contactId : null,
        markedOn: today,
        enrolmentId: Number(elem.enrolmentId),
        studentId: Number(elem.studentId),
        studentName: elem.studentName,
        assessmentId: row.assessmentId,
        grade: gradeItems ? gradeItems[0]?.lowerBound : null
      };
      submissionUpdater.current([newSubmission, ...row.submissions]);
    } else {
      const updatedSubmissions = row.submissions.map((s, index) => {
        if (elem.submissionIndex === index) {
          const gradeIndex = grade ? gradeItems?.findIndex(g => g.lowerBound === grade.lowerBound) : -1;
          return { ...s, grade: gradeItems[gradeIndex + 1]?.lowerBound };
        }
        return s;
      });
      submissionUpdater.current(updatedSubmissions);
    }
  };

  const onAssessmentChange = assessment => {
    dispatch(change(form, `${item}.assessmentId`, assessment?.id));
    dispatch(change(form, `${item}.gradingTypeId`, assessment && assessment["gradingType.id"] && Number(assessment["gradingType.id"])));
    dispatch(change(form, `${item}.submissions`, row.submissions.map(s => ({ ...s, assessmentId: assessment.id }))));
  };

  const onCodeChange = assessment => {
    dispatch(change(form, `${item}.assessmentName`, assessment?.name));
    onAssessmentChange(assessment);
  };

  const onNameChange = assessment => {
    dispatch(change(form, `${item}.assessmentCode`, assessment?.code));
    onAssessmentChange(assessment);
  };

  const validateDueDate = useCallback(
    value => (differenceInDays(new Date(row.releaseDate), new Date(value)) > 0
        ? "Due date can't be before release date"
        : undefined),
    [row.releaseDate]
  );

  const onTutorChange = useCallback(
    (checked, tutor: CourseClassTutor) => {
      const updated = [...row.contactIds];

      if (checked) {
        updated.push(tutor.contactId);
      } else {
        updated.splice(updated.indexOf(tutor.contactId), 1);
      }
      tutorsUpdater.current(updated);
    },
    [row.contactIds]
  );

  const tutorsFieldStub = useCallback(props => {
    tutorsUpdater.current = props.input.onChange;
    return stubComponent();
  }, []);

  const submissionFieldStub = useCallback(props => {
    submissionUpdater.current = props.input.onChange;
    return stubComponent();
  }, []);

  const tutorsCheckboxes = useMemo(() => {
    const idsSet = new Set([]);

    return tutors.map(t => {
      const rendered = idsSet.has(t.contactId) ? null : (
        <div key={t.id}>
          <FormControlLabel
            className="checkbox"
            control={(
              <StyledCheckbox
                checked={row.contactIds.includes(t.contactId)}
                onChange={(e, v) => onTutorChange(v, t)}
                color="secondary"
              />
            )}
            label={defaultContactName(t.tutorName)}
          />
        </div>
      );
      idsSet.add(t.contactId);
      return rendered;
    });
  }, [tutors, row.contactIds]);

  const rowsIds = rows.map(r => r.assessmentId).filter(r => r);

  const assessmentAql = `active is true${rowsIds.length ? ` and id not (${rowsIds.toString()})` : ""}`;

  const assessmentAqlCols = "code,name,gradingType.id";

  const titlePostfix = modalProps[0] === "Marked" ? " and assessor" : "";

  const title = modalProps[0] && (modalProps[2] === "all"
    ? `All students ${modalProps[0].toLowerCase()} date`
    : `${modalProps[2]} ${modalProps[0].toLowerCase()} date${titlePostfix}`);

  return (
    <Grid container>
      <Grid item={true} xs={12} container className="pb-3">
        <Grid item xs={twoColumn ? 8 : 12} container>
          <Grid item xs={twoColumn ? 6 : 12}>
            <GradeModal
              gradeMenuAnchorEl={gradeMenuAnchorEl}
              handleGradeMenuClose={handleGradeMenuClose}
              gradedItems={studentsForRender}
              onChangeGrade={onChangeGrade}
              onChangeAllGrades={onChangeAllGrades}
              gradeItems={gradeItems}
              gradeType={gradeType}
            />
            <SubmissionModal
              modalProps={modalProps}
              tutors={submissionTutors}
              title={title}
              onSave={onPickerClose}
              onClose={() => setModalOpenedBy(null)}
              selectDefault={modalProps[2] === "all" ? today : modalProps[0] === "Submitted"
                ? null
                : row?.submissions[modalProps[1]]?.markedById}
              dateDefault={modalProps[2] === "all" ? today : modalProps[0] === "Submitted"
                ? row?.submissions[modalProps[1]]?.submittedOn || today
                : row?.submissions[modalProps[1]]?.markedOn || today}
            />
            <Field name={`${item}.submissions`} component={submissionFieldStub} />
            <Field name={`${item}.contactIds`} component={tutorsFieldStub} />
            <FormField
              type="remoteDataSearchSelect"
              entity="Assessment"
              aqlFilter={assessmentAql}
              aqlColumns={assessmentAqlCols}
              name={`${item}.assessmentCode`}
              label="Code"
              selectValueMark="code"
              selectLabelMark="code"
              onInnerValueChange={onCodeChange}
              rowHeight={36}
              fullWidth
              required
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="remoteDataSearchSelect"
              entity="Assessment"
              aqlFilter={assessmentAql}
              aqlColumns={assessmentAqlCols}
              name={`${item}.assessmentName`}
              label="Name"
              selectValueMark="name"
              selectLabelMark="name"
              onInnerValueChange={onNameChange}
              rowHeight={36}
              fullWidth
              required
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="dateTime"
              name={`${item}.releaseDate`}
              label="Release date"
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="dateTime"
              name={`${item}.dueDate`}
              label="Due date"
              validate={[validateSingleMandatoryField, validateDueDate]}
            />
          </Grid>
        </Grid>

        <Grid container item xs={twoColumn ? 4 : 12}>
          <Grid item xs={12}>
            <div>
              <div className="heading">Assessors</div>
              {tutorsCheckboxes}
            </div>
          </Grid>
        </Grid>
      </Grid>

      {typeof row.id === "number" ? (
        <Grid item={true} xs={12} container className="pb-3">
          <div className="heading">Assessment Submission</div>
          <Grid container xs={12} className={classes.tableHeader}>
            <Grid item xs={4} />
            <Grid item xs={2} className={classes.center}>
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
            <Grid xs={2} className={classes.center}>
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
            <Grid xs={2} className={classes.center}>
              <span className="relative">
                Grade
                <IconButton
                  size="small"
                  className={classes.hiddenTitleIcon}
                  onClick={handleGradeMenuOpen}
                  id="allGrades"
                  style={gradeType?.entryType === "number" ? { bottom: "unset" } : undefined}
                >
                  {gradeType?.entryType === "choice list"
                      ? <ExpandMore color="disabled" fontSize="small" />
                      : <Edit color="disabled" className="editInPlaceIcon" />}
                </IconButton>
              </span>
            </Grid>
          </Grid>
          <Grid container xs={12} className={classes.items}>
            {studentsForRender.map((elem, index) => (
              <CourseClassAssessmentStudent
                elem={elem}
                index={index}
                onChangeStatus={onChangeStatus}
                onToggleGrade={onToggleGrade}
                onChangeGrade={onChangeGrade}
                handleGradeMenuOpen={handleGradeMenuOpen}
                triggerAsyncChange={triggerAsyncChange}
                classes={classes}
                gradeType={gradeType}
                gradeItems={gradeItems}
                tutors={tutors.filter(t => row.contactIds.includes(t.contactId))}
              />
            ))}
          </Grid>
        </Grid>
      ) : (
        <div>
          <div className="heading">Assessment Submission</div>
          <Typography component="div" className="mt-2 mb-3" variant="caption" color="textSecondary">
            Please save new assessment before editinig submissions
          </Typography>
        </div>
      )}
    </Grid>
  );
};

export default withStyles(styles)(CourseClassAssessmentItems);
