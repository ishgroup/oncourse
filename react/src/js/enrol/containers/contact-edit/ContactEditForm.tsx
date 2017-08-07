import {FormProps, reduxForm} from "redux-form";
import * as React from "react";
import {connect} from "react-redux";
import classnames from 'classnames';

import {Contact, ValidationError, ContactFields} from "../../../model";
import {IshState} from "../../../services/IshState";
import {ContactEdit} from "./components/ContactEdit";
import CheckoutService from "../../services/CheckoutService";
import {changePhase, showFormValidation} from "../../actions/Actions";
import {submitEditContact} from "./actions/Actions";
import {getConcessionTypes} from "../concession/actions/Actions";
import {validate as concessionFormValidate} from "../concession/Concession";
import ConcessionForm from "../concession/components/ConcessionForm";
import {toFormKey} from "../../../components/form/FieldFactory";

export const NAME = "ContactEditForm";

class ContactEditForm extends React.Component<Props, any> {
  constructor(props) {
    super(props);
  }

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {handleSubmit, pristine, touch, invalid, submitting, concessionTypes, isNewContact, onCancel, page} = this.props;
    const contact: Contact = this.props.contact;
    const fields: ContactFields = this.props.fields;

    return (
      <div>
        <form onSubmit={handleSubmit} id="contactEditorForm" className={classnames({submitting})}>
          <ContactEdit touch={touch} contact={contact} fields={fields}/>

          {concessionTypes &&
          <fieldset>
            <ConcessionForm
              concessionTypes={concessionTypes}
              onTypeChange={() => console.log('todo: partial reset')}
            />
          </fieldset>
          }

          <div className="form-controls">
            <input
              value="OK"
              className="btn btn-primary"
              name="submitContact"
              type="submit"
              disabled={invalid || submitting}
            />

            {!isNewContact &&
              <a
                href="#"
                type="button"
                className="cancel"
                onClick={() => onCancel(page)}
              >
                Cancel
              </a>
            }
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
  onCancel: (page) => void;
  concessionTypes: any;
  isNewContact: boolean;
  page: number;
}

const Form = reduxForm({
  form: NAME,
  validate: (data, props: Props) => {
    const errors = {};
    const concessionErrors = concessionFormValidate(data, props);
    props.fields.headings.map(headings =>
      headings.fields.map(field =>
        (field.mandatory && (field.dataType !== "BOOLEAN" || field.key === "isMale") && !data[toFormKey(field.key)])
          ? errors[toFormKey(field.key)] = `Field '${field.name}' is required`
          : field,
      ),
    );

    return {...errors, ...concessionErrors};
  }
,
  onSubmitSuccess: (result, dispatch, props: any) => {
    dispatch(submitEditContact({...props.contact, parentRequired: result.parentRequired}));
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
  const isNewContact = !state.checkout.contacts.result.length;
  const page = state.checkout.page;

  return {
    contact,
    fields,
    errors,
    concessionTypes,
    isNewContact,
    page,
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onSubmit: (data, dispatch, props): any => {
      return CheckoutService.submitContactDetails(data, props.fields);
    },
    onInit: () => {
      dispatch(getConcessionTypes());
    },
    onCancel: page => {
      dispatch(changePhase(page));
    },
  };
};


const Container = connect(
  mapStateToProps,
  mapDispatchToProps,
)(Form as any);


export default Container;
