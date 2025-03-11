/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import $t from '@t';
import React from 'react';
import { connect } from 'react-redux';
import { CheckoutSummaryListItem } from '../../../../model/checkout';
import { State } from '../../../../reducers/state';
import { getContactFullName } from '../../../entities/contacts/utils';
import { CheckoutPage } from '../../constants';
import { HeaderFieldTypo } from '../HeaderField';

interface Props {
  classes?: any;
  activeField: string;
  setActiveField: any;
  selectedContacts: any[];
  summaryList?: CheckoutSummaryListItem[];
  summaryFinalTotal?: number;
  clearDiscountView?: () => void;
  currencySymbol?: any;
}

const CheckoutSummaryHeaderField = React.memo<Props>(props => {
  const {
    activeField,
    setActiveField,
    selectedContacts,
    summaryList,
    summaryFinalTotal,
    clearDiscountView,
    currencySymbol
  } = props;

  const onClickSummaryInvoice = React.useCallback(() => {
    setActiveField(CheckoutPage.summary);
    clearDiscountView();
  }, []);

  return (
    <>
      <HeaderFieldTypo
        title={$t('this_invoice')}
        activeField={activeField}
        field={CheckoutPage.summary}
        onClick={onClickSummaryInvoice}
        amount={summaryFinalTotal}
        currencySymbol={currencySymbol}
        caption={selectedContacts.length > 0 ? getContactFullName(summaryList.find(l => l.payer).contact as any) : ""}
        className="pl-2 pr-2 pt-1 pb-2"
      />
    </>
  );
});

const mapStateToProps = (state: State) => ({
  summaryList: state.checkout.summary.list,
  summaryFinalTotal: state.checkout.summary.finalTotal,
  currencySymbol: state.currency && state.currency.shortCurrencySymbol
});

export default connect<any, any, any>(mapStateToProps, null)(CheckoutSummaryHeaderField);
