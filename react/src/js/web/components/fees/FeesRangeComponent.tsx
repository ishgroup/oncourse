import * as React from "react";
import * as L from "lodash";
import {Dialog} from "./EstimateFeeDialog";
import {CourseClassPrice} from "../../../model/web/CourseClassPrice";

import {Field} from "../../../model/field/Field";
import {FeesComponent, Props as FeesCompProps} from "./FeesComponent";


export interface Model {
  header: string
  message: string
  fields: Field[]
  price: CourseClassPrice
}

export interface Values {

}

export interface Props {
  model: Model
  estimatedDiscountIndex?: number;
  onSubmit?: (Values) => void
  onClose?: () => void
}

export class FeesRangeComponent extends React.Component<Props, any> {
  private dialog: Dialog;

  render() {
    const {model, onClose, onSubmit, estimatedDiscountIndex} = this.props;
    if (L.isNil(estimatedDiscountIndex)) {
      return (
        <div className="price">
          <button onClick={() => this.dialog.setState({visible: true})}>Estimate my fee</button>
          <Dialog ref={(r) => this.dialog = r} model={model} onClose={onClose} onSubmit={onSubmit}/>
        </div>
      );
    } else {
      const props: FeesCompProps = Object.assign({isPaymentGatewayEnabled: true}, L.cloneDeep(this.props.model.price));
      props.feeOverriden = props.possibleDiscounts[estimatedDiscountIndex].discountedFee;
      return (<FeesComponent  {...props}/>)
    }
  }
}