import React from "react";
import {connect} from "react-redux";
import {IshState} from "../../../services/IshState";
import {Contact} from "../../../model/web/Contact";
import ConcessionForm from "./components/ConcessionForm";
import {Field, reduxForm} from "redux-form";
import Checkbox from "../../../components/form-new/Checkbox";
import {changePhase} from "../../actions/Actions";
import {getConcessionTypes} from "./actions/Actions";

export interface Props {
  contact: Contact;
  concessions: any[];
}

export const NAME = "Concession";

class Concession extends React.Component<any, any> {
  constructor(props) {
    super(props);
  }

  componentWillMount() {
    this.state = {
      validConcession: false,
      isConcessionAgreed: false,
    };
  }

  componentDidMount() {
    this.props.onInit();
  }

  onTypeChange = value => {
    this.setState({
      validConcession: (value.key !== -1),
    });
  }

  onConcessionAgreed = value => {
    this.setState({
      isConcessionAgreed: !value.target.value,
    });
  }

  render() {
    const {contact, handleSubmit, pristine, invalid, submitting, onCancel, page, concessionTypes} = this.props;

    return (
      <div className="concessionEditor">
        <h3>Add concession for {contact.firstName + " " + contact.lastName}</h3>
        <form onSubmit={handleSubmit}>
          <fieldset>
            {concessionTypes &&
              <ConcessionForm concessionTypes={concessionTypes} onTypeChange={this.onTypeChange}/>
            }
            {this.state.validConcession &&
            <ConcessionText onConcessionAgreed={this.onConcessionAgreed}/>
            }
          </fieldset>
          <p>
            {this.state.isConcessionAgreed &&
            <button type="submit" className="btn" disabled={invalid || pristine || submitting}>Save Concession</button>
            }
            <button id="cancelConcession" className="btn" onClick={() => onCancel(page)}>Cancel</button>
          </p>
        </form>
      </div>
    );
  }
}

const ConcessionText = props => {
  return (
    <div className="clearfix conditions">
      <Field
        component={Checkbox}
        type="checkbox"
        name="concessionAgree"
        required={true}
        onChange={props.onConcessionAgreed}
        value={true}
      />
      I certify that the concession I have claimed is valid and
      I understand that the details may be checked with the issuing body.
    </div>
  );
};

const Form = reduxForm({
  form: NAME,
  // validate: validate,
  onSubmitSuccess: (result, dispatch, props: any) => {

  },
  onSubmitFail: (error, dispatch, submitError, props) => {

  },
})(Concession);


const mapStateToProps = (state: IshState) => {
  return {
    contact: state.checkout.contacts.entities.contact[state.checkout.concession.contactId],
    page: state.checkout.page,
    concessionTypes: state.checkout.concession.types,
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onSubmit: (data, dispatch, props): any => {
      return '';
    },
    onCancel: phase => {
      dispatch(changePhase(phase));
    },
    onInit: () => {
      dispatch(getConcessionTypes());
    },
  };
};


const Container = connect(
  mapStateToProps,
  mapDispatchToProps,
)(Form as any);


export default Container;