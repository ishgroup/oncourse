import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {Messages, Progress} from "../../../js/enrol/containers/Functions";

import "../../../scss/index.scss";

import {Contact} from "../../../js/model/web/Contact";
import {NoCourseClassPlaces} from "../../../js/enrol/containers/summary/Messages";
import {Props as ApplicationProps} from "../../../js/enrol/containers/summary/components/ApplicationComp";

import {CourseClass} from "../../../js/model/web/CourseClass";
import {CheckoutApiMock} from "../../mocks/CheckoutApiMock";
import {MockConfig} from "../../mocks/mocks/MockConfig";
import {MockDB} from "../../mocks/mocks/MockDB";
import ApplicationComp from "../../../js/enrol/containers/summary/components/ApplicationComp";
import {Application} from "../../../js/model/checkout/Application";

let config: MockConfig = new MockConfig();
const checkoutApi:CheckoutApiMock = new CheckoutApiMock(config);
config = checkoutApi.config;
const db:MockDB = config.db;

config.init((config:MockConfig) => {
  render(config);
});

const classes:CourseClass[]  = [db.getCourseClassByIndex(0),db.getCourseClassByIndex(1)];
const contacts:Contact[]  = [db.getContactByIndex(0)];

const applications:Application[]  = checkoutApi.createEnrolmentsBy(contacts, classes);

applications[0].errors = [NoCourseClassPlaces];

const createEnrolmentProps = (e: Application): ApplicationProps => {
  return {
    contact: db.getContactById(e.contactId),
    courseClass: db.getCourseClassById(e.classId),
    application: e
  };
};

const createContactProps = (contact: Contact): ApplicationProps => {
  return {
    contact: contact,
    application: applications.filter((e) => e.contactId == contact.id)[0],
    courseClass: applications.filter((e) => e.contactId == contact.id).map((e: Application) => db.getCourseClassById(e.classId))[0],
  }
};

export const applicationPropses: ApplicationProps[] = contacts.map(createContactProps);

const onSelect = (item, contact) => {
};

const render = (config) => ReactDOM.render(
  <Provider store={config.store}>
    <div id="checkout" className="col-xs-24 payments">
      <Progress/>
      <Messages/>
      {applicationPropses.map((props, index) => {
        return <ApplicationComp key={index} contact={props.contact} application={props.application[0].application} courseClass={props.courseClass[0]} onChange={onSelect} />
      })}
    </div>

  </Provider>,
  document.getElementById("root")
);
