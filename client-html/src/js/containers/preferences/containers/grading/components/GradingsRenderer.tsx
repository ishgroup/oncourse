/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { GradingEntryType, GradingType } from '@api/model';
import { Button, Card, Collapse, Grid } from '@mui/material';
import { makeAppStyles, normalizeNumber } from 'ish-ui';
import React from 'react';
import { Dispatch } from 'redux';
import { change, FieldArray, WrappedFieldArrayProps } from 'redux-form';
import { IAction } from '../../../../../common/actions/IshAction';
import FormField from '../../../../../common/components/form/formFields/FormField';
import { validateUniqueNamesInArray } from '../../../../../common/utils/validation';
import GradingItemsRenderer from './GradingItemsRenderer';

interface Props {
  classes?: any;
  onDelete?: any;
  dispatch?: Dispatch<IAction>;
}

const GradingEntryTypes = Object.keys(GradingEntryType)
  // @ts-ignore
  .map(value => ({ value, label: value === "name" ? "Choice list" : value.capitalize() }));

const useStyles = makeAppStyles()(theme => ({
  delete: {
    position: "absolute",
    color: theme.palette.error.main,
    right: 0,
    top: 0
  },
  gradingItemsRoot: {
    position: "relative",
    top: "-6px"
  }
}));

export default (props: WrappedFieldArrayProps & Props) => {
  const {
    fields, meta: { form }, onDelete, dispatch
  } = props;

  const { classes } = useStyles();

  const onTypeChange = (type, item) => {
    dispatch(change(form, `${item}.gradingItems`, []));
    switch (type) {
      case "choice list": {
        dispatch(change(form, `${item}.minValue`, 0));
        dispatch(change(form, `${item}.maxValue`, 100));
      }
    }
  };

  return (
    <Grid item xs={12} lg={10}>
      {fields.map((item, index) => {
        const field: GradingType = fields.get(index);
        return (
          <Card className="card" key={`${field.id}${index}`} id={`grading-type-${index}`}>
            <Grid container columnSpacing={3}>
              <Grid item xs={6}>
                <Grid container rowSpacing={2} columnSpacing={3}>
                  <Grid item xs={6}>
                    <FormField
                      type="text"
                      name={`${item}.name`}
                      label="Name"
                      validate={validateUniqueNamesInArray}
                                            required
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <FormField
                      type="select"
                      name={`${item}.entryType`}
                      label="Entry Type"
                      items={GradingEntryTypes}
                      onChange={val => onTypeChange(val, item)}
                      debounced={false}
                                          />
                  </Grid>

                  <Grid item xs={12}>
                    <Collapse in={field.entryType === "number"}>
                      <Grid container columnSpacing={3}>
                        <Grid item xs={6}>
                          <FormField
                            type="number"
                            name={`${item}.minValue`}
                            normalize={normalizeNumber}
                            debounced={false}
                            label="Min value"
                            required={field.entryType === "number"}
                                                      />
                        </Grid>
                        <Grid item xs={6}>
                          <FormField
                            type="number"
                            name={`${item}.maxValue`}
                            normalize={normalizeNumber}
                            debounced={false}
                            label="Max value"
                            required={field.entryType === "number"}
                                                      />
                        </Grid>
                      </Grid>
                    </Collapse>
                  </Grid>
                </Grid>
              </Grid>
              <Grid item xs={6} className="relative">
                <FieldArray
                  name={`${item}.gradingItems` as string}
                  component={GradingItemsRenderer}
                  classes={classes}
                  parent={field}
                />
                <Button
                  size="small"
                  className={classes.delete}
                  onClick={() => onDelete(index)}
                >
                  Delete
                </Button>
              </Grid>
            </Grid>
          </Card>
        );
      })}
    </Grid>
  );
};
