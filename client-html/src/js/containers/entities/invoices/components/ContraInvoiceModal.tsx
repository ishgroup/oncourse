/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Currency } from '@api/model';
import { Button, Grid, Typography } from '@mui/material';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import $t from '@t';
import { BooleanArgFunction, decimalPlus, formatCurrency } from 'ish-ui';
import React, { useCallback, useMemo } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { change, DecoratedComponentClass, FieldArray, getFormValues, reduxForm } from 'redux-form';
import { ContactLinkAdornment } from '../../../../common/components/form/formFields/FieldAdornments';
import FormField from '../../../../common/components/form/formFields/FormField';
import NestedTable from '../../../../common/components/list-view/components/list/ReactTableNestedList';
import { NestedTableColumn } from '../../../../model/common/NestedTable';
import { State } from '../../../../reducers/state';
import { postContraInvoices } from '../actions';
import { ContraInvoiceFormData } from '../reducers/state';

const contraInvoiceColumnsBase: NestedTableColumn[] = [
  {
    name: "includeInPayment",
    title: " ",
    type: "checkbox",
    disableSort: true
  },
  {
    name: "dueDate",
    title: "Date due",
    type: "date"
  },
  {
    name: "invoiceNumber",
    title: "Invoice#",
    defaultSort: true
  },
  {
    name: "owing",
    title: "Owing",
    type: "currency"
  },
  {
    name: "toBePaid",
    title: "To be paid",
    type: "currency"
  }
];

interface Props {
  opened: boolean;
  setDialogOpened: BooleanArgFunction;
  values?: ContraInvoiceFormData;
  handleSubmit?: any;
  postContraInvoices?: (id: number, invoicesToPay: number[]) => void;
  currency?: Currency;
  classes?: any;
  dirty?: boolean;
  dispatch?: any;
  reset?: any;
}

const initialValues: ContraInvoiceFormData = {
  id: null,
  contactId: null,
  contactName: null,
  amountTotal: null,
  amountLeft: null,
  contraInvoices: []
};

const ContraInvoiceModal: React.FunctionComponent<Props> = props => {
  const {
    opened,
    handleSubmit,
    postContraInvoices,
    setDialogOpened,
    values,
    currency,
    dirty,
    dispatch,
    reset
  } = props;

  const onClose = useCallback(() => {
    setDialogOpened(false);
    reset();
  }, []);

  const onSubmit = useCallback((values: ContraInvoiceFormData) => {
    postContraInvoices(
      values.id,
      values.contraInvoices.filter(c => c.includeInPayment).map(c => c.id)
    );
    onClose();
  }, []);

  const onPaymentCheck = useCallback(
    (row, checked) => {
      let owing;
      let amountLeft;
      let toBePaid;

      const rowIndex = values.contraInvoices.findIndex(c => c.id === row.id);
      const fieldName = `contraInvoices[${rowIndex}]`;

      if (checked) {
        owing = row.owing > values.amountLeft ? row.owing - values.amountLeft : 0;

        amountLeft = values.amountLeft > row.owing ? values.amountLeft - row.owing : 0;

        toBePaid = values.amountLeft > row.owing ? row.owing : values.amountLeft;
      } else {
        owing = decimalPlus(row.owing, row.toBePaid);
        amountLeft = decimalPlus(values.amountLeft, row.toBePaid);
        toBePaid = 0;
      }

      dispatch(change("ContraInvoiceForm", "amountLeft", amountLeft));
      dispatch(change("ContraInvoiceForm", `${fieldName}.owing`, owing));
      dispatch(change("ContraInvoiceForm", `${fieldName}.toBePaid`, toBePaid));
    },
    [values.amountLeft, values.contraInvoices]
  );

  const invoiceDisabledCondition = useCallback(row => values.amountLeft === 0 && !row.includeInPayment, [
    values.amountLeft
  ]);

  const contraInvoiceColumns = useMemo(() => {
    const columns = [...contraInvoiceColumnsBase];

    columns[0].onChangeHandler = onPaymentCheck;
    columns[0].disabledHandler = invoiceDisabledCondition;

    return columns;
  }, [onPaymentCheck, invoiceDisabledCondition]);

  const amountLeft = useMemo(() => formatCurrency(values.amountLeft, currency.shortCurrencySymbol), [values.amountLeft, currency.shortCurrencySymbol]);

  const amountTotal = useMemo(() => formatCurrency(values.amountTotal, currency.shortCurrencySymbol), [values.amountTotal, currency.shortCurrencySymbol]);

  const contraInvoicesTitle = useMemo(() => "pay item" + (values.contraInvoices.length !== 1 ? "s" : ""), [
    values.contraInvoices
  ]);

  return (
    <Dialog open={opened} onClose={onClose} maxWidth="md" scroll="body">
      <form autoComplete="off" onSubmit={handleSubmit(onSubmit)}>
        <DialogTitle>{$t('contra_invoice2')}</DialogTitle>

        <DialogContent className="overflow-hidden">
          <Grid container columnSpacing={3}>
            <Grid item xs={6}>
              <FormField
                type="text"
                name="contactName"
                label={$t('payment_from')}
                labelAdornment={
                  <ContactLinkAdornment id={values?.contactId} />
                }
                disabled
              />
            </Grid>

            <Grid item xs={6}>
              <div>
                <Typography variant="caption" color="textSecondary">
                  {$t('amount_left_to_allocate')}
                </Typography>
                <Typography className="money">{amountLeft}</Typography>
              </div>
            </Grid>

            <Grid item xs={12} className="pr-3 pb-1">
              <Typography variant="caption">
                {$t('please_choose_one_or_more_invoices_to_contra_again')}
                <Typography variant="caption" component="span" className="money ">
                  {" "}
                  {amountTotal}
                </Typography>
              </Typography>
            </Grid>

            <Grid
              item
              xs={12}
              className="d-flex"
              style={{
                height: values.contraInvoices.length > 10 ? 300 : "auto"
              }}
            >
              <FieldArray
                name="contraInvoices"
                title={contraInvoicesTitle}
                component={NestedTable}
                columns={contraInvoiceColumns}
                rerenderOnEveryChange
                hideHeader
                sortable
              />
            </Grid>
          </Grid>
        </DialogContent>

        <DialogActions className="p-3">
          <Button color="primary" onClick={onClose}>
            {$t('cancel')}
          </Button>

          <Button variant="contained" color="primary" type="submit" disabled={!dirty}>
            {$t('save2')}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

const mapStateToProps = (state: State) => ({
  values: getFormValues("ContraInvoiceForm")(state),
  currency: state.location.currency
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    postContraInvoices: (id: number, invoicesToPay: number[]) => dispatch(postContraInvoices(id, invoicesToPay))
  });

export default reduxForm({
  form: "ContraInvoiceForm",
  initialValues
})(
  connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ContraInvoiceModal)
) as DecoratedComponentClass<any, Props>;
