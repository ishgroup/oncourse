/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback, useMemo } from "react";
import Decimal from "decimal.js-light";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import { IconButton } from "@material-ui/core";
import Delete from "@material-ui/icons/Delete";
import Edit from "@material-ui/icons/Edit";
import { ClassCost } from "@api/model";
import { formatCurrency } from "../../../../../common/utils/numbers/numbersNormalizing";
import { NumberArgFunction } from "../../../../../model/common/CommonFunctions";

interface BudgetItemRowProps {
  value: ClassCost;
  currencySymbol: string;
  maxBasedValue: number;
  projectedBasedValue: number;
  actualBasedValue: number;
  openEditModal: (data: ClassCost) => void;
  onDeleteClassCost: NumberArgFunction;
  classes?: any;
}

const BudgetItemRow = React.memo<BudgetItemRowProps>(
  ({
     value,
     currencySymbol,
     maxBasedValue,
     projectedBasedValue,
     actualBasedValue,
     classes,
     openEditModal,
     onDeleteClassCost
   }) => {
    const onEditClick = () => openEditModal(value);
    const onDeleteClick = useCallback(
      () => (onDeleteClassCost ? onDeleteClassCost(value.id || value["temporaryId"]) : null),
      [value.id, onDeleteClassCost]
    );

    const amountLabel = useMemo(
      () =>
        `${formatCurrency(value.perUnitAmountExTax, currencySymbol)} ${
          value.repetitionType === "Discount" ? "" : value.repetitionType
        }`,
      [value.perUnitAmountExTax, value.repetitionType, currencySymbol]
    );

    const maxBasedLabel = useMemo(() => formatCurrency(maxBasedValue, currencySymbol), [maxBasedValue, currencySymbol]);

    const projectedBasedLabel = useMemo(() => formatCurrency(projectedBasedValue, currencySymbol), [
      projectedBasedValue,
      currencySymbol
    ]);

    const actualBasedLabel = useMemo(() => formatCurrency(actualBasedValue, currencySymbol), [
      actualBasedValue,
      currencySymbol
    ]);

    const percentOfProjectedValue = useMemo(
      () =>
        (projectedBasedValue <= 0
          ? 0
          : new Decimal(actualBasedValue)
            .div(projectedBasedValue || 1)
            .mul(100)
            .toDecimalPlaces(0)
            .toFixed(2)),

      [projectedBasedValue, actualBasedValue]
    );

    const description = useMemo(() =>
        (value.description + (value.tutorRole ? ` (${value.tutorRole})` : "")),
      [value.description, value.tutorRole]);

    return (
      <Grid
        item
        xs={12}
        container
        alignItems="center"
        direction="row"
        className={classes.tableTab}
        onDoubleClick={onEditClick}
      >
        <Grid item xs={3}>
          <Typography variant="body2">{description}</Typography>
        </Grid>
        <Grid item xs={2} className={classes.rowItemCol1}>
          <Typography variant="body2" className="money text-end">
            {amountLabel}
          </Typography>
        </Grid>
        <Grid item xs={2} className={classes.rowItemCol2}>
          <Typography variant="body2" className="money text-end">
            {maxBasedLabel}
          </Typography>
        </Grid>
        <Grid item xs={2} className={classes.rowItemCol3}>
          <Typography variant="body2" className="money text-end">
            {projectedBasedLabel}
          </Typography>
        </Grid>
        <Grid item xs={2} container alignItems="center" className={classes.rowItemCol4}>
          <Typography variant="body2" className="disabled">
            (
            {percentOfProjectedValue}
            %)&nbsp;
          </Typography>
          <Typography variant="body2" className="money">
            {actualBasedLabel}
          </Typography>
        </Grid>
        <Grid item xs={1} container alignItems="center">
          <div className="flex-fill" />

          <div className={classes.tableTabButtons}>
            {onDeleteClassCost && (
              <IconButton className="lightGrayIconButton" onClick={onDeleteClick}>
                <Delete fontSize="inherit" />
              </IconButton>
            )}

            <IconButton className="lightGrayIconButton" onClick={onEditClick}>
              <Edit fontSize="inherit" />
            </IconButton>
          </div>
        </Grid>
      </Grid>
    );
  }
);

export default BudgetItemRow;
