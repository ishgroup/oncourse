import * as React from "react";

import {PaymentResponse} from "../../../../model/checkout/payment/PaymentResponse";
import {PaymentStatus} from "../../../../model/checkout/payment/PaymentStatus";
import {Successful} from "./Successful";
import {Failed} from "./Failed";
import {Undefined} from "./Undefined";
import {InProgress} from "./InProgress";

interface Props {
  response: PaymentResponse
}
export class ResultComp extends React.Component<Props, any> {
  render() {
    const {response} = this.props;
    return (<div>
      {response.status === PaymentStatus.SUCCESSFUL && <Successful refId={response.reference}/>}
      {response.status === PaymentStatus.FAILED && <Failed/>}
      {response.status === PaymentStatus.UNDEFINED && <Undefined/>}
      {response.status === PaymentStatus.IN_PROGRESS && <InProgress/>}
    </div>);
  }
}