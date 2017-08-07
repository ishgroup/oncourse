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
  updateCheckoutModel?: () => void;
}

class VoucherComp extends React.Component<Props, any> {

  private updatePrice = item => {
    const val = item.target.value;
    const reg = (/^[0-9]+\.?[0-9]*$/);

    if (val > 0 && reg.test(val)) {
      this.props.onPriceValueChange(val);
    }

    return false;
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
        {voucher.selected && <VoucherPrice voucher={voucher} onPriceValueChange={item => this.updatePrice(item)}/>}
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
          This voucher have no specified price.
          <dt className="label">Please enter the amount:</dt>
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
                <dd>${voucher.value}</dd>
              </div>)
          }
        </div>)
      }
    </div>
  );
};

const VoucherPrice = (props): any => {
  const voucher = props.voucher;

  return (
    <div className="col-xs-8 col-md-7 alignright priceValue">
      <div className="row">
        { voucher.isEditablePrice ?
          <div className="col-xs-24 col-md-24 fee-full fullPrice text-right">
            <input type="text" name="priceValue" value={voucher.price} onChange={props.onPriceValueChange.bind(this)}/>
          </div>
          : <span className="col-xs-24 col-md-24 fee-full fullPrice text-right">${ voucher.price }</span>
        }
      </div>
    </div>
  );
};

export default VoucherComp;
