/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import $t from '@t';
import React from 'react';
import { initialize, reduxForm } from 'redux-form';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { onSubmitFail } from '../../../../../../common/utils/highlightFormErrors';
import { ServiceNSWVoucherTypes } from '../../../../../../model/automation/integrations/IntegrationsFields';

class NSWBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);
    // Initializing form with values
    props.dispatch(initialize("NSWForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("NSWForm", this.props.item));
    }
  }

  render() {
    const {
      handleSubmit, onSubmit, AppBarContent
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        <AppBarContent>
          <FormField name="fields.voucher" label={$t('voucher_type')} type="select" items={ServiceNSWVoucherTypes} required className="mb-2" />
          <FormField name="fields.chanelCode" label={$t('store_channel_code')} type="text" required className="mb-2" />
          <FormField name="fields.terminalId" label={$t('pos_terminal_id')} type="text" required className="mb-2" />
          <FormField name="fields.programme" label={$t('programme')} type="text" required className="mb-2" />
          <FormField name="fields.apiKey" label={$t('api_key')} type="password" required className="mb-2" />
        </AppBarContent>
      </form>
    );
  }
}

export const NSWForm = reduxForm({
  form: "NSWForm",
  onSubmitFail
})(NSWBaseForm);
