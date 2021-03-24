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
import { AssessmentSubmission, Enrolment } from "@api/model";
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
import AssessmentSubmissionModal from "../../courseClasses/components/assessments/AssessmentSubmissionModal";
import { getArrayFieldMeta } from "../../../../common/utils/common";
import EntityService from "../../../../common/services/EntityService";
import instantFetchErrorHandler from "../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import { getContactName } from "../../contacts/utils";

interface Props {
  classes?: any;
  values: Enrolment;
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
  const [tutors, setTutors] = useState([]);
  const [allSubmissionsDate, setAllSubmissionsDate] = useState<string>(null);

  useEffect(() => {
    setModalProps(modalOpenedBy ? modalOpenedBy.split("-") : []);
  }, [modalOpenedBy]);

  useEffect(() => {
    if (modalProps[3]) {
      EntityService.getPlainRecords(
        "Contact",
        "firstName,lastName",
        `tutor.assessmentClassTutors.assessmentClass.courseClass.id is ${values.courseClassId} and tutor.assessmentClassTutors.assessmentClass.assessment.id is ${values.assessments[modalProps[3]].id}`
      )
        .then(res => {
          setTutors(res.rows.map(r => ({
            contactId: Number(r.id),
            tutorName: getContactName({ firstName: r.values[0], lastName: r.values[1] })
          })));
        })
        .catch(err => instantFetchErrorHandler(dispatch, err));
    }
  }, [values, modalProps]);

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

  const onPickerClose = () => {
    setModalOpenedBy(null);
    if (modalProps[2] === "all") {
      dispatch(change(form, "submissions", values.assessments.map(a => {
        const submission = values.submissions.find(s => s.assessmentId === a.id);
        return !allSubmissionsDate && modalProps[0] === "Submitted" ? null : {
          id: submission ? submission.id : null,
          submittedOn: modalProps[0] === "Submitted" ? allSubmissionsDate : submission ? submission.submittedOn : null,
          markedById: submission ? submission.markedById : null,
          markedOn: modalProps[0] === "Marked" ? allSubmissionsDate : submission ? submission.markedOn : null,
          enrolmentId: values.id,
          studentId: values.studentContactId,
          studentName: values.studentName,
          assessmentId: a.id
        };
      }).filter(s => s)));
    }
    setAllSubmissionsDate(null);
  };

  const triggerAsyncChange = (event, newValue, previousValue, name) => {
      const { field, index } = getArrayFieldMeta(name);
      if (field === "submittedOn" && modalProps[2] !== "all") {
        setTimeout(() => {
          const updatedSubmissions = [...values.submissions];
          if (!newValue) {
            updatedSubmissions.splice(index, 1);
            dispatch(change(form, "submissions", updatedSubmissions.filter(s => s.hasOwnProperty("assessmentId"))));
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
                assessmentId: assessment?.id
              });
              dispatch(change(form, "submissions", updatedSubmissions.filter(s => s.hasOwnProperty("assessmentId"))));
            }
          }
        }, 500);
      }
  };

  const titlePostfix = modalProps[0] === "Marked" ? " and assessor" : "";

  const title = modalProps[0] && (modalProps[2] === "all"
    ? `All assessments ${modalProps[0].toLowerCase()} date`
    : `${modalProps[2]} ${modalProps[0].toLowerCase()} date${titlePostfix}`);

  return values.assessments && values.assessments.length ? (
    <Grid item={true} xs={12} container>
      <AssessmentSubmissionModal
        name={`submissions[${modalProps[1]}]`}
        modalProps={modalProps}
        tutors={tutors}
        title={title}
        onClose={onPickerClose}
        triggerAsyncChange={triggerAsyncChange}
        allSubmissionsDate={allSubmissionsDate}
        setAllSubmissionsDate={setAllSubmissionsDate}
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
                setAllSubmissionsDate(today);
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
                setAllSubmissionsDate(today);
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
                <Grid item xs={4} className="d-inline-flex-center">
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
                          {submission?.markedById && tutors && (
                            <span>
                              Assessor:
                              {" "}
                              {tutors.find(t => t.contactId === submission.markedById)?.tutorName}
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
