/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import {
  change, getFormValues, initialize, reduxForm
} from "redux-form";
import { connect } from "react-redux";
import Typography from "@material-ui/core/Typography";
import Button from "../../../../../../common/components/buttons/Button";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import CustomAppBar from "../../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../../common/components/dialog/confirm/RouteChangeConfirm";
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
     match: { params: { name } }, item, dispatch, canSave
    } = this.props;

    if (name && !item.name) {
      dispatch(change("XeroForm", "name", name));
    }
    canSave(false);
  }

  componentDidUpdate(prevProps) {
    const {
     item, dispatch, location: { search }, canSave
    } = this.props;
    const { hideConfig } = this.state;

    if (prevProps.item.id !== item.id) {
      // Reinitializing form with values
      dispatch(initialize("XeroForm", item));
      this.setState({
        hideConfig: false
      });
    }

    if (search && !hideConfig) {
      const params = new URLSearchParams(search);
      const code = params.get("code");

      if (code) {
        this.setState({
          hideConfig: true
        });
        dispatch(change("XeroForm", "verificationCode", code));
        canSave(true);
      }
    }
  }

  beforeSubmit = integration => {
    const { onSubmit, dispatch } = this.props;

    onSubmit(integration);
    dispatch(change("XeroForm", "verificationCode", null));
  };

  showTokenField = () => {
    const { values } = this.props;

    this.setState({
      loading: true
    });

    window.open(
      // eslint-disable-next-line max-len
      `https://login.xero.com/identity/connect/authorize?response_type=code&client_id=A05FD21034974F29ABD4301FC54513BC&redirect_uri=https://secure-payment.oncourse.net.au/services/s/integrations/xero/auth.html&scope=accounting.transactions payroll.employees payroll.payruns payroll.payslip payroll.settings offline_access&state=${window.location.href}/${values && values.name}`,
      "_self"
    );
  }

  render() {
    const {
      appBarContent, dirty, handleSubmit, values, item, form
    } = this.props;
    const { hideConfig, loading } = this.state;

    const name = values && values.name;

    return (
      <form onSubmit={handleSubmit(this.beforeSubmit)}>
        {dirty && <RouteChangeConfirm form={form} when={dirty} />}
        <CustomAppBar>{appBarContent}</CustomAppBar>

        {item.id ? (
          <Typography variant="caption" component="div">
            Xero integration is set up
          </Typography>
        ) : (
          <>
            <FormField
              type="stub"
              name="verificationCode"
              validate={validateSingleMandatoryField}
            />

            <Typography variant="caption" component="div">
              {hideConfig
                      ? 'Xero access has been set up. Press "Save" to complete configuration process.'
                      : ' Press "Configure" to proceed with authorising onCourse to access your Xero account.'}
            </Typography>

            {!hideConfig && (
              <Button
                text="Configure"
                variant="contained"
                className="mt-1"
                disabled={!name}
                loading={loading}
                onClick={this.showTokenField}
              />
            )}
          </>
      )}
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
    mapStateToProps,
    null
  )(XeroBaseForm)
);
