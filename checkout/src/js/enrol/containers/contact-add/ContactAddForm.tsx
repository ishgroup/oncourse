import {reduxForm} from "redux-form";
import * as React from "react";
import classnames from "classnames";

import {ContactAdd} from "./components/ContactAdd";
import {validateContact} from "./actions/Validations";
import {NAME, Values} from "./actions/Actions";
import {showFormValidation} from "../../actions/Actions";
import {ContactId} from "../../../model";
import CheckoutService from "../../services/CheckoutService";
import {Phase} from "../../reducers/State";


class ContactAddForm extends React.Component<any, any> {
  render() {
    const {handleSubmit, onCancel, pristine, invalid, submitting, fetching, phase, childName} = this.props;

    const getFormLabel = () => (
      phase === Phase.ChangeParent && childName && `Change guardian or parent for ${childName}` ||
      phase === Phase.AddContact && `Add a student` ||
      phase === Phase.AddPayer && `Add a student` ||
      phase === Phase.AddContactAsPayer && `Add a payer` ||
      phase === Phase.AddParent && 'Add a parent or guardian'
    )

    return (
      <div>
        <h2>{getFormLabel()}</h2>
        <form
          onSubmit={handleSubmit(values => CheckoutService.createOrGetContact(values))}
          id="contactEditorForm"
          className={classnames({submitting: submitting || fetching})}
        >
          <ContactAdd/>
          <div className="form-controls flex">
            {onCancel &&
              <a
                href="#"
                onClick={onCancel}
              > Cancel
              </a>
            }

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
  validate: validateContact,
  onSubmitSuccess: (result, dispatch, props: any) => {
    dispatch(props.onSuccess(result as ContactId, props.values as Values));
  },
  onSubmitFail: (errors, dispatch, submitError, props) => {
    dispatch(showFormValidation(submitError, NAME));
  },
})(ContactAddForm);


export default Form;
