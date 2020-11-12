/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ClassCostRepetitionType, DefinedTutorRole } from "@api/model";

export interface DefinedTutorRoleExtended extends DefinedTutorRole {
  "currentPayrate.oncostRate": string;
  "currentPayrate.rate": string;
  "currentPayrate.type": ClassCostRepetitionType;
}
