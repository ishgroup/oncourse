/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { FormControlLabel, Grid } from "@material-ui/core";
import { change } from "redux-form";
import { format } from "date-fns";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { contactLabelCondition, defaultContactName } from "../../contacts/utils";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { StyledCheckbox } from "../../../../common/components/form/form-fields/CheckboxField";

const AssessmentSubmissionGeneralTab = props => {
  const {
    dispatch, form, twoColumn, values
  } = props;

  const onChangeMarked = (e: any, value: boolean) => {
    if (value) {
      dispatch(change(form, "markedOn", format(new Date(), 'yyyy-MM-dd')));
    } else {
      dispatch(change(LIST_EDIT_VIEW_FORM_NAME, "markedOn", null));
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
            control={<FormField type="checkbox" name="submittedOn" color="secondary" fullWidth />}
            label="Submitted"
            disabled
          />
        </Grid>
        <Grid item xs={twoColumn ? 4 : 12} className="d-flex align-items-center">
          <FormControlLabel
            className="checkbox"
            label="Marked"
            control={<StyledCheckbox checked={!!values.markedOn} onChange={onChangeMarked} />}
          />
        </Grid>
        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField
            type="remoteDataSearchSelect"
            entity="Contact"
            aqlFilter={"tutor.assessmentClassTutors.assessmentClass.assessmentSubmissions.id = " + values.id}
            name="markedById"
            label="Assessor"
            selectValueMark="id"
            selectLabelMark="id"
            selectLabelCondition={contactLabelCondition}
            defaultDisplayValue={values && defaultContactName(values.tutorName)}
            itemRenderer={ContactSelectItemRenderer}
            rowHeight={55}
          />
        </Grid>
      </Grid>

      <Grid container>
        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField
            label="Submitted on"
            name="submittedOn"
            type="date"
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
            type="date"
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