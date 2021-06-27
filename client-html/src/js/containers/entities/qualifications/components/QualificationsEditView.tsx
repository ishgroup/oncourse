/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { FormControlLabel } from "@material-ui/core";
import { QualificationType } from "@api/model";
import Grid from "@material-ui/core/Grid";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { validateSingleMandatoryField } from "../../../../common/utils/validation";
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
    <Grid container className="pt-1 p-3">
      <Grid item lg={twoColumn ? 6 : 12} md={twoColumn ? 8 : 12} xs={12}>
        <Grid container>
          <Grid item xs={12} className="d-flex">
            <div className="heading mt-2 mb-1">AVETMISS DATA</div>
          </Grid>

          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="select"
              disabled={isDisabled}
              name="type"
              label="Type"
              items={qualificationTypes}
              validate={isNew || isCustom ? validateSingleMandatoryField : undefined}
              autoWidth={false}
              fullWidth
            />
            <FormField
              type="text"
              disabled={isDisabled}
              name="qualLevel"
              label="Level"
              validate={isNew || isCustom ? validateSingleMandatoryField : undefined}
              fullWidth
            />
            <FormField
              type="multilineText"
              disabled={isDisabled}
              name="title"
              label="Title"
              validate={isNew || isCustom ? validateSingleMandatoryField : undefined}
              fullWidth
            />
            <FormField
              type="text"
              disabled={!isNew}
              name="nationalCode"
              label="National code"
              validate={isNew || isCustom ? validateSingleMandatoryField : undefined}
              fullWidth
            />
          </Grid>

          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField type="text" disabled={isDisabled} name="anzsco" label="ANZSCO" fullWidth />
            <FormField
              type="text"
              disabled={isDisabled}
              name="fieldOfEducation"
              label="Field of education"
              fullWidth
            />
            <FormField
              type="text"
              name="specialization"
              label="Specialization"
              maxLength="128"
              fullWidth
            />
            <FormField
              type="number"
              normalize={value => (value || value === 0 ? Number(value) : null)}
              name="nominalHours"
              label="Nominal hours"
              fullWidth
            />
          </Grid>

          <Grid item xs={12}>
            <div className="heading mb-2 mt-2">Internal options</div>
            <FormControlLabel
              className="checkbox"
              control={<FormField type="checkbox" name="isOffered" fullWidth />}
              label="Is Offered"
            />
          </Grid>
        </Grid>
      </Grid>
    </Grid>
  );
};

export default QualificationsEditView;
