/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { FormControlLabel } from "@mui/material";
import Grid from "@mui/material/Grid";
import { ModuleType } from "@api/model";
import FormField from "../../../../common/components/form/formFields/FormField";
import { greaterThanNullValidation } from "../../../../common/utils/validation";
import { sortDefaultSelectItems } from "../../../../common/utils/common";
import { normalizeNumberToPositive } from "../../../../common/utils/numbers/numbersNormalizing";

const moduleTypes = Object.keys(ModuleType).map(key => ({ value: ModuleType[key], label: key }));

moduleTypes.sort(sortDefaultSelectItems);

const ModulesEditView = (props: any) => {
  const {
    isNew, values, updateDeleteCondition, twoColumn
  } = props;

  const isCustom = values && values.isCustom === true;

  if (updateDeleteCondition) {
    updateDeleteCondition(isCustom);
  }

  const isDisabled = isNew ? false : !isCustom;

  return (
    <Grid container rowSpacing={2} columnSpacing={3} className="pt-2 pl-3 pr-3">
      <Grid item xs={12} className="d-flex">
        <div className="heading mt-2 mb-1">AVETMISS DATA</div>
      </Grid>
      
      <Grid item container columnSpacing={3} rowSpacing={2} xs={twoColumn ? 6 : 12}>
        <Grid item xs={12}>
          <FormField
            type="text"
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
      </Grid>

      <Grid item container columnSpacing={3} rowSpacing={2} xs={twoColumn ? 6 : 12}>
        <Grid item xs={12}>
          <FormField
            type="number"
            normalize={normalizeNumberToPositive}
            max="99999999.99"
            name="creditPoints"
            label="Credit points"
            debounced={false}
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="number"
            normalize={normalizeNumberToPositive}
            onKeyPress={ev => {
              if (ev.key.match(/\./)) {
                ev.preventDefault();
              }
            }}
            name="expiryDays"
            label="Expiry days"
            debounced={false}
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="number"
            normalize={normalizeNumberToPositive}
            name="nominalHours"
            label="Nominal hours"
            debounced={false}
          />
        </Grid>
        <Grid item xs={12}>
          <FormField
            type="select"
            disabled={isDisabled}
            name="type"
            label="Type"
            items={moduleTypes}
            required
          />
        </Grid>
        
      </Grid>

      <Grid item xs={12}>
        <div className="heading mb-2 mt-2">Internal options</div>
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="isOffered" />}
          label="Is offered"
        />
      </Grid>
    </Grid>
  );
};

export default ModulesEditView;
