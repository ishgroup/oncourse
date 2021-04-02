import * as React from "react";
import * as _ from "lodash";
import { format } from "date-fns";
import { defaultComponents } from "../../common/Default.Components";
import TutorRolesForm from "../../../js/containers/preferences/containers/tutor-roles/components/TutorRolesForm";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import { formatCurrency } from "../../../js/common/utils/numbers/numbersNormalizing";

describe("Virtual rendered TutorRolesForm", () => {
  defaultComponents({
    entity: "TutorRolesForm",
    View: props => <TutorRolesForm {...props} />,
    record: mockedApi => mockedApi.db.getTutorRole(1),
    defaultProps: ({ initialValues }) => {
      const payRates = (
        initialValues.payRates
        && initialValues.payRates.length > 0
        && _.orderBy(initialValues.payRates, ["validFrom"], ["desc"])
      ) || [];

      const values = { ...initialValues, payRates };

      return {
        form: "TutorRolesForm",
        initialValues: values,
        values,
        value: values,
        onSubmit: jest.fn(),
        showConfirm: jest.fn()
      };
    },
    render: (wrapper, initialValues) => {
      expect(wrapper.find("#description").text()).toContain(initialValues.description);
      expect(wrapper.find("input[type='checkbox']").props().checked).toEqual(initialValues.active);

      initialValues.payRates.forEach((payRate, index) => {
        expect(wrapper.find(`div[id='payRates[${index}].validFrom']`).text()).toContain(
          format(new Date(payRate.validFrom), III_DD_MMM_YYYY).toString()
        );
        expect(wrapper.find(`div[id='payRates[${index}].rate']`).text()).toContain(formatCurrency(`${payRate.rate}`, "null"));
        expect(wrapper.find(`div[id='payRates[${index}].type']`).text()).toContain(payRate.type);
        expect(wrapper.find(`div[id='payRates[${index}].oncostRate']`).text()).toContain(`${payRate.oncostRate * 100}%`);
      });
    }
  });
});
