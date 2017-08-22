import * as React from "react";

import {
  Enrolment, Membership, Article, Voucher, Application, PurchaseItem, Concession, StudentMembership,
} from "../../../../model";

import EnrolmentComp, {Props as EnrolmentProps} from "./EnrolmentComp";
import ApplicationComp, {Props as ApplicationProps} from "./ApplicationComp";
import MembershipComp, {Props as MembershipProps} from "./MembershipComp";
import ArticleComp, {Props as ArticleProps} from "./ArticleComp";
import {ContactInfo} from "../../../components/ContactInfo";
import VoucherComp, {Props as VoucherProps} from "./VoucherComp";
import {ContactState} from "../../../../services/IshState";


export interface Props {
  contact: ContactState;
  enrolments: EnrolmentProps[];
  applications: ApplicationProps[];
  vouchers: VoucherProps[];
  memberships: MembershipProps[];
  articles: ArticleProps[];
  onSelect?: (item: PurchaseItem, selected: boolean) => void;
  onPriceValueChange?: (productItem: PurchaseItem, val: any) => void;
  onAddConcession?: () => void;
  concessions?: Concession[];
  studentMemberships?: StudentMembership[];
  onChangeParent?: (contactId) => void;
}

class ContactComp extends React.Component<Props, any> {
  render() {
    const {
      contact, enrolments, applications, vouchers, memberships, concessions,
      articles, onSelect, onPriceValueChange, onAddConcession, studentMemberships, onChangeParent,
    } = this.props;

    return (
      <div className="row">
        <ContactInfo
          contact={contact}
          controls={<AddConcessionLink onAddConcession={onAddConcession} contact={contact}/>}
          concessions={concessions}
          memberships={studentMemberships}
          onChangeParent={onChangeParent}
        />
        <div className="col-xs-24 checkoutList">

          {enrolments.map((props, index) =>
            props.courseClass && <EnrolmentComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Enrolment(), props.enrolment), !props.enrolment.selected)}
            />,
          )}

          {applications.map((props, index) =>
            props.courseClass && <ApplicationComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Application(), props.application), !props.application.selected)}
            />,
          )}

          {vouchers.map((props, index) =>
            props.product && <VoucherComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Voucher(), props.voucher), !props.voucher.selected)}
              onPriceValueChange={val => onPriceValueChange(Object.assign(new Voucher(), props.voucher), val)}
            />,
          )}

          {memberships.map((props, index) =>
            props.product && <MembershipComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Membership(), props.membership), !props.membership.selected)}
            />,
          )}

          {articles.map((props, index) =>
            props.product && <ArticleComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Article(), props.article), !props.article.selected)}
            />,
          )}


        </div>
      </div>
    );
  }
}

const AddConcessionLink = props => {
  return (
    <div>
      <a className="add-concession" href="#" onClick={() => props.onAddConcession(props.contact.id)}>Add Concession</a>
    </div>
  );
};

export default ContactComp;
