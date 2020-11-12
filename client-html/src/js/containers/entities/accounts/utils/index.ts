import { Account } from "@api/model";

export const accountLabelCondition = (account: Account) => `${account.description} ${account.accountCode}`;
