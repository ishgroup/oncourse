import * as React from "react";
import { mount } from "enzyme";
import { mockedAPI, TestEntry } from "../TestEntry";
import AuditsEditView from "../../js/containers/audits/components/AuditsEditView";
import EditView from "../../js/common/components/list-view/components/edit-view/EditView";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../js/common/components/list-view/constants";
import { III_DD_MMM_YYYY_HH_MM } from "../../js/common/utils/dates/format";
import { format } from "date-fns";

// Expected Form values
const initialValues = mockedAPI.db.getAudit(1);

const props = {
  EditViewContent: AuditsEditView,
  rootEntity: "Audit",
  initialValues,
  form: LIST_EDIT_VIEW_FORM_NAME,
  hasSelected: true,
  creatingNew: false
};

const MockedEditView = pr => <EditView {...{ ...pr, ...props }} />;

window.performance.getEntriesByName = jest.fn(() => []);

describe("Virual rendered AuditsEditView", () => {
  it("should render with given values", () => {
    // Setup wrapper and assign props.

    const wrapper = mount(
      <TestEntry>
        <MockedEditView />
      </TestEntry>
    );

    expect(wrapper.find("#created").text()).toContain(format(new Date(initialValues.created), III_DD_MMM_YYYY_HH_MM));
    expect(wrapper.find("#entityIdentifier").text()).toContain(initialValues.entityIdentifier);
    expect(wrapper.find("#entityId").text()).toContain(initialValues.entityId);
    expect(wrapper.find("#action").text()).toContain(initialValues.action);
    expect(wrapper.find("#message").text()).toContain(initialValues.message);
  });
});
