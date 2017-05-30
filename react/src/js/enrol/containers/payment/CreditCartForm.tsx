import React from "react";
import {DataShape, FormProps, reduxForm} from "redux-form";
import {Contact} from "../../../model/web/Contact";
import {IshState} from "../../../services/IshState";
import {connect} from "react-redux";
import CreditCardComp from "./components/CreditCardComp";
import {Amount} from "../../../model/checkout/Amount";
import {Conditions} from "./components/Conditions";

interface Props extends FormProps<DataShape, any, any> {
  amount: Amount;
  contacts: Contact[];
  onSubmit: (data, dispatch, props) => any;
}

const NAME = "CreditCartForm";

class CreditCartForm extends React.Component<Props, any> {
  render() {
    const {amount, contacts, handleSubmit} = this.props;
    return (
      <div id="tabable-container">
        <div className="tab-content">
          <form onSubmit={handleSubmit} id="payment-form">
            <CreditCardComp amount={amount} contacts={contacts}/>
            <Conditions/>
            <div className="form-controls enrolmentsSelected">
              <input value="Confirm" className="btn btn-primary" id="paymentSubmit" name="paymentSubmit" type="submit" />
            </div>
          </form>
        </div>
      </div>
    )
  }
}


const Form = reduxForm({
  form: NAME,
  validate: (values) => {
    return values
  },
  onSubmitSuccess: (result, dispatch, props: any) => {
  },
  onSubmitFail: (errors, dispatch, submitError, props) => {
  }
})(CreditCartForm);


const mapStateToProps = (state: IshState) => {
  return {
    contacts: [state.checkout.payer.entity],
    amount: state.checkout.amount
  }
};

const mapDispatchToProps = (dispatch) => {
  return {
    onSubmit: (data, dispatch, props): any => {
    }
  };
};


export const Container = connect(
  mapStateToProps,
  mapDispatchToProps
)(Form);


