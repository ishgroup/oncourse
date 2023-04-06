import * as React from "react";
import { defaultComponents } from "../../common/Default.Components";
import CollectionRulesForm, { DATA_COLLECTION_RULES_FORM }
  from "../../../js/containers/preferences/containers/data-collection-rules/components/CollectionRulesForm";

describe("Virtual rendered CollectionRules", () => {
  defaultComponents({
    entity: DATA_COLLECTION_RULES_FORM,
    View: props => <CollectionRulesForm {...props} />,
    record: mockedApi => mockedApi.db.dataCollectionRules,
    defaultProps: ({ initialValues, mockedApi }) => {
      const value = initialValues[0];

      return {
        form: DATA_COLLECTION_RULES_FORM,
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
    render: ({ screen, initialValues, fireEvent }) => {
      const rule = initialValues[0];

      expect(screen.getByRole(DATA_COLLECTION_RULES_FORM)).toHaveFormValues({
        name: rule.name,
        enrolmentFormName: rule.enrolmentFormName,
        applicationFormName: rule.applicationFormName,
        payerFormName: rule.payerFormName,
        waitingListFormName: rule.waitingListFormName,
        parentFormName: rule.parentFormName,
        productFormName: rule.productFormName,
        voucherFormName: rule.voucherFormName,
        membershipFormName: rule.membershipFormName,
      });

      fireEvent.click(screen.getByTestId('appbar-submit-button'));
    }
  });
});
