import { promiseResolve } from "../../MockAdapter";

export function ContactApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/contact/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getContact(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/contact/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/contact").reply(config => {
    this.db.createContact(config.data);
    return promiseResolve(config, this.db.getContacts());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/contact/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeContact(id);
    return promiseResolve(config, this.db.getContacts());
  });

  this.api.onGet("v1/list/entity/contact/merge").reply(config => promiseResolve(config, this.db.getMergeContacts()));
}
