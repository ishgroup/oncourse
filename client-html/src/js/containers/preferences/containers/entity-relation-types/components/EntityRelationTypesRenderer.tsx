/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { EntityRelationCartAction, EntityRelationType } from '@api/model';
import { Button, FormControlLabel, Grid } from '@mui/material';
import Card from '@mui/material/Card';
import $t from '@t';
import { mapSelectItems, sortDefaultSelectItems } from 'ish-ui';
import * as React from 'react';
import FormField from '../../../../../common/components/form/formFields/FormField';
import Subtitle from '../../../../../common/components/layout/Subtitle';
import { validateUniqueNamesInArray } from '../../../../../common/utils/validation';

const CartActions = Object.keys(EntityRelationCartAction)
  .filter(val => isNaN(Number(val)))
  .map(mapSelectItems);

CartActions.sort(sortDefaultSelectItems);

const renderEntityRelationTypes = props => {
  const {
    fields, classes, onDelete, discounts
  } = props;

  return (
    <Grid size={12}>
      {fields.map((item: EntityRelationType, index) => {
        const field = fields.get(index);

        return (
          <Card id={`entity-relation-type-${index}`} className="card" key={index}>
            <Grid container spacing={3}>
              <Grid size={4}>
                <FormField
                  type="text"
                  name={`${item}.name`}
                  label={$t('name_of_relationship')}
                  disabled={field.systemType}
                  required
                  validate={validateUniqueNamesInArray}
                />
              </Grid>

              <Grid size={7}>
                <FormField
                  type="text"
                  name={`${item}.description`}
                  label={$t('description')}
                  disabled={field.systemType}
                  className="flex-fill pr-3"
                />
              </Grid>

              <Grid size={1}>
                <div className="relative">
                  {!field.systemType && (
                    <Button
                      size="small"
                      color="secondary"
                      className={classes.deleteButton}
                      onClick={() => onDelete(field, index)}
                    >
                      {$t('delete2')}
                    </Button>
                  )}
                </div>
              </Grid>

              <Grid size={4}>
                <FormField
                  type="text"
                  name={`${item}.fromName`}
                  label={$t('from_name')}
                  disabled={field.systemType}
                  required
                />
              </Grid>

              <Grid size={4} container>
                <Grid size={12} container columnSpacing={3} rowSpacing={2} className={classes.shoppingCartActionBox}>
                  <Grid size={12}>
                    <Subtitle label={$t('shopping_cart_action')} />
                  </Grid>
                  <Grid size={12}>
                    <FormField
                      type="select"
                      name={`${item}.shoppingCart`}
                      label={$t('cart_action')}
                      items={CartActions}
                      required
                      hideLabel
                    />
                  </Grid>
                  <Grid size={12}>
                    <FormField
                      type="select"
                      name={`${item}.discountId`}
                      label={$t('apply_discount_to_item_on_right')}
                      items={discounts}
                      allowEmpty
                    />
                  </Grid>
                  <Grid size={12}>
                    <FormControlLabel
                      className={classes.checkbox}
                      control={(
                        <FormField
                          type="checkbox"
                          name={`${item}.considerHistory`}
                          color="primary"
                        />
                      )}
                      label={$t('consider_history')}
                    />
                  </Grid>
                </Grid>
              </Grid>

              <Grid size={4}>
                <FormField
                  type="text"
                  name={`${item}.toName`}
                  label={$t('to_name')}
                  disabled={field.systemType}
                  required
                  rightAligned
                />
              </Grid>

              <Grid size={6}>
                <FormControlLabel
                  className={classes.checkbox}
                  control={(
                    <FormField
                      type="checkbox"
                      name={`${item}.isShownOnWeb`}
                      color="primary"
                    />
                      )}
                  label={$t('show_on_web')}
                />
              </Grid>
            </Grid>
          </Card>
        );
      })}
    </Grid>
  );
};

export default renderEntityRelationTypes;
