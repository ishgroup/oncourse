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

class CloudAssessBaseForm extends IntegrationFormBase {
  render() {
    const {
     handleSubmit, onSubmit, AppBarContent
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        <AppBarContent>
          <FormField type="text" name="fields.apiKey" label={$t('api_key')} className="mb-2"  />
        </AppBarContent>
      </form>
    );
  }
}

export const CloudAssessForm = reduxForm({
  onSubmitFail
})(CloudAssessBaseForm);