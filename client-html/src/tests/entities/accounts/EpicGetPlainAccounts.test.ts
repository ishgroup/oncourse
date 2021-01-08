import { AccountType } from "@api/model";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_PLAIN_ACCOUNTS_FULFILLED,
  getPlainAccounts
} from "../../../js/containers/entities/accounts/actions";
import { EpicGetPlainAccounts } from "../../../js/containers/entities/accounts/epics/EpicGetPlainAccounts";
import { AccountExtended } from "../../../js/model/entities/Account";

describe("Get plain accounts list epic tests", () => {
  it("EpicGetPlainAccounts should returns correct values", () => DefaultEpic({
    action: getPlainAccounts(),
    epic: EpicGetPlainAccounts,
    processData: mockedApi => {
      const records = mockedApi.db.getPlainAccounts();
      const items: AccountExtended[] = [];
      records.rows.forEach(row => {
        items.push({
          id: Number(row.values[0]),
          description: row.values[1],
          accountCode: row.values[2],
          type: row.values[3] as AccountType,
          taxId: row.values[4]
        });
      });

      items.sort((a, b) => (a.description[0] > b.description[0] ? 1 : -1));

      return [
        {
          type: GET_PLAIN_ACCOUNTS_FULFILLED,
          payload: { items }
        }
      ];
    }
  }));
});
