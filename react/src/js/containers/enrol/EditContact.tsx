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
  warn,
  initialValues: {
    country: "Australia",
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

function warn(values: EditContactFormFields) {
  const warnings: EditContactFormFields = {};

  warnings.country = "Please specify Country if outside of Australia.";
  warnings.postcode = "Enter a postcode or zipcode.";
  warnings.homePhoneNumber = "Enter 10 digit home phone number including area code for Australian numbers.";
  warnings.businessPhoneNumber = "Enter 10 digit business phone number including area code for Australian numbers.";
  warnings.faxNumber = "Enter 10 digit fax number including area code for Australian numbers.";
  warnings.mobilePhoneNumber = "Enter 10 digit mobile phone number for Australian numbers.";
  warnings.dateOfBirth = "Enter your date of birth in the form DD/MM/YYYY.";

  return warnings;
}


export interface EditContactFormFields {
  address?: string;
  suburb?: string;
  country?: string;
  postcode?: string;
  state?: string;
  homePhoneNumber?: string;
  businessPhoneNumber?: string;
  faxNumber?: string;
  mobilePhoneNumber?: string;
  dateOfBirth?: string;
  gender?: string;
  isMarketingViaEmailAllowed?: boolean;
  isMarketingViaPostAllowed?: boolean;
  isMarketingViaSMSAllowed?: boolean;
}
