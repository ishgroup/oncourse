import * as React from "react";
import { format } from "date-fns";
import { RepeatEnum } from "@api/model";
import { defaultComponents } from "../../common/Default.Components";
import { III_DD_MMM_YYYY_HH_MM } from "ish-ui";
import Holidays from "../../../js/containers/preferences/containers/holidays/Holidays";
import { repeatEndListItems, repeatListItems } from "../../../js/containers/preferences/containers/holidays/ListItems";
import { HOLIDAYS_FORM } from "../../../js/containers/preferences/containers/holidays/components/HolidaysForm";

describe("Virtual rendered HolidaysForm", () => {
  defaultComponents({
    entity: HOLIDAYS_FORM,
    View: props => <Holidays {...props} />,
    record: mockedApi => mockedApi.db.holiday,
    defaultProps: ({ initialValues }) => {
      const values = { holidays: initialValues };
      return {
        form: HOLIDAYS_FORM,
        initialValues: values,
        values
      };
    },
    render: ({ screen, initialValues, fireEvent }) => {
      const holidays = {};

      initialValues.forEach((holiday, key) => {
        holidays[`holidays[${key}].description`] = holiday.description;
        holidays[`holidays[${key}].startDateTime`] = format(new Date(holiday.startDateTime), III_DD_MMM_YYYY_HH_MM).toString();
        holidays[`holidays[${key}].endDateTime`] = format(new Date(holiday.endDateTime), III_DD_MMM_YYYY_HH_MM).toString();
        holidays[`holidays[${key}].repeat`] = repeatListItems.find(r => r.value === holiday.repeat).label;

        if (holiday.repeat !== RepeatEnum.none) {
          holidays[`holidays[${key}].repeatEnd`] = repeatEndListItems.find(r => r.value === holiday.repeatEnd).label;
        }
      });

      expect(screen.getByRole(HOLIDAYS_FORM)).toHaveFormValues(holidays);

      fireEvent.click(screen.getByTestId('appbar-submit-button'));
    }
  });
});
