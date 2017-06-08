import * as React from "react";

import {Contact} from "../../../../model/web/Contact";
import EnrolmentComp, {Props as EnrolmentProps} from "./EnrolmentComp";
import ApplicationComp, {Props as ApplicationProps} from "./ApplicationComp";
import ContactInfo from "../../../components/ContactInfo";
import {Enrolment} from "../../../../model/checkout/Enrolment";
import {Membership} from "../../../../model/checkout/Membership";
import {Article} from "../../../../model/checkout/Article";
import {Voucher} from "../../../../model/checkout/Voucher";
import VoucherComp, {Props as VoucherProps} from "./VoucherComp";

export interface Props {
  contact: Contact
  enrolments: EnrolmentProps[]
  applications: ApplicationProps[]
  vouchers: VoucherProps[],
  onSelect?: (item: Enrolment | Membership | Article | Voucher, selected: boolean) => void,
  onPriceValueChange?: (item: any, product: VoucherProps[], productItem: Voucher) => void,
}

class ContactComp extends React.Component<Props, any> {
  render() {
    const {contact, enrolments, applications, vouchers, onSelect, onPriceValueChange} = this.props;
    return (
      <div className="row">
        <ContactInfo contact={contact} controls={<AddConcessionLink/>}/>
        <div className="col-xs-24 checkoutList">
          {enrolments.map((props, index) => {
            return <EnrolmentComp key={index} {...props}
                                  onChange={ () => onSelect(props.enrolment, !props.enrolment.selected)}/>
            })}

            {vouchers.map((props, index) => {
              return <VoucherComp key={index} {...props}
                                  onChange={ () => onSelect(props.voucher, !props.voucher.selected) }
                                  onPriceValueChange={(item) => onPriceValueChange(item, vouchers, props.voucher)} />
            })}
            
            {applications.map((props, index) => {
                return <ApplicationComp key={index} {...props}
                                      onChange={ () => onSelect(props.application, !props.application.selected)}/>
            })}
            
            
        </div>
      </div>
    );
  }
}

const AddConcessionLink = (props) => {
  return (<div><a className="add-concession" href="#">Add Concession</a></div>);
};

export default ContactComp;