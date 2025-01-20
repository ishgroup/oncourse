/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ManualType } from "../../model/common/Manual";

export const getManualLink = (docFor: ManualType) => `https://ishoncourse.readme.io/docs/${docFor}`;