import * as React from "react";

import {
  Enrolment, Membership, Article, Voucher, Application, PurchaseItem, Concession, StudentMembership, WaitingList,
  ConcessionType, CourseClass,
} from "../../../../model";

import EnrolmentComp, {Props as EnrolmentProps} from "./EnrolmentComp";
import ApplicationComp, {Props as ApplicationProps} from "./ApplicationComp";
import MembershipComp, {Props as MembershipProps} from "./MembershipComp";
import ArticleComp, {Props as ArticleProps} from "./ArticleComp";
import {ContactInfo} from "../../../components/ContactInfo";
import VoucherComp, {Props as VoucherProps} from "./VoucherComp";
import WaitingListComp from "./WaitingListComp";
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
  onChangeClass?: (item1: PurchaseItem, item2: PurchaseItem, newClasses: CourseClass[] ) => void;
  onPriceValueChange?: (productItem: Voucher, val: number) => void;
  onQuantityValueChange?: (productItem: Voucher|Article, val: number) => void;
  onAddConcession?: () => void;
  concessions?: Concession[];
  concessionTypes?: ConcessionType[];
  studentMemberships?: StudentMembership[];
  onChangeParent?: (contactId) => void;
  onRemoveContact?: (contactId) => void;
  onChangeEnrolmentFields?: (form, type) => any;
  readonly?: boolean;
  isPayer?: boolean;
  isOnlyApplications?: boolean;
}

class ContactComp extends React.Component<Props, any> {
  render() {
    const {
      contact, enrolments, applications, vouchers, memberships, concessions, onChangeEnrolmentFields,
      articles, onSelect, onPriceValueChange, onQuantityValueChange, onAddConcession, studentMemberships, onChangeParent, waitingLists,
      onUpdateWaitingCourse, concessionTypes, onRemoveContact, readonly, isPayer, onChangeClass,
      isOnlyApplications
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

        {isPayer && readonly && !isOnlyApplications &&
          <div className="col-xs-24">
            <div className="message">
              <strong>Invoice email sent</strong>
              <div>
                Please check your email account or spam folder for your tax invoice
              </div>
            </div>
          </div>
        }
        {readonly && Boolean(applications.length) && applications.some(e => e.application.selected) &&
        <div className="col-xs-24">
          <div className="message">
            <strong>Application confirmation sent</strong>
            <div>
              Please check your email account or spam folder for your application confirmation and for future course updates.
            </div>
          </div>
        </div>
        }
        {readonly && Boolean(enrolments.length) && enrolments.some(e => e.enrolment.selected) &&
        <div className="col-xs-24">
          <div className="message">
            <strong>Enrolment confirmation sent</strong>
            <div>
              Please check your email account or spam folder for your enrolment confirmation and for future course updates.
            </div>
          </div>
        </div>
        }
        <div className="col-xs-24 checkoutList">
          {enrolments.map((props, index) =>
            props.courseClass && (readonly ? props.enrolment.selected : true) && <EnrolmentComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Enrolment(), props.enrolment), !props.enrolment.selected)}
              onChangeClass={(classId, newClasses) => onChangeClass(Object.assign(new Enrolment(), props.enrolment), Object.assign(new Enrolment(), props.enrolment, { classId }), newClasses)}
              onChangeFields={onChangeEnrolmentFields}
              readonly={readonly}
            />,
          )}
          {applications.map((props, index) =>
            props.courseClass && (readonly ? props.application.selected : true) && <ApplicationComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Application(), props.application), !props.application.selected)}
              onChangeFields={onChangeEnrolmentFields}
              readonly={readonly}
            />,
          )}
          {vouchers.map((props, index) =>
            props.product && (readonly ? props.voucher.selected : true) && <VoucherComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Voucher(), props.voucher), !props.voucher.selected)}
              onPriceValueChange={val => onPriceValueChange(Object.assign(new Voucher(), props.voucher), val)}
              onQuantityValueChange={val => onQuantityValueChange(Object.assign(new Voucher(), props.voucher), val)}
              readonly={readonly}
            />,
          )}
          {memberships.map((props, index) =>
            props.product && (readonly ? props.membership.selected : true) && <MembershipComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Membership(), props.membership), !props.membership.selected)}
              readonly={readonly}
            />,
          )}
          {articles.map((props, index) =>
            props.product && (readonly ? props.article.selected : true) && <ArticleComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new Article(), props.article), !props.article.selected)}
							onQuantityValueChange={val => onQuantityValueChange(Object.assign(new Article(), props.article), val)}
              readonly={readonly}
            />,
          )}
          {waitingLists.map((props, index) =>
            props.waitingList && (readonly ? props.waitingList.selected : true) && <WaitingListComp
              key={index} {...props}
              onChange={() => onSelect(Object.assign(new WaitingList(), props.waitingList), !props.waitingList.selected)}
              onUpdate={prop => onUpdateWaitingCourse(Object.assign(new WaitingList(), props.waitingList), prop)}
              onChangeFields={onChangeEnrolmentFields}
              readonly={readonly}
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
