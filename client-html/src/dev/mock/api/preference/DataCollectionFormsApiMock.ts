import { ValidationError } from "@api/model";
import { promiseReject, promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function DataCollectionFormsApiMock() {
  this.returnError = false;

  /**
   * Data Collection Forms items
   * */
  this.api.onGet("/v1/datacollection/form").reply(config => promiseResolve(config, this.db.dataCollectionForms));
  /**
   * Data Collection Forms field types
   * */
  this.api.onGet("v1/datacollection/field/type").reply(config => promiseResolve(config, this.db.getFieldTypes(config.params.formType)));
  /**
   * Update Data Collection Form with success or error
   * */
  this.api.onPut(new RegExp(`v1/datacollection/form/.+`)).reply(config => {
    this.returnError = !this.returnError;

    if (this.returnError) {
      const errorObj: ValidationError = {
        id: "test.form.1",
        propertyName: "name",
        errorMessage: "Name is invalid"
      };

      return promiseReject(config, errorObj);
    }

    const id = config.url.split("/")[3];
    const data = JSON.parse(config.data);
    this.db.updateCollectionForm(id, data);
    return promiseResolve(config, JSON.parse(JSON.stringify(this.db.dataCollectionForms)));
  });
  /**
   * Create Data Collection Form
   * */
  this.api.onPost("v1/datacollection/form").reply(config => {
    const data = JSON.parse(config.data);
    this.db.createCollectionForm(data);
    return promiseResolve(config, JSON.parse(JSON.stringify(this.db.dataCollectionForms)));
  });
  /**
   * Delete Data Collection Form
   * */
  this.api.onDelete(new RegExp(`v1/datacollection/form/.+`)).reply(config => {
    const id = getParamsId(config);
    this.db.deleteCollectionForm(id);
    return promiseResolve(config, JSON.parse(JSON.stringify(this.db.dataCollectionForms)));
  });
}
