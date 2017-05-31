import * as React from "react";
import {isNil} from "lodash";
import {Formats} from "../../../../constants/Formats";
import * as FormatUtils from "../../../../common/utils/FormatUtils";

import classnames from "classnames";
import {Contact} from "../../../../model/web/Contact";
import {Voucher} from "../../../../model/checkout/Voucher";
import {ProductClass} from "../../../../model/web/ProductClass";
import {Article} from "../../../../model/checkout/Article";
import {Membership} from "../../../../model/checkout/Membership";


export interface Props {
  contact: Contact;
  productItem: Voucher | Article | Membership;
  productClass: ProductClass;
  onChange?: () => void;
  onPriceValueChange?: () => void;
  type: Voucher | Article | Membership;
}

class ProductListComp extends React.Component<Props, any> {
  public render(): JSX.Element {
    const {productItem, productClass, contact, onChange, onPriceValueChange, type} = this.props;
    const divClass = classnames("row", "enrolmentItem", {"disabled": !productItem.selected});
    const checkBoxName = `productItem-${contact.id}-${productItem.productId}`;
    const title: string = `${productClass.product.name}`;

    let warning = productItem.warnings && productItem.warnings.length ? this.props.productItem.warnings[0] : null;
    const error = productItem.warnings && productItem.errors.length ? this.props.productItem.errors[0] : null;

    return (
      <div className={divClass}>
        <div className="col-xs-16 col-md-17 enrolmentInfo">
          <label>
            <input className="productItemCheckbox"
                   type="checkbox"
                   name={checkBoxName}
                   onChange={ onChange }
                   checked={productItem.selected } disabled={!isNil(error)} />
            { title }
          </label>
          {warning && (<span dangerouslySetInnerHTML={{__html: warning}} />)}
          {error && <span className="disabled" dangerouslySetInnerHTML={{__html: error}} />}
          <br/>
          <VoucherDetails productItem={productItem} type={type} />
        </div>
        {productItem.selected && productClass.price && <ClassPrice product={productItem} onPriceValueChange={onPriceValueChange} />}
      </div>
    );
  }
}


const ClassPrice = (props): any => {
  const product: Voucher | Article | Membership = props.product;
  const price = product.price;

  return (
    <div className="col-xs-8 col-md-7 alignright priceValue">
      <div className="row">
        { (price === "0" || price === "") ?
          <div className="col-xs-24 col-md-24 fee-full fullPrice text-right">
            <input type="text" name="priceValue" value={price} onChange={props.onPriceValueChange.bind(this)} />
            <div className="button update-voucher-price display-none priceValue">Update</div>
          </div>
          : <span className="col-xs-24 col-md-24 fee-full fullPrice text-right">${ price }</span>
        }
      </div>
    </div>
  )
};

const VoucherDetails = (props): any => {
  const productItem: Voucher = props.productItem;

  return (
    <div>
      {props.type === Voucher && (
        (productItem.price !== "0" && productItem.price !== "") ? (<div>
          This is a voucher with specified price.
          {productItem.classes ? (
            <div>
              <dt className="label">This voucher valid for</dt>
              <dd>{VoucherClasses({classes: productItem.classes})}</dd>
            </div>)
            : (
              <div>
                <dt className="label">Value</dt>
                <dd>{ productItem.value }</dd>
              </div>)
          }
        </div>) : <div>
          This voucher have no specified price.
          <dt className="label">Please enter the amount:</dt>
        </div>
      )}
    </div>
  )
};

const VoucherClasses = (props): JSX.Element => {
  return props.classes.map((c, i) =>
    <li key={i}>{c}</li>
  );
}


export default ProductListComp;
