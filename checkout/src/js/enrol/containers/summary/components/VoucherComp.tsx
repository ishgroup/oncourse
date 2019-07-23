import * as React from "react";
import classnames from "classnames";

import { Contact, Voucher, Product } from "../../../../model";
import { ItemWrapper } from "./ItemWrapper";


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
    super(props);

    this.state = {
      price: props.voucher.price || 0,
      quantity: props.voucher.quantity || 1,
    };
  }

  componentWillReceiveProps(props) {
    this.setState({
      price: props.voucher.price,
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
    const { onPriceValueChange } = this.props;
    onPriceValueChange(this.state.price);
  }

  private handleQuantityBlur() {
    const { onQuantityValueChange } = this.props;
    onQuantityValueChange(this.state.quantity || 1);
  }

  public render(): JSX.Element {
    const { voucher, product, contact, onChange } = this.props;
    const divClass = classnames("row", "enrolmentItem", { disabled: !voucher.selected });
    const warning = voucher.warnings && voucher.warnings.length ? this.props.voucher.warnings[0] : null;
    const error = voucher.warnings && voucher.errors.length ? this.props.voucher.errors[0] : null;
    const name = `voucher-${contact.id}-${voucher.productId}`;

    return (
      <div className={divClass}>
        <ItemWrapper title={product.name}
          name={name}
          error={error}
          warning={warning}
          selected={voucher.selected}
          item={voucher}
          contact={contact}
          onChange={onChange}
          quantity={this.state.quantity}
          onQuantityChange={val => this.updateQuantity(val)}
          onQuantityBlur={val => this.handleQuantityBlur()}

        >
          <VoucherDetails voucher={voucher} />
        </ItemWrapper>
        {voucher.selected &&
          <div className="col-xs-8 col-md-8 alignright">
            <VoucherPrice
              voucher={voucher}
              price={this.state.price}
              onChange={val => this.updateValue(val)}
              onBlur={val => this.handleValueBlur()}
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

  return voucher.isEditablePrice ?
    <div className="row" style={{ display: "flex", alignItems: "center" }}>

      <span className="col-xs-12 col-md-12">
        <input
          type="text"
          name="priceValue"
          value={`$${price}`}
          onChange={e => props.onChange(e.target.value.replace('$', ''))}
          onBlur={e => props.onBlur(e)}
          style={{ margin: 0 }}
        />
      </span>

      <span className="col-xs-12 col-md-12 fee-full fullPrice text-right">
        ${Number(voucher.total).toFixed(2)}
      </span>

    </div>
    :
    <div className="row">
      <div className="col-xs-24 col-md-24 fullPrice text-right">
        ${Number(voucher.total).toFixed(2)}
      </div>
    </div>;

};


export default VoucherComp;
