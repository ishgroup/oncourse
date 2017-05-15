import {reduxForm} from "redux-form";
import * as React from "react";
import {connect} from "react-redux";

import {Contact} from "../../../model/web/Contact";
import {IshState} from "../../../services/IshState";
import {ContactFields} from "../../../model/field/ContactFields";
import {ContactEdit} from "../../components/contact/edit/ContactEdit";
import MockContactFields from "../../../httpStub/MockContactFields"


export const NAME = "EditContactForm";

class EditContactForm extends React.Component<any, any> {
  render() {
    const {handleSubmit, pristine, invalid, submitting} = this.props;
    const contact:Contact = this.props.contact;
    const fields:ContactFields = this.props.fields;

    return (
      <form onSubmit={handleSubmit} id="contactEditorForm">
        <ContactEdit contact={contact} fields={fields}/>
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

  componentWillMount() {
    this.props.loadFields();
  }
}

interface Props {
  contact: Contact,
  contactFields: ContactFields,
  loadFields: () => void;
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
    contact: state.enrol.payer.entity,
    fields: MockContactFields ,
    loadFields: () => {}
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
