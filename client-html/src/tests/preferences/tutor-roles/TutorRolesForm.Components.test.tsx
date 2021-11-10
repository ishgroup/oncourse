import * as React from "react";
import * as _ from "lodash";
import { format } from "date-fns";
import { defaultComponents } from "../../common/Default.Components";
import TutorRolesForm from "../../../js/containers/preferences/containers/tutor-roles/components/TutorRolesForm";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";

// TODO Enable test on fix

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
    render: (wrapper, initialValues, shallow) => {
      // expect(wrapper.find("#description input").val()).toContain(initialValues.description);
      // expect(shallow.find("input[type='checkbox']").props().checked).toEqual(initialValues.active);
      //
      // initialValues.payRates.forEach((payRate, index) => {
      //   expect(wrapper.find(`div[id='payRates[${index}].validFrom'] input`).val()).toContain(
      //     format(new Date(payRate.validFrom), III_DD_MMM_YYYY).toString()
      //   );
      //   expect(wrapper.find(`div[id='payRates[${index}].rate'] input`).val()).toContain(payRate.rate);
      //   expect(wrapper.find(`div[id='payRates[${index}].type'] input`).val()).toContain(payRate.type);
      //   expect(wrapper.find(`div[id='payRates[${index}].oncostRate'] input`).val()).toContain("10");
      // });
    }
  });
});
