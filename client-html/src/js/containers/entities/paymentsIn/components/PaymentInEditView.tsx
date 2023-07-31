/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import { addDays, compareAsc, format as formatDate } from "date-fns";
import { PaymentIn } from "@api/model";
import Typography from "@mui/material/Typography";
import { FieldArray, getFormInitialValues } from "redux-form";
import { Checkbox, FormControlLabel, Grid } from "@mui/material";
import { connect } from "react-redux";
import { ContactLinkAdornment } from "../../../../common/components/form/formFields/FieldAdornments";
import FormField from "../../../../common/components/form/formFields/FormField";
import { D_MMM_YYYY, III_DD_MMM_YYYY } from  "ish-ui";
import { openInternalLink } from "ish-ui";
import { NestedTableColumn } from "../../../../model/common/NestedTable";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import Uneditable from "../../../../common/components/form/formFields/Uneditable";
import { State } from "../../../../reducers/state";
import { LinkAdornment } from  "ish-ui";
import { getAdminCenterLabel, openSiteLink } from "../../sites/utils";
import { SiteState } from "../../sites/reducers/state";

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
          label="Payment from"
          labelAdornment={
            <ContactLinkAdornment id={values?.payerId} />
          }
        />
      </Grid>
      <Grid item {...gridItemProps}>
        <FormField
          type="select"
          name="administrationCenterId"
          label="Site"
          defaultValue={values && values.administrationCenterName}
          selectLabelCondition={getAdminCenterLabel}
          items={adminSites || []}
          labelAdornment={<LinkAdornment link={values && values.administrationCenterId} linkHandler={openSiteLink} />}
          disabled={!!initialValues.dateBanked}
        />
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable value={values.paymentInType} label="Type" />
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable value={values.status} label="Status" />
      </Grid>
      {values.ccSummary && values.ccSummary.length > 0 && (
      <Grid item {...gridItemProps}>
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
      <Grid item {...gridItemProps}>
        {Object.keys(values.chequeSummary).map(item => (
          <Uneditable value={values.chequeSummary[item]} label={item} />
            ))}
      </Grid>
        )}
      <Grid item {...gridItemProps}>
        <Uneditable value={values.amount} money label="Amount" />
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable value={values.accountInName} label="Account" />
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable value={values.source} label="Source" />
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable value={values.ccTransaction} label="CC transaction" />
      </Grid>
      <Grid item {...gridItemProps}>
        <FormControlLabel
          className="pr-3"
          control={<Checkbox checked={values.emailConfirmation} />}
          label="Email confirmation"
          disabled
        />
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable
          value={values.datePayed}
          label="Date paid"
          format={value => (value ? formatDate(new Date(value), III_DD_MMM_YYYY) : value)}
        />
      </Grid>
      <Grid item {...gridItemProps}>
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
              validate={[validateSettlementDate, validateSettlementDateBanked]}
            />
          )}
      </Grid>
      <Grid item {...gridItemProps}>
        <Uneditable value={values.createdBy} label="Created by" />
      </Grid>
      <Grid item xs={12} className="saveButtonTableOffset">
        <FieldArray
          name="invoices"
          goToLink="/invoice"
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
