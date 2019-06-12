import * as React from "react";
import classnames from "classnames";

import {Contact, Voucher, Product} from "../../../../model";
import {ItemWrapper} from "./ItemWrapper";


export interface Props {
  contact: Contact;
  voucher: Voucher;
  product: Product;
  onChange?: () => void;
  onPriceValueChange?: (val: number) => any;
  onQuantityValueChange?: (val: number) => any;
  updateCheckoutModel?: () => void;
}


export interface State {
  price: number;
  quantity: number;
}

class VoucherComp extends React.PureComponent<Props, State> {

  constructor(props) {
    super(props)

    this.state = {
      price: props.voucher.price || 0,
      quantity: props.voucher.quantity || 1,
    };
  }

  componentWillReceiveProps(props) {
    this.setState({
      price:  props.voucher.price,
      quantity: props.voucher.quantity,
    });
  }

  private updateValue = val => {
    const reg = (/^[0-9]+\.?[0-9]*$/);

    if (val > 0 && reg.test(val)) {
      this.setState({
        price: Number(val),
      });
    }
    return false;
  }

  private updateQuantity = val => {
    const reg = (/^[0-9]+/);

    if (val === '' || (val > 0 && reg.test(val))) {
      this.setState({
        quantity: Number(val),
      });
    }
    return false;
  }

  private handleValueBlur() {
    const {onPriceValueChange} = this.props;
    onPriceValueChange(this.state.price);
  }

  private handleQuantityBlur() {
    const {onQuantityValueChange} = this.props;
    onQuantityValueChange(this.state.quantity || 1);
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
				<div className="col-xs-8 col-md-7">
					<div className="row">
						<VoucherQuantity
							voucher={voucher}
							quantity={this.state.quantity}
							onChange={val => this.updateQuantity(val)}
							onBlur={ val => this.handleQuantityBlur()}/>
						<VoucherPrice
							voucher={voucher}
							price={this.state.price}
							onChange={val => this.updateValue(val)}
							onBlur={ val => this.handleValueBlur()}
						/>
					</div>
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

  return voucher.isEditablePrice ?
        <div className="col-xs-14 col-md-14 alignright priceValue">
					<div className = "row">
            
            <span className="col-xs-14 col-md-14">
              <input
                type="text"
                className="text-right"
                name="priceValue"
                value={`$${price}`}
                onChange={e => props.onChange(e.target.value.replace('$', ''))}
                onBlur={e => props.onBlur(e)}
              />
            </span>
        
            <span className="col-xs-10 col-md-10 fee-full fullPrice text-right">
              ${Number(voucher.total).toFixed(2)}
            </span>
            
          </div>
        </div> 
        :
        <div className="col-xs-14 col-md-14 alignright priceValue text-right">
          ${Number(voucher.total).toFixed(2)}
        </div>;

};

const VoucherQuantity = (props): any => {
  const quantity = props.quantity;

  return (
    <div className="col-xs-10 col-md-10">
      <div className="row">
        <span className="col-xs-12 col-md-12 fee-full quantity">Quantity</span>
        <span className="col-xs-12 col-md-12" >
          <input
            type="text"
            className="text-right"
            name="quantityValue"
            value={quantity}
            onChange={e => props.onChange(e.target.value)}
            onBlur={e => props.onBlur(e)}
          />
        </span>
      </div>
    </div>
  );
};

export default VoucherComp;
