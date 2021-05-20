import { DefaultEpic } from "../../common/Default.Epic";
import { deleteCustomField, getCustomFields } from "../../../js/containers/preferences/actions";
import { EpicDeleteCustomField } from "../../../js/containers/preferences/containers/custom-fields/epics/EpicDeleteCustomField";
import { showMessage } from "../../../js/common/actions";

describe("Delete custom field epic tests", () => {
  it("EpicDeleteCustomField should returns correct values", () => DefaultEpic({
    action: deleteCustomField("886543"),
    epic: EpicDeleteCustomField,
    processData: () => [
      showMessage({
        message: "Custom field was successfully deleted",
        success: true
      }),
      getCustomFields()
    ]
  }));
});
