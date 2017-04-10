import {reduxForm} from "redux-form";
import {Forms} from "../../constants/Forms";
import {connect} from "react-redux";
import {
  EditContactComponent,
  EditContactComponentProps
} from "../../components/enrol/editContact/EditContact";
import {IshState} from "../../services/IshState";

const EditContactStore = connect(mapStateToProps, mapDispatchToProps)(EditContactComponent);

function mapDispatchToProps(dispatch) {
  return {};
}

function mapStateToProps(state: IshState, ownProps: EditContactComponentProps) {
  return {};
}

export const EditContact = reduxForm({
  form: Forms.EDIT_CONTACT,
  validate,
  initialValues: {
    isMarketingViaEmailAllowed: true,
    isMarketingViaPostAllowed: true,
    isMarketingViaSMSAllowed: true
  }
})(EditContactStore);

function validate(values: EditContactFormFields) {
  const errors: EditContactFormFields = {};

  if (!values.dateOfBirth) {
    errors.dateOfBirth = "The Date of Birth is required.";
  }

  return errors;
}


export interface EditContactFormFields {
  address?: string;
  suburb?: string;
  country?: string;
  postcode?: string;
  state?: string;
  homePhone?: string;
  workPhone?: string;
  fax?: string;
  mobilePhone?: string;
  dateOfBirth?: string;
  gender?: string;
  isMarketingViaEmailAllowed?: boolean;
  isMarketingViaPostAllowed?: boolean;
  isMarketingViaSMSAllowed?: boolean;
}
