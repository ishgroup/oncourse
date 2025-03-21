/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Button from '@mui/material/Button';
import $t from '@t';
import React from 'react';
import { useDispatch } from 'react-redux';
import { Dispatch } from 'redux';
import { reset } from 'redux-form';
import { IAction } from '../../../common/actions/IshAction';
import { checkoutClearState } from '../actions';
import { checkoutGetActivePaymentMethods } from '../actions/checkoutPayment';
import { CHECKOUT_SELECTION_FORM_NAME as SELECTION_FORM } from './CheckoutSelection';
import { CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM } from './fundingInvoice/CheckoutFundingInvoiceSummaryList';
import { CHECKOUT_SUMMARY_FORM as SUMMARRY_FORM } from './summary/CheckoutSummaryList';

const RestartButton: React.FC<any> = () => {
  const dispatch = useDispatch<Dispatch<IAction>>();

  return (
    <Button
      classes={{
        root: "text-nowrap",
      }}
      color="primary"
      variant="contained"
      disableElevation
      onClick={() => {
      dispatch(checkoutClearState());
      dispatch(checkoutGetActivePaymentMethods());
      dispatch(reset(SUMMARRY_FORM));
      dispatch(reset(SELECTION_FORM));
      dispatch(reset(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM));
    }}
    >
      {$t('start_new_checkout')}
    </Button>
);
};

export default RestartButton;
