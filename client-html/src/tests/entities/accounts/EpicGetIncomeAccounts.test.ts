import {
  Account, AccountType, DataResponse, Tax
} from "@api/model";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_INCOME_ACCOUNTS_FULFILLED,
  getIncomeAccounts
} from "../../../js/containers/entities/accounts/actions";
import { EpicGetIncomeAccounts } from "../../../js/containers/entities/accounts/epics/EpicGetEnabledAccounts";

export const accountParseResponse = (records: DataResponse) => {
  const items: Account[] = [];
  for (const row of records.rows) {
    const taxId = row.values[4] ? Number(row.values[4]) : null;
    items.push({
      id: Number(row.values[0]),
      description: row.values[1],
      accountCode: row.values[2],
      type: row.values[3] as AccountType,
      tax: {
        id: taxId
      } as Tax
    });
  }
  items.sort((a, b) => (a.description[0] > b.description[0] ? 1 : -1));
  return items;
};

describe("Get enabled income accounts epic tests", () => {
  it("EpicGetIncomeAccounts should returns correct values", () => DefaultEpic({
    action: getIncomeAccounts(),
    epic: EpicGetIncomeAccounts,
    processData: mockedApi => {
      const records = mockedApi.db.getPlainAccounts();
      return [
        {
          type: GET_INCOME_ACCOUNTS_FULFILLED,
          payload: { incomeItems: accountParseResponse(records) }
        }
      ];
    }
  }));
});
