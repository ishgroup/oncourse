import {reduxForm} from "redux-form";
import * as React from "react";
import {connect} from "react-redux";

import {Contact} from "../../../model/web/Contact";
import {IshState} from "../../../services/IshState";

export const NAME = "EditContactForm";

class EditContactForm extends React.Component<any, any> {
  render() {
    const {handleSubmit, pristine, invalid, submitting} = this.props;
    const contact:Contact = this.props.contact;
    return (
      <form onSubmit={handleSubmit}>
        <div className="student">{contact.firstName} {contact.lastName} <span>- {contact.email}</span></div>
        <div className="message">
          <p>We require a few more details to create the contact record.
            It is important that we have correct contact information in case we need to let you know about course
            changes.
            Please enter the details as you would like them to appear on a certificate or invoice.
          </p>
        </div>
        <div className="form-controls">
          <input value="OK"
                 className="btn btn-primary"
                 name="submitContact"
                 type="submit"
                 disabled={invalid || pristine || submitting}
          />
        </div>
      </form>
    )
  }
}

interface Props {
  contact: Contact
}

const Form = reduxForm({
  form: NAME,
  validate: null,
  onSubmitSuccess: (result, dispatch, props) => {
  },
  onSubmitFail: (errors, dispatch, submitError, props) => {
  }
})(EditContactForm);


const mapStateToProps = (state:IshState) => {
  return {
    contact: state.enrol.payer.entity
  }
};

const mapDispatchToProps = (dispatch) => {
  return {
    onSubmit: (values): any => {},
  };
};


const Container = connect(
  mapStateToProps,
  mapDispatchToProps
)(Form);


export default Container;
