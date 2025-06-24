/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PaymentIn } from '@api/model';
import { Checkbox, FormControlLabel, Grid } from '@mui/material';
import Typography from '@mui/material/Typography';
import $t from '@t';
import { addDays, compareAsc, format as formatDate } from 'date-fns';
import { D_MMM_YYYY, III_DD_MMM_YYYY, LinkAdornment, openInternalLink } from 'ish-ui';
import React, { useCallback } from 'react';
import { connect } from 'react-redux';
import { FieldArray, getFormInitialValues } from 'redux-form';
import { ContactLinkAdornment } from '../../../../common/components/form/formFields/FieldAdornments';
import FormField from '../../../../common/components/form/formFields/FormField';
import Uneditable from '../../../../common/components/form/formFields/Uneditable';
import NestedTable from '../../../../common/components/list-view/components/list/ReactTableNestedList';
import { NestedTableColumn } from '../../../../model/common/NestedTable';
import { State } from '../../../../reducers/state';
import { SiteState } from '../../sites/reducers/state';
import { getAdminCenterLabel, openSiteLink } from '../../sites/utils';

const invoiceColumns: NestedTableColumn[] = [
  {
    name: "dateDue",
    title: "Due date",
    type: "date",
    width: 125
  },
  {
    name: "invoiceNumber",
    title: "Invoice number",
    width: 110
  },
  {
    name: "amountOwing",
    title: "Owing",
    type: "currency"
  },
  {
    name: "amount",
    title: "Amount",
    type: "currency"
  }
];

const openRow = value => {
  openInternalLink(`/invoice/${value.id}`);
};

interface PaymentInEditViewProps {
  classes?: any;
  twoColumn?: boolean;
  manualLink?: string;
  values?: PaymentIn;
  initialValues?: PaymentIn;
  dispatch?: any;
  form?: string;
  adminSites?: SiteState["adminSites"];
  lockedDate?: any;
}

const isDateLocked = (lockedDate: any, settlementDate: any) => {
  if (!lockedDate) {
    return true;
  }
  if (!settlementDate) {
    return false;
  }
  return (
    compareAsc(
      addDays(new Date(lockedDate), 1),
      new Date(settlementDate)
    ) > 0
  );
};

const validateSettlementDateBanked = (settlementDate, allValues) => {
  if (!settlementDate) {
    return undefined;
  }
  if (compareAsc(new Date(settlementDate), new Date(allValues.datePayed)) < 0) {
    return `Date banked must be after or equal to date paid`;
  }

  return undefined;
};

const PaymentInEditView: React.FC<PaymentInEditViewProps> = props => {
  const {
   twoColumn, values, lockedDate, initialValues, adminSites
  } = props;

  const validateSettlementDate = useCallback(
    settlementDate => {
      if (!settlementDate || !lockedDate) {
        return undefined;
      }
      if (!initialValues || initialValues.dateBanked === settlementDate) {
        return undefined;
      }
      const date = new Date(lockedDate);
      return compareAsc(addDays(date, 1), new Date(settlementDate)) > 0
        ? `Date must be after ${formatDate(date, D_MMM_YYYY)}`
        : undefined;
    },
    [lockedDate, initialValues]
  );

  const dateBankedDisabled = initialValues && values ? isDateLocked(lockedDate, initialValues.dateBanked)
    || ["Contra", "Internal", "Reverse", "Voucher"].find(item => item === values.paymentInType)
    || !["Success", "Reversed"].find(item => item === values.status) : true;

  const gridItemProps = { xs: twoColumn ? 6 : 12, lg: twoColumn ? 4 : 12 };

  return values ? (
    <Grid container columnSpacing={3} rowSpacing={2} className="p-3">
      <Grid item {...gridItemProps}>
        <Uneditable
          value={values.payerName}
          label={$t('payment_from')}
          labelAdornment={
            <ContactLinkAdornment id={values?.payerId} />
          }
        />
      </Grid>
      <Grid item {...gridItemProps}>
        <FormField
          type="select"
          name="administrationCenterId"
          label={$t('site')}
          defaultValue={values && values.administrationCenterName}
          selectLabelCondition={getAdminCenterLabel}
          items={adminSites || []}
          labelAdornment={<LinkAdornment link={values && values.administrationCenterId} linkHandler={openSiteLink} />}
          disabled={!!initialValues.dateBanked}
        />
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable value={values.paymentInType} label={$t('type')} />
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable value={values.status} label={$t('status')} />
      </Grid>
      {values.ccSummary && values.ccSummary.length > 0 && (
      <Grid item {...gridItemProps}>
        <div className="textField">
          <div>
            <Typography variant="caption" color="textSecondary">
              {$t('credit_card')}
            </Typography>
            {values.ccSummary.map(line => (
              <Typography variant="body1">{line}</Typography>
                ))}
          </div>
        </div>
      </Grid>
        )}
      {values.chequeSummary && Object.keys(values.chequeSummary).length > 0 && (
      <Grid item {...gridItemProps}>
        {Object.keys(values.chequeSummary).map(item => (
          <Uneditable value={values.chequeSummary[item]} label={item} />
            ))}
      </Grid>
        )}
      <Grid item {...gridItemProps}>
        <Uneditable value={values.amount} money label={$t('amount')} />
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable value={values.accountInName} label={$t('account')} />
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable value={values.source} label={$t('source')} />
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable value={values.ccTransaction} label={$t('cc_transaction')} />
      </Grid>
      <Grid item {...gridItemProps}>
        <FormControlLabel
          className="pr-3"
          control={<Checkbox checked={values.emailConfirmation} />}
          label={$t('email_confirmation')}
          disabled
        />
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable
          value={values.datePayed}
          label={$t('date_paid')}
          format={value => (value ? formatDate(new Date(value), III_DD_MMM_YYYY) : value)}
        />
      </Grid>
      <Grid item {...gridItemProps}>
        {dateBankedDisabled ? (
          <Uneditable
            value={values.dateBanked}
            label={$t('date_banked')}
            format={value => (value ? formatDate(new Date(value), III_DD_MMM_YYYY) : value)}
          />
          ) : (
            <FormField
              type="date"
              name="dateBanked"
              label={$t('date_banked')}
              validate={[validateSettlementDate, validateSettlementDateBanked]}
            />
          )}
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable value={values.createdBy} label={$t('created_by')} />
      </Grid>
      <Grid item xs={12} className="saveButtonTableOffset">
        <FieldArray
          name="invoices"
          goToLink={`/invoice?search=paymentInLines.paymentIn in (${values.id})`}
          title={(values && values.invoices && values.invoices.length) === 1 ? "Invoice" : "Invoices"}
          component={NestedTable}
          columns={invoiceColumns}
          onRowDoubleClick={openRow}
          rerenderOnEveryChange
          sortBy={(a, b) => b.invoiceNumber - a.invoiceNumber}
          calculateHeight
        />
      </Grid>
    </Grid>

  ) : null;
};

const mapStateToProps = (state: State, props) => ({
  initialValues: getFormInitialValues(props.form)(state),
  lockedDate: state.lockedDate,
  adminSites: state.sites.adminSites
});

export default connect<any, any, any>(mapStateToProps, null)(PaymentInEditView);
