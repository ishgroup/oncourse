import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {Messages, Progress} from "../../../js/enrol/containers/Functions";


import "../../../scss/index.scss";

import {mockAmount} from "../../mocks/mocks/MockFunctions";
import {Amount} from "../../../js/model/checkout/Amount";
import {Contact} from "../../../js/model/web/Contact";

import {Enrolment} from "../../../js/model/checkout/Enrolment";
import {NoCourseClassPlaces} from "../../../js/enrol/containers/summary/Messages";

import {Props as EnrolmentProps} from "../../../js/enrol/containers/summary/components/EnrolmentComp";
import {Props as ApplicationProps} from "../../../js/enrol/containers/summary/components/ApplicationComp";
import {Props as ContactProps} from "../../../js/enrol/containers/summary/components/ContactComp";

import {Props as VoucherProps} from "../../../js/enrol/containers/summary/components/VoucherComp";

import faker from "faker";
import {CourseClass} from "../../../js/model/web/CourseClass";
import {CheckoutApiMock} from "../../mocks/CheckoutApiMock";
import {MockConfig} from "../../mocks/mocks/MockConfig";
import {MockDB} from "../../mocks/mocks/MockDB";
import {SummaryComp} from "../../../js/enrol/containers/summary/components/SummaryComp";
import {Product} from "../../../js/model/web/Product";
import {Voucher} from "../../../js/model/checkout/Voucher";
import {Application} from "../../../js/model/checkout/Application";

let config: MockConfig = new MockConfig();
const checkoutApi: CheckoutApiMock = new CheckoutApiMock(config);
config = checkoutApi.config;
const db: MockDB = config.db;

config.init((config: MockConfig) => {
  render(config);
});

const classes: CourseClass[] = [db.getCourseClassByIndex(0), db.getCourseClassByIndex(1), db.getCourseClassByIndex(2), db.getCourseClassByIndex(3)];
const contacts: Contact[] = [db.getContactByIndex(0), db.getContactByIndex(1)];
const enrolments: Enrolment[] = checkoutApi.createEnrolmentsBy(contacts, classes);
const applications: Application[] = checkoutApi.createApplicationBy(contacts, classes);

const products: Product[] = [db.getProductClassByIndex(0), db.getProductClassByIndex(1), db.getProductClassByIndex(2), db.getProductClassByIndex(3)];
const vouchers: Voucher[] = checkoutApi.createVouchersBy(contacts, products);


enrolments[0].errors = [NoCourseClassPlaces];


const createEnrolmentProps = (e: Enrolment): EnrolmentProps => {
  return {
    contact: db.getContactById(e.contactId),
    courseClass: db.getCourseClassById(e.classId),
    enrolment: e,
  };
};

const createApplicationProps = (a: Application): ApplicationProps => {
  return {
    contact: db.getContactById(a.contactId),
    courseClass: db.getCourseClassById(a.classId),
    application: a,
  };
};

const createVoucherProps = (e: Voucher): VoucherProps => {
  return {
    contact: db.getContactById(e.contactId),
    product: db.getProductClassById(e.productId),
    voucher: e,
  };
};

const createContactProps = (contact: Contact): ContactProps => {
  return {
    contact,
    enrolments: enrolments.filter(e => e.contactId === contact.id).map(createEnrolmentProps),
    applications: applications.filter(a => a.contactId === contact.id).map(createApplicationProps),
    vouchers: vouchers.filter(e => e.contactId === contact.id).map(createVoucherProps),
    memberships: [],
    articles: [],
  };
};

export const contactPropses: ContactProps[] = contacts.map(createContactProps);


contactPropses[1].enrolments[0].enrolment.selected = false;
contactPropses[1].enrolments[1].courseClass.start = faker.date.past();

export const amount: Amount = mockAmount();


const onProceedToPayment = () => {
};
const onSelect = (contact, item) => {
};
const onPriceValueChange = (productItem) => {
};
const onAddCode = (code: string): void => {
  alert(code);
};


const render = config => ReactDOM.render(
  <Provider store={config.store}>
    <div id="checkout" className="col-xs-24 payments">
      <Progress/>
      <Messages/>
      <SummaryComp
        contacts={[contactPropses[0], contactPropses[1]]}
        amount={amount}
        promotions={[]}
        onAddCode={onAddCode}
        onProceedToPayment={onProceedToPayment}
        onSelect={onSelect}
        onPriceValueChange={onPriceValueChange}
        hasSelected={true}
        parentExist={true}
      />
    </div>

  </Provider>,
  document.getElementById("root"),
);
