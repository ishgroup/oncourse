/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { prefixer } from "./prefixer";

export const transform = (value = "none") => prefixer("transform", value);
