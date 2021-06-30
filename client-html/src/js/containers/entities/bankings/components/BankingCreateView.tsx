/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { change, FieldArray, getFormInitialValues, initialize } from "redux-form";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import { addDays, format as formatDate } from "date-fns";
import { Report } from "@api/model";
import { connect } from "react-redux";
import EditInPlaceField from "../../../../common/components/form/form-fields/EditInPlaceField";
import FormField from "../../../../common/components/form/form-fields/FormField";
import FormSubmitButton from "../../../../common/components/form/FormSubmitButton";
import { NestedTableColumn } from "../../../../model/common/NestedTable";
import { State } from "../../../../reducers/state";
import { getFormattedTotal } from "../../common/bankingPaymentUtils";
import { validateMinMaxDate } from "../../../../common/utils/validation";
import { getDepositPayments, updateBankingAccountId } from "../actions";
import { BankingReport } from "../consts";
import { DD_MMM_YYYY_MINUSED, YYYY_MM_DD_MINUSED } from "../../../../common/utils/dates/format";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { SYSTEM_USER_ADMINISTRATION_CENTER } from "../../../../constants/Config";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import AppBarHelpMenu from "../../../../common/components/form/AppBarHelpMenu";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { getAdminCenterLabel, openSiteLink } from "../../sites/utils";
import { StyledCheckbox } from "../../../../common/components/form/form-fields/CheckboxField";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import { EditViewProps } from "../../../../model/common/ListView";
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";

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

interface Props {
  openNestedView: AnyArgFunction;
}

class BankingCreateView extends React.PureComponent<Props & EditViewProps & ReturnType<typeof mapStateToProps>, any> {
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
      dispatch(getDepositPayments(Number(accounts[0]?.id), values.administrationCenterId));
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
    const date = new Date(lockedDate);
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
      manualLink,
      values,
      adminSites,
      rootEntity,
      onCloseClick,
      invalid,
      toogleFullScreenEditView
    } = this.props;

    const hasNoAccounts = this.hasNoAccounts();

    return (
      <div className="appBarContainer">
        <CustomAppBar noDrawer>
          <Grid container className="flex-fill">
            <Grid item xs={6}>
              <FormField
                type="searchSelect"
                name="administrationCenterId"
                defaultDisplayValue={values && values.adminSite}
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
                  text: "appHeaderFontSize primaryContarstText primaryContarstHover",
                  input: "primaryContarstText",
                  underline: "primaryContarstUnderline",
                  selectMenu: "textPrimaryColor",
                  loading: "primaryContarstText",
                  editIcon: "primaryContarstText"
                }}
                formatting="inline"
                required
              />
            </Grid>
          </Grid>
          <div>
            {manualLink && (
              <AppBarHelpMenu
                auditsUrl={`audit?search=~"${rootEntity}" and entityId in (${values ? values.id : 0})`}
                manualUrl={manualLink}
              />
            )}

            <Button onClick={hasNoAccounts ? toogleFullScreenEditView : onCloseClick} className="closeAppBarButton">
              Close
            </Button>
            <FormSubmitButton
              disabled={(values && !values.payments)}
              invalid={invalid}
            />
          </div>
        </CustomAppBar>
        <div className="flex-column p-3 h-100">
          <Grid container>
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
                    ? addDays(new Date(lockedDate), 1)
                    : undefined
                }
                fullWidth
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

export default connect(mapStateToProps)(BankingCreateView);
