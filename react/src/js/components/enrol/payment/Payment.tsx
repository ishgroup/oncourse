import * as React from "react";
import {connect, Dispatch} from "react-redux";
import {withRouter} from "react-router";
import {IshState} from "../../../services/IshState";
import {ReactRouter} from "../../../types";
import {Paths} from "../../../config/Paths";

class PaymentComponent extends React.Component<PaymentProps, PaymentState> {
  constructor() {
    super();
  }

  render() {
    const {router} = this.props;

    return (
      <div onClick={() => router.push(Paths.checkout)}>
        Payment
      </div>
    );
  }
}

interface PaymentProps {
  router: ReactRouter;
}

interface PaymentState {
}

interface DispatchProps {
}

const mapStateToProps = (state: any, ownProps: any): PaymentState => {
  return {
    router: ownProps.router
  };
};

const mapDispatchToProps = (dispatch: Dispatch<IshState>): DispatchProps => {
  return {
    fetchCandidate: (id: string) => {
      dispatch({
        type: "SOME_TYPE"
      }); // {} -> action
    }
  }
};


export const Payment = withRouter(connect(mapStateToProps, mapDispatchToProps)(PaymentComponent));

