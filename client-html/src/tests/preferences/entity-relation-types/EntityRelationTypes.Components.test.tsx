import * as React from "react";
import getTimestamps from "../../../js/common/utils/timestamps/getTimestamps";
import { defaultComponents } from "../../common/Default.Components";
import EntityRelationTypesForm, { ENTITY_RELATION_TYPES_FORM }
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
        form: ENTITY_RELATION_TYPES_FORM,
        data: { types: initialValues },
        entityRelationTypes: initialValues,
        timestamps: getTimestamps(initialValues),
        fetch: false,
        discountsMap: items
      };
    },
    render: ({ screen, initialValues, fireEvent }) => {
      const relationTypes = {};

      initialValues.forEach((type, key) => {
        relationTypes[`types[${key}].name`] = type.name;
        relationTypes[`types[${key}].description`] = type.description;
        relationTypes[`types[${key}].fromName`] = type.fromName;
        relationTypes[`types[${key}].shoppingCart`] = type.shoppingCart;
        relationTypes[`types[${key}].toName`] = type.toName;
      });

      expect(screen.getByRole(ENTITY_RELATION_TYPES_FORM)).toHaveFormValues(relationTypes);

      fireEvent.click(screen.getByTestId('appbar-submit-button'));
    }
  });
});
