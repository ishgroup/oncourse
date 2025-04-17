/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import $t from '@t';
import React from 'react';
import { reduxForm } from 'redux-form';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { onSubmitFail } from '../../../../../../common/utils/highlightFormErrors';
import IntegrationFormBase from './IntegrationFormBase';

class AzureFormBase extends IntegrationFormBase {
  render() {
    const {
      handleSubmit, onSubmit, AppBarContent
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        <AppBarContent>
          <FormField type="text" name="fields.account" label={$t('account')} required className="mb-2" />
          <FormField type="text" name="fields.key" label={$t('key')} required className="mb-2" />
          <FormField type="text" name="fields.container" label={$t('container')} required className="mb-2" />
        </AppBarContent>
      </form>
    );
  }
}

export const AzureForm = reduxForm({
  onSubmitFail
})(AzureFormBase);