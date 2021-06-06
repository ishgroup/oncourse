/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import { FieldArray, getFormInitialValues } from "redux-form";
import { connect } from "react-redux";
import { addDays, compareAsc, format } from "date-fns";
import { Grid } from "@material-ui/core";
import { PaymentMethod, PaymentOut } from "@api/model";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { D_MMM_YYYY, III_DD_MMM_YYYY } from "../../../../common/utils/dates/format";
import { openInternalLink } from "../../../../common/utils/links";
import { NestedTableColumn } from "../../../../model/common/NestedTable";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import Uneditable from "../../../../common/components/form/Uneditable";
import { State } from "../../../../reducers/state";
import { SiteState } from "../../sites/reducers/state";
import { getAdminCenterLabel, openSiteLink } from "../../sites/utils";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { EditViewProps } from "../../../../model/common/ListView";
import { defaultContactName, openContactLink } from "../../contacts/utils";

const invoiceColumns: NestedTableColumn[] = [
  {
    name: "dateDue",
    title: "Due date",
    type: "date",
    width: 160
  },
  {
    name: "invoiceNumber",
    title: "Invoice #",
    width: 80
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

interface PaymentOutEditViewProps extends EditViewProps<PaymentOut> {
  classes?: any;
  twoColumn?: boolean;
  lockedDate?: any;
  initialValues?: PaymentOut;
  adminSites?: SiteState["adminSites"];
  paymentMethods?: PaymentMethod[];
  accountItems?: any;
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
      addDays(new Date(lockedDate.year, lockedDate.monthValue - 1, lockedDate.dayOfMonth), 1),
      new Date(settlementDate)
    ) > 0
  );
};

const isDatePayedLocked = (lockedDate: any, datePayed: any, settlementDate: any) => {
  if (!lockedDate) {
    return true;
  }
  if (!datePayed) {
    return false;
  }
  if (settlementDate) {
    return true;
  }
  return (
    compareAsc(
      addDays(new Date(lockedDate.year, lockedDate.monthValue - 1, lockedDate.dayOfMonth), 1),
      new Date(datePayed)
    ) > 0
  );
};

const getPaymentNameById = (paymentMethods: PaymentMethod[], id: number) => paymentMethods.find(payment => payment.id === id).name;

const validateSettlementDatePayed = (settlementDate, allValues) => {
  if (!settlementDate) {
    return `Date must be set`;
  }
  if (allValues.dateBanked && compareAsc(new Date(settlementDate), new Date(allValues.dateBanked)) > 0) {
    return `Date paid must be before or equal to date banked`;
  }

  return undefined;
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

const PaymentOutEditView: React.FC<PaymentOutEditViewProps> = props => {
  const {
 twoColumn, values, lockedDate, initialValues, paymentMethods, accountItems, adminSites
} = props;

  const validateLockedDate = useCallback(
    settlementDate => {
      if (!lockedDate || !settlementDate ) return undefined;
      const lockedDateValue = new Date(lockedDate.year, lockedDate.monthValue - 1, lockedDate.dayOfMonth);
      return compareAsc(lockedDateValue, new Date(settlementDate)) === 1
        ? `You must choose date after "Transaction locked" date (${format(lockedDateValue, D_MMM_YYYY)})`
        : undefined;
    },
    [lockedDate]
  );

  const getAccountById = (accountItems, id) => {
    const account = accountItems.find(account => account.id === id);
    if (!account) return "Account is disabled";
    const accountDescription = account.description;
    const { accountCode } = account;
    let accountName = "";

    if (accountDescription && accountCode) {
      accountName = `${accountDescription} ${accountCode}`;
    }
    return accountName;
  };

  const datePayedDisabled = values.dateBanked && (
    isDatePayedLocked(lockedDate, initialValues.datePayed, values.dateBanked)
    || ["Contra", "Internal", "Reverse", "Voucher"].includes(values.type)
    || !["Success", "Reversed"].includes(values.status));

  const dateBankedDisabled = isDateLocked(lockedDate, initialValues.dateBanked)
    || ["Contra", "Internal", "Reverse", "Voucher"].includes(values.type)
    || !["Success", "Reversed"].includes(values.status);

  return (
    <div className="flex-column p-3 h-100">
      <Grid container>
        <Grid item xs={twoColumn ? 6 : 12}>
          <Uneditable
            value={defaultContactName(values.payeeName)}
            label="Payment to"
            labelAdornment={<LinkAdornment link={values && values.payeeId} linkHandler={openContactLink} />}
          />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="searchSelect"
            name="administrationCenterId"
            label="Site"
            defaultDisplayValue={values && values.administrationCenterName}
            selectLabelCondition={getAdminCenterLabel}
            items={adminSites || []}
            labelAdornment={<LinkAdornment link={values && values.administrationCenterId} linkHandler={openSiteLink} />}
            disabled={initialValues.dateBanked}
          />
        </Grid>
        <Grid item xs={twoColumn ? 2 : 6}>
          <Uneditable value={paymentMethods && getPaymentNameById(paymentMethods, values.paymentMethodId)} label="Type" />
        </Grid>
        <Grid item xs={twoColumn ? 2 : 6}>
          <Uneditable value={values.status} label="Status" />
        </Grid>
      </Grid>
      <Grid container>
        <Grid item xs={6}>
          <Uneditable value={getAccountById(accountItems, values.accountOut)} label="Account" />
        </Grid>
      </Grid>
      <Grid container>
        {values.chequeSummary && Object.keys(values.chequeSummary).length > 0 && (
        <Grid item xs={twoColumn ? 2 : 6}>
          {Object.keys(values.chequeSummary).map(item => (
            <Uneditable value={values.chequeSummary[item]} label={item} />
          ))}
        </Grid>
        )}
        <Grid item xs={twoColumn ? 2 : 6}>
          <Uneditable value={values.amount} money label="Amount" />
        </Grid>
      </Grid>
      <Grid container>
        <Grid item xs={twoColumn ? 2 : 6}>
          {datePayedDisabled
            ? <Uneditable value={values.datePayed} format={v => v && format(new Date(v), III_DD_MMM_YYYY)} label="Date paid" />
          : (
            <FormField
              type="date"
              name="datePayed"
              label="Date paid"
              validate={[validateSettlementDatePayed, validateLockedDate]}
              minDate={
                lockedDate
                  ? addDays(new Date(lockedDate.year, lockedDate.monthValue - 1, lockedDate.dayOfMonth), 1)
                  : undefined
              }
            />
          )}
        </Grid>
        <Grid item xs={twoColumn ? 2 : 6}>
          {dateBankedDisabled
            ? <Uneditable value={values.dateBanked} format={v => v && format(new Date(v), III_DD_MMM_YYYY)} label="Date banked" />
            : (
              <FormField
                type="date"
                name="dateBanked"
                label="Date banked"
                validate={[validateSettlementDateBanked, validateLockedDate]}
                minDate={
                lockedDate
                  ? addDays(new Date(lockedDate.year, lockedDate.monthValue - 1, lockedDate.dayOfMonth), 1)
                  : undefined
              }
              />
)}
        </Grid>
        <Grid item xs={twoColumn ? 9 : 12}>
          <FormField type="multilineText" name="privateNotes" label="Private notes" fullWidth />
        </Grid>
        <Grid item xs={12}>
          <Uneditable value={values.createdBy} label="Created by" />
        </Grid>
      </Grid>
      <FieldArray
        name="invoices"
        goToLink="/invoice"
        title={(values && values.invoices && values.invoices.length) === 1 ? "Invoice" : "Invoices"}
        component={NestedTable}
        columns={invoiceColumns}
        onRowDoubleClick={openRow}
        rerenderOnEveryChange
        sortBy={(a, b) => b.invoiceNumber - a.invoiceNumber}
      />
    </div>
);
};

const mapStateToProps = (state: State, props) => ({
  lockedDate: state.lockedDate,
  initialValues: getFormInitialValues(props.form)(state),
  paymentMethods: state.paymentsOut.paymentOutMethods,
  accountItems: state.plainSearchRecords.Account.items,
  adminSites: state.sites.adminSites
});

export default connect<any, any, any>(mapStateToProps, null)(
  (props: any) => (props.values ? <PaymentOutEditView {...props} /> : null)
);
