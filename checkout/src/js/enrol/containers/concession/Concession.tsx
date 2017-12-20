import React from "react";
import {connect} from "react-redux";
import classnames from "classnames";
import {IshState} from "../../../services/IshState";
import {Contact} from "../../../model";
import ConcessionForm from "./components/ConcessionForm";
import {reduxForm, FormErrors} from "redux-form";
import {changePhase, showFormValidation, showSyncErrors} from "../../actions/Actions";
import {getConcessionTypes} from "./actions/Actions";
import CheckoutService from "../../services/CheckoutService";
import {scrollToTop} from "../../../common/utils/DomUtils";

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
    scrollToTop();
  }

  onTypeChange = () => {
    this.props.reset();
  }

  render() {
    const {contact, handleSubmit, pristine, submitting, onCancel, page, concessionTypes} = this.props;
    return (
      <div className="concessionEditor">
        <h3>Add concession for {contact.firstName + " " + contact.lastName}</h3>
        <form onSubmit={handleSubmit} className={classnames({submitting})}>
          <fieldset>
            {concessionTypes &&
              <ConcessionForm concessionTypes={concessionTypes} onTypeChange={this.onTypeChange}/>
            }

          </fieldset>

          <div className="form-controls flex">
            <a id="cancelConcession" onClick={() => onCancel(page)}>Cancel</a>
            <button type="submit" className="btn" disabled={pristine || submitting}>Save Concession</button>
          </div>
        </form>
      </div>
    );
  }
}

export const validate = (data, props) => {
  const errors = {};

  // skip validation if concession type not set
  if (!data.concessionType) return errors;

  const concessionType = props.concessionTypes.find(item => item.id === data.concessionType.key);

  if (concessionType.hasExpireDate && !data.expiryDate) {
    errors['expiryDate'] =  'Date is incorrect';
  }

  if (concessionType.hasNumber && !data.number) {
    errors['number'] = 'The number cannot be blank.';
  }

  if (concessionType.id !== '-1' && !data.concessionAgree) {
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
  onSubmitFail: (errors, dispatch, submitError, props) => {
    if (errors && !submitError) {
      dispatch(showSyncErrors(errors));
    } else {
      dispatch(showFormValidation(submitError, NAME));
    }
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
      return CheckoutService.submitConcession(data, props);
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
