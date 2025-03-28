/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Account, CancelEnrolment, Tax } from '@api/model';
import LoadingButton from '@mui/lab/LoadingButton';
import { Button, FormControlLabel, Typography } from '@mui/material';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import $t from '@t';
import clsx from 'clsx';
import { BooleanArgFunction, WarningMessage } from 'ish-ui';
import React, { useCallback, useMemo } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { FieldArray, getFormValues, initialize, InjectedFormProps, reduxForm } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { getUserPreferences } from '../../../../../common/actions';
import { getCommonPlainRecords, setCommonPlainSearch } from '../../../../../common/actions/CommonPlainRecordsActions';
import FormField from '../../../../../common/components/form/formFields/FormField';
import { ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID } from '../../../../../constants/Config';
import { State } from '../../../../../reducers/state';
import { cancelEnrolment } from '../../actions';
import CancelEnrolmentInvoiceLines from './CancelEnrolmentInvoiceLines';
import { useOutcomeWarnings } from './hooks';
import { enrolmentModalStyles } from './styles';

interface CancelEnrolmentModalProps {
  opened: boolean;
  setDialogOpened: BooleanArgFunction;
  closeMenu?: any;
  selection?: any;
  value?: CancelEnrolmentModalValues;
  classes?: any;
  invoiceLines?: any;
  accounts?: Account[];
  taxes?: Tax[];
  dispatch?: any;
  cancelEnrolment?: (values: CancelEnrolment) => void;
  setEnrolmentSearch?: (id: string) => void;
  getPlainEnrolmentRecord?: () => void;
  enrolmentPlainRecord?: any;
  getDefaultIncomeAccount?: () => void;
  defaultIncomeAccountId?: string;
}

const FORM: string = "CANCEL_ENROLMENT_FORM";

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

interface CancelEnrolmentModalValues {
  deleteNotSetOutcomes: boolean;
  invoices: InvoiceLine[];
}

const initialValues: CancelEnrolmentModalValues = {
  deleteNotSetOutcomes: true,
  invoices: []
};

const InvoiceInitialValues: InvoiceLine = {
  id: null,
  isReverseCreditNotes: false,
  isChargeFee: false,
  transfer: false,
  sendInvoice: false,
  chargedFee: 0,
  cancellationFeeExTax: 0,
  taxId: 1,
  incomeAccountId: 15
};

const sortAccounts = (a: Account, b: Account) => (a.description[0].toLowerCase() > b.description[0].toLowerCase() ? 1 : -1);

const CancelEnrolmentModalForm = React.memo<CancelEnrolmentModalProps & InjectedFormProps>(props => {
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
    cancelEnrolment,
    setEnrolmentSearch,
    getPlainEnrolmentRecord,
    enrolmentPlainRecord,
    getDefaultIncomeAccount,
    defaultIncomeAccountId
  } = props;

  const [loading, setLoading] = React.useState(false);
  const [plainEnrolmentRecord, setPlainEnrolmentRecord] = React.useState({
    cancelWarningMessage: null,
    studentName: "",
    courseClassName: "",
    courseClassCode: ""
  });

  const [exportOutcomesWarning, certificateOutcomesWarning] = useOutcomeWarnings(enrolmentPlainRecord, dispatch, selection);

  React.useEffect(() => {
    getDefaultIncomeAccount();
    if (selection.length === 1) {
      setEnrolmentSearch(selection[0]);
      getPlainEnrolmentRecord();
    }
  }, []);

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
      const { cancelWarningMessage } = enrolmentItem;
      const firstName = enrolmentItem["student.contact.firstName"];
      const lastName = enrolmentItem["student.contact.lastName"];

      setPlainEnrolmentRecord({
        studentName: firstName === lastName ? lastName : `${firstName} ${lastName}`,
        courseClassName: enrolmentItem["courseClass.course.name"],
        courseClassCode: `${enrolmentItem["courseClass.course.code"]}-${enrolmentItem["courseClass.code"]}`,
        cancelWarningMessage:
          cancelWarningMessage != null && cancelWarningMessage !== "No warnings" ? cancelWarningMessage : null
      });
    }
  }, [enrolmentPlainRecord]);

  const onClose = useCallback(() => {
    setDialogOpened(null);
    setLoading(false);
  }, []);

  const incomeAccounts = useMemo(() => {
    const income = accounts.filter(a => a.type === "income");
    income.sort(sortAccounts);
    return income;
  }, [accounts.length]);

  const onSubmit = useCallback(() => {
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

    const cancelParams = {
      enrolmentIds: selection[0],
      deleteNotSetOutcomes: value.deleteNotSetOutcomes,
      transfer: false,
      invoiceLineParam: invoiceLineData
    };

    cancelEnrolment(cancelParams);

    setDialogOpened(null);
    setLoading(true);
    closeMenu();
  }, [value, invoiceLines]);

  return (
    <Dialog open={opened} onClose={onClose} fullWidth maxWidth="md">
      <form autoComplete="off" onSubmit={handleSubmit(onSubmit)}>
        <DialogContent>
          <div className="heading pt-1">{$t('cancel_enrolment')}</div>
          <div className={clsx("centeredFlex pt-1", !plainEnrolmentRecord.cancelWarningMessage && "invisible")}>
            <Typography variant="body2" className="errorColor">
              {plainEnrolmentRecord.cancelWarningMessage}
            </Typography>
          </div>
          <div className="pt-2">
            <Typography variant="body2">
              {plainEnrolmentRecord.studentName}
              {$t('is_currently_enrolled_in')}
              {plainEnrolmentRecord.courseClassName}
              {" "}
              {plainEnrolmentRecord.courseClassCode}
            </Typography>
            <FormControlLabel
              control={<FormField type="checkbox" name="deleteNotSetOutcomes" color="secondary" />}
              label={`Delete outcomes linked to this enrolment with status "not set"`}
            />

            {value && value.deleteNotSetOutcomes
              && (
              <>
                {exportOutcomesWarning && <WarningMessage className="zeroTopMargin" warning={exportOutcomesWarning} />}
                {certificateOutcomesWarning && <WarningMessage warning={certificateOutcomesWarning} />}
              </>
              )}
          </div>

          <div className="pt-2">
            <FieldArray
              component={CancelEnrolmentInvoiceLines}
              name="invoices"
              dispatch={dispatch}
              incomeAccounts={incomeAccounts}
              taxes={taxes}
              classes={classes}
              rerenderOnEveryChange
            />
          </div>
        </DialogContent>
        <DialogActions className="p-3">
          <Button color="primary" onClick={onClose}>
            {$t('cancel')}
          </Button>
          <LoadingButton variant="contained" color="primary" type="submit" loading={loading}>
            {$t('proceed')}
          </LoadingButton>
        </DialogActions>
      </form>
    </Dialog>
  );
});

const mapStateToProps = (state: State) => ({
  invoiceLines: state.enrolments.invoiceLines,
  value: getFormValues(FORM)(state),
  accounts: state.plainSearchRecords.Account.items,
  taxes: state.taxes.items,
  enrolmentPlainRecord: state.plainSearchRecords["Enrolment"],
  defaultIncomeAccountId: state.userPreferences[ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID]
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  cancelEnrolment: (values: CancelEnrolment) => dispatch(cancelEnrolment(values, "cancel")),
  setEnrolmentSearch: id => dispatch(setCommonPlainSearch("Enrolment", `id is ${id}`)),
  getDefaultIncomeAccount: () => dispatch(getUserPreferences([ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID])),
  getPlainEnrolmentRecord: () => dispatch(
    getCommonPlainRecords(
      "Enrolment",
      0,
      
      "cancelWarningMessage,student.contact.lastName,student.contact.firstName,courseClass.course.name,courseClass.course.code,courseClass.code"
    )
  )
});

export default reduxForm<any, CancelEnrolmentModalProps>({
  form: FORM
})(connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(CancelEnrolmentModalForm, enrolmentModalStyles)));
