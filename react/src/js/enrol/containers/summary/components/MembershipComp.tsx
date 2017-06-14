import * as React from "react";
import {Contact} from "../../../../model/web/Contact";
import ProductListComp from "./ProductListComp";
import {Membership} from "../../../../model/checkout/Membership";
import {Product} from "../../../../model/web/Product";


export interface Props {
  contact: Contact;
  membership: Membership;
  product: Product;
  onChange?: (item, contact) => void;
}

class MembershipComp extends React.Component<Props, any> {
  public render(): JSX.Element {
    const {membership, product, contact, onChange} = this.props;

    return (
      <div>
        <ProductListComp type={Membership} contact={contact} productItem={membership} product={product} onChange={onChange} />
      </div>
    );
  }
}

export default MembershipComp;
