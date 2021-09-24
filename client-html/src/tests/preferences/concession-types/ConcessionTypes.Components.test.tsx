import * as React from "react";
import getTimestamps from "../../../js/common/utils/timestamps/getTimestamps";
import ConcessionTypesForm
  from "../../../js/containers/preferences/containers/concession-types/components/ConcessionTypesForm";
import { defaultComponents } from "../../common/Default.Components";

describe("Virtual rendered ConcessionTypes", () => {
  defaultComponents({
    entity: "ConcessionTypes",
    View: props => <ConcessionTypesForm {...props} />,
    record: mockedApi => mockedApi.db.concessionTypes,
    defaultProps: ({ initialValues }) => ({
      form: "ConcessionTypesForm",
      data: { types: initialValues },
      concessionTypes: initialValues,
      timestamps: getTimestamps(initialValues),
      fetch: false
    }),
    render: (wrapper, initialValues) => {
      initialValues.forEach((type, key) => {
        expect(wrapper.find(`#concession-type-${key} div[id="types[${key}].name"] input`).val()).toContain(type.name);
      });
    }
  });
});
