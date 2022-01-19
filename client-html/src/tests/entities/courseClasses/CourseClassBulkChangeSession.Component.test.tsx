/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
// import { addDays, format as formatDate } from "date-fns";
// import { DD_MMM_YYYY } from "../../../js/common/utils/dates/format";
import CourseClassBulkChangeSession, {
  initialValues
} from "../../../js/containers/entities/courseClasses/components/timetable/CourseClassBulkChangeSession";
import { defaultComponents } from "../../common/Default.Components";

// TODO Enable test when find solution to test @mui dialogs

describe("Virtual rendered CourseClassBulkChangeSession of Class edit view", () => {
  defaultComponents({
    entity: "CourseClassBulkChangeSession",
    View: props => <div><CourseClassBulkChangeSession {...props} /></div>,
    record: mockedApi => ({ sessions: mockedApi.db.getTimetableSessionList() }),
    defaultProps: ({ mockedApi }) => ({
      opened: true,
      selection: [9500, 9499, 3596],
      onClose: jest.fn(),
      tutors: [],
      sessions: mockedApi.db.getTimetableSessionList(),
      initialValues: {
        ...initialValues,
        payableDuration: 2,
        duration: 3,
        siteTimezone: "Australia/West",
        start: "2021-11-25T17:05:00.000Z",
        moveForward: "2",
      },
      rooms: [],
    }),
    render: (wrapper, initial, shallow) => {
      expect(shallow.find("div.heading").text()).toContain("Bulk change");

      shallow.find("input[type=\"checkbox\"]").at(0).simulate("click");
      expect(shallow.find("#roomId label").text()).toContain("Site and room");

      shallow.find("input[type=\"checkbox\"]").at(1).simulate("click");
      expect(shallow.find("#actualPayableDuration label").text()).toContain("Actual payable duration");
      expect(shallow.find("#actualPayableDuration input").props().value).toContain("2");

      shallow.find("input[type=\"checkbox\"]").at(2).simulate("click");
      expect(shallow.find("#start label").text()).toContain("Start time (Australia/West)");
      expect(shallow.find("#start input").props().value).toContain("01:05");

      shallow.find("input[type=\"checkbox\"]").at(3).simulate("click");
      expect(shallow.find("#duration label").text()).toContain("Duration");
      expect(shallow.find("#duration input").props().value).toContain("3");

      shallow.find("input[type=\"checkbox\"]").at(4).simulate("click");
      expect(shallow.find("#moveForward input").props().value).toContain("2");

      // const laterDate = formatDate(addDays(new Date(initial.sessions[0].start), 2), DD_MMM_YYYY).toString();

      // expect(wrapper.find("#moveForwardInfo").text()).toContain(`Earliest selected session will starts ${laterDate}`);
    }
  });
});
