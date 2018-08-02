import * as React from "react";
import {CourseClassPriceState} from "../../../services/IshState";
import {formatMoney} from "../../../common/utils/FormatUtils";

export interface Props extends CourseClassPriceState {
  isPaymentGatewayEnabled: boolean;
  isAllowByApplication: boolean;
}

export class FeesComponent extends React.Component<Props, any> {
  render() {
    const {fee, feeOverriden, hasTax, isAllowByApplication} = this.props;

    if (isAllowByApplication) {
      return (<div className="price">
        <ul>
          {this.feeRange()}
        </ul>
      </div>);
    }

    if (feeOverriden) {
      return (
        <div className="price">
          <ul>
            {formatMoney(feeOverriden)}
            {hasTax && this.tax()}
          </ul>
        </div>
      );
    } else {
      return (
        <div className="price">
          {(fee || fee === 0)
            ? this.fee()
            : <ul>
                <li>
                  <abbr title="To Be Advised">TBA</abbr>
                </li>
              </ul>
          }
        </div>
      );
    }
  }

  private fee() {
    const {hasTax, isPaymentGatewayEnabled, possibleDiscounts} = this.props;

    return (
      <ul>
        {this.discount()}
        {hasTax && this.tax()}
        {isPaymentGatewayEnabled && Boolean(possibleDiscounts.length) && this.discountItems()}
      </ul>
    );
  }

  private feeRange() {
    const {fee, appliedDiscount} = this.props;

    const min = appliedDiscount ? appliedDiscount.discountedFee > fee ? fee : appliedDiscount.discountedFee : fee;

    const max = appliedDiscount ? appliedDiscount.discountedFee > fee ? appliedDiscount.discountedFee : fee : fee;

    return (
        <li  className="fee-discount-range">
          <span className="price-low">{formatMoney(min)}</span>
          <span className="price-separator">-</span>
          <span className="price-high">{formatMoney(max)}</span>
        </li>
    );
  }

  private discountItems() {
    const {possibleDiscounts} = this.props;

    return (
      <li>
        <ul>
          {possibleDiscounts.map((discount, i) => (
            <li key={i}>
              <span className="discount-price"> / </span>
              <abbr className="discount-price" title={discount.title}>
                {formatMoney(discount.discountedFee)}
              </abbr>
            </li>
          ))}
        </ul>
      </li>
    );
  }

  private tax() {
    const {hasTax} = this.props;
    return (
      <li className="gst">
        {hasTax && ' inc '}
        <abbr title="Goods and Services Tax">GST</abbr>
        {!hasTax && ' free'}
    </li>
    );
  }

  private discount() {
    const {fee, appliedDiscount} = this.props;

    if (appliedDiscount) {
      return ([
        <li key="fee-disabled" className="fee-disabled">
            {formatMoney(fee)}
          </li>,
        <li key="fee-discounted" className="fee-discounted">
          <abbr title={appliedDiscount.title}>
            <span>
              {formatMoney(appliedDiscount.discountedFee)}
            </span>
          </abbr>
        </li>,
      ]);
    } else {
      return (
        <li>
          {formatMoney(fee)}
        </li>
      );
    }
  }
}
