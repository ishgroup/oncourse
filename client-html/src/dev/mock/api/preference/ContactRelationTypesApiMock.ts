import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function ContactRelationTypesApiMock(mock) {
  /**
   * Contact Relation Type items
   * */

  this.returnError = false;

  this.api.onGet("v1/preference/contact/relation/type").reply(config => promiseResolve(config, this.db.contactRelationTypes));
  /**
   * Mock Contact Relation Types save success or error
   * */
  this.api.onPost("v1/preference/contact/relation/type").reply(config => {
    const data = JSON.parse(config.data);
    data.forEach(i => {
      if (!i.id) i.id = Math.random().toString();
    });
    this.db.saveContactRelationTypes(data);
    return promiseResolve(config, this.db.contactRelationTypes);
  });
  /**
   * Mock Contact Relation Type delete
   * */
  this.api.onDelete(new RegExp(`v1/preference/contact/relation/type/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeContactRelationType(id);
    return promiseResolve(config, this.db.contactRelationTypes);
  });
}
