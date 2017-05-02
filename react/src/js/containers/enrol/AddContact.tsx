import {connect} from "react-redux";
import {
  AddContactComponent,
  AddContactComponentProps,
  AddContactFormFields
} from "../../components/enrol/addContact/AddContact";
import {IshState} from "../../services/IshState";
import {getFormValues, reduxForm} from "redux-form";
import {IshActions} from "../../constants/IshActions";
import {CreateContactParams} from "../../model/CreateContactParams";
import {Forms} from "../../constants/Forms";

const AddContactStore = connect(mapStateToProps, mapDispatchToProps)(AddContactComponent);

function mapDispatchToProps(dispatch) {
  return {
    submitAddContact: (form: CreateContactParams) => {
      dispatch({
        type: IshActions.SUBMIT_ADD_CONTACT_FORM,
        payload: form,
        meta: {
          form: Forms.ADD_CONTACT
        }
      });
    }
  };
}

function mapStateToProps(state: IshState, ownProps: AddContactComponentProps) {
  return {
    values: getFormValues(Forms.ADD_CONTACT)(state),
    errors: state.enrol.payer.error.formErrors
  };
}

export const AddContact = reduxForm({
  form: Forms.ADD_CONTACT,
  validate
})(AddContactStore);

function validate(values: AddContactFormFields) {
  const errors: AddContactFormFields = {};

  if (!values.firstName) {
    errors.firstName = "The student's first name is required.";
  }

  if (!values.lastName) {
    errors.lastName = "The student's last name is required.";
  }

  if (!values.email) {
    errors.email = "The student's email is required.";
  } else if (!/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(values.email)) {
    errors.email = "The email address does not appear to be valid.";
  }

  return errors;
}
