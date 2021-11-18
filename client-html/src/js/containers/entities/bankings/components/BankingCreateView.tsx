/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import {
 change, FieldArray, getFormInitialValues, initialize
} from "redux-form";
import FormControlLabel from "@mui/material/FormControlLabel";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import { addDays, format as formatDate } from "date-fns";
import { Report } from "@api/model";
import EditInPlaceField from "../../../../common/components/form/formFields/EditInPlaceField";
import FormField from "../../../../common/components/form/formFields/FormField";
import { NestedTableColumn } from "../../../../model/common/NestedTable";
import { State } from "../../../../reducers/state";
import { getFormattedTotal } from "../../common/bankingPaymentUtils";
import { validateMinMaxDate } from "../../../../common/utils/validation";
import { getDepositPayments, updateBankingAccountId } from "../actions";
import { BankingReport } from "../consts";
import { DD_MMM_YYYY_MINUSED, YYYY_MM_DD_MINUSED } from "../../../../common/utils/dates/format";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { SYSTEM_USER_ADMINISTRATION_CENTER } from "../../../../constants/Config";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { getAdminCenterLabel, openSiteLink } from "../../sites/utils";
import { StyledCheckbox } from "../../../../common/components/form/formFields/CheckboxField";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";

const paymentColumns: NestedTableColumn[] = [
  {
    name: "selected",
    title: " ",
    type: "checkbox"
  },
  {
    name: "paymentTypeName",
    title: "Payment"
  },
  {
    name: "paymentMethodName",
    title: "Type"
  },
  {
    name: "status",
    title: "Status"
  },
  {
    name: "paymentDate",
    title: "Date",
    type: "date"
  },
  {
    name: "contactName",
    title: "Contact Name",
    type: "link",
    linkPath: "contactId",
    linkEntity: "contact"
  },
  {
    name: "amount",
    title: "Amount",
    type: "currency"
  }
];

class BankingCreateView extends React.PureComponent<any, any> {
  private paymentsAreSet;

  constructor(props) {
    super(props);

    this.paymentsAreSet = false;
  }

  componentDidUpdate() {
    const {
 accounts, adminSites, dispatch, adminCenterName, form, values
} = this.props;

    if (!values) {
      return;
    }

    if (!this.paymentsAreSet && accounts && accounts.length && values.administrationCenterId) {
      this.paymentsAreSet = true;
      dispatch(getDepositPayments(accounts[0].id, values.administrationCenterId));
    }

    if (!values.administrationCenterId && !values.adminSite && adminCenterName && adminSites && adminSites.length) {
      dispatch(
        initialize(form, {
          ...values,
          adminSite: adminCenterName,
          administrationCenterId: adminSites.find(s => s.label === adminCenterName).value
        })
      );
    }
  }

  totalAmount = () => {
    const { values, currency } = this.props;
    if (!values || !values.payments) {
      return (
        <Typography variant="body1" className="placeholderContent">
          No value
        </Typography>
      );
    }

    return (
      <Typography variant="body1" className="money">
        {getFormattedTotal(values.payments, currency, true)}
      </Typography>
    );
  };

  isAllSelected = () => {
    const { values } = this.props;
    if (values && values.payments) {
      return values.payments.length === values.payments.filter(v => v.selected).length;
    }
    return false;
  };

  getSelectedCount = () => {
    const { values } = this.props;
    if (values && values.payments) {
      return values.payments.filter(v => v.selected).length;
    }
    return 0;
  };

  selectAll = (e, checked) => {
    const { values, dispatch, form } = this.props;
    if (values && values.payments) {
      const updatedPayments = values.payments.map(v => ({
          ...v,
          selected: checked
        }));
      dispatch(change(form, "payments", updatedPayments));
    }
  };

  validateSettlementDate = (value: any) => {
    const { lockedDate, editRecord } = this.props;
    if (!lockedDate || !editRecord || editRecord.settlementDate === value) {
      return undefined;
    }
    const date = new Date(lockedDate.year, lockedDate.monthValue - 1, lockedDate.dayOfMonth);
    const dateString = date.toISOString();
    return validateMinMaxDate(
      value,
      dateString,
      "",
      `You must choose the settlement date after 'Transaction Locked' date (${formatDate(date, DD_MMM_YYYY_MINUSED)})`
    );
  };

  onChangeAccount = (id: number) => {
    const { dispatch, values } = this.props;
    dispatch(getDepositPayments(id, values.administrationCenterId));
    dispatch(updateBankingAccountId(id));
  };

  paymentsTitle = () => {
    const { payments } = this.props;
    if (!payments) {
      return "Payments";
    }
    return "Payment" + (payments.length !== 1 ? "s" : "");
  };

  hasNoAccounts = () => {
    const { accounts, values } = this.props;
    return !accounts || accounts.length === 0 || (values && !values.payments);
  };

  onSiteIdChange = (id: number) => {
    const { dispatch, selectedAccountId } = this.props;
    dispatch(getDepositPayments(selectedAccountId, id));
  };

  render() {
    const {
      accounts,
      lockedDate,
      openNestedView,
      selectedAccountId,
      values,
      adminSites,
    } = this.props;

    const hasNoAccounts = this.hasNoAccounts();

    return (
      <div className="">
        <FullScreenStickyHeader
          title={values && adminSites && adminSites.filter(s => s.value === values.administrationCenterId).map(s => s.label)}
          fields={(
            <Grid container columnSpacing={0} className="flex-fill">
              <Grid item xs={6}>
                <FormField
                  type="searchSelect"
                  name="administrationCenterId"
                  placeholder="Administration center"
                  selectLabelCondition={getAdminCenterLabel}
                  onChange={this.onSiteIdChange as any}
                  items={adminSites || []}
                  endAdornment={(
                    <LinkAdornment
                      link={values && values.administrationCenterId}
                      linkHandler={openSiteLink}
                      linkColor="inherit"
                      className="appHeaderFontSize"
                    />
                  )}
                  fieldClasses={{
                    text: "appHeaderFontSize",
                    selectMenu: "textPrimaryColor",
                  }}
                  required
                />
              </Grid>
            </Grid>
          )}
          twoColumn
        />
        <div className="flex-column p-3 h-100">
          <Grid container columnSpacing={3}>
            <Grid item xs={4}>
              <EditInPlaceField
                items={accounts || []}
                label="Account"
                input={{ name: "id", value: selectedAccountId, onChange: this.onChangeAccount }}
                meta={{ error: null, invalid: false, touched: false }}
                selectValueMark="id"
                selectLabelMark="description"
                select
                disabled={hasNoAccounts}
              />
            </Grid>
            <Grid item xs={4}>
              <FormField
                name="settlementDate"
                label="Date banked"
                type="date"
                normalize={v => (v ? formatDate(new Date(v), YYYY_MM_DD_MINUSED) : v)}
                validate={this.validateSettlementDate}
                minDate={
                  lockedDate
                    ? addDays(new Date(lockedDate.year, lockedDate.monthValue - 1, lockedDate.dayOfMonth), 1)
                    : undefined
                }
                disabled={hasNoAccounts}
              />
            </Grid>
            <Grid item xs={4} />
            <Grid item xs={4}>
              <FormControlLabel
                classes={{
                  root: "pr-3 checkbox"
                }}
                control={<StyledCheckbox onChange={this.selectAll} checked={this.isAllSelected()} />}
                label={
                  "Select all ("
                  + this.getSelectedCount()
                  + " payment"
                  + (this.getSelectedCount() > 1 ? "s" : "")
                  + " selected)"
                }
                disabled={hasNoAccounts}
              />
            </Grid>
          </Grid>

          <FieldArray
            name="payments"
            className="saveButtonTableOffset"
            goToLink="/paymentIn"
            title={this.paymentsTitle()}
            total={this.totalAmount()}
            component={NestedTable}
            columns={paymentColumns}
            onRowDoubleClick={openNestedView}
            rerenderOnEveryChange
          />
        </div>
      </div>
    );
  }
}

const findReport = (reports: Report[], reportName: string): Report => (reports ? reports.find(v => v.name === reportName) : null);

const mapStateToProps = (state: State) => ({
  accounts: state.banking.accounts,
  adminSites: state.sites.adminSites,
  payments: state.banking.payments,
  selectedAccountId: state.banking.selectedAccountId,
  lockedDate: state.lockedDate,
  currency: state.currency,
  editRecord: state.list.editRecord,
  initial: getFormInitialValues(LIST_EDIT_VIEW_FORM_NAME)(state),
  report: findReport(state.share.pdfReports as Report[], BankingReport),
  adminCenterName: state.userPreferences[SYSTEM_USER_ADMINISTRATION_CENTER]
});

export default connect<any, any, any>(mapStateToProps, null)(BankingCreateView);
