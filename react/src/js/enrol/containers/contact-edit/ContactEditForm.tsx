import {FormProps, reduxForm} from "redux-form";
import * as React from "react";
import {connect} from "react-redux";

import {Contact} from "../../../model/web/Contact";
import {IshState} from "../../../services/IshState";
import {ContactFields} from "../../../model/field/ContactFields";
import {ContactEdit} from "./components/ContactEdit";
import CheckoutService from "../../services/CheckoutService";
import {ValidationError} from "../../../model/common/ValidationError";
import {showFormValidation} from "../../actions/Actions";
import {submitEditContact} from "./actions/Actions";
import {getConcessionTypes} from "../concession/actions/Actions";
import {validate as concessionFormValidate} from "../concession/Concession";
import ConcessionForm from "../concession/components/ConcessionForm";

export const NAME = "ContactEditForm";

class ContactEditForm extends React.Component<Props, any> {
  constructor(props) {
    super(props);
  }

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {handleSubmit, pristine, invalid, submitting, concessionTypes} = this.props;
    const contact: Contact = this.props.contact;
    const fields: ContactFields = this.props.fields;

    return (
      <div>
        <form onSubmit={handleSubmit} id="contactEditorForm">
          <ContactEdit contact={contact} fields={fields}/>

          {concessionTypes &&
            <fieldset>
              <ConcessionForm concessionTypes={concessionTypes} onTypeChange={() => console.log('todo: partial reset')}/>
            </fieldset>
          }

          <div className="form-controls">
            <input value="OK"
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

interface Props extends FormProps<FormData, Props, any> {
  contact: Contact;
  fields: ContactFields;
  errors: ValidationError;
  onInit: () => void;
  concessionTypes: any;
}

const Form = reduxForm({
  form: NAME,
  validate: (data, props) => {
    const errors = {};
    const concessionErrors = concessionFormValidate(data, props);

    return {...errors, ...concessionErrors};
  },
  onSubmitSuccess: (result, dispatch, props: any) => {
    dispatch(submitEditContact(props.contact));
  },
  onSubmitFail: (errors, dispatch, submitError, props) => {
    dispatch(showFormValidation(submitError, NAME));
  },
})(ContactEditForm);


const mapStateToProps = (state: IshState) => {
  // state.checkout.contacts.entities.contact[state.checkout.fields.contactId]
  const contact = state.checkout.contactAddProcess.contact;
  const fields = state.checkout.fields;
  const errors = state.checkout.error;
  const concessionTypes = state.checkout.concession.types;
  return {
    contact,
    fields,
    errors,
    concessionTypes,
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onSubmit: (data, dispatch, props): any => {
      return CheckoutService.submitContactDetails(data, props);
    },
    onInit: () => {
      dispatch(getConcessionTypes());
    },
  };
};


const Container = connect(
  mapStateToProps,
  mapDispatchToProps,
)(Form as any);


export default Container;
