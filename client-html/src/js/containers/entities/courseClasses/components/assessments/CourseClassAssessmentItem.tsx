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
import { differenceInDays } from "date-fns";
import { Assessment, AssessmentClass, CourseClassTutor } from "@api/model";
import { createStyles, withStyles } from "@material-ui/core/styles";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { StyledCheckbox } from "../../../../../common/components/form/form-fields/CheckboxField";
import { validateSingleMandatoryField } from "../../../../../common/utils/validation";
import { stubComponent } from "../../../../../common/utils/common";
import { defaultContactName } from "../../../contacts/utils";
import { State } from "../../../../../reducers/state";
import AssesmentsSubmissionActionsMenu from "./AssessmentsSubnissionActionMenu";
import AssessmentSubmissionIconButton, { AssessmentsSubmissionType } from "./AssessmentSubmissionIconButton";
import { AppTheme } from "../../../../../model/common/Theme";

const styles = (theme: AppTheme) =>
  createStyles({
    rowWrapper: {
      minHeight: "36px",
      padding: "0 8px",
      "&:hover .invisible": {
        visibility: "visible",
      }
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
      justifyContent: "center"
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

  const [studentsForRender, setStudentsForRender] = useState([]);

  useEffect(() => {
    const result = courseClassEnrolments && courseClassEnrolments.reduce((acc, elem) => [...acc, {
      studentName: elem.student,
      sumbittedIndex: 0,
      submittedValue: "Not submitted",
      submittedOn: null,
      markedIndex: 0,
      markedValue: "Not submitted",
      markedOn: null,
      tutorName: '',
      tutorId: tutors ? tutors[0].contactId : 0,
    }], []);

    setStudentsForRender(result);
  }, [courseClassEnrolments]);

  const onChangeStatus = (type: string, student: any) => {
    if (type === "Submitted") {
      const newStudentsForRender = studentsForRender.map(elem => {
        if (student.studentName === elem.studentName) {
          const type = [2, -1].includes(student.sumbittedIndex)
            ? AssessmentsSubmissionType[0] : AssessmentsSubmissionType[student.sumbittedIndex + 1];
          elem.submittedValue = type;
          elem.sumbittedIndex = [2, -1].includes(student.sumbittedIndex) ? 0 : student.sumbittedIndex + 1;
        }

        return elem;
      });
      setStudentsForRender(newStudentsForRender);
    }

    if (type === "Marked") {
      const newStudentsForRender = studentsForRender.map(elem => {
        if (student.studentName === elem.studentName) {
          const type = [2, -1].includes(student.markedIndex)
            ? AssessmentsSubmissionType[0] : AssessmentsSubmissionType[student.markedIndex + 1];
          elem.markedValue = type;
          elem.markedIndex = [2, -1].includes(student.markedIndex) ? 0 : student.markedIndex + 1;
        }

        return elem;
      });
      setStudentsForRender(newStudentsForRender);
    }
  };

  const onSelectTutor = (tutorName: string, studentName:string) => {
    const newStudentsForRender = studentsForRender.map(elem => {
      if (elem.studentName === studentName) {
        elem.tutorName = tutorName;
      }

      return elem;
    });
    setStudentsForRender(newStudentsForRender);
  };

  const tutorsUpdater = useRef<any>();

  const onCodeChange = useCallback((assessment: Assessment) => {
    dispatch(change(form, `${item}.assessmentName`, assessment ? assessment.name : null));
    dispatch(change(form, `${item}.assessmentId`, assessment ? assessment.id : null));
  }, []);

  const onNameChange = useCallback((assessment: Assessment) => {
    dispatch(change(form, `${item}.assessmentCode`, assessment ? assessment.code : null));
    dispatch(change(form, `${item}.assessmentId`, assessment ? assessment.id : null));
  }, []);

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
            Submitted
          </Grid>
          <Grid xs={2} className={classes.center}>
            Marked
          </Grid>
        </Grid>
        <Grid container xs={12} className={classes.items}>
          {studentsForRender.map((elem: any) => (
            <Grid container key={elem.studentName} className={clsx(classes.rowWrapper, "align-items-center d-inline-flex-center")}>
              <Grid item xs={4} className="d-inline-flex-center">
                {elem.studentName}
                &#8194;
                {elem.tutorName ? `(${elem.tutorName})` : ""}

                <AssesmentsSubmissionActionsMenu
                  className="invisible"
                  onChange={onSelectTutor}
                  items={tutors}
                  student={elem}
                />
              </Grid>
              <Grid item xs={2} className={classes.center}>
                <AssessmentSubmissionIconButton
                  status={AssessmentsSubmissionType[elem.sumbittedIndex]}
                  onClick={() => onChangeStatus("Submitted", elem)}
                />
              </Grid>
              <Grid item xs={2} className={classes.center}>
                <AssessmentSubmissionIconButton
                  status={AssessmentsSubmissionType[elem.markedIndex]}
                  onClick={() => onChangeStatus("Marked", elem)}
                />
              </Grid>
            </Grid>
          ))}
        </Grid>
      </Grid>
    </Grid>
  );
};

const mapStateToProps = (state: State) => ({
  courseClassEnrolments: state.courseClass.enrolments,
});

export default connect(mapStateToProps, null)(withStyles(styles)(CourseClassAssessmentItem));
