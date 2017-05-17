import {reduxForm} from "redux-form";
import * as React from "react";
import {connect} from "react-redux";

import {ContactAdd} from "../../components/contact/ContactAdd";
import {validateContact} from "../../actions/Validations";
import {ContactAddAction, ContactAddRejectAction} from "../../actions/Actions";
import {Injector} from "../../../injector";
import {CreateContactParams} from "../../../model/web/CreateContactParams";
import {FieldSet} from "../../../model/field/FieldSet";

export const NAME = "ContactAddForm";

class ContactAddForm extends React.Component<any, any> {
  render() {
    const {handleSubmit, pristine, invalid, submitting} = this.props;
    return (
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
    dispatch(ContactAddRejectAction(submitError.data));
  }
})(ContactAddForm);


const {
  contactApi,
} = Injector.of();


const mapStateToProps = (state) => {
  return {}
};

const mapDispatchToProps = (dispatch) => {
  return {
    onSubmit: (values): any => {
      const request:CreateContactParams = Object.assign({}, values, {fieldSet: FieldSet.enrolment});
      return contactApi.createOrGetContact(request);
    },
  };
};


const Container = connect(
  mapStateToProps,
  mapDispatchToProps
)(Form);


export default Container;