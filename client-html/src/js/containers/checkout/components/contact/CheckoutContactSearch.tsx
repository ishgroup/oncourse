/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import $t from '@t';
import debounce from 'lodash.debounce';
import React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { getCommonPlainRecords, setCommonPlainSearch } from '../../../../common/actions/CommonPlainRecordsActions';
import { CheckoutSummary } from '../../../../model/checkout';
import { CHECKOUT_CONTACT_COLUMNS, CheckoutPage } from '../../constants';
import HeaderField from '../HeaderField';
import SelectedContactRenderer from './SelectedContactRenderer';

export interface Props {
  setActiveField: (field: string) => void;
  selectedContacts: any[];
  form: string;
  dispatch: string;
  value: any;
  handleFocusCallback: any;
  setContactsSearch?: (value: string) => void;
  onClearContactsSearch: () => void;
  getContacts?: (offset: number) => void;
  onContactDeleteHandler: (index: number) => void;
  selectedItems: any[];
  selectedContact: any;
  openContactRow: (item: any) => void;
  summary: CheckoutSummary;
}

const CheckoutContactSearch = React.memo<Props>(props => {
  const {
    form,
    dispatch,
    value,
    handleFocusCallback,
    setContactsSearch,
    onClearContactsSearch,
    getContacts,
    onContactDeleteHandler,
    selectedContacts,
    selectedItems,
    selectedContact,
    openContactRow,
    summary
  } = props;

  const onSetContactsSearch = React.useCallback<any>(debounce((name, value) => {
    setContactsSearch(value ? `~"${value}"` : "");
    if (value) {
      getContacts(0);
    }
  }, 800), []);

  const onFocus = React.useCallback(e => {
    handleFocusCallback(e);
  }, []);

  const voucherLinkedToPayer = summary.vouchers.find(v => v.redeemableById);

  return (
    <HeaderField
      heading="Contacts"
      name={CheckoutPage.contacts}
      placeholder={$t('find_contact')}
      onFocus={onFocus}
      SelectedItemView={(
        <SelectedContactRenderer
          cartSelectedItems={selectedItems}
          selected={selectedContact}
          items={selectedContacts}
          openRow={openContactRow}
          onDelete={onContactDeleteHandler}
          disabledDeleteId={voucherLinkedToPayer && voucherLinkedToPayer.redeemableById}
          {...props}
        />
      )}
      form={form}
      dispatch={dispatch}
      onSearch={onSetContactsSearch}
      onClearSearch={onClearContactsSearch}
      value={value}
    />
  );
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getContacts: (offset?: number) => dispatch(getCommonPlainRecords("Contact", offset, CHECKOUT_CONTACT_COLUMNS, true, "lastName,firstName", 65000)),
  setContactsSearch: (search: string) => dispatch( setCommonPlainSearch("Contact", search))
});

export default connect<any, any, any>(null, mapDispatchToProps)(CheckoutContactSearch);
