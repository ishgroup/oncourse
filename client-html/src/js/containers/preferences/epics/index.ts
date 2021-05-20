import { combineEpics } from "redux-observable";
import { EpicGetPreferences } from "./EpicGetPreferences";
import { EpicSavePreferences } from "./EpicSavePreferences";
import { EpicGetTimezones } from "./EpicGetTimezones";
import { EpicGetEnum } from "./EpicGetEnum";
import { EpicHolidays } from "../containers/holidays/epics";
import { EpicDataCollectionForms } from "../containers/data-collection-forms/epics";
import { EpicDataCollectionRules } from "../containers/data-collection-rules/epics";
import { EpicLdap } from "../containers/ldap/epics";
import { EpicPaymentTypes } from "../containers/payment-types/epics";
import { EpicTaxTypes } from "../containers/tax-types/epics";
import { EpicMessaging } from "../containers/messaging/epics";
import { EpicConcessionTypes } from "../containers/concession-types/epics";
import { EpicContactRelationTypes } from "../containers/contact-relation-types/epics";
import { EpicEntityRelationTypes } from "../containers/entity-relation-types/epics";
import { EpicCustomFields } from "../containers/custom-fields/epics";
import { EpicGetComplexPath } from "./EpicGetComplexPath";
import { EpicGetIsLogged } from "./EpicGetIsLogged";
import { EpicGetCountries } from "./EpicGetCountries";
import { EpicGetColumnsWidth } from "./EpicGetColumnsWidth";
import { EpicUpdateColumnsWidth } from "./EpicUpdateColumnsWidth";
import { EpicGetCurrency } from "./EpicGetCurrency";
import { EpicGetPreferencesByKeys } from "./EpicGetPreferencesByKeys";
import { EpicGetLockedDate } from "./EpicGetLockedDate";
import { EpicFundingContracts } from "../containers/funding-contracts/epics";
import { EpicTutorRoles } from "../containers/tutor-roles/epics";
import { EpicGetLanguages } from "./EpicGetLanguages";
import { EpicGetUSISortwareId } from "./EpicGetUSISortwareId";
import { EpicGradings } from "../containers/grading/epics";

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
  EpicFundingContracts,
  EpicGetLockedDate,
  EpicTutorRoles
);
