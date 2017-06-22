import {reduxForm} from "redux-form";
import * as React from "react";

import {ContactAdd} from "./components/ContactAdd";
import {validateContact} from "./actions/Validations";
import {NAME, Values} from "./actions/Actions";
import {showFormValidation} from "../../actions/Actions";
import {ContactId} from "../../../model/web/ContactId";
import CheckoutService from "../../services/CheckoutService";


class ContactAddForm extends React.Component<any, any> {
  render() {
    const {handleSubmit, onCancel, pristine, invalid, submitting} = this.props;

    return (
      <div>
        <form onSubmit={handleSubmit(values => CheckoutService.createOrGetContact(values))} id="contactEditorForm">
          <ContactAdd/>
          <div className="form-controls">
            <input
              value="OK"
              className="btn btn-primary"
              name="submitContact"
              type="submit"
              disabled={invalid || pristine || submitting}
            />

            {onCancel &&
            <a
              href="#"
              type="button"
              className="cancel"
              onClick={onCancel}
            > Cancel
            </a>
            }
          </div>
        </form>
      </div>
    );
  }
}

const Form = reduxForm({
  form: NAME,
  validate: validateContact,
  onSubmitSuccess: (result, dispatch, props: any) => {
    dispatch(props.onSuccess(result as ContactId, props.values as Values));
  },
  onSubmitFail: (errors, dispatch, submitError, props) => {
    dispatch(showFormValidation(submitError, NAME));
  },
})(ContactAddForm);


export default Form;
