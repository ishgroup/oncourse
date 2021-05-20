import * as React from "react";
import getTimestamps from "../../../js/common/utils/timestamps/getTimestamps";
import ContactRelationTypesForm
  from "../../../js/containers/preferences/containers/contact-relation-types/components/ContactRelationTypesForm";
import { defaultComponents } from "../../common/Default.Components";

describe("Virtual rendered ContactRelationTypes", () => {
  defaultComponents({
    entity: "ContactRelationType",
    View: props => <ContactRelationTypesForm {...props} />,
    record: mockedApi => mockedApi.db.contactRelationTypes,
    defaultProps: ({ initialValues }) => ({
      form: "ContactRelationTypesForm",
      data: { types: initialValues },
      contactRelationTypes: initialValues,
      timestamps: getTimestamps(initialValues),
      fetch: false
    }),
    render: (wrapper, initialValues) => {
      initialValues.forEach((type, key) => {
        const warpperId = `#contact-relation-type-${key}`;
        expect(wrapper.find(`${warpperId} div[id="types[${key}].relationName"]`).text()).toContain(type.relationName);
        expect(wrapper.find(`${warpperId} div[id="types[${key}].reverseRelationName"]`).text()).toContain(type.reverseRelationName);
        expect(wrapper.find(`${warpperId} input[type="checkbox"]`).at(0).props().checked).toEqual(type.portalAccess);
      });
    }
  });
});
