/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { withStyles } from "@mui/styles";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { SystemPreference } from "@api/model";
import { SubmissionError, initialize } from "redux-form";
import { withRouter, RouteComponentProps } from "react-router";
import createStyles from "@mui/styles/createStyles";
import * as college from "../../../model/preferences/College";
import * as ldap from "../../../model/preferences/Ldap";
import * as licences from "../../../model/preferences/Licences";
import * as messaging from "../../../model/preferences/Messaging";
import * as classDefaults from "../../../model/preferences/ClassDefaults";
import * as maintenance from "../../../model/preferences/Maintenance";
import * as avetmiss from "../../../model/preferences/Avetmiss";
import { getPreferences, savePreferences } from "../actions";
import { Categories } from "../../../model/preferences";
import * as financial from "../../../model/preferences/Financial";
import * as security from "../../../model/preferences/security";
import { State } from "../../../reducers/state";
import { Fetch } from "../../../model/common/Fetch";
import { setUserPreference, showConfirm } from "../../../common/actions";
import { ShowConfirmCaller } from "../../../../ish-ui/model/Confirm";
import { ACCOUNT_DEFAULT_INVOICELINE_ID } from "../../../constants/Config";

const styles = () =>
  createStyles({
    subheadingButton: {
      width: "30px",
      minWidth: "unset"
    },
    buttonIcon: {
      fontSize: "18px"
    }
  });

export const preferencesFormRole: string = "preferences-form";

interface Props {
  category: Categories;
  data: any;
  form: (roleName?: string) => any;
  formName: string;
  history: any;
  onInit?: (category) => void;
  onSubmit?: (category: Categories, fields) => void;
  accounts?: any;
  timezones?: string[];
  licence?: boolean;
  validationPromise?: any;
  resolveValidation?: any;
  rejectValidation?: any;
  classes?: any;
  testLdapConnection?: any;
  emailQueued?: any;
  smsQueued?: any;
  dispatch?: any;
  enums?: any;
  skipOnInit?: boolean;
  fetch?: Fetch;
  openConfirm?: ShowConfirmCaller;
  nextLocation?: string
}

const FieldsModel = {
  college,
  ldap,
  licences,
  messaging,
  classDefaults,
  maintenance,
  avetmiss,
  financial,
  security
};

class FormContainer extends React.Component<Props & RouteComponentProps, any> {
  private resolveValidation;

  private rejectValidation;

  private isValidating: boolean = false;

  componentDidMount() {
    const { onInit, skipOnInit } = this.props;

    if (!skipOnInit) onInit(this.props.category);
  }

  componentDidUpdate(prevProps) {
    if (!this.isValidating) {
      return;
    }
    if (this.props.fetch && this.props.fetch.success === false) {
      this.rejectValidation(this.props.fetch.formError);
    }
    if (this.props.fetch && this.props.fetch.success) {
      this.resolveValidation();
      this.isValidating = false;
    }
  }

  // Getting form mandatory fields from Model for validation function
  getMangatoryFields(category) {
    const mandatoryFields = [];
    const fields = FieldsModel[category];
    Object.keys(fields).forEach(key => {
      if (fields[key].mandatory) {
        mandatoryFields.push(fields[key].uniqueKey.replace(/\./g, "-"));
      }
    });
    return mandatoryFields;
  }

  // Format form data for Redux Form (get rid of dots in field names)
  formatData(data) {
    const dataCopy = JSON.parse(JSON.stringify(data));
    const formattedObj = {};
    Object.keys(dataCopy).forEach(key => {
      const formattedProp = key.replace(/\./g, "-");
      formattedObj[formattedProp] = dataCopy[key];
    });
    return formattedObj;
  }

  // Format form data back after form submit
  parseData(data) {
    const formattedObj: SystemPreference = {};
    Object.keys(data).forEach(key => {
      const formattedProp = key.replace(/-/g, ".");
      const isTimestamp = key === "created" || key === "modified";

      if (!isTimestamp && (!this.props.data || this.props.data[formattedProp] !== data[key])) {
        formattedObj[formattedProp] = data[key];
      }
    });
    return formattedObj;
  }

  // Format model object uniqueKey values for Redux Form
  formatModel(data) {
    const formattedObj = JSON.parse(JSON.stringify(data));
    for (const key in formattedObj) {
      formattedObj[key].uniqueKey = formattedObj[key].uniqueKey.replace(/\./g, "-");
    }
    return formattedObj;
  }

  onSave(val) {
    this.isValidating = true;
    return new Promise((resolve, reject) => {
      this.resolveValidation = resolve;
      this.rejectValidation = reject;
      this.props.onSubmit(this.props.category, this.parseData(val));
    })
      .then(() => {
        const {
          dispatch, data, formName
        } = this.props;

        dispatch(initialize(formName, this.formatData(data)));
      })
      .catch(error => {
        this.isValidating = false;
        const errorObj = {};

        if (error) {
          errorObj[error.propertyName.replace(/\./g, "-")] = error.errorMessage;
        }
        throw new SubmissionError(errorObj);
      });
  }

  render() {
    const {
      data,
      accounts,
      timezones,
      onSubmit,
      form,
      category,
      licence,
      classes,
      testLdapConnection,
      emailQueued,
      smsQueued,
      enums,
      openConfirm
    } = this.props;

    const formData = data ? this.formatData(data) : {};

    const mandatoryFields = this.getMangatoryFields(Categories[category]);

    // Extend component props
    const componentForm = React.cloneElement(form(preferencesFormRole), {
      data,
      enums,
      formData,
      accounts,
      timezones,
      licence,
      classes,
      testLdapConnection,
      emailQueued,
      smsQueued,
      mandatoryFields,
      openConfirm,
      onSubmit: fields => onSubmit(category, fields),
      onSave: this.onSave.bind(this),
      formatModel: this.formatModel
    });

    return <div>{componentForm}</div>;
  }
}

const mapStateToProps = (state: State) => ({
  fetch: state.fetch,
  nextLocation: state.nextLocation
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  dispatch,
  onInit: category => dispatch(getPreferences(category)),
  onSubmit: (category, {defaultInvoiceLineAccount, ...fields}) => {
    if (defaultInvoiceLineAccount) {
      dispatch(setUserPreference({key: ACCOUNT_DEFAULT_INVOICELINE_ID, value: defaultInvoiceLineAccount}));
    }
    dispatch(savePreferences(category, fields));
  },
  openConfirm: props => dispatch(showConfirm(props))
});

export default connect(mapStateToProps, mapDispatchToProps)(withStyles(styles)(withRouter(FormContainer)));