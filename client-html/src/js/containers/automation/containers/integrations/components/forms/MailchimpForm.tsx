/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import $t from '@t';
import * as React from 'react';
import { reduxForm } from 'redux-form';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { onSubmitFail } from '../../../../../../common/utils/highlightFormErrors';
import IntegrationFormBase from './IntegrationFormBase';

class MailchimpBaseForm extends IntegrationFormBase {
  render() {
    const {
     handleSubmit, onSubmit, AppBarContent
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        <AppBarContent>
          <FormField name="fields.apiKey" label={$t('api_key')} type="text" className="mb-2"/>
          <FormField name="fields.listId" label={$t('audience_id')} type="text" className="mb-2" />
        </AppBarContent>
      </form>
    );
  }
}

export const MailchimpForm = reduxForm({
  onSubmitFail
})(MailchimpBaseForm);
