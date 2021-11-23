import * as React from "react";
import { format } from "date-fns";
import { RepeatEnum } from "@api/model";
import { defaultComponents } from "../../common/Default.Components";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import Holidays from "../../../js/containers/preferences/containers/holidays/Holidays";

// TODO Enable test on fix

describe.skip("Virtual rendered HolidaysForm", () => {
  defaultComponents({
    entity: "HolidaysForm",
    View: props => <Holidays {...props} />,
    record: mockedApi => mockedApi.db.holiday,
    defaultProps: ({ initialValues }) => {
      const values = { holidays: initialValues };
      return {
        form: "HolidaysForm",
        initialValues: values,
        values
      };
    },
    render: (wrapper, initialValues, shallow) => {
      initialValues.forEach((value, index) => {
        expect(wrapper.find(`#holidays-item-${index} div[id='holidays[${index}].description'] input`).val())
          .toEqual(value.description);

        expect(shallow.find(`#holidays-item-${index} input[type='checkbox']`).props().checked).toEqual(false);

        expect(wrapper.find(`#holidays-item-${index} div[id='holidays[${index}].startDateTime'] input`).val()).toContain(
          format(new Date(value.startDateTime), III_DD_MMM_YYYY).toString()
        );

        expect(wrapper.find(`#holidays-item-${index} div[id='holidays[${index}].endDateTime'] input`).val()).toContain(
          format(new Date(value.endDateTime), III_DD_MMM_YYYY).toString()
        );

        expect(wrapper.find(`#holidays-item-${index} div[id='holidays[${index}].repeat'] input`).val()).toEqual(value.repeat);

        if (value.repeat !== RepeatEnum.none) {
          expect(wrapper.find(`#holidays-item-${index} div[id='holidays[${index}].repeatEnd'] input`).val())
            .toEqual(value.repeatEnd);
        }
      });
    }
  });
});
