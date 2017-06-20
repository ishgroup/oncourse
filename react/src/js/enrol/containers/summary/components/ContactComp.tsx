import * as React from "react";

import {Contact} from "../../../../model/web/Contact";
import EnrolmentComp, {Props as EnrolmentProps} from "./EnrolmentComp";
import ApplicationComp, {Props as ApplicationProps} from "./ApplicationComp";
import MembershipComp, {Props as MembershipProps} from "./MembershipComp";
import ArticleComp, {Props as ArticleProps} from "./ArticleComp";
import {ContactInfo} from "../../../components/ContactInfo";
import {Enrolment} from "../../../../model/checkout/Enrolment";
import {Membership} from "../../../../model/checkout/Membership";
import {Article} from "../../../../model/checkout/Article";
import {Voucher} from "../../../../model/checkout/Voucher";
import VoucherComp, {Props as VoucherProps} from "./VoucherComp";
import {Application} from "../../../../model/checkout/Application";


export interface Props {
  contact: Contact;
  enrolments: EnrolmentProps[];
  applications: ApplicationProps[];
  vouchers: VoucherProps[];
  memberships: MembershipProps[];
  articles: ArticleProps[];
  onSelect?: (item: Enrolment | Membership | Article | Voucher, selected: boolean) => void;
  onPriceValueChange?: (item: any, product: VoucherProps[], productItem: Voucher) => void;
}

class ContactComp extends React.Component<Props, any> {
  render() {
    const {contact, enrolments, applications, vouchers, memberships, articles, onSelect, onPriceValueChange} = this.props;
    return (
      <div className="row">
        <ContactInfo contact={contact} controls={<AddConcessionLink/>}/>
        <div className="col-xs-24 checkoutList">
          {enrolments.map((props, index) => {
            return <EnrolmentComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Enrolment(), props.enrolment), !props.enrolment.selected)}/>;
          })}

          {applications.map((props, index) => {
            return <ApplicationComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Application(), props.application), !props.application.selected)}/>;
          })}

          {vouchers.map((props, index) => {
            return <VoucherComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Voucher(), props.voucher), !props.voucher.selected) }
              onPriceValueChange={(item) => onPriceValueChange(item, vouchers, props.voucher)}/>;
          })}

          {memberships.map((props, index) => {
            return <MembershipComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Membership(), props.membership), !props.membership.selected) }/>;
          })}

          {articles.map((props, index) => {
            return <ArticleComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Article(), props.article), !props.article.selected) }/>;
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
