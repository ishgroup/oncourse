import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import CollectionRulesForm
  from "../../../js/containers/preferences/containers/data-collection-rules/components/CollectionRulesForm";

describe("Virtual rendered CollectionRules", () => {
  defaultComponents({
    entity: "CollectionRulesForm",
    View: props => <CollectionRulesForm {...props} />,
    record: mockedApi => mockedApi.db.dataCollectionRules,
    defaultProps: ({ initialValues, mockedApi }) => {
      const value = initialValues[0];

      return {
        form: "CollectionRulesForm",
        initialValues: value,
        values: value,
        value,
        item: value,
        collectionForms: mockedApi.db.dataCollectionForms,
        collectionRules: initialValues,
        fetch: false,
        match: {
          params: {
            action: "edit",
            id: value.id
          }
        },
        history: {
          listen: jest.fn()
        },
        onSubmit: jest.fn()
      };
    },
    render: (wrapper, initialValues) => {
      const rule = initialValues[0];

      expect(wrapper.find("#enrolmentFormName").text()).toContain(rule.enrolmentFormName);
      expect(wrapper.find("#surveyForms").text()).toContain(rule.surveyForms.join(", "));
      expect(wrapper.find("#applicationFormName").text()).toContain(rule.applicationFormName);
      expect(wrapper.find("#payerFormName").text()).toContain("No value");
      expect(wrapper.find("#waitingListFormName").text()).toContain(rule.waitingListFormName);
      expect(wrapper.find("#parentFormName").text()).toContain("No value");
    }
  });
});
