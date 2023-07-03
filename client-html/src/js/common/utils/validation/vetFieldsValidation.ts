/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

export const validateCharacter = (value, len, label) =>
  (value && value.length > len ? `${label} is not valid. It must contain ${len} or fewer characters` : undefined);

export const validateVetPurchasingContractIdentifier = value => validateCharacter(value, 12, "NSW Commitment ID");

export const validateDefaultPurchasingContractIdentifier = value => validateCharacter(value, 12, "Default purchasing contract identifier");

export const validateAssociatedCourseIdentifier = value => validateCharacter(
  value,
  10,
  "Associated course identifier"
);

export const validateDETBookingIdentifier = value => validateCharacter(
  value,
  10,
  "DET Booking Identifier"
);

export const validateCourseSiteIdentifier = value => validateCharacter(
  value,
  10,
  "Course site identifier"
);

export const validateSpecificProgramIdentifier = value => validateCharacter(
  value,
  10,
  "Specific program identifier"
);

export const validatePurchasingContractScheduleIdentifier = value => validateCharacter(
  value,
  3,
  "Purchasing contract schedule identifier"
);

export const validateOutcomeIdTrainingOrg = value => validateCharacter(value, 3, "Outcome identifier");

export const validateCricosConfirmation = value => validateCharacter(value, 32, "Confirmation of Enrolment");

export const validateVetFundingSourceState = value => validateCharacter(value, 3, "Funding source state");

export const validateFundingSourse = value => validateCharacter(
  value, 12, "Funding Source State is not valid. It must contain 12 or fewer characters."
);
