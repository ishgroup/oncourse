import * as React from "react";
import * as ReactDOM from "react-dom";
import {FeesComponent, Props} from "../../../../js/web/components/fees/FeesComponent";
import {mockCourseClassPrice} from "../../../mocks/mocks/MockFunctions";

const props: Props = Object.assign({isPaymentGatewayEnabled: true}, mockCourseClassPrice());
console.log(props);
props.feeOverriden = null;

const render = () => ReactDOM.render(
  <div>
    <FeesComponent {...props}/>
  </div>,
  document.getElementById("react-fees-component")
);

render();