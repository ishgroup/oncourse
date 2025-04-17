/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import $t from '@t';
import * as React from 'react';
import { reduxForm } from 'redux-form';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { onSubmitFail } from '../../../../../../common/utils/highlightFormErrors';
import IntegrationFormBase from './IntegrationFormBase';

class MicropowerBaseForm extends IntegrationFormBase {
  render() {
    const {
     handleSubmit, onSubmit, AppBarContent
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        <AppBarContent>
          <FormField name="fields.identity" label={$t('identity')} type="text" className="mb-2" />
          <FormField name="fields.signature" label={$t('signature')} type="text" className="mb-2" />
          <FormField name="fields.clientId" label={$t('client_id2')} type="text" className="mb-2" />
          <FormField name="fields.productSku" label={$t('product_sku')} type="text" className="mb-2" />
        </AppBarContent>
      </form>
    );
  }
}

export const MicropowerForm = reduxForm({
  onSubmitFail
})(MicropowerBaseForm);
