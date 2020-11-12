/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export const VARIANTS = {
  permanent: "permanent",
  persistent: "persistent",
  temporary: "temporary"
};

export const getResultId = (i, name) => `${i}-${name.replace(/ /g, "-").toLowerCase()}`;
