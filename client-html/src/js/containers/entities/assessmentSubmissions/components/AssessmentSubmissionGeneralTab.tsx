/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect, useState } from "react";
import { FormControlLabel, Grid } from "@material-ui/core";
import { change } from "redux-form";
import { AssessmentSubmission } from "@api/model";
import clsx from "clsx";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { getContactName } from "../../contacts/utils";
import { StyledCheckbox } from "../../../../common/components/form/form-fields/CheckboxField";
import EntityService from "../../../../common/services/EntityService";
import instantFetchErrorHandler from "../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import { EditViewProps } from "../../../../model/common/ListView";

const AssessmentSubmissionGeneralTab: React.FC<EditViewProps<AssessmentSubmission>> = props => {
  const {
    dispatch, form, twoColumn, values
  } = props;

  const [tutors, setTutors] = useState([]);

  useEffect(() => {
    EntityService.getPlainRecords(
      "Contact",
      "firstName,lastName",
      `tutor.assessmentClassTutors.assessmentClass.assessmentSubmissions.id is ${values.id}`
    )
      .then(res => {
        setTutors(res.rows.map(r => ({
          contactId: Number(r.id),
          tutorName: getContactName({ firstName: r.values[0], lastName: r.values[1] })
        })));
      })
      .catch(err => instantFetchErrorHandler(dispatch, err));
  }, [values.id]);

  const onChangeMarked = (e: any, value: boolean) => {
    if (value) {
      dispatch(change(form, "markedOn", new Date().toISOString()));
    } else {
      dispatch(change(form, "markedOn", null));
      if (typeof values.markedById === "number") {
        dispatch(change(form, "markedById", null));
      }
    }
  };

  const onAssessorChange = (e, newValue) => {
    if (newValue && !values.markedOn) {
      onChangeMarked(null, true);
    }
  };

  return (
    <Grid container className="pt-3 pl-3 pr-3">
      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          label="Student name"
          name="studentName"
          type="text"
          placeholder={twoColumn ? "Name" : undefined}
          disabled
          fullWidth
        />
      </Grid>
      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          label="Class name"
          name="courseClassName"
          type="text"
          placeholder={twoColumn ? "Class Name" : undefined}
          disabled
          fullWidth
        />
      </Grid>
      <Grid item xs={twoColumn ? 4 : 12}>
        <FormField
          label="Assessmment name"
          name="assessment"
          type="text"
          placeholder={twoColumn ? "Assessmment Name" : undefined}
          disabled
          fullWidth
        />
      </Grid>

      <Grid container className="pb-2">
        <Grid item xs={twoColumn ? 4 : 12} className="d-flex align-items-center">
          <FormControlLabel
            className="checkbox"
            control={<StyledCheckbox checked={values.submittedOn} />}
            label="Submitted"
            disabled
          />
        </Grid>
        <Grid item xs={twoColumn ? 4 : 12} className={clsx("d-flex align-items-center", !twoColumn && "mb-2")}>
          <FormControlLabel
            className="checkbox"
            label="Marked"
            control={<StyledCheckbox checked={values.markedOn} onChange={onChangeMarked} />}
          />
        </Grid>
        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField
            type="select"
            selectValueMark="contactId"
            selectLabelMark="tutorName"
            name="markedById"
            label="Assessor"
            items={tutors}
            onChange={onAssessorChange}
            allowEmpty
          />
        </Grid>
      </Grid>

      <Grid container>
        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField
            label="Submitted on"
            name="submittedOn"
            type="dateTime"
            placeholder={twoColumn ? "Submitted On" : undefined}
            fullWidth
            required
          />
        </Grid>
        <Grid item xs={twoColumn ? 4 : 12}>
          {values.markedOn && (
          <FormField
            label="Marked on"
            name="markedOn"
            type="dateTime"
            placeholder={twoColumn ? "Marked On" : undefined}
            fullWidth
          />
            )}
        </Grid>
      </Grid>
    </Grid>
  );
};

export default AssessmentSubmissionGeneralTab;
