/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import LoadingButton from '@mui/lab/LoadingButton';
import Typography from '@mui/material/Typography';
import $t from '@t';
import * as React from 'react';
import { connect } from 'react-redux';
import { change, getFormValues, initialize, reduxForm } from 'redux-form';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { onSubmitFail } from '../../../../../../common/utils/highlightFormErrors';
import { validateSingleMandatoryField } from '../../../../../../common/utils/validation';
import { State } from '../../../../../../reducers/state';

class CanvasBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    this.state = {
      loading: false
    };

    // Initializing form with values
    props.dispatch(initialize(props.form, props.item));
  }

  componentDidMount() {
    const {location: {search}, dispatch, form} = this.props;

    if (search) {
      const params = new URLSearchParams(search);
      const code = params.get("code");
      const state = params.get("state");

      if (code && state) {
        const newValues = { ...JSON.parse(state) };
        newValues.fields.redirectUrl = window.location.origin;

        dispatch(initialize(form, newValues));
        dispatch(change(form, 'fields.verificationCode', code));
      }
    }
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize(this.props.form, this.props.item));
    }
  }

  configure = () => {
    const { values } = this.props;
    this.setState({
      loading: true
    });

    window.open(
      
      `https://${values.fields.baseUrl}/login/oauth2/auth?client_id=${values.fields.clientToken}&response_type=code&state=${JSON.stringify(values)}&redirect_uri=${window.location.href}`,
      "_self"
    );
  };

  render() {
    const {
     handleSubmit, onSubmit, AppBarContent, item, values
    } = this.props;

    const { loading } = this.state;

    const configured = values && values.fields.verificationCode;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        <AppBarContent>
          {
            !values?.id && (
              <FormField
                type="stub"
                name="fields.verificationCode"
                validate={validateSingleMandatoryField}
              />
            )
          }

          <FormField disabled={configured} type="text" name="fields.baseUrl" label={$t('base_url2')} required className="mb-2" />
          <FormField disabled={configured} type="text" name="fields.accountId" label={$t('account_id')} required className="mb-2" />
          <FormField disabled={configured} type="text" name="fields.clientToken" label={$t('client_id')} required className="mb-2" />
          <FormField
            disabled={configured}
            type="password"
            name="fields.clientSecret"
            label={$t('client_secret')}
            required
            className="mb-2"
          />
          {values && values.fields && values.fields.baseUrl && values.fields.clientToken && values.fields.clientSecret ? (
            configured
              ? (
                <Typography variant="caption">
                  {$t('canvas_access_has_been_set_up')}
                  {
                    item && item.id ? "" : ". Press Save to finish"
                  }
                </Typography>
                )
              : (
                <>
                  <Typography variant="caption">
                    {$t('press_configure_to_proceed_with_authorising_oncour')}
                  </Typography>
                  <div>
                    <LoadingButton
                      loading={loading}
                      variant="contained"
                      className="mt-1"
                      onClick={this.configure}
                    >
                      {$t('configure')}
                    </LoadingButton>
                  </div>
                </>
              )
          ) : (
            <Typography variant="caption">{$t('fill_all_fields_to_proceed_with_authorization')}</Typography>
          )}
        </AppBarContent>
      </form>
    );
  }
}

const mapStateToProps = (state: State, ownProps) => ({
  values: getFormValues(ownProps.form)(state)
});

export const CanvasForm = reduxForm({
  onSubmitFail
})(connect(mapStateToProps)(CanvasBaseForm));
