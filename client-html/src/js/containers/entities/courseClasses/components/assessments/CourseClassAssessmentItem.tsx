/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo, useRef } from "react";
import { Dispatch } from "redux";
import { change, Field } from "redux-form";
import Grid from "@material-ui/core/Grid";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import { differenceInDays } from "date-fns";
import { Assessment, AssessmentClass, CourseClassTutor } from "@api/model";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { StyledCheckbox } from "../../../../../common/components/form/form-fields/CheckboxField";
import { validateSingleMandatoryField } from "../../../../../common/utils/validation";
import { AnyArgFunction, StringArgFunction } from "../../../../../model/common/CommonFunctions";
import { stubComponent } from "../../../../../common/utils/common";
import { defaultContactName } from "../../../contacts/utils";

interface Props {
  form: string;
  item: string;
  dispatch: Dispatch;
  tutors: CourseClassTutor[];
  twoColumn?: boolean;
  row?: AssessmentClass;
  rows?: AssessmentClass[];
  assessments?: AssessmentClass[];
  assessmentsLoading?: boolean;
  assessmentsRowsCount?: number;
  getAssessments?: AnyArgFunction;
  setAssessmentSearch?: StringArgFunction;
  clearAssessmentItems?: AnyArgFunction;
}

const CourseClassAssessmentItem: React.FC<Props> = props => {
  const {
    form,
    row,
    rows,
    item,
    tutors,
    twoColumn,
    dispatch
  } = props;

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
  );
};

export default CourseClassAssessmentItem;
