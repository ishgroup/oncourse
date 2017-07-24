import * as React from "react";
import {CourseClassPriceState} from "../../../services/IshState";
import {formatMoney} from "../../../common/utils/FormatUtils";

export interface Props extends CourseClassPriceState {
  isPaymentGatewayEnabled: boolean;
}

export class FeesComponent extends React.Component<Props, any> {
  render() {
    const {fee, feeOverriden, hasTax} = this.props;
    if (feeOverriden) {
      return (
        <div className="price">
          {formatMoney(feeOverriden)}
          {hasTax && this.tax()}
        </div>
      );
    } else {
      return (
        <div className="price">
          {fee ? this.fee() : <abbr title="To Be Advised">TBA</abbr>}
        </div>
      );
    }
  }

  private fee() {
    const {hasTax, isPaymentGatewayEnabled} = this.props;

    return (
      <div>
        {this.discount()}
        {hasTax && this.tax()}
        {isPaymentGatewayEnabled && this.discountItems()}
      </div>
    );
  }

  private discountItems() {
    const {possibleDiscounts} = this.props;

    return (
      <span>
        {possibleDiscounts.map((discount, i) => (
          <span key={i}>
              <span className="discount-price">/</span>
              <abbr className="discount-price" title={discount.title}>
                {formatMoney(discount.discountedFee)}
              </abbr>
            </span>
        ))}
      </span>
    );
  }

  private tax() {
    const {hasTax} = this.props;
    return (
      <span className="gst">
        {hasTax && ' inc '}
        <abbr title="Goods and Services Tax">GST</abbr>
        {!hasTax && ' free'}
    </span>
    );
  }

  private discount() {
    const {fee, appliedDiscount} = this.props;

    if (appliedDiscount) {
      return (
        <span>
          <span className="fee-disabled">
            {formatMoney(fee)}
          </span>
            <span className="fee-discounted">
            <abbr title={appliedDiscount.title}>
              {formatMoney(appliedDiscount.discountedFee)}
            </abbr>
          </span>
        </span>
      );
    } else {
      return (
        <span>
          {formatMoney(fee)}
        </span>
      );
    }
  }
}



