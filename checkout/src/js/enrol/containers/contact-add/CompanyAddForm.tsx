import {reduxForm} from "redux-form";
import * as React from "react";
import classnames from "classnames";

import {CompanyAdd} from "./components/CompanyAdd";
import {validateCompany} from "./actions/Validations";
import {NAME, Values} from "./actions/Actions";
import {showFormValidation, showSyncErrors} from "../../actions/Actions";
import {ContactId} from "../../../model";
import CheckoutService from "../../services/CheckoutService";


class CompanyAddForm extends React.Component<any, any> {
  render() {
    const {handleSubmit, onCancel, pristine, submitting, fetching} = this.props;

    return (
      <div>
        <h2>Add a company</h2>
        <form
          onSubmit={handleSubmit(values => CheckoutService.createOrGetContact({...values, company: true}))}
          className={classnames({submitting: submitting || fetching})}
          id="contactEditorForm"
        >
          <CompanyAdd/>
          <div className="form-controls flex">
            <span>
              {onCancel &&
              <a
                href="#"
                type="button"
                onClick={onCancel}
              > Cancel
              </a>
              }
            </span>

            <input
              value="OK"
              className="btn btn-primary"
              name="submitContact"
              type="submit"
              disabled={pristine || submitting}
            />

          </div>
        </form>
      </div>
    );
  }
}

const Form = reduxForm({
  form: NAME,
  validate: validateCompany,
  onSubmitSuccess: (result, dispatch, props: any) => {
    dispatch(props.onSuccess(result as ContactId, props.values as Values));
  },
  onSubmitFail: (errors, dispatch, submitError, props) => {
    if (errors && !submitError) {
      dispatch(showSyncErrors(errors));
    } else {
      dispatch(showFormValidation(submitError, NAME));
    }
  },
})(CompanyAddForm);


export default Form;
