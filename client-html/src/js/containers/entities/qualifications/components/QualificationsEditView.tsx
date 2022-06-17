/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { FormControlLabel } from "@mui/material";
import { QualificationType } from "@api/model";
import Grid from "@mui/material/Grid";
import FormField from "../../../../common/components/form/formFields/FormField";
import { sortDefaultSelectItems } from "../../../../common/utils/common";

const qualificationTypes = Object.keys(QualificationType)
  .filter(i => Number.isNaN(Number(i)))
  .map(j => ({
    label: j,
    value: j
  }));

qualificationTypes.sort(sortDefaultSelectItems);

const QualificationsEditView: React.FC<any> = (props: any) => {
  const {
    isNew, values, updateDeleteCondition, twoColumn
  } = props;

  const [isCustom, setIsCustom] = React.useState<boolean>(false);

  React.useEffect(() => {
    const isCustomDelete = values && values.isCustom === true;
    setIsCustom(isCustomDelete);
    if (updateDeleteCondition) {
      updateDeleteCondition(isCustomDelete);
    }
  }, [values && values.isCustom, updateDeleteCondition]);

  const isDisabled = isNew ? false : !isCustom;

  return (
    <Grid container columnSpacing={3} rowSpacing={2} className="pt-2 pl-3 pr-3">
      <Grid item xs={12} className="d-flex">
        <div className="heading mt-2 mb-1">AVETMISS DATA</div>
      </Grid>

      <Grid item container columnSpacing={3} rowSpacing={2} xs={twoColumn ? 6 : 12}>
        <Grid item xs={12}>
          <FormField
            type="select"
            disabled={isDisabled}
            name="type"
            label="Type"
            items={qualificationTypes}
            required={isNew || isCustom}
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="text"
            disabled={isDisabled}
            name="qualLevel"
            label="Level"
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="multilineText"
            disabled={isDisabled}
            name="title"
            label="Title"
            required={isNew || isCustom}
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="text"
            disabled={!isNew}
            name="nationalCode"
            label="National code"
            required={isNew || isCustom}
          />
        </Grid>
      </Grid>

      <Grid item container columnSpacing={3} rowSpacing={2} xs={twoColumn ? 6 : 12}>
        <Grid item xs={12}>
          <FormField type="text" disabled={isDisabled} name="anzsco" label="ANZSCO" />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="text"
            disabled={isDisabled}
            name="fieldOfEducation"
            label="Field of education"
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="text"
            name="specialization"
            label="Specialization"
            maxLength="128"
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="number"
            normalize={value => (value || value === 0 ? Number(value) : null)}
            name="nominalHours"
            label="Nominal hours"
          />
        </Grid>
      </Grid>

      <Grid item xs={12}>
        <div className="heading mb-2 mt-2">Internal options</div>
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="isOffered" />}
          label="Is Offered"
        />
      </Grid>
    </Grid>
  );
};

export default QualificationsEditView;
