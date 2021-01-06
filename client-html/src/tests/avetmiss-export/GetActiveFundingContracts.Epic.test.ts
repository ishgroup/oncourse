import { mockedAPI } from "../TestEntry";
import {
  GET_ACTIVE_FUNDING_CONTRACTS_REQUEST,
  GET_ACTIVE_FUNDING_CONTRACTS_FULFILLED
} from "../../js/containers/avetmiss-export/actions";
import { EpicGetActiveFundingContracts } from "../../js/containers/avetmiss-export/epics/EpicGetActiveFundingContracts";
import { DefaultEpic } from "../common/Default.Epic";

describe("Get active funding contracts epics tests", () => {
  it("GetActiveFundingContracts should return correct actions", () => DefaultEpic({
    action: {
      type: GET_ACTIVE_FUNDING_CONTRACTS_REQUEST
    },
    epic: EpicGetActiveFundingContracts,
    processData: () => {
      // Expected response
      const response = mockedAPI.db.getAvetmissExportPlainList();

      const contracts: any[] = response.rows.map(({ id, values }) => ({
        id: Number(id),
        name: values[0],
        flavour: values[1]
      }));

      contracts.sort((a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1));

      return [
        {
          type: GET_ACTIVE_FUNDING_CONTRACTS_FULFILLED,
          payload: { contracts }
        }
      ];
    }
  }));
});
