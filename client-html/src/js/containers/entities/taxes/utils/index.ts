/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Tax } from "@api/model";

export const getCurrentTax = (taxes: Tax[], id: number): Tax => taxes.find(t => t.id === id);
