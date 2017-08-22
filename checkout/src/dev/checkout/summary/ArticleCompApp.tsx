import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {Messages, Progress} from "../../../js/enrol/containers/Functions";

import "../../../scss/index.scss";

import {Contact, Article, Product} from "../../../js/model";
import {NoCourseClassPlaces} from "../../../js/enrol/containers/summary/Messages";
import {
  default as ArticleComp,
  Props as ArticleProps,
} from "../../../js/enrol/containers/summary/components/ArticleComp";

import {CheckoutApiMock} from "../../mocks/CheckoutApiMock";
import {MockConfig} from "../../mocks/mocks/MockConfig";
import {MockDB} from "../../mocks/mocks/MockDB";

let config: MockConfig = new MockConfig();
const checkoutApi:CheckoutApiMock = new CheckoutApiMock(config);
config = checkoutApi.config;
const db:MockDB = config.db;

config.init((config:MockConfig) => {
  render(config);
});

const products:Product[]  = [db.getProductClassByIndex(0),db.getProductClassByIndex(1)];
const contacts:Contact[]  = [db.getContactByIndex(0)];

const article:Article[]  = checkoutApi.createArticlesBy(contacts, products);

article[0].errors = [NoCourseClassPlaces];

const createContactProps = (contact: Contact): ArticleProps => {
  return {
    contact: contact,
    article: article.filter((e) => e.contactId == contact.id)[0],
    product: article.filter((e) => e.contactId == contact.id).map((e: Article) => db.getProductClassById(e.productId))[0],
  }
};

export const articlePropses: ArticleProps[] = contacts.map(createContactProps);

const onSelect = (item, contact) => {

};

const render = (config) => ReactDOM.render(
  <Provider store={config.store}>
    <div id="checkout" className="col-xs-24 payments">
      <Progress/>
      <Messages/>
      {articlePropses.map((props, index) => {
        return <ArticleComp key={index} contact={props.contact} article={props.article} product={props.product} onChange={onSelect} />
      })}
    </div>

  </Provider>,
  document.getElementById("root")
);
