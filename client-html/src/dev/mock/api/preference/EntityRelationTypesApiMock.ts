import { promiseResolve } from "../../MockAdapter";

export function EntityRelationTypesApiMock(mock) {
  /**
   * Entity Relation Type items
   * */
  this.api.onGet("v1/preference/entity/relation/type").reply(config => promiseResolve(config, this.db.getEntityRelationTypes()));

  this.api.onPost("v1/preference/entity/relation/type").reply(config => {
    return promiseResolve(config, this.db.getEntityRelationTypes());
  });
}
