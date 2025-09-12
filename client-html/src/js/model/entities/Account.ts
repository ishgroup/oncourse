/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Account } from "@api/model";

export interface AccountExtended extends Account {
  taxId?: string;
}

export interface AccountTypes {
  income: Account[];
  cos: Account[];
  all: Account[]
}
