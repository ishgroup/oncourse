import * as React from "react";
import getTimestamps from "../../../js/common/utils/timestamps/getTimestamps";
import ContactRelationTypesForm, { CONTACT_RELATION_TYPES_FORM }
  from "../../../js/containers/preferences/containers/contact-relation-types/components/ContactRelationTypesForm";
import { defaultComponents } from "../../common/Default.Components";

describe("Virtual rendered ContactRelationTypes", () => {
  defaultComponents({
    entity: "ContactRelationType",
    View: props => <ContactRelationTypesForm {...props} />,
    record: mockedApi => mockedApi.db.contactRelationTypes,
    defaultProps: ({ initialValues }) => ({
      form: CONTACT_RELATION_TYPES_FORM,
      data: { types: initialValues },
      contactRelationTypes: initialValues,
      timestamps: getTimestamps(initialValues),
      fetch: false
    }),
    render: ({ screen, initialValues, fireEvent }) => {
      const contactRelationTypes = {};

      initialValues.forEach((types, index) => {
        contactRelationTypes[`types[${index}].relationName`] = types.relationName;
        contactRelationTypes[`types[${index}].reverseRelationName`] = types.reverseRelationName;
        contactRelationTypes[`types[${index}].portalAccess`] = types.portalAccess;
      });

      expect(screen.getByRole(CONTACT_RELATION_TYPES_FORM)).toHaveFormValues(contactRelationTypes);

      fireEvent.click(screen.getByTestId('appbar-submit-button'));
    }
  });
});
