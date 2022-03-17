/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Card, IconButton, Grid } from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import Tooltip from "@mui/material/Tooltip";
import { ClassCostRepetitionType } from "@api/model";
import clsx from "clsx";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { YYYY_MM_DD_MINUSED } from "../../../../../common/utils/dates/format";
import {
  formatFieldPercent,
  parseFieldPercent,
  preventNegativeOrLogEnter
} from "../../../../../common/utils/numbers/numbersNormalizing";
import { mapSelectItems } from "../../../../../common/utils/common";
import { valiadateSelectItemAvailable } from "../../../../../common/utils/validation";

const repetitionTypes = Object.keys(ClassCostRepetitionType).filter(t => !["Discount", "Per student contact hour"].includes(t)).map(mapSelectItems);

const validateRepetition = val => valiadateSelectItemAvailable(val, repetitionTypes);

const validatePercentage = value => (!value && value !== 0 ? "Field is mandatory" : undefined);

const PayRateItem = props => {
  const { fields, classes, onDelete } = props;

  return fields.map((item, index) => (
    <Card key={index} className={clsx("card", classes.payRateItem)}>
      <Grid container rowSpacing={2}>
        <Grid item xs={12} container columnSpacing={3} rowSpacing={2}>
          <Grid item xs={4}>
            <FormField
              type="date"
              name={`${item}.validFrom`}
              label="Valid from"
              formatValue={YYYY_MM_DD_MINUSED}
              fullWidth
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
              type="persent"
              name={`${item}.oncostRate`}
              label="Oncosts"
              step="0.01"
              format={formatFieldPercent}
              parse={parseFieldPercent}
              onKeyPress={preventNegativeOrLogEnter}
              validate={validatePercentage}
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
          <FormField type="multilineText" name={`${item}.notes`} label="Description" fullWidth />
        </Grid>
      </Grid>
    </Card>
  ));
};

export default PayRateItem;
