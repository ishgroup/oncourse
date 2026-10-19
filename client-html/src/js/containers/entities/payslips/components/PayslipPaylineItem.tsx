/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Currency } from '@api/model';
import { Typography, Button, Card, Grid } from '@mui/material';
import $t from '@t';
import clsx from 'clsx';
import { format } from 'date-fns';
import { EEE_D_MMM_YYYY, normalizeNumber } from 'ish-ui';
import React, { useMemo } from 'react';
import FormField from '../../../../common/components/form/formFields/FormField';
import Uneditable from '../../../../common/components/form/formFields/Uneditable';
import { PayLineWithDefer } from '../../../../model/entities/Payslip';

interface Props {
  threeColumn: boolean;
  paylineLayout: any;
  item: string;
  index: number;
  onDelete: (index: number) => void;
  field: PayLineWithDefer;
  classes: any;
  currency: Currency;
}

const PayslipPaylineItem = (props: Props) => {
  const {
 threeColumn, classes, paylineLayout, item, field, index, onDelete, currency
} = props;

  const valueTotal = useMemo(() => Number((field.quantity * field.value).toFixed(4)), [field.quantity, field.value]);

  const budgetTotal = useMemo(() => Number((field.budgetedQuantity * field.budgetedValue).toFixed(4)), [
    field.budgetedQuantity,
    field.budgetedValue
  ]);

  const hasQuantityAndTotal = useMemo(
    () => field.className && !(field.type === "Per session" || field.type === "Fixed"),
    [field.className, field.type]
  );

  const unit = useMemo(
    () => {
      switch (field?.type) {
        case 'Per unit':
          return 'Units';
        case 'Per enrolment':
          return 'Enrolments';
        default:
          return 'Hours';
      }
    },
    [field?.type]
  );

  const shortCurrencySymbol = useMemo(() => (currency != null ? currency.shortCurrencySymbol : "$"), [currency]);

  return (
    <Card className={clsx(threeColumn ? classes.threeColumnCard : "card", "relative")}>
      <Grid container rowSpacing={2}>
        <Grid size={paylineLayout[1].xs} className="d-flex">
          <Grid container >
            {field.type && (
              <Grid size={paylineLayout[3].xs} className={threeColumn ? undefined : "centeredFlex"}>
                <Typography variant="body1" color="textSecondary" className={threeColumn ? "flex-fill" : undefined}>
                  {field.tutorRoleType}, {field.type}
                </Typography>
              </Grid>
            )}
            <Grid size={paylineLayout[3].xs} className={threeColumn ? undefined : "centeredFlex"}>
              {field.className ? (
                <div className={threeColumn ? "flex-column text-end" : "centeredFlex"}>
                  <Typography variant="caption" color="textSecondary">
                    {$t('include_in_payslip')}
                  </Typography>

                  <FormField
                    type="switch"
                    name={`${item}.deferred`}
                    className={clsx(classes.deferSwitch, {
                      "mt-0 ": !threeColumn
                    })}
                  />
                </div>
              ) : (
                <div className={clsx(threeColumn ? classes.deleteButtonMargin : "m-0", "errorColor")}>
                  <Button size="small" color="inherit" onClick={() => onDelete(index)}>
                    {$t('delete2')}
                  </Button>
                </div>
              )}
            </Grid>
          </Grid>
        </Grid>

        <Grid size={paylineLayout[4].xs}>
          <Grid container columnSpacing={3} rowSpacing={2} className="h-100">
            <Grid size={12}>
              {field.className && (
                <Grid size={paylineLayout[2].xs}>
                  <div className="flex-fill">
                    <Uneditable
                      label={$t('date_for')}
                      value={format(new Date(field.dateFor), EEE_D_MMM_YYYY)}
                    />
                  </div>
                </Grid>
              )}
            </Grid>
            <Grid size={12} className={clsx("pr-2", threeColumn ? "pt-2" : undefined)}>
              <FormField
                type="multilineText"
                disabled={!field.deferred}
                name={`${item}.description`}
                label={$t('description')}
                required
              />
            </Grid>
          </Grid>
        </Grid>

        <Grid size={paylineLayout[5].xs}>
          <Grid container className={classes.infoContainer}>
            {hasQuantityAndTotal && (
              <>
                <Grid size={2} />

                <Grid size={5} className={classes.infoItem}>
                  <Typography variant="caption">{$t('pay')}</Typography>
                </Grid>

                <Grid size={5} className={classes.infoItem}>
                  <Typography variant="caption">{$t('budget')}</Typography>
                </Grid>

                <Grid size={3} className="centeredFlex">
                  <Typography variant="caption">{unit}</Typography>
                </Grid>

                <Grid size={4} className="text-nowrap d-flex justify-content-end">
                  <Typography
                    variant="subtitle1"
                    className="fw300"
                  >
                    <FormField
                      type="number"
                      disabled={!field.deferred}
                      name={`${item}.quantity`}
                      normalize={normalizeNumber}
                      debounced={false}
                      inline
                      rightAligned
                    />
                  </Typography>
                </Grid>

                <Grid size={5} className={classes.infoItem}>
                  <Typography
                    variant="subtitle1"
                    className="fw300"
                    color={field.quantity === field.budgetedQuantity ? "textSecondary" : "error"}
                  >
                    {field.budgetedQuantity}
                  </Typography>
                </Grid>
              </>
            )}

            <Grid size={2} className="centeredFlex">
              <Typography variant="caption">{field.className ? "Rate" : "Total"}</Typography>
            </Grid>

            <Grid size={hasQuantityAndTotal ? 5 : 10} className="text-nowrap d-flex justify-content-end">
              <Typography
                variant="subtitle1"
                className="fw300"
              >
                <FormField
                  type="money"
                  disabled={!field.deferred}
                  name={`${item}.value`}
                  inline
                  rightAligned
                  required
                />
              </Typography>
            </Grid>

            {hasQuantityAndTotal && (
              <Grid size={5} className={classes.infoItem}>
                <Typography
                  variant="subtitle1"
                  color={field.value === field.budgetedValue ? "textSecondary" : "error"}
                  className="money fw300"
                >
                  {shortCurrencySymbol}
                  {" "}
                  {field.budgetedValue.toFixed(2) || 0}
                </Typography>
              </Grid>
            )}

            {hasQuantityAndTotal && (
              <>
                <Grid size={2} className="centeredFlex">
                  <Typography variant="caption">{$t('total')}</Typography>
                </Grid>

                <Grid size={5} className={classes.infoItem}>
                  <div className="w-100 text-end">
                    <Typography variant="subtitle1" className="money fw300" noWrap>
                      {shortCurrencySymbol}
                      {" "}
                      {valueTotal.toFixed(2)}
                    </Typography>
                  </div>
                </Grid>

                <Grid size={5} className={classes.infoItem}>
                  <Typography
                    variant="subtitle1"
                    color={valueTotal === budgetTotal ? "textSecondary" : "error"}
                    className="money fw300"
                    noWrap
                  >
                    {shortCurrencySymbol}
                    {" "}
                    {budgetTotal.toFixed(2)}
                  </Typography>
                </Grid>
              </>
            )}
          </Grid>
        </Grid>
      </Grid>
    </Card>
  );
};

export default PayslipPaylineItem;
