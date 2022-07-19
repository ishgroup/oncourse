import { from, filter, toArray } from "rxjs";
import { transformDataType } from "../../../js/containers/entities/common/utils";
import { store, mockedAPI } from "../../TestEntry";
import { FETCH_START, FETCH_FINISH } from "../../../js/common/actions";
import {
  GET_CUSTOM_FIELD_TYPES,
  GET_CUSTOM_FIELD_TYPES_FULFILLED
} from "../../../js/containers/entities/customFieldTypes/actions";
import { EpicGetCustomFieldTypes } from "../../../js/containers/entities/customFieldTypes/epics/EpicGetCustomFieldTypes";

export const GetCustomFieldTypes = (entity: string) => {
  // Expected response
  const response = mockedAPI.db.getCustomFields(`entityIdentifier=${entity}`);

  // Redux action to trigger epic
  const action$ = from([{ type: GET_CUSTOM_FIELD_TYPES, payload: entity }]);

  // Initializing epic instance
  const epic$ = EpicGetCustomFieldTypes(action$, store, {});

  const types = response.rows.map(item => ({
    id: item.id,
    fieldKey: item.values[0],
    name: item.values[1],
    defaultValue: item.values[2],
    mandatory: item.values[3] === "true",
    dataType: item.values[4] === "URL" ? item.values[4] : transformDataType(item.values[4]),
    sortOrder: Number(item.values[5])
  }));

  // Testing epic to be resolved with array of actions
  return expect(
    epic$
      .pipe(
        // Filtering common actions
        filter(a => ![FETCH_START, FETCH_FINISH].includes(a.type)),
        toArray()
      )
      .toPromise()
  ).resolves.toEqual([
    {
      type: GET_CUSTOM_FIELD_TYPES_FULFILLED,
      payload: { types, entity }
    }
  ]);
};
