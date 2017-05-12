import {reduxForm} from "redux-form";
import * as React from "react";
import {normalize} from "normalizr";
import {connect} from "react-redux";

import {AddContact} from "../../components/contact/AddContact";
import {validateContact} from "../../actions/Validations";
import {contactsSchema} from "../../../schema";
import {ContactAdd, ContactAddReject} from "../../actions/Actions";
import {ContactApiStub} from "../../../httpStub/ContactApiStub";

export const NAME = "AddContactForm";

class AddContactForm extends React.Component<any, any> {
  render() {
    const {handleSubmit, pristine, invalid, submitting} = this.props;
    return (
      <form onSubmit={handleSubmit}>
        <AddContact/>
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

const Form = reduxForm({
  form: NAME,
  validate: validateContact,
  onSubmitSuccess: (result, dispatch, props) => {
    dispatch({
      type: ContactAdd,
      payload: normalize(result, contactsSchema),
      meta: {
        from: NAME
      }
    });
  },
  onSubmitFail: (errors, dispatch, submitError, props) => {
    dispatch({
      type: ContactAddReject,
      payload: submitError.data,
      meta: {
        from: NAME
      }
    });
  }
})(AddContactForm);


const mapStateToProps = (state) => {
  return {}
};

const mapDispatchToProps = (dispatch) => {
  return {
    onSubmit: (values): any => {
      const api = new ContactApiStub(null);
      return api.createOrGetContact(values);
    },
  };
};


const Container = connect(
  mapStateToProps,
  mapDispatchToProps
)(Form);


export default Container;