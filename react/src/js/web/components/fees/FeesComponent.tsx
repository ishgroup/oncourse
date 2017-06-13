import * as React from "react";
import {CourseClassPriceState} from "../../../services/IshState";

export interface Props extends CourseClassPriceState {
  isPaymentGatewayEnabled: boolean;
}

export const FeesComponent = (props: Props) => {
  const {fee, feeOverriden, hasTax} = props;

  if (feeOverriden) {
    return (
      <div className="price">
        {formatMoney(feeOverriden)}
        {hasTax && getTax(hasTax)}
      </div>
    );
  } else {
    return (
      <div className="price">
        {hasFee(fee) ? getFee(props) : <abbr title="To Be Advised">TBA</abbr>}
      </div>
    );
  }

};

function getFee(props: Props) {
  const {hasTax, isPaymentGatewayEnabled} = props;

  return (
    <div>
      {getDiscount(props)}
      {hasTax && getTax(hasTax)}
      {isPaymentGatewayEnabled && getDiscountItems(props)}
    </div>
  );
}

function getDiscountItems(props: Props) {
  const {possibleDiscounts} = props;

  return (
    <span>
        {possibleDiscounts.map(discount => (
          <span>
              <span className="discount-price">/</span>
              <abbr className="discount-price" title={discount.title}>
                {formatMoney(discount.discountedFee)}
              </abbr>
            </span>
        ))}
      </span>
  );
}

function getTax(hasTax) {
  return (
    <span className="gst">
        {hasTax && 'inc '}
      <abbr title="Goods and Services Tax">GST</abbr>
      {!hasTax && ' free'}
    </span>
  );
}

function hasFee(fee) {
  return !!fee;
}

function getDiscount(props: Props) {
  const {fee, appliedDiscount} = props;

  if (appliedDiscount) {
    return (
      <div>
        <span className="fee-disabled">
          {formatMoney(fee)}
        </span>
        <span className="fee-discounted">
          <abbr title={appliedDiscount.title}>
            {formatMoney(appliedDiscount.discountedFee)}
          </abbr>
        </span>
      </div>
    );
  } else {
    return (
      <div>
        {formatMoney(fee)}
      </div>
    );
  }
}

function formatMoney(value) {
  return `$${value} `;
}
