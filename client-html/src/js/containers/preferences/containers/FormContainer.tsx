/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { withStyles } from "@material-ui/core";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { SystemPreference } from "@api/model";
import { SubmissionError, initialize } from "redux-form";
import { withRouter } from "react-router";
import createStyles from "@material-ui/core/styles/createStyles";
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
import { setNextLocation, showConfirm } from "../../../common/actions";

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

interface Props {
  category: Categories;
  data: any;
  form: any;
  formName: string;
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
  openConfirm?: (onConfirm: any, confirmMessage?: string) => void;
  history?: any,
  nextLocation?: string,
  setNextLocation?: (nextLocation: string) => void,
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

class FormContainer extends React.Component<Props, any> {
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
        const { dispatch, data, formName, nextLocation, setNextLocation, history } = this.props;

        dispatch(initialize(formName, this.formatData(data)));

        nextLocation && history.push(nextLocation);
        setNextLocation('');
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
    const componentForm = React.cloneElement(form, {
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

const getFormName = form => {
  return form && Object.keys(form)[0];
};

const mapStateToProps = (state: State) => ({
  fetch: state.fetch,
  formName: getFormName(state.form),
  nextLocation: state.nextLocation
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    dispatch,
    onInit: category => dispatch(getPreferences(category)),
    onSubmit: (category, fields) => dispatch(savePreferences(category, fields)),
    openConfirm: (onConfirm: any, confirmMessage?: string) => dispatch(showConfirm(onConfirm, confirmMessage)),
    setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(withRouter(FormContainer)));
