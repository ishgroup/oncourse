import * as React from "react";
import {CourseClassPriceState} from "../../services/IshState";

export interface FeesComponentProps extends CourseClassPriceState {
  paymentGatewayEnabled: boolean;
}

export const FeesComponent = (props: FeesComponentProps) => {
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

function getFee(props: FeesComponentProps) {
  const {hasTax, paymentGatewayEnabled} = props;

  return (
    <div>
      {getDiscount(props)}
      {hasTax && getTax(hasTax)}
      {paymentGatewayEnabled && getDiscountItems(props)}
    </div>
  );
}

function getDiscountItems(props: FeesComponentProps) {
  const {possibleDiscounts} = props;

  return (
    <div>

    {possibleDiscounts.map(discount => (
      <div key={discount.id}>
        <span className="discount-price">/</span>
        <abbr className="discount-price" title={discount.title}>
          {formatMoney(discount.discountedFee)}
        </abbr>
      </div>
    ))}
    </div>
  );
}

function getTax(hasTax) {
  return (
    <span className="gst">
        {!hasTax && 'inc '}
      <abbr title="Goods and Services Tax">GST</abbr>
      {hasTax && ' free'}
    </span>
  );
}

function hasFee(fee) {
  return !!fee;
}

function getDiscount(props: FeesComponentProps) {
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
