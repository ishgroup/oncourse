/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import { Grid, Typography } from '@mui/material';
import $t from '@t';
import React from 'react';
import Uneditable from '../../../../../common/components/form/formFields/Uneditable';

const VoucherEditView: React.FC<any> = ({ values, summaryVoucher }) => {
  if (!values) {
    return null;
  }

  const isClassVoucher = values.maxCoursesRedemption !== null;

  const value = isClassVoucher
    ? `${values.maxCoursesRedemption} enrolment${values.maxCoursesRedemption > 1 ? "s" : ""}`
    : values.value === null
      ? summaryVoucher.price : values.value;

  return (
    <Grid container columnSpacing={3} rowSpacing={2} className="ml-0">
      <Grid item sm={2}>
        <Uneditable value={values.code} label={$t('sku')} />
      </Grid>
      <Grid item sm={2}>
        <Uneditable value={values.feeExTax || 0} label={$t('sale_price')} money />
      </Grid>
      <Grid item sm={3}>
        <Uneditable value={value} label={$t('value')} money={!isClassVoucher} />
      </Grid>
      {
        isClassVoucher
        && (
        <Grid item sm={12} className="pt-2 pb-1">
          <div>
            <div className="heading">
              {$t('courses')}
            </div>
            <Typography variant="caption" component="div" color="textSecondary" className="pb-1">
              {$t('can_be_redeemed_for_in_classes_from',[value])}
            </Typography>
            {values.courses && values.courses.map(c => (
              <Typography key={c.id} variant="body2" component="div" className="pb-2">
                {c.name}
                {" "}
                (
                {c.code}
                )
              </Typography>
            ))}
          </div>
        </Grid>
        )
      }
      <Grid item sm={12}>
        <Uneditable value={values.description} label={$t('description')} multiline />
      </Grid>
    </Grid>
  );
};

export default VoucherEditView;
