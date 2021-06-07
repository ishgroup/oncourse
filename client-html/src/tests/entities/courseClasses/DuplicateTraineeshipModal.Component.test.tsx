/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { createMount } from "@material-ui/core/test-utils";
import * as React from "react";
import { TestEntry } from "../../TestEntry";
import DuplicateTraineeshipModal from "../../../js/containers/entities/courseClasses/components/duplicate-courseClass/DuplicateTraineeshipModal";
import { stubFunction } from "../../../js/common/utils/common";

describe("Virtual rendered DuplicateTraineeshipModal of Class list view", () => {
  let mount;

  beforeAll(() => {
    mount = createMount();
  });

  afterAll(() => {
    mount.cleanUp();
  });

  const defaultProps = {
    opened: true,
    selection: [],
    setDialogOpened: stubFunction
  };

  const MockedEditView = pr => <DuplicateTraineeshipModal {...{ ...pr, ...defaultProps }} />;

  it(`DuplicateTraineeshipModal should render with default store`, () => {
    const wrapper = mount(
      <TestEntry>
        <MockedEditView />
      </TestEntry>
    );

    expect(wrapper.find("div.heading").at(0).text()).toContain("Duplicate traineeship class");
  });
});
