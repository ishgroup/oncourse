import {reduxForm} from "redux-form";
import * as React from "react";
import classnames from "classnames";

import {ContactAdd} from "./components/ContactAdd";
import {validateContact} from "./actions/Validations";
import {NAME, Values} from "./actions/Actions";
import {showFormValidation, showSyncErrors} from "../../actions/Actions";
import {ContactId} from "../../../model";
import CheckoutService from "../../services/CheckoutService";
import {Phase} from "../../reducers/State";

class ContactAddForm extends React.Component<any, any> {

  componentDidMount() {
    // get children age preferences
  }

  getMessages() {
    const {childName, childAge = 18, phase} = this.props;

    switch (phase) {
      case Phase.ChangeParent:
        return {
          title: `Change a parent or guardian for ${childName}`,
          message: `Enter the person who will attend the class or make a purchase.`,
        };
      case Phase.AddPayer:
        return {
          title: `Add a payer`,
          message: `Enter the person to be invoiced and paying for this transaction.`,
        };
      case Phase.AddContactAsPayer:
        return {
          title: `Add a payer`,
          message: `Enter the person to be invoiced and paying for this transaction.`,
        };
      case Phase.AddParent:
        return {
          title: `Add a parent or guardian`,
          message: `Because a student is under ${childAge} we require the details of 
                  a parent or guardian for our records. Please enter that person.`,
        };

      default:
        return {
          title: `Add a person`,
          message: `Enter the person who will attend the class or make a purchase.`,
        };
    }

  }

  render() {
    const {handleSubmit, onCancel, pristine, invalid, submitting, fetching, phase, childName, fieldset} = this.props;

    return (
      <div>
        <h2>{this.getMessages().title}</h2>
        <form
          onSubmit={handleSubmit(values => CheckoutService.createOrGetContact(values, fieldset))}
          id="contactEditorForm"
          className={classnames({submitting: submitting || fetching})}
        >
          <ContactAdd header={this.getMessages().message}/>
          <div className="form-controls flex">
            <span>
              {onCancel &&
              <a
                href="#"
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
  validate: validateContact,
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
})(ContactAddForm);


export default Form;
