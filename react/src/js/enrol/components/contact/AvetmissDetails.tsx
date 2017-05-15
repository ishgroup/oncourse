import * as React from "react";
import {Field} from "redux-form";
import {TextField} from "../form/TextField";
import {AutocompleteField} from "../form/AutocompleteField";
import {ComboboxField} from "../form/ComboboxField";

/**
export function AvetmissDetails(props: AtemnissDetailsComponentProps) {
  const {isOptional} = props;

  return (
    <div>
      {getMessage(isOptional)}
      {getForm(props)}
    </div>
  );
}


const englishProficiency = [
  {key: "Please select...", value: "DEFAULT_POPUP_OPTION"},
  {key: "Very Well", value: "VERY_WELL"},
  {key: "Well", value: "WELL"},
  {key: "Not Well", value: "NOT_WELL"},
  {key: "Not at all", value: "NOT_AT_ALL"}
];

const indigenousStatus = [
  {key: "Please select...", value: "DEFAULT_POPUP_OPTION"},
  {key: "Aboriginal", value: "ABORIGINAL"},
  {key: "Torres Strait Islander", value: "TORRES"},
  {key: "Aboriginal and Torres Strait Islander", value: "ABORIGINAL_AND_TORRES"},
  {key: "Neither", value: "NEITHER"}
];

const highestSchoolLevel = [
  {key: "Please select...", value: "DEFAULT_POPUP_OPTION"},
  {key: "Did not go to school", value: "DID_NOT_GO_TO_SCHOOL"},
  {key: "Year 8 or below", value: "COMPLETED_YEAR_8_OR_BELOW"},
  {key: "Year 9", value: "COMPLETED_YEAR_9"},
  {key: "Year 10", value: "COMPLETED_YEAR_10"},
  {key: "Year 11", value: "COMPLETED_YEAR_11"},
  {key: "Year 12", value: "COMPLETED_YEAR_12"}
];

const stillAtSchool = [
  {key: "Please select...", value: "DEFAULT_POPUP_OPTION"},
  {key: "Yes", value: "YES"},
  {key: "No", value: "NO"}
];

const priorEducationCode = [
  {key: "Please select...", value: "DEFAULT_POPUP_OPTION"},
  {key: "Bachelor degree or higher degree level", value: "BACHELOR"},
  {key: "Advanced diploma or associate degree level", value: "ADVANCED_DIPLOMA"},
  {key: "Diploma level", value: "DIPLOMA"},
  {key: "Certificate IV", value: "CERTIFICATE_IV"},
  {key: "Certificate III", value: "CERTIFICATE_III"},
  {key: "Certificate II", value: "CERTIFICATE_II"},
  {key: "Certificate I", value: "CERTIFICATE_I"},
  {key: "Miscellaneous education", value: "MISC"}
];

const labourForceStatus = [
  {key: "Please select...", value: "DEFAULT_POPUP_OPTION"},
  {key: "Full-time employee", value: "FULL_TIME"},
  {key: "Part-time employee", value: "PART_TIME"},
  {key: "Self-employed, not employing others", value: "SELF_EMPLOYED"},
  {key: "Employer", value: "EMPLOYER"},
  {key: "Employed - unpaid in family business", value: "UNPAID_FAMILY_WORKER"},
  {key: "Unemployed - seeking full-time work", value: "UNEMPLOYED_SEEKING_FULL_TIME"},
  {key: "Unemployed - seeking part-time work", value: "UNEMPLOYED_SEEKING_PART_TIME"},
  {key: "Not employed - not seeking employment", value: "UNEMPLOYED_NOT_SEEKING"}
];

const disabilityTypeLabel = `Do you consider yourself to have a disability, impairment or long-term condition? Please <a href="/about/contact" title="Open contact page in a new window" class="popup">contact us</a> ASAP if your disability will affect your course attendance. We may be able to help.`;

const disabilityType = [
  {key: "Please select...", value: "DEFAULT_POPUP_OPTION"},
  {key: "none", value: "NONE"},
  {key: "Hearing/Deaf", value: "HEARING"},
  {key: "Physical", value: "PHYSICAL"},
  {key: "Intellectual", value: "INTELLECTUAL"},
  {key: "Learning", value: "LEARNING"},
  {key: "Mental illness", value: "MENTAL"},
  {key: "Acquired brain impairment", value: "BRAIN_IMPAIRMENT"},
  {key: "Vision", value: "VISION"},
  {key: "Medical condition", value: "MEDICAL_CONDITION"},
  {key: "Other", value: "OTHER"}
];

function getForm(props: AtemnissDetailsComponentProps) {
  return (
    <fieldset id="questionnaire-block">
      <br/>
      {/* Default: Australia *//*}
      <Field
        component={AutocompleteField}
        autocomplete="countryOfBirth"
        name="countryOfBirth"
        label="In which country were you born?"
        type="text"
      />
      <Field
        component={TextField}
        name="languageHome"
        label="What language do you normally speak at home?"
        type="text"
      />
      <ComboboxField
        name="englishProficiency"
        suggestions={englishProficiency}
        label="How well do you speak English?"
      />
      <ComboboxField
        name="indigenousStatus"
        suggestions={indigenousStatus}
        label="Are you of Aboriginal/Torres Strait Islander origin?"
      />
      <ComboboxField
        name="highestSchoolLevel"
        suggestions={highestSchoolLevel}
        label="What is your highest completed school level?"
      />
      <Field
        component={TextField}
        name="yearSchoolCompleted"
        label="In which year did you complete that school level?"
        type="text"
      />
      <ComboboxField
        name="stillAtSchool"
        suggestions={stillAtSchool}
        label="Are you still attending secondary school?"
      />
      <ComboboxField
        name="priorEducationCode"
        suggestions={priorEducationCode}
        label="What is the highest qualification level you have successfully completed?"
      />
      <ComboboxField
        name="labourForceStatus"
        suggestions={labourForceStatus}
        label="Of the following categories, which best describes your current employment status?"
      />
      <ComboboxField
        name="disabilityType"
        suggestions={disabilityType}
        label={disabilityTypeLabel}
      />
    </fieldset>
  );
}

function getMessage(isOptional: boolean) {
  return (
    <div className="message">
      <p>The Federal Government requires all Colleges to ask the following questions to help with
        educational planning. Your personal details will remain confidential and will not be identified in any National
        collection.
        {isOptional && <strong id="questionnaire" className="collapse">Answering these questions is optional</strong>}
      </p>
    </div>
  );
}

interface AtemnissDetailsComponentProps {
  isOptional: boolean;
}*/
