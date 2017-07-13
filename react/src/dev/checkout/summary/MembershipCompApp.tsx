import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {Messages, Progress} from "../../../js/enrol/containers/Functions";

import "../../../scss/index.scss";

import {Contact} from "../../../js/model/web/Contact";
import {NoCourseClassPlaces} from "../../../js/enrol/containers/summary/Messages";
import {
  default as MembershipComp,
  Props as MembershipProps
} from "../../../js/enrol/containers/summary/components/MembershipComp";

import {CheckoutApiMock} from "../../mocks/CheckoutApiMock";
import {MockConfig} from "../../mocks/mocks/MockConfig";
import {MockDB} from "../../mocks/mocks/MockDB";
import {Membership} from "../../../js/model/checkout/Membership";
import {Product} from "../../../js/model/web/Product";

let config: MockConfig = new MockConfig();
const checkoutApi:CheckoutApiMock = new CheckoutApiMock(config);
config = checkoutApi.config;
const db:MockDB = config.db;

config.init((config:MockConfig) => {
  render(config);
});

const products:Product[]  = [db.getProductClassByIndex(0),db.getProductClassByIndex(1)];
const contacts:Contact[]  = [db.getContactByIndex(0)];

const membership:Membership[]  = checkoutApi.createMembershipsBy(contacts, products);

membership[0].errors = [NoCourseClassPlaces];

const createMembershipProps = (e: Membership): MembershipProps => {
  return {
    contact: db.getContactById(e.contactId),
    product: db.getProductClassById(e.productId),
    membership: e
  };
};

const createContactProps = (contact: Contact): MembershipProps => {
  return {
    contact: contact,
    membership: membership.filter((e) => e.contactId == contact.id)[0],
    product: membership.filter((e) => e.contactId == contact.id).map((e: Membership) => db.getProductClassById(e.productId))[0],
  }
};

export const menbershipPropses: MembershipProps[] = contacts.map(createContactProps);

const onSelect = (item, contact) => {

};

const render = (config) => ReactDOM.render(
  <Provider store={config.store}>
    <div id="checkout" className="col-xs-24 payments">
      <Progress/>
      <Messages/>
      {menbershipPropses.map((props, index) => {
        return <MembershipComp key={index} contact={props.contact} membership={props.membership} product={props.product} onChange={onSelect} />
      })}
    </div>

  </Provider>,
  document.getElementById("root")
);
