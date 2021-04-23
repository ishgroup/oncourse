/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { useEffect, useState } from "react";
import { Tooltip } from "@material-ui/core";
import { AssessmentSubmission } from "@api/model";
import Grid from "@material-ui/core/Grid";
import IconButton from "@material-ui/core/IconButton";
import DateRange from "@material-ui/icons/DateRange";
import clsx from "clsx";
import { format } from "date-fns";
import { withStyles } from "@material-ui/core/styles";
import { Dispatch } from "redux";
import { arrayInsert, arrayRemove, change } from "redux-form";
import AssessmentSubmissionIconButton from "../../courseClasses/components/assessments/AssessmentSubmissionIconButton";
import { III_DD_MMM_YYYY, YYYY_MM_DD_MINUSED } from "../../../../common/utils/dates/format";
import styles from "../../courseClasses/components/assessments/styles";
import SubmissionModal from "../../courseClasses/components/assessments/SubmissionModal";
import { EnrolmentExtended } from "../../../../model/entities/Enrolment";

interface Props {
  classes?: any;
  values: EnrolmentExtended;
  dispatch: Dispatch;
  form: string;
}

const today = format(new Date(), YYYY_MM_DD_MINUSED);

const EnrolmentSubmissions: React.FC<Props> = props => {
  const {
    classes, values, form, dispatch
  } = props;

  const [modalOpenedBy, setModalOpenedBy] = useState<string>(null);
  const [modalProps, setModalProps] = useState<string[]>([]);

  useEffect(() => {
    setModalProps(modalOpenedBy ? modalOpenedBy.split("-") : []);
  }, [modalOpenedBy]);

  const onChangeStatus = (type, submissionIndex, prevStatus, assessment, index) => {
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
          assessmentId: assessment.id
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
    if (prevStatus !== "Submitted") {
      setModalOpenedBy(`${type}-${pathIndex}-${assessment.name}-${index}`);
    }
  };

  const triggerAsyncChange = (newValue, field, index) => {
    const updatedSubmissions = values.submissions.map((s, sInd) => ({
      ...s,
      [field]: sInd === index ? newValue : s[field]
    }));

    if (field === "submittedOn" && modalProps[2] !== "all") {
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
          grade: submission?.grade
        };
      }).filter(s => s)));
    }
  };

  const titlePostfix = modalProps[0] === "Marked" ? " and assessor" : "";

  const title = modalProps[0] && (modalProps[2] === "all"
    ? `All assessments ${modalProps[0].toLowerCase()} date`
    : `${modalProps[2]} ${modalProps[0].toLowerCase()} date${titlePostfix}`);

  return values.assessments && values.assessments.length ? (
    <Grid item={true} xs={12} container>
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
      <div className="heading">Assessments Submissions</div>
      <Grid container item={true} xs={12} className={classes.tableHeader}>
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
      </Grid>
      <Grid container item={true} xs={12} className={classes.items}>
        {values.assessments.map((elem, index) => {
            const submissionIndex = values.submissions.findIndex(s => s.assessmentId === elem.id);
            const submission = submissionIndex !== -1 && values.submissions[submissionIndex];
            const submitStatus = submission && submission.submittedOn ? "Submitted" : "Not submitted";
            const markedStatus = submission && submission.markedOn ? "Submitted" : "Not submitted";

            const submittedContent = (
              <div className="d-flex relative">
                <AssessmentSubmissionIconButton
                  status={submitStatus}
                  onClick={() => onChangeStatus("Submitted", submissionIndex, submitStatus, elem, index)}
                />
                {submitStatus === "Submitted" && (
                  <IconButton
                    size="small"
                    className={classes.hiddenIcon}
                    onClick={() => setModalOpenedBy(`Submitted-${submissionIndex}-${elem.name}-${index}`)}
                  >
                    <DateRange color="disabled" fontSize="small" />
                  </IconButton>
                )}
              </div>
            );

            const markedContent = (
              <div className="d-flex relative">
                <AssessmentSubmissionIconButton
                  status={markedStatus}
                  onClick={() => onChangeStatus("Marked", submissionIndex, markedStatus, elem, index)}
                />
                {markedStatus === "Submitted" && (
                  <IconButton
                    size="small"
                    className={classes.hiddenIcon}
                    onClick={() => setModalOpenedBy(`Marked-${submissionIndex}-${elem.name}-${index}`)}
                  >
                    <DateRange color="disabled" fontSize="small" />
                  </IconButton>
                )}
              </div>
            );

            return (
              <Grid container key={index} className={clsx(classes.rowWrapper, "align-items-center d-inline-flex-center")}>
                <Grid item xs={4} className="d-inline-flex-center pl-1">
                  {elem.name}
                </Grid>
                <Grid item xs={2} className={classes.center}>
                  {submitStatus === "Submitted"
                    ? (
                      <Tooltip
                        title={(
                          <span>
                            Submitted date:
                            {" "}
                            {submission && format(new Date(submission.submittedOn), III_DD_MMM_YYYY)}
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
                  {markedStatus === "Submitted" ? (
                    <Tooltip
                      title={(
                        <span>
                          Marked date:
                          {" "}
                          {submission && format(new Date(submission.markedOn), III_DD_MMM_YYYY)}
                          <br />
                          {submission?.markedById && Boolean(elem.tutors?.length) && (
                            <span>
                              Assessor:
                              {" "}
                              {elem.tutors.find(t => t.contactId === submission.markedById)?.tutorName}
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
  ) : null;
};

export default withStyles(styles)(EnrolmentSubmissions);
