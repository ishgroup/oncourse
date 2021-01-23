/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

const weights = [1, 4, 3, 7, 5, 8, 6, 9, 10];

export const parseTFN = value => value && value.replace(/\D/g, "");
export const formatTFN = value => value && value.replace(/^(\d{3})(\d{3})(\d{3})$/g, '$1-$2-$3');

export const validateTFN = value => {
  if (value === undefined || value === null || value === "") return undefined;

  const tfn = value.replace(/\D/g, "");

  if (tfn.length === 8) {
    return "Can not validate TFN that are 8 digits long.";
  }

  if (tfn.length !== 9) {
    return "TFN should contain exactly 9 characters!";
  }

  if (tfn.length === 9) {
    let total = 0;

    for (let i = 0; i < 9; i++) {
      total += weights[i] * tfn.charAt(i);
    }

    const remainder = total % 11;

    if (total === 0 || remainder !== 0) {
      return "Invalid TFN";
    }
  }
  return undefined;
};
