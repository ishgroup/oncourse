import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import TestStore from "../../../js/constants/Store";
import { mockedEditView } from "../../common/MockedEditView.Components";
import CourseEditView from "../../../js/containers/entities/courses/components/CourseEditView";

const AVAILABLE_TAG_ID = 11;
const UNAVAILABLE_TAG_ID = 987654;

const entityTags = [{
  id: 101,
  name: "Available group",
  system: false,
  color: "f5bc76",
  requirements: [],
  childrenCount: 1,
  childTags: [{
    id: AVAILABLE_TAG_ID,
    name: "Available child",
    system: false,
    color: "21c2b7",
    requirements: [],
    childrenCount: 0,
    childTags: []
  }]
}];

const formTags = () => TestStore.getState().form[LIST_EDIT_VIEW_FORM_NAME].values?.tags;

describe("CourseEditView with a tag which is not available for the entity", () => {
  mockedEditView({
    entity: "Course",
    EditView: CourseEditView,
    record: mockedApi => ({ ...mockedApi.db.getCourse(1), tags: [AVAILABLE_TAG_ID, UNAVAILABLE_TAG_ID] }),
    state: () => ({ tags: { entityTags: { Course: entityTags }, entitySpecialTags: {} } }),
    render: async ({ fireEvent, screen, mockedApi }) => {
      // a tag which is not in the tag menu is not rendered as a "Tag not found!" chip
      expect(screen.queryByText(/tag not found/i)).toBeNull();
      expect(screen.getByText("Available child")).toBeInTheDocument();

      // the form value keeps the tag, it is removed from the record when it is saved
      expect(formTags()).toEqual([AVAILABLE_TAG_ID, UNAVAILABLE_TAG_ID]);

      // completed checklist tasks are kept in the same field, they must not be filtered away
      const task = mockedApi.db.getTags()[0].childTags[0];
      const taskLabel = (await screen.findAllByText(task.name)).pop();
      fireEvent.click(taskLabel);
      await new Promise(resolve => setTimeout(resolve, 700));

      expect(formTags()).toEqual([AVAILABLE_TAG_ID, UNAVAILABLE_TAG_ID, task.id]);

      // a task has no entry in the tag menu, so it gets no chip - the checklist card owns its state.
      // Only the available tag is rendered, so the Tags field has a single deletable chip
      expect(document.querySelectorAll(".MuiChip-deleteIcon")).toHaveLength(1);
      expect(screen.queryByText(/tag not found/i)).toBeNull();

      // editing the tag list reports back the displayed ids only, the task which the tag input can
      // not render has to survive it - otherwise checking it off is undone by the next tag change
      fireEvent.click(document.querySelector(".MuiChip-deleteIcon"));

      // ...and the value the input renders must not pick up an id it can not resolve either, the
      // removed tag is not the only one which stays in the form value
      expect(screen.queryByText(/tag not found/i)).toBeNull();

      await new Promise(resolve => setTimeout(resolve, 700));

      expect(screen.queryByText(/tag not found/i)).toBeNull();
      expect(formTags()).toEqual([UNAVAILABLE_TAG_ID, task.id]);
    }
  });
});
