/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicGetContact } from "./EpicGetContact";
import { EpicGetContactCertificates } from "./EpicGetContactCertificates";
import { EpicGetContactEnrolments } from "./EpicGetContactEnrolments";
import { EpicGetContactOutcomes } from "./EpicGetContactOutcomes";
import { EpicGetContactPriorLearnings } from "./EpicGetContactPriorLearnings";
import { EpicGetContactsConcessionTypes } from "./EpicGetContactsConcessionTypes";
import { EpicGetContactsRelationTypes } from "./EpicGetContactsRelationTypes";
import { EpicGetContactsStoredCC } from "./EpicGetContactsStoredCC";
import { EpicGetContactsTaxTypes } from "./EpicGetContactsTaxTypes";
import { EpicGetContactTags } from "./EpicGetContactTags";
import { EpicGetMergeContacts } from "./EpicGetMergeContacts";
import { EpicPostMergeContacts } from "./EpicPostMergeContacts";
import { EpicVerifyUSI } from "./EpicVerifyUSI";

export const EpicContacts = combineEpics(
  EpicGetMergeContacts,
  EpicPostMergeContacts,
  EpicGetContact,
  EpicGetContactEnrolments,
  EpicGetContactPriorLearnings,
  EpicGetContactOutcomes,
  EpicGetContactCertificates,
  EpicGetContactsRelationTypes,
  EpicGetContactsConcessionTypes,
  EpicGetContactsTaxTypes,
  EpicVerifyUSI,
  EpicGetContactTags,
  EpicGetContactsStoredCC
);