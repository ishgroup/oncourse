import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function EntityRelationTypesApiMock(mock) {
  /**
   * Entity Relation Type items
   * */
  this.api.onGet("v1/preference/entity/relation/type").reply(config => promiseResolve(config, this.db.getEntityRelationTypes()));

  this.api.onPost("v1/preference/entity/relation/type").reply(config => {
    return promiseResolve(config, this.db.getEntityRelationTypes());
  });

  this.api.onDelete(new RegExp(`v1/preference/entity/relation/type/\\-?\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeEntityRelationType(id);
    return promiseResolve(config, this.db.getEntityRelationTypes());
  });
}
