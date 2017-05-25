import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {CreateStore, RestoreState} from "../../../js/CreateStore";
import {MessagesRedux, ProgressRedux} from "../../../js/enrol/containers/Functions";


import "../../../scss/index.scss";

import Summary from "../../../js/enrol/containers/summary/Summary";
import {mockAmount} from "../../../js/httpStub/mocks/MockFunctions";
import {Amount} from "../../../js/model/checkout/Amount";
import {Contact} from "../../../js/model/web/Contact";

import {Enrolment} from "../../../js/model/checkout/Enrolment";
import {NoCourseClassPlaces} from "../../../js/enrol/containers/summary/Messages";

import {Props as EnrolmentProps} from "../../../js/enrol/containers/summary/components/EnrolmentComp";
import {Props as ContactProps} from "../../../js/enrol/containers/summary/components/ContactComp"

import faker from "faker";
import {CourseClass} from "../../../js/model/web/CourseClass";
import {CheckoutApiMock} from "../../../js/httpStub/CheckoutApiMock";
import {MockConfig} from "../../../js/httpStub/mocks/MockConfig";
import {MockDB} from "../../../js/httpStub/mocks/MockDB";


const store = CreateStore();
RestoreState(store, () => render());

const checkoutApi:CheckoutApiMock = new CheckoutApiMock(null);
const config: MockConfig = checkoutApi.config;
const db:MockDB = config.db;


const classes:CourseClass[]  = [db.getCourseClassByIndex(0),db.getCourseClassByIndex(1),db.getCourseClassByIndex(2), db.getCourseClassByIndex(3)];
const contacts:Contact[]  = [db.getContactByIndex(0),db.getContactByIndex(1)];
const enrolments:Enrolment[]  = checkoutApi.createEnrolmentsBy(contacts, classes);


enrolments[0].errors = [NoCourseClassPlaces];


const createEnrolmentProps = (e: Enrolment):EnrolmentProps => {
  return {
    contact: db.getContactById(e.contactId),
    courseClass: db.getCourseClassById(e.classId),
    onSelect: (e,i) => {},
    selected: e.errors.length == 0,
    enrolment: e
  };
};

const createContactProps = (contact: Contact): ContactProps => {
  return {
    contact: contact,
    enrolments: enrolments.filter((e) => e.contactId == contact.id).map(createEnrolmentProps)
  }
};

export const contactPropses: ContactProps[] = contacts.map(createContactProps);


contactPropses[1].enrolments[0].selected = false;
contactPropses[1].enrolments[1].courseClass.start = faker.date.past();

export const amount: Amount = mockAmount();




const onAddContact = () => {
};
const onProceedToPayment = () => {
};


const render = () => ReactDOM.render(
  <Provider store={store}>
    <div id="checkout" className="col-xs-24 payments">
      <ProgressRedux/>
      <MessagesRedux/>
      <Summary contacts={[
        contactPropses[0],
        contactPropses[1]
      ]} amount={amount} onAddContact={onAddContact} onProceedToPayment={onProceedToPayment}/>
    </div>

  </Provider>,
  document.getElementById("root")
);
