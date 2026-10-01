import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import TestStore from "../../../js/constants/Store";
import { mockedEditView } from "../../common/MockedEditView.Components";
import CourseEditView from "../../../js/containers/entities/courses/components/CourseEditView";

const AVAILABLE_TAG_ID = 11;
const SUBJECT_TAG_ID = 22;
const UNAVAILABLE_TAG_ID = 987654;

const entityTags = [{
  id: 1,
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
}, {
  id: 2,
  name: "Subjects",
  system: true,
  color: "f5bc76",
  requirements: [],
  childrenCount: 1,
  childTags: [{
    id: SUBJECT_TAG_ID,
    name: "Maths",
    system: false,
    color: "21c2b7",
    requirements: [],
    childrenCount: 0,
    childTags: []
  }]
}];

const formTags = () => TestStore.getState().form[LIST_EDIT_VIEW_FORM_NAME].values?.tags;

describe("CourseEditView with special types displayed", () => {
  mockedEditView({
    entity: "Course",
    EditView: CourseEditView,
    record: mockedApi => ({ ...mockedApi.db.getCourse(1), tags: [AVAILABLE_TAG_ID, SUBJECT_TAG_ID, UNAVAILABLE_TAG_ID] }),
    state: () => ({
      userPreferences: { "ish.display.extendedSearchTypes": "true" },
      tags: { entityTags: { Course: entityTags }, entitySpecialTags: {} }
    }),
    render: async ({ screen }) => {
      // subjects are rendered in their own field, the tag field can not resolve them
      expect(screen.getAllByText("Maths")).toHaveLength(1);
      expect(screen.getByText("Available child")).toBeInTheDocument();

      // neither the subject nor the tag of a removed group shows as a "Tag not found!" chip
      expect(screen.queryByText(/tag not found/i)).toBeNull();

      // the form value is untouched, the tag of the removed group goes away with the save
      expect(formTags()).toEqual([AVAILABLE_TAG_ID, SUBJECT_TAG_ID, UNAVAILABLE_TAG_ID]);
    }
  });
});
