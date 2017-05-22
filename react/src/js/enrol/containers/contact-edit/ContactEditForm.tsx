import {reduxForm} from "redux-form";
import * as React from "react";
import {connect} from "react-redux";

import {Contact} from "../../../model/web/Contact";
import {IshState} from "../../../services/IshState";
import {ContactFields} from "../../../model/field/ContactFields";
import {ContactEdit} from "./components/ContactEdit";
import CheckoutService from "../../services/CheckoutService";
import {ValidationError} from "../../../model/common/ValidationError";
import {showErrors} from "../../actions/Actions";
import {ItemsLoadRequest, OpenSummaryRequest} from "../summary/actions/Actions";


export const NAME = "ContactEditForm";

class ContactEditForm extends React.Component<any, any> {
  render() {
    const {handleSubmit, pristine, invalid, submitting} = this.props;
    const contact: Contact = this.props.contact;
    const fields: ContactFields = this.props.fields;

    return (
      <div>
        <form onSubmit={handleSubmit} id="contactEditorForm">
          <ContactEdit contact={contact} fields={fields}/>
          <div className="form-controls">
            <input value="OK"
                   className="btn btn-primary"
                   name="submitContact"
                   type="submit"
                   disabled={invalid || pristine || submitting}
            />
          </div>
        </form>
      </div>
    )
  }
}

interface Props {
  contact: Contact,
  fields: ContactFields,
  errors: ValidationError,
}

const validate = (values) => {
  return values;
};

const Form = reduxForm({
  form: NAME,
  //validate: validate,
  onSubmitSuccess: (result, dispatch, props: any) => {
    dispatch({type: OpenSummaryRequest});
  },
  onSubmitFail: (errors, dispatch, submitError, props) => {
    dispatch(showErrors(submitError, NAME));
  }
})(ContactEditForm);


const mapStateToProps = (state: IshState) => {
  const contact = state.checkout.payer.entity;
  const fields = state.checkout.fields;
  const errors = state.checkout.error;
  return {
    contact: contact,
    fields: fields,
    errors: errors
  }
};

const mapDispatchToProps = (dispatch) => {
  return {
    onSubmit: (data, dispatch, props): any => {
      return CheckoutService.submitContactDetails(props.fields, data);
    }
  };
};


const Container = connect(
  mapStateToProps,
  mapDispatchToProps
)(Form);


export default Container;
