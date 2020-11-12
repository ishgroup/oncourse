/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ValidationError } from "@api/model";

export type ApiMethods = "PUT" | "POST" | "GET" | "PATCH" | "DELETE";

export interface ServerResponse {
  data: ValidationError;
  status: number;
}
