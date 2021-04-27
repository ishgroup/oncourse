/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Card from "@material-ui/core/Card";
import Button from "@material-ui/core/Button";
import { FormControlLabel } from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import { PaymentType, PayType } from "@api/model";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { validateSingleMandatoryField, validateUniqueNamesInArray } from "../../../../../common/utils/validation";
import { mapSelectItems, sortDefaultSelectItems } from "../../../../../common/utils/common";

const PayTypes = Object.keys(PayType)
  .filter(val => Number.isNaN(Number(val)))
  .map(mapSelectItems);

PayTypes.sort(sortDefaultSelectItems);

const renderPaymentTypes = props => {
  const {
    fields, classes, onDelete, assetAccounts
  } = props;

  return (
    <Grid item xs={12}>
      {fields.map((item: PaymentType, index) => {
        const field = fields.get(index);

        return (
          <Card id={`payment-type-item-${index}`} className="card" key={index}>
            <Grid container spacing={2} className="relative">
              <Grid item xs={12}>
                <Grid container>
                  <Grid item xs={10} md={11}>
                    <FormField
                      type="text"
                      name={`${item}.name`}
                      label="Name"
                      fullWidth
                      disabled={field.systemType}
                      validate={[validateSingleMandatoryField, validateUniqueNamesInArray]}
                    />
                  </Grid>

                  <Grid item xs={2} md={1}>
                    {!field.systemType && (
                      <div className="d-flex">
                        <div className="flex-fill" />
                        <Button
                          size="small"
                          color="secondary"
                          className={classes.deleteButton}
                          onClick={() => onDelete(field, index)}
                        >
                          Delete
                        </Button>
                      </div>
                    )}
                  </Grid>

                  <Grid item xs={4} className={classes.checkbox}>
                    <FormControlLabel
                      control={(
                        <FormField
                          type="checkbox"
                          name={`${item}.active`}
                          color="primary"
                          value="true"
                          disabled={field.systemType}
                          fullWidth
                        />
                      )}
                      label="Active"
                    />
                  </Grid>

                  <Grid item xs={4} className={classes.checkbox}>
                    <FormControlLabel
                      control={(
                        <FormField
                          type="checkbox"
                          name={`${item}.bankedAuto`}
                          color="primary"
                          value="true"
                          disabled={field.systemType}
                          fullWidth
                        />
                      )}
                      label="Banked automatically"
                    />
                  </Grid>

                  <Grid item xs={4} className={classes.checkbox}>
                    <FormControlLabel
                      control={(
                        <FormField
                          type="checkbox"
                          name={`${item}.reconcilable`}
                          color="primary"
                          value="true"
                          disabled={field.systemType}
                          fullWidth
                        />
                      )}
                      label="Reconcilable"
                    />
                  </Grid>

                  {!field.systemType && (
                    <>
                      <Grid item xs={4}>
                        <FormField
                          type="select"
                          name={`${item}.type`}
                          label="Type"
                          items={PayTypes}
                          className={classes.field}
                          required
                          fullWidth
                        />
                      </Grid>

                      <Grid item xs={4}>
                        <FormField
                          type="select"
                          name={`${item}.undepositAccountId`}
                          label="Undeposited funds account"
                          items={assetAccounts}
                          className={classes.field}
                          required
                          fullWidth
                        />
                      </Grid>

                      <Grid item xs={4}>
                        <FormField
                          type="select"
                          name={`${item}.accountId`}
                          label="Account"
                          items={assetAccounts}
                          className={classes.field}
                          required
                          fullWidth
                        />
                      </Grid>
                    </>
                  )}
                </Grid>
              </Grid>
            </Grid>
          </Card>
        );
      })}
    </Grid>
  );
};

export default renderPaymentTypes;
