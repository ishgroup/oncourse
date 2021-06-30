/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import { addDays, compareAsc, format as formatDate } from "date-fns";
import Typography from "@material-ui/core/Typography";
import { FieldArray, getFormInitialValues } from "redux-form";
import { Checkbox, FormControlLabel, Grid } from "@material-ui/core";
import { connect } from "react-redux";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { D_MMM_YYYY, III_DD_MMM_YYYY } from "../../../../common/utils/dates/format";
import { openInternalLink } from "../../../../common/utils/links";
import { NestedTableColumn } from "../../../../model/common/NestedTable";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import Uneditable from "../../../../common/components/form/Uneditable";
import { State } from "../../../../reducers/state";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { getAdminCenterLabel, openSiteLink } from "../../sites/utils";
import { defaultContactName, openContactLink } from "../../contacts/utils";
import { EditViewProps } from "../../../../model/common/ListView";

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

const PaymentInEditView: React.FC<EditViewProps & ReturnType<typeof mapStateToProps>> = props => {
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

  return values ? (
    <div className="p-3 h-100 flex-column">
      <Grid container>
        <Grid item xs={twoColumn ? 4 : 12}>
          <Uneditable
            value={defaultContactName(values.payerName)}
            label="Payment from"
            labelAdornment={<LinkAdornment link={values && values.payerId} linkHandler={openContactLink} />}
          />

        </Grid>
        <Grid item xs={twoColumn ? 8 : false} />

        <Grid item xs={twoColumn ? 4 : 12}>
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
        <Grid item xs={twoColumn ? 8 : false} />

        <Grid item xs={twoColumn ? 2 : 6}>
          <Uneditable value={values.paymentInType} label="Type" />
        </Grid>
        <Grid item xs={twoColumn ? 2 : 6}>
          <Uneditable value={values.status} label="Status" />
        </Grid>
      </Grid>
      <Grid container>
        {values.ccSummary && values.ccSummary.length > 0 && (
          <Grid item xs={twoColumn ? 2 : 6}>
            <div className="textField">
              <div>
                <Typography variant="caption" color="textSecondary">
                  Credit card
                </Typography>
                {values.ccSummary.map(line => (
                  <Typography variant="body1">{line}</Typography>
                ))}
              </div>
            </div>
          </Grid>
        )}
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
          <Uneditable value={values.accountInName} label="Account" />
        </Grid>
        <Grid item xs={twoColumn ? 2 : 6}>
          <Uneditable value={values.source} label="Source" />
        </Grid>
      </Grid>
      <Grid container>
        <Grid item xs={twoColumn ? 2 : 6}>
          <Uneditable value={values.ccTransaction} label="CC transaction" />
        </Grid>
        <Grid item xs={twoColumn ? 2 : 6}>
          <FormControlLabel
            className="pr-3"
            control={<Checkbox checked={values.emailConfirmation} />}
            label="Email confirmation"
            disabled
          />
        </Grid>
      </Grid>
      <Grid container>
        <Grid item xs={twoColumn ? 2 : 6}>
          <Uneditable
            value={values.datePayed}
            label="Date paid"
            format={value => (value ? formatDate(new Date(value), III_DD_MMM_YYYY) : value)}
          />
        </Grid>
        <Grid item xs={twoColumn ? 2 : 6}>
          {dateBankedDisabled ? (
            <Uneditable
              value={values.dateBanked}
              label="Date banked"
              format={value => (value ? formatDate(new Date(value), III_DD_MMM_YYYY) : value)}
            />
          ) : (
            <FormField
              type="date"
              name="dateBanked"
              label="Date banked"
              validate={validateSettlementDate}
              minDate={
                lockedDate
                  ? addDays(new Date(lockedDate), 1)
                  : undefined
              }
            />
          )}
        </Grid>
      </Grid>
      <Uneditable value={values.createdBy} label="Created by" />
      <FieldArray
        name="invoices"
        goToLink="/invoice"
        className="saveButtonTableOffset"
        title={(values && values.invoices && values.invoices.length) === 1 ? "Invoice" : "Invoices"}
        component={NestedTable}
        columns={invoiceColumns}
        onRowDoubleClick={openRow}
        rerenderOnEveryChange
        sortBy={(a, b) => b.invoiceNumber - a.invoiceNumber}
      />
    </div>
  ) : null;
};

const mapStateToProps = (state: State, props) => ({
  initialValues: getFormInitialValues(props.form)(state),
  lockedDate: state.lockedDate,
  adminSites: state.sites.adminSites
});

export default connect(mapStateToProps)(PaymentInEditView);
