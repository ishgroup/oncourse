import {reduxForm} from "redux-form";
import * as React from "react";
import {connect} from "react-redux";

import {ContactAdd} from "./components/ContactAdd";
import {validateContact} from "./actions/Validations";
import {ContactAddAction} from "./actions/Actions";
import CheckoutService from "../../services/CheckoutService";
import {showErrors} from "../../actions/Actions";


export const NAME = "ContactAddForm";

class ContactAddForm extends React.Component<any, any> {
  render() {
    const {handleSubmit, pristine, invalid, submitting} = this.props;
    return (
      <div>
        <form onSubmit={handleSubmit}>
          <ContactAdd/>
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

const Form = reduxForm({
  form: NAME,
  validate: validateContact,
  onSubmitSuccess: (result, dispatch, props: any) => {
    dispatch(ContactAddAction(result, props.values));
  },
  onSubmitFail: (errors, dispatch, submitError, props) => {
    dispatch(showErrors(submitError, NAME));
  }
})(ContactAddForm);


const mapStateToProps = (state) => {
  return {}
};

const mapDispatchToProps = (dispatch) => {
  return {
    onSubmit: CheckoutService.createOrGetContact
  };
};


const Container = connect(
  mapStateToProps,
  mapDispatchToProps
)(Form);


export default Container;