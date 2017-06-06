import * as React from "react";
import {Contact} from "../../../../model/web/Contact";
import {ProductClass} from "../../../../model/web/ProductClass";
import ProductListComp from "./ProductListComp";
import {Membership} from "../../../../model/checkout/Membership";


export interface Props {
  contact: Contact;
  membership: Membership;
  productClass: ProductClass;
  onChange?: (item, contact) => void;
}

class MembershipComp extends React.Component<Props, any> {
  public render(): JSX.Element {
    const {membership, productClass, contact, onChange} = this.props;

    return (
      <div>
        <ProductListComp type={Membership} contact={contact} productItem={membership} productClass={productClass} onChange={onChange} />
      </div>
    );
  }
}

export default MembershipComp;
