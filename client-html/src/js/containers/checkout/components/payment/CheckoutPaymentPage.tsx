/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useMemo } from 'react';
import { connect } from 'react-redux';
import AppBarContainer from '../../../../common/components/layout/AppBarContainer';
import LoadingIndicator from '../../../../common/components/progress/LoadingIndicator';
import { attachScript, attachScriptHTML } from '../../../../common/utils/common';
import {
  CheckoutDiscount,
  CheckoutItem,
  CheckoutPayment,
  CheckoutPaymentGateway,
  CheckoutSummary
} from '../../../../model/checkout';
import { State } from '../../../../reducers/state';
import { getContactFullName } from '../../../entities/contacts/utils';
import { CheckoutPage } from '../../constants';
import CheckoutAppBar from '../CheckoutAppBar';
import RestartButton from '../RestartButton';
import CheckoutPreviousInvoiceList from '../summary/CheckoutPreviousInvoiceList';
import CheckoutDiscountEditView from '../summary/promocode/CheckoutDiscountEditView';
import EwayPaymentPage from './components/payment-methods/EwayPaymentPage';
import PaymentPage from './components/payment-methods/PaymentPage';
import SquarePaymentPage from './components/payment-methods/SquarePaymentPage';
import StripePaymentPage from './components/payment-methods/StripePaymentPage';
import WindcavePaymentPage from './components/payment-methods/WindcavePaymentPage';

interface PaymentPageProps {
  payment?: CheckoutPayment;
  summary?: CheckoutSummary;
  summaryVouchers?: CheckoutDiscount[];
  selectedDiscount?: CheckoutItem;
  isPaymentProcessing?: boolean;
  disablePayment?: boolean;
  activeField: any;
  titles: any;
  gateway?: CheckoutPaymentGateway;
}

const CheckoutPaymentPage = React.memo<PaymentPageProps>(props => {
  const {
 payment, activeField, titles, summary, selectedDiscount, summaryVouchers, isPaymentProcessing, disablePayment, gateway
} = props;

  const selectedPaymentType = payment.availablePaymentTypes.find(t => t.name === payment.selectedPaymentType);

  const voucherItem = selectedDiscount ? summaryVouchers.find(v => v.id === selectedDiscount.id) : null;

  useEffect(() => {
    attachScriptHTML('vice={config:{viceAccountId:\'93bcb219-23a6-4426-806c-d32eb914c411\',viceSiteId:\'baf2d5c8-68fa-49c7-9379-49f248dc8fb3\'}}');
    attachScript('//vice-prod.sdiapi.com/vice_loader/93bcb219-23a6-4426-806c-d32eb914c411/baf2d5c8-68fa-49c7-9379-49f248dc8fb3');
  }, []);


  useEffect(() => {
    if (['EWAY', 'EWAY_TEST'].includes(gateway)) {
      [
        'https://secure.ewaypayments.com/scripts/eWAY.min.js',
        // Switch between production and test environments
        gateway === 'EWAY' ? 'https://static.assets.eway.io/cerberus/6.6.2.54470/assets/sdk/cerberus.bundle.js' : 'https://static.assets.eway.io/cerberus/6.6.2.54470/assets/sdk/cerberus-sandbox.bundle.js'
      ].forEach(attachScript);
    }
    if (['SQUARE', 'SQUARE_TEST'].includes(gateway)) {
      attachScript(`https://${gateway === 'SQUARE_TEST' ? "sandbox." : ""}web.squarecdn.com/v1/square.js`);
    }
    
  }, [gateway]);

  const payerName = useMemo(() => {
    const payer = summary.list.find(l => l.payer);
    return payer ? getContactFullName(payer.contact as any) : "";
  }, [summary.list]);

  const title = payment.process.status === "success" ? "Transaction successful"
    : payment.process.status === "fail" ? "Transaction failed"
      : payment.process.status === "cancel"
        ? "Transaction cancel"
        : selectedPaymentType
          ? selectedPaymentType.name
          : payment.selectedPaymentType || "Select payment method";
  
  const renderGatewayForm = useMemo(() => {
    let PaymentComp = null;
    
    switch (gateway) {
      case 'SQUARE':
      case 'SQUARE_TEST':
        PaymentComp = SquarePaymentPage;
        break;
      case 'EWAY':
      case 'EWAY_TEST':
        PaymentComp = EwayPaymentPage;
        break;
      case 'WINDCAVE':
      case 'TEST':
        PaymentComp = WindcavePaymentPage;
        break;
      case 'STRIPE':
      case 'STRIPE_TEST':
        PaymentComp = StripePaymentPage;
        break;
      case 'OFFLINE':
      case 'DISABLED':
      default:
        return null;
    }
    
    return <PaymentComp
      isPaymentProcessing={isPaymentProcessing}
      payerName={payerName}
      summary={summary}
      disablePayment={disablePayment}
    />;
    
  }, [
    gateway,
    isPaymentProcessing,
    payerName,
    summary,
    disablePayment
  ]);

  return (
    <>
      {[CheckoutPage.previousCredit, CheckoutPage.previousOwing].includes(activeField) ? (
        <CheckoutPreviousInvoiceList activeField={activeField} titles={titles} previousInvoices={summary[activeField]} />
      ) : activeField === "vouchers" && voucherItem
        ? <CheckoutDiscountEditView type="voucher" selectedDiscount={voucherItem} />
        : (
          <div className="root">
            <LoadingIndicator />
            <AppBarContainer
              hideHelpMenu
              hideSubmitButton
              disableInteraction
              title={(
                <CheckoutAppBar title={title} />
              )}
              actions={
                payment.process.status === "success" && <RestartButton />
              }
            >
              {selectedPaymentType && selectedPaymentType.type === "Credit card"
                && renderGatewayForm}
              {((selectedPaymentType && selectedPaymentType.type !== "Credit card")
                || (!selectedPaymentType && ["No payment", "Saved credit card"].includes(payment.selectedPaymentType)))
              && <PaymentPage paymentType={payment.selectedPaymentType} payerName={payerName} summary={summary} />}
            </AppBarContainer>
          </div>
      )}
    </>
  );
});

const mapStateToProps = (state: State) => ({
  payment: state.checkout.payment,
  summary: state.checkout.summary,
  summaryVouchers: state.checkout.summary.vouchers,
  isPaymentProcessing: state.checkout.payment.isProcessing,
  gateway: state.userPreferences['payment.gateway.type']
});

export default connect<any, any, any>(mapStateToProps, null)(CheckoutPaymentPage);