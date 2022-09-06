/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import {
  change, getFormValues, initialize, reduxForm
} from "redux-form";
import { connect } from "react-redux";
import Typography from "@mui/material/Typography";
import LoadingButton from "@mui/lab/LoadingButton";
import Button from "@mui/material/Button";
import FormField from "../../../../../../common/components/form/formFields/FormField";
import { onSubmitFail } from "../../../../../../common/utils/highlightFormClassErrors";
import { validateSingleMandatoryField } from "../../../../../../common/utils/validation";
import { State } from "../../../../../../reducers/state";

class XeroBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    this.state = {
      hideConfig: false,
      loading: false
    };

    // Initializing form with values
    props.dispatch(initialize("XeroForm", props.item));
  }

  componentDidMount() {
    const {
     match: { params: { name } }, item, dispatch
    } = this.props;

    if (name && !item.name) {
      dispatch(change("XeroForm", "name", name));
    }
  }

  componentDidUpdate(prevProps) {
    const {
     item, dispatch, location: { search }, history, dirty
    } = this.props;
    const { hideConfig } = this.state;

    if (prevProps.item.id !== item.id) {
      // Reinitializing form with values
      dispatch(initialize("XeroForm", item));
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
        dispatch(change("XeroForm", "verificationCode", code));
        dispatch(change("XeroForm", "name", name));
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
      // eslint-disable-next-line max-len
      `https://login.xero.com/identity/connect/authorize?response_type=code&client_id=A05FD21034974F29ABD4301FC54513BC&redirect_uri=https://secure-payment.oncourse.net.au/services/s/integrations/xero/auth.html&scope=accounting.transactions payroll.employees payroll.payruns payroll.payslip payroll.settings offline_access&state=${state}`,
      "_self"
    );
  };

  onDisconnect = () => {
    const { dispatch } = this.props;
    this.setState({
      hideConfig: true
    });
    dispatch(change("XeroForm", "fields.active", "false"));
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
                Disconnect from Xero
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
                    className="mb-2"
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
                  Connect to Xero
                </LoadingButton>
              )}
            </>
          )}
        </AppBarContent>
      </form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("XeroForm")(state)
});

export const XeroForm = reduxForm({
  form: "XeroForm",
  onSubmitFail
})(
  connect<any, any, any>(
    mapStateToProps
  )(XeroBaseForm)
);
