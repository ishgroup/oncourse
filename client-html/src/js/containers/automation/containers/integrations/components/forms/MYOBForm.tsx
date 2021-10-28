/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { change, getFormValues, initialize, reduxForm } from "redux-form";
import { connect } from "react-redux";
import Typography from "@material-ui/core/Typography";
import Button from "../../../../../../common/components/buttons/Button";
import FormField from "../../../../../../common/components/form/formFields/FormField";
import CustomAppBar from "../../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { onSubmitFail } from "../../../../../../common/utils/highlightFormClassErrors";
import { validateSingleMandatoryField } from "../../../../../../common/utils/validation";
import { State } from "../../../../../../reducers/state";

class MYOBBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    this.state = {
      hideConfig: false,
      loading: false
    };
    // Initializing form with values
    props.dispatch(initialize("MYOBForm", props.item));
  }

  componentDidMount() {
    const {
      match: { params: { name } }, item, dispatch
    } = this.props;

    if (name && !item.name) {
      dispatch(change("MYOBForm", "name", name));
    }
  }

  componentDidUpdate(prevProps) {
    const {
      item, dispatch, location: { search }, history, dirty
    } = this.props;
    const { hideConfig } = this.state;

    if (prevProps.item.id !== item.id) {
      // Reinitializing form with values
      dispatch(initialize("MYOBForm", item));
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
      const values = JSON.parse(params.get("values"));
      const url = values.url;
      const file = values.file;
      const owner = values.owner;
      const password = values.password;
      const code = params.get("code");

      this.updateValue('url', "myobBaseUrl", url);
      this.updateValue('owner', "myobUser", owner);
      this.updateValue('file', "myobFileName", file);

      this.props.dispatch(change("MYOBForm", "fields.myobPassword", password));
      params.delete('values');

      if (code) {
        this.setState({
          hideConfig: true
        });
        dispatch(change("MYOBForm", "verificationCode", code));
        params.delete('code');

        history.replace({
          pathname: history.location.pathname,
          search: decodeURIComponent(params.toString())
        });
      }
    }
  }

  updateValue(name: string, fieldKey: string, value: string) {
    if (value) {
      this.props.dispatch(change("MYOBForm", "fields." + fieldKey, value));
    }
  }

  showTokenField = () => {
    const { values } = this.props;
    const url = values.fields.myobBaseUrl;
    const filename = values.fields.myobFileName;
    const owner = values.fields.myobUser;
    const password = values.fields.myobPassword;

    this.setState({
      loading: true
    });

    const map = new Map<string, string>();
    this.checkAndSet(map, 'url', url);
    this.checkAndSet(map, 'file', filename);
    this.checkAndSet(map, 'owner', owner);

    map.set('password', password);

    let params = "";
    if (map.size !== 0) {
      const result = Object.fromEntries(map);
      params = "?values=" + JSON.stringify(result);
    }

    const state = encodeURI(`${window.location.href}${values.id ? "" : `/${values.name}`}${params}`);

    window.open(
      // eslint-disable-next-line max-len
      `https://secure.myob.com/oauth2/account/authorize?client_id=07545d14-9a95-4398-b907-75af3b3841ae&redirect_uri=https://secure-payment.oncourse.net.au/services/s/integrations/myob/auth.html&response_type=code&scope=CompanyFile&state=${state}`,
      "_self"
    );
  }

  checkAndSet(map: Map<string, string>, name: string, value: string) {
    if (value !== "" && value !== undefined) {
      map.set(name, value);
    }
  }

  onDisconnect = () => {
    const { dispatch } = this.props;
    this.setState({
      hideConfig: true
    });
    dispatch(change("MYOBForm", "fields.active", "false"));
  }

  render() {
    const {
      handleSubmit, onSubmit, AppBarContent, values = {}, dirty, item, form
    } = this.props;
    const configured = item && item.id;
    const { hideConfig, loading } = this.state;
    const configuredLabel = "MYOB access has been set up. Press ‘Configure’ to restart the configuration process.";
    const unconfiguredLabel = ' Press "Configure" to proceed with authorising onCourse to access your MYOB account.';

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        {dirty && <RouteChangeConfirm form={form} when={dirty} />}
        <CustomAppBar>
          <AppBarContent />
        </CustomAppBar>
        <FormField name="fields.myobBaseUrl" label="Base URL" type="text" fullWidth required />
        <FormField name="fields.myobFileName" label="File name" type="text" fullWidth required />
        <FormField name="fields.myobUser" label="File owner" type="text" fullWidth required />
        <FormField name="fields.myobPassword" type="password" label="File owner password" fullWidth />

        <Typography variant="caption" component="div">
          {configured ? configuredLabel : unconfiguredLabel}
        </Typography>
        {values?.fields?.active === "true" ? (
          <>
            <Typography variant="caption" component="div">
              { `You are connected to Myob` }
            </Typography>
            <Button
              text="Disconnect from Myob"
              variant="contained"
              className="mt-1"
              onClick={this.onDisconnect}
            />
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
                ? 'Myob access has been set up. Press "Save" to complete configuration process.'
                : 'Press to proceed with authorising onCourse to access your Myob account.'}
            </Typography>

            {!hideConfig && (
              <Button
                text="Connect to Myob"
                variant="contained"
                className="mt-1"
                disabled={!values.name}
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
  values: getFormValues("MYOBForm")(state)
});

export const MYOBForm = reduxForm({
  form: "MYOBForm",
  onSubmitFail
})(
  connect<any, any, any>(
    mapStateToProps
  )(MYOBBaseForm)
);
