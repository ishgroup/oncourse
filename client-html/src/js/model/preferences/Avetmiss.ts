import { AvetmissExportSettings } from "@api/model";
import { PreferenceSchema } from "./PreferencesSchema";

export const Address1: PreferenceSchema = {
  uniqueKey: "avetmiss.address.line1",
  mandatory: false,
  editable: true
};

export const Address2: PreferenceSchema = {
  uniqueKey: "avetmiss.address.line2",
  mandatory: false,
  editable: true
};

export const AvetmissCollegeName: PreferenceSchema = {
  uniqueKey: "avetmiss.collegename",
  mandatory: false,
  editable: true
};

export const CertSignatoryName: PreferenceSchema = {
  uniqueKey: "avetmiss.certificate.signatory.name",
  mandatory: false,
  editable: true
};

export const CollegeShortName: PreferenceSchema = {
  uniqueKey: "avetmiss.collegeshortname",
  mandatory: false,
  editable: true
};

export const ContactName: PreferenceSchema = {
  uniqueKey: "avetmiss.contactname",
  mandatory: false,
  editable: true
};

export const Email: PreferenceSchema = {
  uniqueKey: "avetmiss.email",
  mandatory: false,
  editable: true
};

export const Fax: PreferenceSchema = {
  uniqueKey: "avetmiss.fax",
  mandatory: false,
  editable: true
};

export const FeeHelpProviderCode: PreferenceSchema = {
  uniqueKey: "avetmiss.fee.help.provider.code",
  mandatory: false,
  editable: true
};

export const Id: PreferenceSchema = {
  uniqueKey: "avetmiss.identifier",
  mandatory: false,
  editable: true
};

export const Jurisdiction: PreferenceSchema = {
  uniqueKey: "avetmiss.jurisdiction",
  mandatory: false,
  editable: true
};

export const Phone: PreferenceSchema = {
  uniqueKey: "avetmiss.phone",
  mandatory: false,
  editable: true
};

export const Postcode: PreferenceSchema = {
  uniqueKey: "avetmiss.address.postcode",
  mandatory: false,
  editable: true
};

export const QldIdentifier: PreferenceSchema = {
  uniqueKey: "avetmiss.qld.identifier",
  mandatory: false,
  editable: true
};

export const ShowGUI: PreferenceSchema = {
  uniqueKey: "enableRTOGUI",
  mandatory: false,
  editable: true
};

export const State: PreferenceSchema = {
  uniqueKey: "avetmiss.address.state",
  mandatory: false,
  editable: true
};

export const StateName: PreferenceSchema = {
  uniqueKey: "avetmiss.address.stateName",
  mandatory: false,
  editable: true
};

export const Suburb: PreferenceSchema = {
  uniqueKey: "avetmiss.address.suburb",
  mandatory: false,
  editable: true
};

export const Type: PreferenceSchema = {
  uniqueKey: "avetmiss.type",
  mandatory: false,
  editable: true
};

export const showOfferedQM: PreferenceSchema = {
  uniqueKey: "use.offered.qualifications.only",
  mandatory: false,
  editable: true
};

export interface AvetmissExportSettingsReqired extends AvetmissExportSettings {
  noAssessment: AvetmissExportSettings["noAssessment"];
  flavour: AvetmissExportSettings["flavour"];
}
