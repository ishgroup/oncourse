/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import LoadingButton from '@mui/lab/LoadingButton';
import { Button, Typography } from '@mui/material';
import Typography from '@mui/material/Typography';
import $t from '@t';
import * as React from 'react';
import { connect } from 'react-redux';
import { change, getFormValues, initialize, reduxForm } from 'redux-form';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { onSubmitFail } from '../../../../../../common/utils/highlightFormErrors';
import { validateSingleMandatoryField } from '../../../../../../common/utils/validation';
import { State } from '../../../../../../reducers/state';

class XeroBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    this.state = {
      hideConfig: false,
      loading: false
    };

    // Initializing form with values
    props.dispatch(initialize(props.form, props.item));
  }

  componentDidMount() {
    const {
      match: {params: {name}}, item, dispatch, form
    } = this.props;

    if (name && !item.name) {
      dispatch(change(form, 'name', name));
    }
  }

  componentDidUpdate(prevProps) {
    const {
      item, dispatch, location: {search}, history, dirty, form
    } = this.props;
    const { hideConfig } = this.state;

    if (prevProps.item.id !== item.id) {
      // Reinitializing form with values
      dispatch(initialize(form, item));
      this.setState({
        hideConfig: false
      });
    }

    if (prevProps.dirty === true && dirty === false) {
      this.setState({
        hideConfig: false
      });
    }

    if (search && !hideConfig) {
      const params = new URLSearchParams(search);
      const code = params.get("code");
      const name = params.get("name");

      if (code) {
        this.setState({
          hideConfig: true
        });
        dispatch(change(form, 'verificationCode', code));
        dispatch(change(form, 'name', name));
        params.delete('code');

        history.replace({
          pathname: history.location.pathname,
          search: decodeURIComponent(params.toString())
        });
      }
    }
  }

  beforeSubmit = integration => {
    const { onSubmit } = this.props;
    onSubmit(integration);
  };

  showTokenField = () => {
    const { values } = this.props;

    this.setState({
      loading: true
    });

    const paramsStr = "?name=" + values?.name;
    const state = encodeURI(`${window.location.href}${paramsStr}`);

    window.open(
      `https://login.xero.com/identity/connect/authorize?response_type=code&client_id=A05FD21034974F29ABD4301FC54513BC&redirect_uri=https://secure-payment.oncourse.net.au/services/s/integrations/xero/auth.html&scope=accounting.transactions payroll.employees payroll.payruns payroll.payslip payroll.settings offline_access&state=${state}`,
      "_self"
    );
  };

  onDisconnect = () => {
    const {dispatch, form} = this.props;
    this.setState({
      hideConfig: true
    });
    dispatch(change(form, 'fields.active', 'false'));
  };

  render() {
    const {
      AppBarContent, handleSubmit, values = {}
    } = this.props;

    const { hideConfig, loading } = this.state;

    return (
      <form onSubmit={handleSubmit(this.beforeSubmit)}>
        <AppBarContent disableName={Boolean(values?.id)}>
          {values?.fields?.active === "true" ? (
            <>
              <Typography variant="caption" component="div">
                {`You are connected to Xero organisation: ${values?.fields?.companyName}`}
              </Typography>
              <Button
                variant="contained"
                className="mt-1"
                onClick={this.onDisconnect}
              >
                {$t('disconnect_from_xero')}
              </Button>
            </>
          ) : (
            <>
              {
                !hideConfig && (
                  <FormField
                    type="stub"
                    name="verificationCode"
                    validate={validateSingleMandatoryField}
                  />
                )
              }

              <Typography variant="caption" component="div">
                {hideConfig
                  ? 'Xero access has been set up. Press "Save" to complete configuration process.'
                  : 'Press to proceed with authorising onCourse to access your Xero account.'}
              </Typography>

              {!hideConfig && (
                <LoadingButton
                  variant="contained"
                  className="mt-1"
                  disabled={!values.name}
                  loading={loading}
                  onClick={this.showTokenField}
                >
                  {$t('connect_to_xero')}
                </LoadingButton>
              )}
            </>
          )}
        </AppBarContent>
      </form>
    );
  }
}

const mapStateToProps = (state: State, ownProps) => ({
  values: getFormValues(ownProps.form)(state)
});

export const XeroForm = reduxForm({
  onSubmitFail
})(
  connect<any, any, any>(
    mapStateToProps
  )(XeroBaseForm)
);