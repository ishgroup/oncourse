import * as React from 'react';
import * as ReactDOM from 'react-dom';

import { Provider } from 'react-redux';
import { Messages, Progress } from '../../../js/enrol/containers/Functions';

import '../../../scss/index.scss';

import { Contact, Membership, Product } from '../../../js/model';
import { NoCourseClassPlaces } from '../../../js/enrol/containers/summary/Messages';
import {
  default as MembershipComp,
  Props as MembershipProps
} from '../../../js/enrol/containers/summary/components/MembershipComp';

import { CheckoutApiMock } from '../../mocks/CheckoutApiMock';
import { MockConfig } from '../../mocks/mocks/MockConfig';
import { MockDB } from '../../mocks/mocks/MockDB';

let config: MockConfig = new MockConfig();
const checkoutApi:CheckoutApiMock = new CheckoutApiMock(config);
config = checkoutApi.config;
const { db } = config;

config.init((config:MockConfig) => {
  render(config);
});

const classes = [db.getCourseClassByIndex(0)];
const contacts:Contact[] = [db.getContactByIndex(0)];

const membership:Membership[] = checkoutApi.createMembershipsBy(contacts, classes);

membership[0].errors = [NoCourseClassPlaces];

const createMembershipProps = (e: Membership): MembershipProps => ({
  contact: db.getContactById(e.contactId),
  product: db.getProductClassById(e.productId),
  membership: e
});

const createContactProps = (contact: Contact): MembershipProps => ({
  contact,
  membership: membership.filter((e) => e.contactId == contact.id)[0],
  product: membership.filter((e) => e.contactId == contact.id).map((e: Membership) => db.getProductClassById(e.productId))[0],
});

export const menbershipPropses: MembershipProps[] = contacts.map(createContactProps);

const onSelect = (item, contact) => {

};

const render = (config) => ReactDOM.render(
  <Provider store={config.store}>
    <div id="checkout" className="col-xs-24 payments">
      <Progress />
      <Messages />
      {menbershipPropses.map((props, index) => <MembershipComp key={index} contact={props.contact} membership={props.membership} product={props.product} onChange={onSelect} />)}
    </div>

  </Provider>,
  document.getElementById('root')
);
