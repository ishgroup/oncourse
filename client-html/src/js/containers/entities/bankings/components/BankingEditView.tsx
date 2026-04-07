/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Payment } from '@api/model';
import Launch from '@mui/icons-material/Launch';
import { FormControlLabel, IconButton } from '@mui/material';
import Checkbox from '@mui/material/Checkbox';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import $t from '@t';
import clsx from 'clsx';
import { compareAsc, format as formatDate } from 'date-fns';
import { Decimal } from 'decimal.js-light';
import { DD_MMM_YYYY_MINUSED, formatCurrency, III_DD_MMM_YYYY, validateMinMaxDate } from 'ish-ui';
import * as React from 'react';
import { connect } from 'react-redux';
import { change, FieldArray, getFormInitialValues } from 'redux-form';
import FormField from '../../../../common/components/form/formFields/FormField';
import Uneditable from '../../../../common/components/form/formFields/Uneditable';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import NestedTable from '../../../../common/components/list-view/components/list/ReactTableNestedList';
import { LIST_EDIT_VIEW_FORM_NAME } from '../../../../common/components/list-view/constants';
import { validateSingleMandatoryField } from '../../../../common/utils/validation';
import { COMMON_PLACEHOLDER } from '../../../../constants/Forms';
import { NestedTableColumn } from '../../../../model/common/NestedTable';
import { State } from '../../../../reducers/state';
import { openSiteLink } from '../../sites/utils';
import { PaymentInType } from '../consts';

const disabledHandler = (p: Payment) => {
  if (!p) {
    return false;
  }
  return !p.reconcilable;
};

const paymentColumns: NestedTableColumn[] = [
  {
    name: "reconciled",
    title: "Reconciled",
    type: "checkbox",
    disabledHandler,
    width: 80
  },
  {
    name: "source",
    title: "Source"
  },
  {
    name: "paymentTypeName",
    title: "Type"
  },
  {
    name: "status",
    title: "Status"
  },
  {
    name: "paymentMethodName",
    title: "Payment Method",
  },
  {
    name: "contactName",
    title: "Payer Name",
    type: "link",
    linkPath: "contactId",
    linkEntity: "contact",
  },
  {
    name: "created",
    title: "Created",
    type: "date",
  },
  {
    name: "amount",
    title: "Amount",
    type: "currency"
  }
];

const paymentColumnsMinified: NestedTableColumn[] = [
  {
    name: "reconciled",
    title: "Reconciled",
    type: "checkbox",
    disabledHandler,
    width: 80
  },
  {
    name: "contactName",
    title: "Payer Name",
    type: "link",
    linkPath: "contactId",
    linkEntity: "contact",
    width: 120
  },
  {
    name: "amount",
    title: "Amount",
    type: "currency",
    width: 100
  }
];

class BankingEditView extends React.PureComponent<any, any> {
  isAllPaymentsReconciled = () => {
    const { values } = this.props;
    if (values && values.payments && values.payments.length) {
      if (values.payments.filter((p: Payment) => p.reconcilable).length === 0) {
        return false;
      }
      return values.payments.filter((p: Payment) => p.reconcilable).findIndex((p: Payment) => !p.reconciled) < 0;
    }
    return false;
  };

  reconcileAllPayments = (e, checked) => {
    const { form, dispatch, values } = this.props;
    if (values && values.payments) {
      const updatedPayments = values.payments.map((p: Payment) => {
        const reconciled = p.reconcilable ? checked : p.reconciled;
        return {
          ...p,
          reconciled
        };
      });
      dispatch(change(form, "payments", updatedPayments));
    }
  };

  isDateLocked = (lockedDate: any, editRecord: any) => {
    if (!lockedDate || !editRecord) {
      return true;
    }
    return (
      compareAsc(
        new Date(lockedDate),
        new Date(editRecord.settlementDate)
      ) > 0
    );
  };

  validateSettlementDate = (value: any) => {
    const { lockedDate, editRecord } = this.props;
    if (!lockedDate || !editRecord || editRecord.settlementDate === value) {
      return undefined;
    }
    const date = new Date(lockedDate);
    const dateString = date.toISOString();
    return validateMinMaxDate(
      value,
      dateString,
      "",
      `You must choose the settlement date after 'Transaction Locked' date (${formatDate(date, DD_MMM_YYYY_MINUSED)})`
    );
  };

  totalAmount = () => {
    const { values, currency } = this.props;
    const shortCurrencySymbol = currency != null ? currency.shortCurrencySymbol : "$";
    if (!values || !values.payments) {
      return (
        <Typography component="span" variant="body1" className="placeholderContent">
          {COMMON_PLACEHOLDER}
        </Typography>
      );
    }
    const total = values.payments.reduce(
      (a, v) => (v.paymentTypeName === PaymentInType ? a.plus(v.amount) : a.minus(v.amount)),
      new Decimal(0)
    );
    return (
      <Typography component="span" variant="body1" className="money">
        {formatCurrency(total, shortCurrencySymbol)}
      </Typography>
    );
  };

  onSettlementDateChanged = (v: any, newValue: string, prevValue: string) => {
    const { values, form } = this.props;
    if (newValue !== prevValue && values) {
      values.payments.filter(v => v.reconcilable).forEach(v => (v.reconciled = false));
      change(form, "payments", values.payments);
    }
  };

  paymentsTitle = () => {
    const { payments } = this.props;
    if (!payments) {
      return "Payments";
    }
    return "Payment" + (payments.length !== 1 ? "s" : "");
  };

  isReconcileAllDisabled = () => {
    const { values } = this.props;
    return values && values.payments && values.payments.filter((p: Payment) => p.reconcilable).length === 0;
  };

  getHeader = () => {
    const { values } = this.props;
    return values ? formatDate(new Date(values.settlementDate), III_DD_MMM_YYYY) + " (edit)" : null;
  };

  render() {
    const {
      twoColumn,
      lockedDate,
      editRecord,
      openNestedView,
      values,
      isNew,
    } = this.props;

    return (
      <div className={clsx("pl-3 pr-3 h-100 d-flex flex-column", twoColumn ? "pt-2" : "pt-3")}>
        <Grid container columnSpacing={3} rowSpacing={2}>
          <Grid item xs={12}>
            <FullScreenStickyHeader
              disableInteraction={!isNew}
              twoColumn={twoColumn}
              title={(
                <div className="d-inline-flex-center">
                  {values?.administrationCenterId
                    ? (
                      <>
                        {values?.adminSite}
                        <IconButton size="small" color="primary" onClick={() => openSiteLink(values?.administrationCenterId)}>
                          <Launch fontSize="inherit" />
                        </IconButton>
                      </>
                    )
                    : this.getHeader()}
                </div>
              )}
            />
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <FormField
              type="date"
              disabled={this.isDateLocked(lockedDate, editRecord)}
              name="settlementDate"
              label={$t('settlement_date')}
              onBlur={this.onSettlementDateChanged}
              validate={[validateSingleMandatoryField, this.validateSettlementDate]}
            />
          </Grid>
          <Grid item xs={twoColumn ? 4 : 12}>
            <Uneditable
              label={$t('created_by')}
              value={values?.createdBy}
            />
          </Grid>
          <Grid item xs={12}>
            <FormControlLabel
              control={<Checkbox onChange={this.reconcileAllPayments} checked={this.isAllPaymentsReconciled()} />}
              label={$t('reconcile_this_banking_deposit')}
              disabled={this.isReconcileAllDisabled()}
            />
          </Grid>
        </Grid>
        <FieldArray
          name="payments"
          className="saveButtonTableOffset"
          goToLink={`/paymentIn?search=banking.id=${values?.id}`}
          title={this.paymentsTitle()}
          component={NestedTable}
          removeEnabled={!this.isDateLocked(lockedDate, editRecord)}
          columns={twoColumn ? paymentColumns : paymentColumnsMinified}
          onRowDoubleClick={openNestedView}
          total={this.totalAmount()}
          rerenderOnEveryChange
        />
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  currency: state.location.currency,
  payments: state.banking.payments,
  lockedDate: state.lockedDate,
  editRecord: state.list.editRecord,
  initial: getFormInitialValues(LIST_EDIT_VIEW_FORM_NAME)(state)
});

export default connect<any, any, any>(mapStateToProps, null)(BankingEditView);
