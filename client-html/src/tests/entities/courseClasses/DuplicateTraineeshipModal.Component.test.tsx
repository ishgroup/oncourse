/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { stubFunction } from "../../../js/common/utils/common";
import * as React from "react";
import DuplicateTraineeshipModal from "../../../js/containers/entities/courseClasses/components/duplicate-courseClass/DuplicateTraineeshipModal";
import { defaultComponents } from "../../common/Default.Components";

describe("Virtual rendered DuplicateTraineeshipModal of Class list view", () => {
  defaultComponents({
    entity: "CheckoutPreviousInvoiceList",
    View: props => <DuplicateTraineeshipModal {...props} />,
    record: () => null,
    defaultProps: () => ({
      opened: true,
      selection: [],
      setDialogOpened: stubFunction
    }),
    render: (wrapper, initialValues, shallow) => {
      expect(shallow.find("div.heading").at(0).text().toContain("Duplicate traineeship class"));
    }
  });
});

