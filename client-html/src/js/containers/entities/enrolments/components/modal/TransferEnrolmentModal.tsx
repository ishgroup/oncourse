/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useMemo } from "react";
import { Dispatch } from "redux";
import { FieldArray, getFormValues, initialize, InjectedFormProps, reduxForm } from "redux-form";
import { connect } from "react-redux";
import withStyles from "@material-ui/core/styles/withStyles";
import Dialog from "@material-ui/core/Dialog";
import DialogContent from "@material-ui/core/DialogContent";
import Grid from "@material-ui/core/Grid/Grid";
import Typography from "@material-ui/core/Typography";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import DialogActions from "@material-ui/core/DialogActions";
import MuiButton from "@material-ui/core/Button";
import { Account, CancelEnrolment, Tax } from "@api/model";
import { getUserPreferences } from "../../../../../common/actions";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID } from "../../../../../constants/Config";
import { BooleanArgFunction } from "../../../../../model/common/CommonFunctions";
import { State } from "../../../../../reducers/state";
import Button from "../../../../../common/components/buttons/Button";
import { cancelEnrolment, setEnrolmentTransfered } from "../../actions";
import { openInternalLink } from "../../../../../common/utils/links";
import WarningMessage from "../../../../../common/components/form/fieldMessage/WarningMessage";
import { useOutcomeWarnings } from "./hooks";
import { enrolmentModalStyles } from "./styles";
import TransferEnrolmentInvoiceLines from "./TransferEnrolmentInvoiceLines";

export const FORM: string = "TRANSFER_ENROLMENT_MODAL_FORM";

interface TransferEnrolmentModalProps {
  opened: boolean;
  setDialogOpened: BooleanArgFunction;
  closeMenu?: any;
  selection?: any;
  value?: TransferEnrolmentModalValues;
  classes?: any;
  invoiceLines?: any;
  accounts?: Account[];
  taxes?: Tax[];
  dispatch?: any;
  transferEnrolment?: (values: CancelEnrolment) => void;
  setEnrolmentSearch?: (id: string) => void;
  getPlainEnrolmentRecord?: () => void;
  enrolmentPlainRecord?: any;
  getDefaultIncomeAccount?: () => void;
  clearEnrolmentTransfered?: () => void;
  defaultIncomeAccountId?: string;
  isTransfered?: boolean;
}

interface InvoiceLine {
  id: any;
  isReverseCreditNotes: boolean;
  isChargeFee: boolean;
  transfer: boolean;
  sendInvoice: boolean;
  chargedFee: number;
  cancellationFeeExTax: number;
  taxId: number;
  incomeAccountId: number;
}

interface TransferEnrolmentModalValues {
  deleteNotSetOutcomes: boolean;
  invoices: InvoiceLine[];
}

const initialValues: TransferEnrolmentModalValues = {
  deleteNotSetOutcomes: true,
  invoices: []
};

const InvoiceInitialValues: InvoiceLine = {
  id: null,
  isReverseCreditNotes: true,
  isChargeFee: false,
  transfer: false,
  sendInvoice: false,
  chargedFee: 0,
  cancellationFeeExTax: 0,
  taxId: 1,
  incomeAccountId: 15
};

const sortAccounts = (a: Account, b: Account) => (a.description[0].toLowerCase() > b.description[0].toLowerCase() ? 1 : -1);

const TransferEnrolmentModalForm = React.memo<TransferEnrolmentModalProps & InjectedFormProps>(props => {
  const {
    opened,
    selection,
    setDialogOpened,
    closeMenu,
    handleSubmit,
    classes,
    value,
    invoiceLines,
    accounts,
    taxes,
    dispatch,
    transferEnrolment,
    isTransfered,
    clearEnrolmentTransfered,
    enrolmentPlainRecord,
    getDefaultIncomeAccount,
    defaultIncomeAccountId
  } = props;

  const [loading, setLoading] = React.useState(false);
  const [plainEnrolmentRecord, setPlainEnrolmentRecord] = React.useState({
    studentName: "",
    courseClassName: "",
    courseClassCode: ""
  });

  const [exportOutcomesWarning, certificateOutcomesWarning] = useOutcomeWarnings(enrolmentPlainRecord, dispatch, selection);

  React.useEffect(() => {
    getDefaultIncomeAccount();
  });

  React.useEffect(() => {
    if (!invoiceLines) return;

    if (defaultIncomeAccountId && (Number(defaultIncomeAccountId) !== InvoiceInitialValues.incomeAccountId)) {
      InvoiceInitialValues.incomeAccountId = Number(defaultIncomeAccountId);
    }

    const formInvoices = [...invoiceLines].map(i => ({ ...InvoiceInitialValues, ...i }));

    const initial = { ...initialValues, invoices: formInvoices };

    dispatch(initialize(FORM, initial ));
  }, [invoiceLines, defaultIncomeAccountId]);

  React.useEffect(() => {
    if (enrolmentPlainRecord && enrolmentPlainRecord.items.length > 0) {
      const enrolmentItem = enrolmentPlainRecord.items[0];
      const firstName = enrolmentItem["student.contact.firstName"];
      const lastName = enrolmentItem["student.contact.lastName"];

      setPlainEnrolmentRecord({
        studentName: firstName === lastName ? lastName : `${firstName} ${lastName}`,
        courseClassName: enrolmentItem["courseClass.course.name"],
        courseClassCode: `${enrolmentItem["courseClass.course.code"]}-${enrolmentItem["courseClass.code"]}`
      });
    }
  }, [enrolmentPlainRecord]);

  const onClose = () => {
    setDialogOpened(null);
    setLoading(false);
  };

  const incomeAccounts = useMemo(() => {
    const income = accounts.filter(a => a.type === "income");
    income.sort(sortAccounts);
    return income;
  }, [accounts.length]);

  const onSubmit = () => {
    const invoiceLineData = [...value.invoices]
      .filter(i => i.isReverseCreditNotes)
      .map(i => {
        let accountID = null;
        let cancelFee = null;

        if (i.isChargeFee) {
          accountID = i.incomeAccountId;
          cancelFee = i.cancellationFeeExTax;
        }

        return {
          invoiceLineId: i.id,
          accountId: accountID,
          cancellationFee: cancelFee,
          sendInvoice: i.sendInvoice,
          taxId: i.taxId
        };
      });

    const transferParams = {
      enrolmentIds: selection[0],
      deleteNotSetOutcomes: value.deleteNotSetOutcomes,
      transfer: true,
      invoiceLineParam: invoiceLineData
    };

    transferEnrolment(transferParams);

    setDialogOpened(null);
    setLoading(true);
  };

  useEffect(() => {
    if (isTransfered) {
      setTimeout(() => {
        openInternalLink(`/checkout?enrolmentId=${selection[0]}`);
      }, 1000);
      clearEnrolmentTransfered();
      setDialogOpened(null);
      closeMenu();
    }
  }, [isTransfered, selection]);

  return (
    <Dialog fullWidth maxWidth="md" open={opened} onClose={onClose}>
      <form autoComplete="off" noValidate onSubmit={handleSubmit(onSubmit)}>
        <DialogContent>
          <Grid container>
            <Grid item xs={12}>
              <div className="centeredFlex">
                <div className="heading mt-2 mb-2">Transfer Enrolment</div>
              </div>

              <Typography variant="body2">
                {plainEnrolmentRecord.studentName}
                {' '}
                is currently enrolled in
                {plainEnrolmentRecord.courseClassName}
                {" "}
                {plainEnrolmentRecord.courseClassCode}
                . This enrolment will be cancelled and a credit note created (if
                selected below). Quick Enrol will then open and allow you to complete the enrolment into the new class,
                using the credit created here.
              </Typography>

              <FormControlLabel
                classes={{
                  root: "checkbox"
                }}
                control={<FormField type="checkbox" name="deleteNotSetOutcomes" color="secondary" />}
                label={`Delete outcomes linked to this enrolment with status "not set"`}
              />

              {value && value.deleteNotSetOutcomes
              && (
                <>
                  {exportOutcomesWarning && <WarningMessage className="m-0" warning={exportOutcomesWarning} />}
                  {certificateOutcomesWarning && <WarningMessage warning={certificateOutcomesWarning} />}
                </>
              )}
            </Grid>
          </Grid>

          <div className="pt-2">
            <FieldArray
              component={TransferEnrolmentInvoiceLines}
              name="invoices"
              dispatch={dispatch}
              incomeAccounts={incomeAccounts}
              taxes={taxes}
              classes={classes}
              rerenderOnEveryChange
            />
          </div>
        </DialogContent>

        <DialogActions className="p-1">
          <MuiButton color="primary" onClick={onClose}>
            Cancel
          </MuiButton>

          <Button color="primary" type="submit" loading={loading}>
            Proceed
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
});

const mapStateToProps = (state: State) => ({
  value: getFormValues(FORM)(state),
  invoiceLines: state.enrolments.invoiceLines,
  isTransfered: state.enrolments.isTransfered,
  accounts: state.plainSearchRecords.Account.items,
  taxes: state.taxes.items,
  enrolmentPlainRecord: state.plainSearchRecords["Enrolment"],
  defaultIncomeAccountId: state.userPreferences[ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID]
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  transferEnrolment: (values: CancelEnrolment) => dispatch(cancelEnrolment(values, "transfer")),
  getDefaultIncomeAccount: () => dispatch(getUserPreferences([ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID])),
  clearEnrolmentTransfered: () => dispatch(setEnrolmentTransfered(false))
});

const TransferEnrolmentModal = reduxForm({
  form: FORM
})(TransferEnrolmentModalForm);

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(enrolmentModalStyles)(TransferEnrolmentModal));
