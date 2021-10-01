import * as React from "react";
import getTimestamps from "../../../js/common/utils/timestamps/getTimestamps";
import { defaultComponents } from "../../common/Default.Components";
import EntityRelationTypesForm
  from "../../../js/containers/preferences/containers/entity-relation-types/components/EntityRelationTypesForm";

describe("Virtual rendered EntityRelationTypes", () => {
  defaultComponents({
    entity: "EntityRelationTypes",
    View: props => <EntityRelationTypesForm {...props} />,
    record: mockedApi => mockedApi.db.getEntityRelationTypes(),
    defaultProps: ({ initialValues, mockedApi }) => {
      const items = mockedApi.db.getPlainDiscounts().rows.map(({ id, values }) => ({
        value: Number(id),
        label: values[0],
      }));

      items.sort((a, b) => (a.label.toLowerCase() > b.label.toLowerCase() ? 1 : -1));

      return {
        form: "EntityRelationTypesForm",
        data: { types: initialValues },
        entityRelationTypes: initialValues,
        timestamps: getTimestamps(initialValues),
        fetch: false,
        discountsMap: items
      };
    },
    render: (wrapper, initialValues) => {
      initialValues.forEach((type, key) => {
        const warpperId = `#entity-relation-type-${key}`;
        expect(wrapper.find(`${warpperId} div[id="types[${key}].name"] input`).val()).toContain(type.name);
        expect(wrapper.find(`${warpperId} div[id="types[${key}].description"] input`).val()).toContain(type.description);
        expect(wrapper.find(`${warpperId} div[id="types[${key}].fromName"] input`).val()).toContain(type.fromName);
        expect(wrapper.find(`${warpperId} div[id="types[${key}].shoppingCart"] input`).val()).toContain(type.shoppingCart);
        expect(wrapper.find(`${warpperId} div[id="types[${key}].toName"] input`).val()).toContain(type.toName);
      });
    }
  });
});
