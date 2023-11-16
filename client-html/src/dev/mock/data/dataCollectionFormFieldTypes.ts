import { FieldType } from "@api/model";
import { CollectionFormFieldTypesEnum } from "../../../js/model/preferences/data-collection-forms/collectionFormSchema";

const fieldTypes = Object.keys(CollectionFormFieldTypesEnum)
  .filter(val => isNaN(Number(val)))
  .map((val, index) => ({
    uniqueKey: val.replace(/\s/g, ""),
    label: val
  }));

export function mockDataCollectionFormFieldTypes(): FieldType[] {
  this.getFieldTypes = type => {
    switch (type) {
      case "Enrolment":
        return fieldTypes.slice(0, 7);
      case "Application":
        return fieldTypes.slice(7, 14);
      case "WaitingList":
        return fieldTypes.slice(14, 21);
      case "Survey":
        return fieldTypes.slice(21, 28);
      default:
        return fieldTypes;
    }
  };

  return fieldTypes;
}
