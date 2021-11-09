/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { FormControlLabel } from "@mui/material";
import Grid from "@mui/material/Grid";
import { ModuleType } from "@api/model";
import FormField from "../../../../common/components/form/formFields/FormField";
import { validateSingleMandatoryField, greaterThanNullValidation } from "../../../../common/utils/validation";
import { sortDefaultSelectItems } from "../../../../common/utils/common";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";

const moduleTypes = Object.keys(ModuleType).map(key => ({ value: ModuleType[key], label: key }));

moduleTypes.sort(sortDefaultSelectItems);

const checkNumber = val => {
  const num = String(val);

  return num.match(/\./) ? num.replace(/[0-9]+\./, "").length > 2 : false;
};

const normalizeCreditPoints = (value, prev) =>
  ((value && value >= 0) || value === 0 ? (checkNumber(value) ? Number(prev) : Number(value)) : null);

const normalizeExpiryDays = value => ((value && value >= 0) || value === 0 ? Number(value) : null);

const normalizeNominalHours = value => (value || value === 0 ? Number(value) : null);

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
    <Grid container columnSpacing={3} className="p-3 pt-1">
      <Grid item lg={twoColumn ? 6 : 12} md={twoColumn ? 8 : 12} xs={12}>
        <Grid container columnSpacing={3}>
          {twoColumn && (
            <FullScreenStickyHeader
              twoColumn={twoColumn}
              title={(
                <span className="d-block text-nowrap text-truncate">
                  {values && values.title}
                </span>
              )}
              fields={(
                <Grid item xs={twoColumn ? 6 : 12}>
                  <FormField
                    type="text"
                    disabled={isDisabled}
                    name="title"
                    label="Title"
                    validate={isNew || isCustom ? validateSingleMandatoryField : undefined}
                  />
                </Grid>
              )}
            />
          )}
          <Grid item xs={12} className="d-flex">
            <div className="heading mt-2 mb-1">AVETMISS DATA</div>
          </Grid>

          <Grid item xs={twoColumn ? 6 : 12}>
            {!twoColumn && (
              <FormField
                type="text"
                disabled={isDisabled}
                name="title"
                label="Title"
                validate={isNew || isCustom ? validateSingleMandatoryField : undefined}
              />
            )}
            <FormField
              type="text"
              disabled={!isNew}
              name="nationalCode"
              label="National code"
              validate={isNew || isCustom ? validateSingleMandatoryField : undefined}
            />
            <FormField
              type="text"
              disabled={isDisabled}
              name="fieldOfEducation"
              label="Field of education"
            />
            <FormField
              type="text"
              name="specialization"
              label="Specialization"
              maxLength="128"
            />
          </Grid>

          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="number"
              normalize={normalizeCreditPoints}
              max="99999999.99"
              name="creditPoints"
              label="Credit points"
              validate={greaterThanNullValidation}
            />

            <FormField
              type="number"
              normalize={normalizeExpiryDays}
              onKeyPress={ev => {
                if (ev.key.match(/\./)) {
                  ev.preventDefault();
                }
              }}
              name="expiryDays"
              label="Expiry days"
              validate={greaterThanNullValidation}
            />

            <FormField
              type="number"
              normalize={normalizeNominalHours}
              name="nominalHours"
              label="Nominal hours"
            />

            <FormField
              type="select"
              disabled={isDisabled}
              name="type"
              label="Type"
              items={moduleTypes}
              required
            />
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
      </Grid>
    </Grid>
  );
};

export default ModulesEditView;
