import { Assessment } from "@api/model";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  defaultAssessmentMap,
  EpicGetAssessments
} from "../../../js/containers/entities/assessments/epics/EpicGetAssessments";
import { GET_ASSESSMENT_ITEMS_FULFILLED, getAssessments } from "../../../js/containers/entities/assessments/actions";
import { getCustomColumnsMap } from "../../../js/common/utils/common";

describe("Get plain assessment list epic tests", () => {
  it("EpicGetPlainAssessments should returns correct values", () => DefaultEpic({
    action: getAssessments(),
    epic: EpicGetAssessments,
    processData: mockedApi => {
      const records = mockedApi.db.getPlainAssessmentList();
      const rows = records.rows;
      const columns = null;
      const offset = 0;
      const pageSize = 20;
      const items: Assessment[] = rows.map(columns ? getCustomColumnsMap(columns) : defaultAssessmentMap);

      return [
        {
          type: GET_ASSESSMENT_ITEMS_FULFILLED,
          payload: { items, offset, pageSize }
        }
      ];
    }
  }));
});
