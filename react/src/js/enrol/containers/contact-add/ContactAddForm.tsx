import {reduxForm} from "redux-form";
import * as React from "react";

import {ContactAdd} from "./components/ContactAdd";
import {validateContact} from "./actions/Validations";
import {NAME, submitAddContact, Values} from "./actions/Actions";
import {showFormValidation} from "../../actions/Actions";
import {ContactId} from "../../../model/web/ContactId";


class ContactAddForm extends React.Component<any, any> {
  render() {
    const {handleSubmit, onCancel, pristine, invalid, submitting} = this.props;
    return (
      <div>
        <form onSubmit={handleSubmit} id="contactEditorForm">
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
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onCancel}
            > Cancel
            </button>
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
    dispatch(submitAddContact(result as ContactId, props.values as Values));
  },
  onSubmitFail: (errors, dispatch, submitError, props) => {
    dispatch(showFormValidation(submitError, NAME));
  },
})(ContactAddForm);


export default Form;
