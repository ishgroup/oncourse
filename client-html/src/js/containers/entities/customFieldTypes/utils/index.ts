/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export const processCustomFields = (entity: any, customFieldsKey?: string) => {
  if (!customFieldsKey) {
    customFieldsKey = "customFields";
  }

  if (entity && entity[customFieldsKey]) {
    Object.keys(entity[customFieldsKey]).forEach(k => {
      if (!entity[customFieldsKey][k]) {
        delete entity[customFieldsKey][k];
      }
    });
  }
};
