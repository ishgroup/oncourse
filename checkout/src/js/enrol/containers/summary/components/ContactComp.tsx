import * as React from "react";

import {
  Enrolment, Membership, Article, Voucher, Application, PurchaseItem, Concession, StudentMembership, WaitingList,
  ConcessionType,
} from "../../../../model";

import EnrolmentComp, {Props as EnrolmentProps} from "./EnrolmentComp";
import ApplicationComp, {Props as ApplicationProps} from "./ApplicationComp";
import MembershipComp, {Props as MembershipProps} from "./MembershipComp";
import ArticleComp, {Props as ArticleProps} from "./ArticleComp";
import {ContactInfo} from "../../../components/ContactInfo";
import VoucherComp, {Props as VoucherProps} from "./VoucherComp";
import WaitingListComp, {Props as WaitingListProps} from "./WaitingListComp";
import {ContactState} from "../../../../services/IshState";


export interface Props {
  contact: ContactState;
  enrolments: EnrolmentProps[];
  applications: ApplicationProps[];
  vouchers: VoucherProps[];
  memberships: MembershipProps[];
  articles: ArticleProps[];
  waitingLists: any;
  onUpdateWaitingCourse?: (waitingCourse, prop) => void;
  onSelect?: (item: PurchaseItem, selected: boolean) => void;
  onPriceValueChange?: (productItem: Voucher, val: number) => void;
  onQuantityValueChange?: (productItem: Voucher|Article, val: number) => void;
  onAddConcession?: () => void;
  concessions?: Concession[];
  concessionTypes?: ConcessionType[];
  studentMemberships?: StudentMembership[];
  onChangeParent?: (contactId) => void;
  onRemoveContact?: (contactId) => void;
  onChangeEnrolmentFields?: (form, type) => any;
}

class ContactComp extends React.Component<Props, any> {
  render() {
    const {
      contact, enrolments, applications, vouchers, memberships, concessions, onChangeEnrolmentFields,
      articles, onSelect, onPriceValueChange, onQuantityValueChange, onAddConcession, studentMemberships, onChangeParent, waitingLists,
      onUpdateWaitingCourse, concessionTypes, onRemoveContact,
    } = this.props;

    return (
      <div className="row">
        <ContactInfo
          contact={contact}
          controls={
            <AddConcessionLink
              onAddConcession={onAddConcession}
              contact={contact}
              concessionTypes={concessionTypes}
            />
          }
          concessions={concessions}
          memberships={studentMemberships}
          onChangeParent={onChangeParent}
          onRemoveContact={onRemoveContact}
        />
        <div className="col-xs-24 checkoutList">

          {enrolments.map((props, index) =>
            props.courseClass && <EnrolmentComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Enrolment(), props.enrolment), !props.enrolment.selected)}
              onChangeFields={onChangeEnrolmentFields}
            />,
          )}

          {applications.map((props, index) =>
            props.courseClass && <ApplicationComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Application(), props.application), !props.application.selected)}
              onChangeFields={onChangeEnrolmentFields}
            />,
          )}

          {vouchers.map((props, index) =>
            props.product && <VoucherComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Voucher(), props.voucher), !props.voucher.selected)}
              onPriceValueChange={val => onPriceValueChange(Object.assign(new Voucher(), props.voucher), val)}
              onQuantityValueChange={val => onQuantityValueChange(Object.assign(new Voucher(), props.voucher), val)}
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
							onQuantityValueChange={val => onQuantityValueChange(Object.assign(new Article(), props.article), val)}
            />,
          )}

          {waitingLists.map((props, index) =>
            props.waitingList && <WaitingListComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new WaitingList(), props.waitingList), !props.waitingList.selected)}
              onUpdate={prop => onUpdateWaitingCourse(Object.assign(new WaitingList(), props.waitingList), prop)}
              onChangeFields={onChangeEnrolmentFields}
            />,
          )}
          
        </div>
      </div>
    );
  }
}

const AddConcessionLink = props => {
  const {onAddConcession, concessionTypes, contact} = props;

  return (
    <div>
      {concessionTypes && !!concessionTypes.filter(c => c.id != -1).length &&
        <a className="add-concession" href="#" onClick={() => onAddConcession(contact.id)}>Add Concession</a>
      }
    </div>
  );
};

export default ContactComp;
