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
import {ContactAddProcessState, Phase} from "../../reducers/State";

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
    const contact: Contact = this.props.contactAddProcess.contact;
    const fields: ContactFields = this.props.fields;

    return (
      <div>
        <form onSubmit={handleSubmit} id="contactEditorForm" className={classnames({submitting})}>
          <ContactEdit
            touch={touch}
            contact={contact}
            fields={fields}
            onChangeSuburb={item => onChangeSuburb(form, item)}
            form={form}
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
  contactAddProcess: ContactAddProcessState;
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

    return {...errors, ...validateAge(data, props)};
  },
  onSubmitSuccess: (result, dispatch, props: any) => {
    dispatch(submitEditContact({
      ...props.contactAddProcess.contact,
      parentRequired:
      result.parentRequired,
    }));
  },
  onSubmitFail: (errors, dispatch, submitError) => {
    if (errors && !submitError) {
      dispatch(showSyncErrors(errors));
    } else if (submitError && submitError.data) {
      dispatch(showFormValidation({
        ...submitError,
        data: { validationErrors: submitError.data }
      },
      NAME));
    }
    setTimeout(() => scrollToValidation(), 50);
  },
})(ContactEditForm);

const validateAge = (data, props) => {
  // validate age if contact is a guardian or parent
  if (props.contactAddProcess && (props.contactAddProcess.type === Phase.AddParent || props.contactAddProcess.type === Phase.ChangeParent)) {
    if (!data.dateOfBirth) return {};

    const dd = data.dateOfBirth.split('/')[0];
    const mm = data.dateOfBirth.split('/')[1];
    const yyyy = data.dateOfBirth.split('/')[2];
    const age = new Date().getFullYear() - new Date(`${mm}/${dd}/${yyyy}`).getFullYear();

    if (age < 18) {
      return {
        dateOfBirth: `Guardian must be over 18`,
      };
    }
  }

  return {};
};

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
  const contactAddProcess = state.checkout.contactAddProcess;
  const fields = state.checkout.fields.current;
  const errors = state.checkout.error;
  const concessionTypes = state.checkout.concession.types;
  const isNewContact = !state.checkout.contacts.result.length;
  const page = state.checkout.page;

  const initialValues = getInitialValues(fields);

  return {
    contactAddProcess,
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
