import {FormProps, reduxForm, change} from "redux-form";
import * as React from "react";
import {connect} from "react-redux";
import classnames from 'classnames';

import {Contact, ValidationError, ContactFields} from "../../../model";
import {IshState} from "../../../services/IshState";
import {ContactEdit} from "./components/ContactEdit";
import CheckoutService from "../../services/CheckoutService";
import {changePhase, showFormValidation, showSyncErrors} from "../../actions/Actions";
import {submitEditContact} from "./actions/Actions";
import {toFormKey} from "../../../components/form/FieldFactory";
import {scrollToTop, scrollToValidation} from "../../../common/utils/DomUtils";

export const NAME = "ContactEditForm";

class ContactEditForm extends React.Component<Props, any> {
  constructor(props) {
    super(props);
  }

  componentDidMount() {
    scrollToTop();
  }

  render() {
    const {handleSubmit, touch, submitting, isNewContact, onCancel, page, form, onChangeSuburb} = this.props;
    const contact: Contact = this.props.contact;
    const fields: ContactFields = this.props.fields;

    return (
      <div>
        <form onSubmit={handleSubmit} id="contactEditorForm" className={classnames({submitting})}>
          <ContactEdit
            touch={touch}
            contact={contact}
            fields={fields}
            onChangeSuburb={item => onChangeSuburb(form, item)}
          />

          <div className="form-controls flex">
            <span>
            {!isNewContact &&
              <a
                href="#"
                type="button"
                onClick={() => onCancel(page)}
              >
                Cancel
              </a>
            }
            </span>

            <input
              value="OK"
              className="btn btn-primary"
              name="submitContact"
              type="submit"
              disabled={submitting}
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
  onCancel: (page) => void;
  concessionTypes: any;
  isNewContact: boolean;
  page: number;
  onChangeSuburb?: (form, item) => void;
}

const Form = reduxForm({
  form: NAME,
  validate: (data, props: Props) => {
    const errors = {};
    props.fields && props.fields.headings.map(headings =>
      headings.fields.map(field =>
        (field.mandatory && (field.dataType !== "BOOLEAN" || field.key === "isMale") && !data[toFormKey(field.key)])
          ? errors[toFormKey(field.key)] = `Field '${field.name}' is required`
          : field,
      ),
    );

    return errors;
  },
  onSubmitSuccess: (result, dispatch, props: any) => {
    dispatch(submitEditContact({...props.contact, parentRequired: result.parentRequired}));
  },
  onSubmitFail: (errors, dispatch, submitError, props) => {
    if (errors && !submitError) {
      dispatch(showSyncErrors(errors));
    } else {
      dispatch(showFormValidation(submitError, NAME));
    }

    setTimeout(() => scrollToValidation(), 50);
  },
})(ContactEditForm);

const getInitialValues = fields => {
  const initialValues = {};

  fields && fields.headings
    .map(h => h.fields
      .filter(f => f.defaultValue)
      .map(f => (initialValues[toFormKey(f.key)] = f.defaultValue)),
    );

  return initialValues;
};

const mapStateToProps = (state: IshState) => {
  // state.checkout.contacts.entities.contact[state.checkout.fields.contactId]
  const contact = state.checkout.contactAddProcess.contact;
  const fields = state.checkout.fields.current;
  const errors = state.checkout.error;
  const concessionTypes = state.checkout.concession.types;
  const isNewContact = !state.checkout.contacts.result.length;
  const page = state.checkout.page;

  const initialValues = getInitialValues(fields);

  return {
    contact,
    fields,
    errors,
    concessionTypes,
    isNewContact,
    page,
    initialValues,
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onSubmit: (data, dispatch, props): any => {
      return CheckoutService.submitContactDetails(data, props.fields);
    },
    onCancel: page => {
      dispatch(changePhase(page));
    },
    onChangeSuburb: (form, item) => {
      if (item && item.postcode) dispatch(change(form, 'postcode', item.postcode));
      if (item && item.state) dispatch(change(form, 'state', item.state));
    },
  };
};


const Container = connect(
  mapStateToProps,
  mapDispatchToProps,
)(Form as any);


export default Container;
