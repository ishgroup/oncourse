import * as React from "react";
import classnames from "classnames";

import {Contact, Voucher, Product} from "../../../../model";
import {ItemWrapper} from "./ItemWrapper";


export interface Props {
  contact: Contact;
  voucher: Voucher;
  product: Product;
  onChange?: () => void;
  onPriceValueChange?: (val: any) => any;
  onQuantityValueChange?: (val: any) => any;
  updateCheckoutModel?: () => void;
}

class VoucherComp extends React.PureComponent<Props, any> {

  constructor(props) {
    super(props)

    this.state = {
      price: props.voucher.price || 0,
      quantity: props.voucher.quantity || 1,
    };
  }

  componentWillReceiveProps(props) {
    this.setState({
      price: Number(props.voucher.price).toFixed(2),
      quantity: Number(props.voucher.quantity).toFixed(0),
    });
  }

  private updatePrice = val => {
    const reg = (/^[0-9]+\.?[0-9]*$/);

    if (val > 0 && reg.test(val)) {
      this.setState({
        price: val,
      });
    }
    return false;
  }

  private updateQuantity = val => {
    const reg = (/^[0-9]+/);

    if (val === '' || (val > 0 && reg.test(val))) {
      this.setState({
        quantity: val,
      });
    }
    return false;
  }

  private handlePriceBlur(val) {
    const {onPriceValueChange} = this.props;
    onPriceValueChange(Number(val).toFixed(2));
  }

  private handleQuantityBlur(val) {
    const {onQuantityValueChange} = this.props;
    onQuantityValueChange(val);
  }

  public render(): JSX.Element {
    const {voucher, product, contact, onChange} = this.props;
    const divClass = classnames("row", "enrolmentItem", {disabled: !voucher.selected});
    const warning = voucher.warnings && voucher.warnings.length ? this.props.voucher.warnings[0] : null;
    const error = voucher.warnings && voucher.errors.length ? this.props.voucher.errors[0] : null;
    const name = `voucher-${contact.id}-${voucher.productId}`;

    return (
      <div className={divClass}>
        <ItemWrapper title={product.name} name={name} error={error} warning={warning} selected={voucher.selected}
                     item={voucher} contact={contact}
                     onChange={onChange}>
          <VoucherDetails voucher={voucher}/>
        </ItemWrapper>
        {voucher.selected &&
            <div>
              <VoucherQuantity
                  voucher={voucher}
                  quantity={this.state.quantity}
                  onChange={val => this.updateQuantity(val)}
                  onBlur={val => this.handleQuantityBlur(val)}/>
              <VoucherPrice
                voucher={voucher}
                price={this.state.price}
                onChange={val => this.updatePrice(val)}
                onBlur={val => this.handlePriceBlur(val)}
              />
            </div>
        }
      </div>
    );
  }
}

const VoucherDetails = (props): any => {
  const voucher: Voucher = props.voucher;

  return (
    <div>
      {voucher.isEditablePrice ?
        <div>
          Please change the suggested voucher price to the amount of your choice.
        </div>
        : (<div>
          This is a voucher with specified price.
          {voucher.classes && voucher.classes.length > 0 ? (
              <div>
                <dt className="label">This voucher valid for</dt>
                <dd>{voucher.classes.map((c, i) =>
                  <li key={i}>{c}</li>)}
                </dd>
              </div>)
            : (
              <div>
                <dt className="label">Value</dt>
                <dd>${Number(voucher.value).toFixed(2)}</dd>
              </div>)
          }
        </div>)
      }
    </div>
  );
};

const VoucherPrice = (props): any => {
  const voucher = props.voucher;
  const price = props.price;

  return (
    <div className="col-xs-8 col-md-7 alignright priceValue">
      <div className="row">
        {voucher.isEditablePrice ?
          <div className="col-xs-24 col-md-24 fee-full fullPrice text-right">
            <input
              type="text"
              className="text-right"
              name="priceValue"
              value={`$${price}`}
              onChange={e => props.onChange(e.target.value.replace('$', ''))}
              onBlur={e => props.onBlur(e.currentTarget.value.replace('$', ''))}
            />
          </div>
          :
          <span className="col-xs-24 col-md-24 fee-full fullPrice text-right">
          ${Number(voucher.price).toFixed(2)}
          </span>
        }
      </div>
    </div>
  );
};

const VoucherQuantity = (props): any => {
  const quantity = props.quantity;

  return (
      <div className="col-xs-8 col-md-7 alignright quantityValue">
        <div className="row">
          <div className="col-xs-24 col-md-24 fee-full quantity text-right">
            <span className="col-xs-24 col-md-24 fee-full quantity text-right">Quantity:</span>
            <input
                type="text"
                className="text-left"
                name="quantityValue"
                value={quantity}
                onChange={e => props.onChange(e.target.value)}
                onBlur={e => props.onBlur(e.currentTarget.value)}
            />
          </div>
        </div>
      </div>
  );
};

export default VoucherComp;
