/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { FormControlLabel } from '@mui/material';
import $t from '@t';
import * as React from 'react';
import { connect } from 'react-redux';
import { initialize, reduxForm } from 'redux-form';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { onSubmitFail } from '../../../../../../common/utils/highlightFormErrors';

class SurveyMonkeyBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("SurveyMonkeyForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("SurveyMonkeyForm", this.props.item));
    }
  }

  render() {
    const {
      AppBarContent, handleSubmit, onSubmit
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        <AppBarContent>
          <FormField name="fields.apiKey" label={$t('api_key')} type="text" className="mb-2" />
          <FormField name="fields.authToken" label={$t('oauth_token')} type="text" className="mb-2" />
          <FormField name="fields.surveyName" label={$t('survey_name')} type="text" className="mb-2" />
          <FormField
            name="fields.courseTag"
            label={$t('only_activate_for_enrolments_in_courses_tagged_wit')}
            type="text"
            className="mb-2"
          />
          <div className="flex-column pt-2">
            <FormControlLabel
              control={<FormField name="fields.sendOnEnrolmentSuccess" color="primary" type="checkbox" />}
              label={$t('send_on_successful_enrolment')}
            />
            <FormControlLabel
              control={<FormField name="fields.sendOnEnrolmentCompletion" color="primary" type="checkbox" />}
              label={$t('send_on_completion')}
            />
          </div>
        </AppBarContent>
      </form>
    );
  }
}

export const SurveyMonkeyForm = reduxForm({
  form: "SurveyMonkeyForm",
  onSubmitFail
})(connect<any, any, any>(null, null)(SurveyMonkeyBaseForm));
