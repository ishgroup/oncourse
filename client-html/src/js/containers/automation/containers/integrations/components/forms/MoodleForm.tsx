/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import $t from '@t';
import * as React from 'react';
import { connect } from 'react-redux';
import { initialize, reduxForm } from 'redux-form';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { onSubmitFail } from '../../../../../../common/utils/highlightFormErrors';

class MoodleBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("MoodleForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("MoodleForm", this.props.item));
    }
  }

  render() {
    const {
     handleSubmit, onSubmit, AppBarContent
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        <AppBarContent>
          <FormField name="fields.baseUrl" label={$t('base_url')} type="text" className="mb-2" />
          <FormField name="fields.username" label={$t('username')} type="text" className="mb-2" />
          <FormField name="fields.password" label={$t('password')} type="password" className="mb-2" />
          <FormField name="fields.serviceName" label={$t('service_name')} type="text" className="mb-2" />
          <FormField
            name="fields.courseTag"
            label={$t('only_activate_for_enrolments_in_courses_tagged_wit')}
            type="text"
            className="mb-2"
          />
        </AppBarContent>
      </form>
    );
  }
}

export const MoodleForm = reduxForm({
  form: "MoodleForm",
  onSubmitFail
})(connect<any, any, any>(null, null)(MoodleBaseForm));
