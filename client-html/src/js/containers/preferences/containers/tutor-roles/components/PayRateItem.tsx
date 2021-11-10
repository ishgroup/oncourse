/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import Grid from "@mui/material/Grid";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import Tooltip from "@mui/material/Tooltip";
import { ClassCostRepetitionType } from "@api/model";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { YYYY_MM_DD_MINUSED } from "../../../../../common/utils/dates/format";
import {
  formatFieldPercent,
  parseFieldPercent,
  preventNegativeOrLogEnter
} from "../../../../../common/utils/numbers/numbersNormalizing";
import { mapSelectItems } from "../../../../../common/utils/common";

const repetitionTypes = Object.keys(ClassCostRepetitionType).filter(t => t !== "Discount").map(mapSelectItems);

const validatePercentage = value => (!value && value !== 0 ? "Field is mandatory" : undefined);

const PayRateItem = props => {
  const { fields, classes, onDelete } = props;

  return fields.map((item, index) => (
    <Grid key={index} item xs={12} className={classes.payRateItem}>
      <Grid container columnSpacing={3} alignContent="space-between">
        <Grid item xs={3}>
          <FormField
            type="date"
            name={`${item}.validFrom`}
            label="Valid from"
            formatValue={YYYY_MM_DD_MINUSED}
            fullWidth
            required
          />
        </Grid>
        <Grid item xs={7}>
          <Grid container columnSpacing={3}>
            <Grid item xs={3}>
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
              />
            </Grid>
            <Grid item xs={3}>
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
            </Grid>
            <Grid item xs={12}>
              <FormField type="multilineText" name={`${item}.notes`} label="Description" fullWidth />
            </Grid>
          </Grid>
        </Grid>
        <Grid item xs={2}>
          <Tooltip title="Remove pay rate">
            <IconButton
              className="lightGrayIconButton"
              children={<DeleteIcon fontSize="inherit" color="inherit" />}
              color="secondary"
              onClick={() => onDelete(index)}
            />
          </Tooltip>
        </Grid>
      </Grid>
    </Grid>
  ));
};

export default PayRateItem;
