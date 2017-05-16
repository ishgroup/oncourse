import {connect} from "react-redux";
import {AddPayerComponent, AddPayerComponentProps, AddPayerFormFields} from "../components/contact/AddPayer";
import {IshState} from "../../services/IshState";
import {getFormValues, reduxForm} from "redux-form";
import {CreateContactParams} from "../../model/web/CreateContactParams";
import {Forms} from "../../constants/Forms";
import * as Actions from "../actions/Actions";


const AddPayerStore = connect(mapStateToProps, mapDispatchToProps)(AddPayerComponent);

function mapDispatchToProps(dispatch) {
  return {
    submit: (form: CreateContactParams) => {
      dispatch({
        type: Actions.ContactAddRequest,
        payload: form,
        meta: {
          form: Forms.ADD_PAYER
        }
      });
    }
  };
}

function mapStateToProps(state: IshState, ownProps: AddPayerComponentProps) {
  return {
    values: getFormValues(Forms.ADD_PAYER)(state),
    errors: state.checkout.error.formErrors
  };
}

export const AddPayer = reduxForm({
  form: Forms.ADD_PAYER,
  validate
})(AddPayerStore);

function validate(values: AddPayerFormFields) {
  const errors: AddPayerFormFields = {};

  if (!values.businessName) {
    errors.businessName = "The Business Name is required.";
  }

  if (!values.email) {
    errors.email = "The student's email is required.";
  } else if (!/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$/i.test(values.email)) {
    errors.email = "The email address does not appear to be valid.";
  }

  return errors;
}
