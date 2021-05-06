/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
 useCallback, useEffect, useMemo, useRef, useState
} from "react";
import clsx from "clsx";
import { Dispatch } from "redux";
import { change, Field } from "redux-form";
import { connect } from "react-redux";
import Grid from "@material-ui/core/Grid";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import { differenceInDays, format } from "date-fns";
import {
 Assessment, AssessmentClass, AssessmentSubmission, CourseClassTutor
} from "@api/model";
import { createStyles, withStyles } from "@material-ui/core/styles";
import IconButton from "@material-ui/core/IconButton";
import DateRange from "@material-ui/icons/DateRange";
import { Tooltip } from "@material-ui/core";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { StyledCheckbox } from "../../../../../common/components/form/form-fields/CheckboxField";
import { validateSingleMandatoryField } from "../../../../../common/utils/validation";
import { stubComponent } from "../../../../../common/utils/common";
import { defaultContactName } from "../../../contacts/utils";
import { State } from "../../../../../reducers/state";
import AssessmentSubmissionIconButton, {
  AssessmentsSubmissionType
} from "./AssessmentSubmissionIconButton";
import { AppTheme } from "../../../../../model/common/Theme";
import AssessmentSubmissionModal from "./AssessmentSubmissionModal";
import { III_DD_MMM_YYYY, YYYY_MM_DD_MINUSED } from "../../../../../common/utils/dates/format";

const styles = (theme: AppTheme) =>
  createStyles({
    rowWrapper: {
      minHeight: "36px",
      padding: "0 8px",
    },
    items: {
      marginLeft: -8,
      marginRight: -8,
      "& > div:nth-child(even)": {
        backgroundColor: theme.table.contrastRow.light
      },
      "&:first-child": {
        marginTop: 0
      }
    },
    tableHeader: {
      marginLeft: -8,
      marginRight: -8,
    },
    center: {
      display: "flex",
      justifyContent: "center",
      "&:hover $hiddenIcon, &:hover $hiddenTitleIcon": {
        visibility: "visible",
      }
    },
    hiddenIcon: {
      visibility: "hidden",
      position: "absolute",
      transform: "translate(calc(100% + 2px),-2px)",
      padding: 0
    },
    hiddenTitleIcon: {
      visibility: "hidden",
      position: "absolute",
      bottom: "-3px"
    }
  });

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
}

interface StudentForRender {
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

const CourseClassAssessmentItem: React.FC<Props> = props => {
  const {
    form,
    row,
    rows,
    classes,
    item,
    tutors,
    twoColumn,
    courseClassEnrolments,
    dispatch
  } = props;

  const [studentsForRender, setStudentsForRender] = useState<StudentForRender[]>([]);
  const [modalOpenedBy, setModalOpenedBy] = useState<string>(null);

  const modalProps = modalOpenedBy ? modalOpenedBy.split("-") : [];

  const tutorsUpdater = useRef<any>();
  const submissionUpdater = useRef<any>();

  useEffect(() => {
    const result = courseClassEnrolments && courseClassEnrolments.reduce((acc, elem) => {
      const submissionIndex = row.submissions.findIndex(s => s.enrolmentId === Number(elem.id));
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
    setStudentsForRender(result);
  }, [courseClassEnrolments, row.submissions]);

  const onPickerClose = () => {
    setModalOpenedBy(null);
    if (modalProps[2] === "all" && row.submissions.length) {
      submissionUpdater.current(courseClassEnrolments.map(elem => ({
        id: null,
        submittedOn: row.submissions[0].submittedOn,
        submittedById: row.submissions[0].submittedById,
        markedOn: row.submissions[0].markedOn,
        enrolmentId: Number(elem.id),
        studentId: Number(elem.contactId),
        studentName: elem.student
      })));
    }
  };

  const onChangeStatus = (type: TickType, student: StudentForRender) => {
    studentsForRender.forEach((elem => {
      if (student.studentId === elem.studentId) {
        const submissionIndex = row.submissions.findIndex(s => s.enrolmentId === elem.enrolmentId);
        let pathIndex = submissionIndex;

        if (elem.submittedValue !== "Submitted") {
          if (submissionIndex === -1) {
            pathIndex = 0;
            const newSubmission: AssessmentSubmission = {
              id: null,
              submittedOn: today,
              submittedById: type === "Marked" && tutors ? tutors[0].contactId : null,
              markedOn: type === "Marked" ? today : null,
              enrolmentId: Number(elem.enrolmentId),
              studentId: Number(elem.studentId),
              studentName: elem.studentName
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
              return { ...s, markedOn: null, submittedById: null };
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
        if ((type === "Marked" && elem.markedValue !== "Submitted")
          || (type === "Submitted" && elem.submittedValue !== "Submitted") ) {
          setModalOpenedBy(`${type}-${pathIndex}-${elem.studentName}`);
        }
      }
    }));
  };

  const onCodeChange = (assessment: Assessment) => {
    dispatch(change(form, `${item}.assessmentName`, assessment ? assessment.name : null));
    dispatch(change(form, `${item}.assessmentId`, assessment ? assessment.id : null));
  };

  const onNameChange = (assessment: Assessment) => {
    dispatch(change(form, `${item}.assessmentCode`, assessment ? assessment.code : null));
    dispatch(change(form, `${item}.assessmentId`, assessment ? assessment.id : null));
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
        <div>
          <FormControlLabel
            key={t.id}
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

  return (
    <Grid container>
      <Grid container className="pb-3">
        <Grid item xs={twoColumn ? 8 : 12} container>
          <Grid item xs={twoColumn ? 6 : 12}>
            <AssessmentSubmissionModal
              item={item}
              modalProps={modalProps}
              tutors={tutors}
              onClose={onPickerClose}
            />
            <Field name={`${item}.submissions`} component={submissionFieldStub} />
            <Field name={`${item}.contactIds`} component={tutorsFieldStub} />
            <FormField
              type="remoteDataSearchSelect"
              entity="Assessment"
              aqlFilter={assessmentAql}
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

      <Grid container className="pb-3">
        <div className="heading">Assessment Submission</div>
        <Grid container xs={12} className={classes.tableHeader}>
          <Grid item xs={4} />
          <Grid item xs={2} className={classes.center}>
            <span className="relative">
              Submitted
              <IconButton
                size="small"
                className={classes.hiddenTitleIcon}
                onClick={() => setModalOpenedBy(`Submitted-0-all`)}
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
                onClick={() => setModalOpenedBy(`Marked-0-all`)}
              >
                <DateRange color="disabled" fontSize="small" />
              </IconButton>
            </span>
          </Grid>
        </Grid>
        <Grid container xs={12} className={classes.items}>
          {studentsForRender.map((elem, index) => {
            const submittedContent = (
              <div className="d-flex">
                <AssessmentSubmissionIconButton
                  status={elem.submittedValue}
                  onClick={() => onChangeStatus("Submitted", elem)}
                />
                {elem.submittedValue === "Submitted" && (
                <IconButton
                  size="small"
                  className={classes.hiddenIcon}
                  onClick={() => setModalOpenedBy(`Submitted-${elem.submissionIndex}-${elem.studentName}`)}
                >
                  <DateRange color="disabled" fontSize="small" />
                </IconButton>
              )}
              </div>
            );

            const markedContent = (
              <div className="d-flex">
                <AssessmentSubmissionIconButton
                  status={elem.markedValue}
                  onClick={() => onChangeStatus("Marked", elem)}
                />
                {elem.markedValue === "Submitted" && (
                <IconButton
                  size="small"
                  className={classes.hiddenIcon}
                  onClick={() => setModalOpenedBy(`Marked-${elem.submissionIndex}-${elem.studentName}`)}
                >
                  <DateRange color="disabled" fontSize="small" />
                </IconButton>
              )}
              </div>
              );

            return (
              <Grid container key={index} className={clsx(classes.rowWrapper, "align-items-center d-inline-flex-center")}>
                <Grid item xs={4} className="d-inline-flex-center">
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
                          {elem?.submission?.submittedById && tutors && (
                            <span>
                              Assessor:
                              {" "}
                              {tutors.find(t => t.contactId === elem.submission.submittedById).tutorName}
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
              </Grid>
            );
          })}
        </Grid>
      </Grid>
    </Grid>
  );
};

const mapStateToProps = (state: State) => ({
  courseClassEnrolments: state.courseClass.enrolments,
});

export default connect(mapStateToProps)(withStyles(styles)(CourseClassAssessmentItem));
