/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Card from "@material-ui/core/Card";
import clsx from "clsx";
import Button from "@material-ui/core/Button";
import { FormControlLabel } from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import { EntityRelationType, EntityRelationCartAction } from "@api/model";
import { Simulate } from "react-dom/test-utils";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { mapSelectItems, sortDefaultSelectItems } from "../../../../../common/utils/common";
import { validateSingleMandatoryField, validateUniqueNamesInArray } from "../../../../../common/utils/validation";
import Subtitle from "../../../../../common/components/layout/Subtitle";

import ended = Simulate.ended;

const CartActions = Object.keys(EntityRelationCartAction)
  .filter(val => isNaN(Number(val)))
  .map(mapSelectItems);

CartActions.sort(sortDefaultSelectItems);

const renderEntityRelationTypes = props => {
  const {
    fields, classes, onDelete, discounts
  } = props;

  return (
    <Grid item xs={12}>
      {fields.map((item: EntityRelationType, index) => {
        const field = fields.get(index);

        return (
          <Card id={`entity-relation-type-${index}`} className="card" key={index}>
            <Grid container spacing={2} className="relative">
              <Grid item xs={12}>
                <Grid container spacing={2}>
                  <Grid item xs={4}>
                    <FormField
                      type="text"
                      name={`${item}.name`}
                      label="Name of relationship"
                      fullWidth
                      className={classes.field}
                      disabled={field.systemType}
                      required
                      validate={validateUniqueNamesInArray}
                    />
                  </Grid>

                  <Grid item xs={8}>
                    <FormField
                      type="text"
                      name={`${item}.description`}
                      label="Description"
                      fullWidth
                      className={classes.field}
                      disabled={field.systemType}
                    />
                    <div>
                      {!field.systemType && (
                        <Button
                          size="small"
                          color="secondary"
                          className={classes.deleteButton}
                          onClick={() => onDelete(field, index)}
                        >
                          Delete
                        </Button>
                      )}
                    </div>
                  </Grid>

                  <Grid item xs={4}>
                    <FormField
                      type="text"
                      name={`${item}.fromName`}
                      label="From name"
                      fullWidth
                      disabled={field.systemType}
                      required
                      classes={{
                        textField: "text-left",
                        fitWidth: clsx("flex-fill", classes.fromNameLabel),
                      }}
                    />
                  </Grid>

                  <Grid item xs={4}>
                    <Grid container className={clsx("p-2", classes.shoppingCartActionBox)}>
                      <Grid item xs={12}>
                        <Subtitle label="Shopping cart action" />
                      </Grid>
                      <Grid item xs={12}>
                        <FormField
                          type="select"
                          name={`${item}.shoppingCart`}
                          label="Cart action"
                          items={CartActions}
                          required
                          fullWidth
                          hideLabel
                        />
                      </Grid>
                      <Grid item xs={12}>
                        <FormField
                          type="select"
                          name={`${item}.discountId`}
                          label="Apply discount to item on right"
                          items={discounts}
                          fullWidth
                          allowEmpty
                        />
                      </Grid>
                      <Grid item xs={12}>
                        <FormControlLabel
                          className={classes.checkbox}
                          control={(
                            <FormField
                              type="checkbox"
                              name={`${item}.considerHistory`}
                              color="primary"
                              value="true"
                              fullWidth
                            />
                          )}
                          label="Consider history"
                        />
                      </Grid>
                    </Grid>
                  </Grid>

                  <Grid item xs={4}>
                    <div className="d-flex">
                      <FormField
                        type="text"
                        name={`${item}.toName`}
                        label="To name"
                        fullWidth
                        className="flex-fill"
                        disabled={field.systemType}
                        required
                        classes={{
                          textField: "text-end",
                          fitWidth: clsx("flex-fill", classes.fromNameLabel),
                          label: clsx("text-end", classes.fromNameLabelOnEdit)
                        }}
                      />
                    </div>
                  </Grid>

                  <Grid item xs={6}>
                    <FormControlLabel
                      className={classes.checkbox}
                      control={(
                        <FormField
                          type="checkbox"
                          name={`${item}.isShownOnWeb`}
                          color="primary"
                          value="true"
                          fullWidth
                        />
                      )}
                      label="Show on web"
                    />
                  </Grid>
                </Grid>
              </Grid>
            </Grid>
          </Card>
        );
      })}
    </Grid>
  );
};

export default renderEntityRelationTypes;
