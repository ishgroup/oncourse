import { combineEpics } from "redux-observable";
import { EpicConcessionTypes } from "../containers/concession-types/epics";
import { EpicContactRelationTypes } from "../containers/contact-relation-types/epics";
import { EpicCustomFields } from "../containers/custom-fields/epics";
import { EpicDataCollectionForms } from "../containers/data-collection-forms/epics";
import { EpicDataCollectionRules } from "../containers/data-collection-rules/epics";
import { EpicEntityRelationTypes } from "../containers/entity-relation-types/epics";
import { EpicFundingContracts } from "../containers/funding-contracts/epics";
import { EpicGradings } from "../containers/grading/epics";
import { EpicHolidays } from "../containers/holidays/epics";
import { EpicLdap } from "../containers/ldap/epics";
import { EpicMessaging } from "../containers/messaging/epics";
import { EpicPaymentTypes } from "../containers/payment-types/epics";
import { EpicTaxTypes } from "../containers/tax-types/epics";
import { EpicTutorRoles } from "../containers/tutor-roles/epics";
import { EpicCreateSpecialTagType } from "./EpicCreateSpecialTagType";
import { EpicGetColumnsWidth } from "./EpicGetColumnsWidth";
import { EpicGetComplexPath } from "./EpicGetComplexPath";
import { EpicGetCountries } from "./EpicGetCountries";
import { EpicGetCurrency } from "./EpicGetCurrency";
import { EpicGetEnum } from "./EpicGetEnum";
import { EpicGetIsLogged } from "./EpicGetIsLogged";
import { EpicGetLanguages } from "./EpicGetLanguages";
import { EpicGetLockedDate } from "./EpicGetLockedDate";
import { EpicGetPreferences } from "./EpicGetPreferences";
import { EpicGetPreferencesByKeys } from "./EpicGetPreferencesByKeys";
import { EpicGetSpecialTagTypes } from "./EpicGetSpecialTagTypes";
import { EpicGetTimezones } from "./EpicGetTimezones";
import { EpicGetUSISortwareId } from "./EpicGetUSISortwareId";
import { EpicSavePreferences } from "./EpicSavePreferences";
import { EpicUpdateColumnsWidth } from "./EpicUpdateColumnsWidth";

export const EpicPreferences = combineEpics(
  EpicDataCollectionRules,
  EpicDataCollectionForms,
  EpicGradings,
  EpicGetPreferences,
  EpicSavePreferences,
  EpicGetTimezones,
  EpicGetEnum,
  EpicHolidays,
  EpicGetComplexPath,
  EpicGetIsLogged,
  EpicLdap,
  EpicTaxTypes,
  EpicPaymentTypes,
  EpicMessaging,
  EpicConcessionTypes,
  EpicContactRelationTypes,
  EpicEntityRelationTypes,
  EpicCustomFields,
  EpicGetCountries,
  EpicGetLanguages,
  EpicGetColumnsWidth,
  EpicUpdateColumnsWidth,
  EpicGetCurrency,
  EpicGetUSISortwareId,
  EpicGetPreferencesByKeys,
  EpicGetSpecialTagTypes,
  EpicFundingContracts,
  EpicGetLockedDate,
  EpicTutorRoles,
  EpicCreateSpecialTagType
);
