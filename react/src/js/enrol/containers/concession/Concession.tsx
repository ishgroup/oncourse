import React from "react";
import {connect} from "react-redux";
import {IshState} from "../../../services/IshState";
import {Contact} from "../../../model";
import ConcessionForm from "./components/ConcessionForm";
import {reduxForm, FormErrors} from "redux-form";
import {changePhase} from "../../actions/Actions";
import {getConcessionTypes} from "./actions/Actions";
import CheckoutService from "../../services/CheckoutService";

export interface Props {
  contact: Contact;
  concessionTypes: any[];
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

  onTypeChange = () => {
    this.props.reset();
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

          </fieldset>
          <p>

            <button type="submit" className="btn" disabled={pristine || submitting}>Save Concession</button>

            <button id="cancelConcession" className="btn" onClick={() => onCancel(page)}>Cancel</button>
          </p>
        </form>
      </div>
    );
  }
}

export const validate = (data, props) => {
  const errors = {};

  // skip validation if concession type not set
  if (!data.concessionType) return errors;

  const concessionType = props.concessionTypes.find(item => item.id === data.concessionType);

  if (concessionType.hasExpireDate && !data.date) {
    errors['date'] =  'Date is incorrect';
  }

  if (concessionType.hasNumber && !data.number) {
    errors['number'] = 'The number cannot be blank.';
  }

  if (concessionType.key !== '-1' && !data.concessionAgree) {
    errors['concessionAgree'] = 'You must agree to the policies before proceeding.';
  }

  return errors;
};

const Form = reduxForm({
  validate,
  form: NAME,
  onSubmitSuccess: (result, dispatch, props: any) => {
    dispatch(changePhase(props.page));
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
      CheckoutService.submitConcession(data, props);
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
