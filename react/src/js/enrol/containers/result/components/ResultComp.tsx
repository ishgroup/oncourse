import * as React from "react";

import {PaymentResponse} from "../../../../model/checkout/payment/PaymentResponse";
import {PaymentStatus} from "../../../../model/checkout/payment/PaymentStatus";
import {Successful} from "./Successful";
import {Failed} from "./Failed";
import {Undefined} from "./Undefined";
import {InProgress} from "./InProgress";

export interface Props {
  response: PaymentResponse;
  onAnotherCard?: () => void;
  onCancel?: () => void;
}

export class ResultComp extends React.Component<Props, any> {
  render() {
    const {response, onAnotherCard, onCancel} = this.props;
    return (
      <div>
        {response.status === PaymentStatus.SUCCESSFUL && <Successful refId={response.reference}/>}
        {response.status === PaymentStatus.FAILED && <Failed onAnotherCard={onAnotherCard} onCancel={onCancel}/>}
        {response.status === PaymentStatus.UNDEFINED && <Undefined/>}
        {response.status === PaymentStatus.IN_PROGRESS && <InProgress/>}
      </div>
    );
  }
}
