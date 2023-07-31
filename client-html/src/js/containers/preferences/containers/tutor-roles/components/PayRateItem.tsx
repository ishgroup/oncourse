/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { Card, IconButton, Grid } from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import Tooltip from "@mui/material/Tooltip";
import { ClassCostRepetitionType } from "@api/model";
import FormField from "../../../../../common/components/form/formFields/FormField";
import {
  formatFieldPercent,
  parseFieldPercent,
  preventNegativeOrLogEnter,
  YYYY_MM_DD_MINUSED
} from "ish-ui";
import { mapSelectItems } from "../../../../../common/utils/common";
import { valiadateSelectItemAvailable } from "../../../../../common/utils/validation";

const repetitionTypes = Object.keys(ClassCostRepetitionType).filter(t => !["Discount", "Per student contact hour"].includes(t)).map(mapSelectItems);

const validateRepetition = val => valiadateSelectItemAvailable(val, repetitionTypes);

const validatePercentage = value => (!value && value !== 0 ? "Field is mandatory" : undefined);

const PayRateItem = props => {
  const {fields, onDelete} = props;

  return fields.map((item, index) => (
    <Card key={index} className="card flex-fill mb-4">
      <Grid container rowSpacing={2}>
        <Grid item xs={12} container columnSpacing={3} rowSpacing={2}>
          <Grid item xs={4}>
            <FormField
              type="date"
              name={`${item}.validFrom`}
              label="Valid from"
              formatValue={YYYY_MM_DD_MINUSED}
              required
            />
          </Grid>
          <Grid item xs={2}>
            <FormField
              type="money"
              name={`${item}.rate`}
              label="Hourly rate"
              required
            />
          </Grid>
          <Grid item xs={4}>
            <FormField
              type="select"
              name={`${item}.type`}
              label="Type"
              items={repetitionTypes}
              validate={validateRepetition}
            />
          </Grid>
          <Grid item xs={2} className="d-flex">
            <FormField
              type="number"
              name={`${item}.oncostRate`}
              label="Oncosts"
              step="0.01"
              format={formatFieldPercent}
              parse={parseFieldPercent}
              onKeyPress={preventNegativeOrLogEnter}
              validate={validatePercentage}
              debounced={false}
            />
            <Tooltip title="Remove pay rate">
              <IconButton
                className="lightGrayIconButton"
                color="secondary"
                onClick={() => onDelete(index)}
              >
                <DeleteIcon fontSize="inherit" color="inherit" />
              </IconButton>
            </Tooltip>
          </Grid>
        </Grid>
        <Grid item xs={12}>
          <FormField type="multilineText" name={`${item}.notes`} label="Description" />
        </Grid>
      </Grid>
    </Card>
  ));
};

export default PayRateItem;
