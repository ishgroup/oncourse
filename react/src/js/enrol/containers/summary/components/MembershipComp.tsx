import * as React from "react";
import {Contact, Membership, Product} from "../../../../model";
import {ItemWrapper} from "./ItemWrapper";
import classnames from "classnames";

export interface Props {
  contact: Contact;
  membership: Membership;
  product: Product;
  onChange?: (item, contact) => void;
}

class MembershipComp extends React.Component<Props, any> {
  public render(): JSX.Element {
    const {membership, product, contact, onChange} = this.props;
    const divClass = classnames("row", "enrolmentItem", {disabled: !membership.selected});
    const warning = membership.warnings && membership.warnings.length ? this.props.membership.warnings[0] : null;
    const error = membership.warnings && membership.errors.length ? this.props.membership.errors[0] : null;
    const name = `membership-${contact.id}-${membership.productId}`;
    return (
      <div className={divClass}>
        <ItemWrapper title={product.name} name={name} error={error} warning={warning} selected={membership.selected}
                     item={membership} contact={contact}
                     onChange={onChange}>
          <div/>
        </ItemWrapper>
        {membership.selected &&
        <div className="col-xs-8 col-md-7 alignright priceValue">
          <div className="row">
            <span className="col-xs-24 col-md-24 fee-full fullPrice text-right">${membership.price}</span>
          </div>
        </div>
        }
      </div>
    );
  }
}

export default MembershipComp;
