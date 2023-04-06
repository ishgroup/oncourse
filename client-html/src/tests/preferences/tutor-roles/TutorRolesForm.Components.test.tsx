import * as React from "react";
// import * as _ from "lodash";
import { format } from "date-fns";
import { defaultComponents } from "../../common/Default.Components";
import TutorRolesForm, { TUTOR_ROLES_FORM } from
    "../../../js/containers/preferences/containers/tutor-roles/components/TutorRolesForm";
import { formatCurrency } from "../../../js/common/utils/numbers/numbersNormalizing";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";

describe("Virtual rendered TutorRolesForm", () => {
  defaultComponents({
    entity: TUTOR_ROLES_FORM,
    View: props => <TutorRolesForm {...props} />,
    record: mockedApi => mockedApi.db.getTutorRole(1),
    defaultProps: ({ initialValues }) => {
      const payRates = (
        initialValues.payRates
        && initialValues.payRates.length > 0
        && initialValues.payRates.sort((a, b) => (b.validFrom > a.validFrom ? 1 : -1))
      ) || [];

      const values = { ...initialValues, payRates };

      return {
        form: TUTOR_ROLES_FORM,
        initialValues: values,
        values,
        value: values,
        onSubmit: jest.fn(),
        showConfirm: jest.fn()
      };
    },
    render: ({ screen, initialValues, fireEvent }) => {
      const tutorRoles = {};

      initialValues.payRates.forEach((payRate, key) => {
        tutorRoles[`payRates[${key}].validFrom`] = format(new Date(payRate.validFrom), III_DD_MMM_YYYY).toString();
        tutorRoles[`payRates[${key}].rate`] = formatCurrency(payRate.rate, "");
        tutorRoles[`payRates[${key}].type`] = payRate.type;
        tutorRoles[`payRates[${key}].oncostRate`] = payRate.oncostRate * 100;
        tutorRoles[`payRates[${key}].notes`] = payRate.notes;
      });

      expect(screen.getByRole(TUTOR_ROLES_FORM)).toHaveFormValues({
        description: initialValues.description,
        active: initialValues.active,
        ...tutorRoles
      });

      fireEvent.click(screen.getByTestId('appbar-submit-button'));
    }
  });
});
