import React from "react";
import classnames from "classnames";
import {DataShape, FormProps, reduxForm} from "redux-form";
import {Contact} from "../../../model/web/Contact";
import {IshState} from "../../../services/IshState";
import {connect} from "react-redux";
import CreditCardComp from "./components/CreditCardComp";
import {Amount} from "../../../model/checkout/Amount";
import {Conditions} from "./components/Conditions";
import {makePayment} from "./actions/Actions";
import {Values} from "./services/PaymentService";

interface Props extends FormProps<DataShape, any, any> {
  amount: Amount;
  contacts: Contact[];
}

export const NAME = "CreditCartForm";

class CreditCartForm extends React.Component<Props, any> {
  render() {
    const {amount, contacts, handleSubmit, invalid, pristine, submitting} = this.props;
    const disabled = (invalid || pristine || submitting);
    const className = classnames("btn", "btn-primary", {"disabled": disabled});
    return (
      <div>
        <form onSubmit={handleSubmit} id="payment-form">
          <CreditCardComp amount={amount} contacts={contacts}/>
          <Conditions/>
          <div className="form-controls enrolmentsSelected">
            <input className={className} id="paymentSubmit" name="paymentSubmit" type="submit"
                   disabled={disabled}/>
          </div>
        </form>
      </div>
    )
  }
}


const Form = reduxForm({
  form: NAME,
  onSubmit: (data: Values, dispatch, props): void => {
    dispatch(makePayment(data));
  }
})(CreditCartForm);


const mapStateToProps = (state: IshState) => {
  return {
    contacts: [state.checkout.payer.entity],
    amount: state.checkout.amount
  }
};

const mapDispatchToProps = (dispatch) => {
  return {}
};

export const Container = connect(
  mapStateToProps,
  mapDispatchToProps
)(Form);


