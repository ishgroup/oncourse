/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
  AvetmissStudentDisabilityType,
  AvetmissStudentEnglishProficiency,
  AvetmissStudentIndigenousStatus,
  AvetmissStudentLabourStatus,
  AvetmissStudentPriorEducation,
  AvetmissStudentSchoolLevel,
  ClientIndustryEmploymentType,
  ClientOccupationIdentifierType,
  Contact,
  Country,
  Language,
  UsiStatus,
  UsiVerificationResult
} from '@api/model';
import ExitToApp from '@mui/icons-material/ExitToApp';
import { FormControlLabel, Grid } from '@mui/material';
import Chip from '@mui/material/Chip';
import Link from '@mui/material/Link';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import $t from '@t';
import { mapSelectItems, SettingsAdornment, usePrevious } from 'ish-ui';
import React, { useCallback, useEffect, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { change } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import FormField from '../../../../common/components/form/formFields/FormField';
import ExpandableContainer from '../../../../common/components/layout/expandable/ExpandableContainer';
import { validateNonNegative } from '../../../../common/utils/validation';
import { formatTFN, parseTFN, validateTFN } from '../../../../common/utils/validation/tfnValidation';
import { EditViewProps } from '../../../../model/common/ListView';
import { State } from '../../../../reducers/state';
import { clearUSIVerificationResult, verifyUSI } from '../actions';
import { TFNInputMask } from './ContactsTutor';

const indigenousStatuses = Object.keys(AvetmissStudentIndigenousStatus).map(mapSelectItems);
const englishProficiencies = Object.keys(AvetmissStudentEnglishProficiency).map(mapSelectItems);
const schoolLevels = Object.keys(AvetmissStudentSchoolLevel).map(mapSelectItems);
const priorEducations = Object.keys(AvetmissStudentPriorEducation).map(mapSelectItems);
const disabilityTypes = Object.keys(AvetmissStudentDisabilityType).map(mapSelectItems);
const industriesOfEmployment = Object.keys(ClientIndustryEmploymentType).map(mapSelectItems);
const occupationIdentifiers = Object.keys(ClientOccupationIdentifierType).map(mapSelectItems);
const avetmissStudentLabourStatuses = Object.keys(AvetmissStudentLabourStatus).map(mapSelectItems);

const openUpgradeLink = () => {
  window.open("https://www.ish.com.au/oncourse/signup?securityKey=%s", "_blank");
};

const styles = () => ({
  exitToCreateUSI: {
    fontSize: "1.2rem"
  },
  verificationError: {
    marginBottom: "6px",
    fontSize: "12px"
  }
});

interface ContactsVETProps extends EditViewProps {
  classes?: any;
  twoColumn?: boolean;
  values: Contact;
  countries?: Country[];
  languages?: Language[];
  verifyUSI?: any;
  isVerifyingUSI?: boolean;
  usiVerificationResult?: UsiVerificationResult;
  clearUSIVerificationResult?: () => void;
  setUsiUpdateLocked?: (v: boolean) => void;
  usiLocked?: boolean;
  namePrefix?: string;
}

const parseIntValue = v => (v ? parseInt(v, 10) : v);

const validateYearSchoolCompleted = v => {
  if (!v) return undefined;

  const year = parseInt(v, 10);
  const maxYear = new Date().getFullYear();
  const minYear = 1940;

  if (year > maxYear) {
    return `Year school completed can't be more than ${maxYear}`;
  }

  if (year < minYear) {
    return `Year school completed can't be earlier than ${minYear}`;
  }

  return undefined;
};

const isSpecialUSI = (values: Contact): boolean => {
  if (!values || !values.student || !values.student.usiStatus) return false;
  const status = values.student.usiStatus;

  return status === "Exemption" || status === "International";
};

const stillAtSchoolItems = [
  { value: true, label: "Yes" },
  { value: false, label: "No" },
  { value: "", label: "Not stated" }
];

const ContactsVET: React.FC<ContactsVETProps> = props => {
  const {
    classes,
    namePrefix,
    twoColumn,
    values,
    countries,
    languages,
    form,
    dispatch,
    verifyUSI,
    isVerifyingUSI,
    usiVerificationResult,
    clearUSIVerificationResult,
    usiLocked,
    setUsiUpdateLocked,
    tabIndex,
    expanded,
    setExpanded,
    syncErrors
  } = props;

  const getName = (name: string) => namePrefix ? `${namePrefix}.${name}` : name;

  const prevId = usePrevious(values.id);
  const [showMenuUSI, setMenuUSI] = useState(null);
  const setUSIStatus = (status: UsiStatus) => {
    dispatch(change(form, getName("student.usiStatus"), status));
  };

  const closeUSIMenu = useCallback(() => {
    setMenuUSI(null);
  }, [setMenuUSI]);

  const validateUSI = useCallback(value => {
    if (!value || isSpecialUSI(values)) {
      return undefined;
    }

    if (value.trim().length !== 10) {
      return "The USI code is not valid";
    }

    if (!values.birthDate) {
      return "Please provide birth date to continue verify USI";
    }

    return undefined;
  }, [values?.birthDate, values?.student?.usiStatus]);

  const getUSIStatusMsg = () => {
    if (!values) return "";

    if (isVerifyingUSI) return <Chip label={$t('verifying_usi')} />;

    if (usiVerificationResult && usiVerificationResult.verifyStatus === "Disabled") {
      return <Chip label={$t('upgrade_for_automatic_verification')} color="secondary" onClick={openUpgradeLink} />;
    }

    if (values.student && values.student.usiStatus) {
      const status = values.student.usiStatus;

      if (status === "Not supplied") {
        if (values.student.usi) {
          return <Chip label={$t('not_verified')} className="errorBackgroundColor text-white" />;
        }
      }

      if (status === "Not verified") return <Chip label={$t('verification_failed')} className="errorBackgroundColor text-white" />;
      if (status === "Verified") return <Chip label={$t('verified')} className="successBackgroundColor text-white" />;
      if (status === "Exemption") return <Chip label={$t('student_has_exemption')} className="successBackgroundColor text-white" />;
      if (status === "International") return <Chip label={$t('international_student')} className="successBackgroundColor text-white" />;
    }

    return "";
  };

  const handleUSIChange = e => {
    const prevUsiCode = values.student && values.student.usi;
    const usiCode = e?.target?.value;

    if (typeof prevUsiCode === "string" && prevUsiCode.length === 10) {
      clearUSIVerificationResult();
      setUSIStatus(null);
    }

    if (typeof usiCode === "string" && usiCode.length === 10) {
      const { firstName, lastName, birthDate } = values;
      if (birthDate) {
        verifyUSI(firstName, lastName, birthDate, usiCode);
        setUsiUpdateLocked(true);
      }
    }
  };

  useEffect(() => {
    if (usiVerificationResult && values.id === prevId) {
      const { verifyStatus } = usiVerificationResult;

      switch (verifyStatus) {
        case "Valid": {
          setUSIStatus("Verified");
          break;
        }

        case "Invalid": {
          setUSIStatus("Not verified");
          break;
        }

        case "Service unavailable": {
          setUSIStatus("Not supplied");
          break;
        }

        case "Disabled": {
          setUSIStatus("Not supplied");
          break;
        }

        case "Invalid format": {
          setUSIStatus("Not verified");
          break;
        }

        default: {
          setUSIStatus(null);
        }
      }
    }
  }, [usiVerificationResult, prevId, values.id]);

  useEffect(() => {
    if (prevId !== values.id && usiVerificationResult) {
      clearUSIVerificationResult();
    }
  }, [prevId, usiVerificationResult]);

  return values ? (
    <div className="pl-3 pr-3">
      <ExpandableContainer formErrors={syncErrors} index={tabIndex} expanded={expanded} setExpanded={setExpanded} header="Vet">
        <Grid container columnSpacing={3} rowSpacing={2}>
          {countries && (
            <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
              <FormField
                type="select"
                selectValueMark="id"
                selectLabelMark="name"
                name="student.countryOfBirth"
                label={$t('country_of_birth')}
                returnType="object"
                items={countries}
                allowEmpty
              />
            </Grid>
          )}
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField type="text" name="student.townOfBirth" label={$t('town_of_birth')} />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField type="select" name="student.indigenousStatus" label={$t('indigenous_status')} items={indigenousStatuses} />
          </Grid>
          {languages && (
            <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
              <FormField
                type="select"
                selectValueMark="id"
                selectLabelMark="name"
                name="student.language"
                label={$t('language_spoken_at_home')}
                returnType="object"
                items={languages}
                allowEmpty
              />
            </Grid>
          )}
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="student.englishProficiency"
              label={$t('proficiency_in_spoken_english')}
              items={englishProficiencies}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="student.highestSchoolLevel"
              label={$t('highest_school_level')}
              items={schoolLevels}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="number"
              name="student.yearSchoolCompleted"
              label={$t('achieved_in_year')}
              validate={[validateNonNegative, validateYearSchoolCompleted]}
              parse={parseIntValue}
              debounced={false}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="student.priorEducationCode"
              label={$t('prior_educational_achievement')}
              items={priorEducations}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="student.labourForceStatus"
              label={$t('employment_category')}
              items={avetmissStudentLabourStatuses}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="student.isStillAtSchool"
              label={$t('still_at_school')}
              items={stillAtSchoolItems}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="student.disabilityType"
              label={$t('disability_type')}
              items={disabilityTypes}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <div className="mt-1 centeredFlex">
              <FormControlLabel
                className="checkbox pr-3"
                control={
                  <FormField type="checkbox" name="student.specialNeedsAssistance" color="secondary" />
                }
                label={$t('disability_support_requested')}
              />
            </div>
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="student.clientIndustryEmployment"
              label={$t('client_industry_of_employment_vic')}
              items={industriesOfEmployment}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="student.clientOccupationIdentifier"
              label={$t('client_occupation_identifier_vic')}
              items={occupationIdentifiers}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name="student.uniqueLearnerIdentifier"
              label={$t('government_student_number_vicqld')}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name="student.chessn"
              label={$t('commonwealth_higher_education_support_number_chess')}
            />
          </Grid>
          <Grid item container xs={12}>
            <Grid item xs={twoColumn ? 6 : 12} md={twoColumn ? 4 : 12}>
              <FormField
                type="text"
                name="student.usi"
                label={$t('unique_student_identifier_usi')}
                validate={validateUSI}
                labelAdornment={<SettingsAdornment clickHandler={e => setMenuUSI(e.currentTarget)} />}
                disabled={isSpecialUSI(values) || usiLocked}
                onChange={handleUSIChange}
              />
              <Menu
                id="menuUSI"
                anchorEl={showMenuUSI}
                open={Boolean(showMenuUSI)}
                onClose={closeUSIMenu}
                disableAutoFocusItem
              >
                <div>
                  {isSpecialUSI(values) ? (
                    <MenuItem
                      onClick={() => {
                          setUSIStatus("Not supplied");
                          dispatch(change(form, "student.usi", null));
                          closeUSIMenu();
                        }}
                    >
                      {$t('remove_special_usi_status')}
                    </MenuItem>
                    ) : (
                      <>
                        <MenuItem>
                          <Link
                            href="https://www.ish.com.au/oncourse/createUSI"
                            target="_blank"
                            color="textPrimary"
                            underline="none"
                            className="centeredFlex"
                          >
                            {$t('spancreate_a_usispan')}
                            <ExitToApp className={`ml-1 ${classes.exitToCreateUSI}`} />
                          </Link>
                        </MenuItem>
                        {values.student && values.student.usiStatus === "Verified" ? (
                          <MenuItem
                            onClick={() => {
                              setUsiUpdateLocked(false);
                              dispatch(change(form, "student.usi", null));
                              dispatch(change(form, "student.usiStatus", null));
                              closeUSIMenu();
                            }}
                          >
                            {$t('update_usi')}
                          </MenuItem>
                        ) : null}
                        <MenuItem
                          onClick={() => {
                            clearUSIVerificationResult();
                            setUSIStatus("Exemption");
                            dispatch(change(form, "student.usi", "INDIV"));
                            closeUSIMenu();
                          }}
                        >
                          {$t('student_has_exemption')}
                        </MenuItem>
                        <MenuItem
                          onClick={() => {
                            clearUSIVerificationResult();
                            setUSIStatus("International");
                            dispatch(change(form, "student.usi", "INTOFF"));
                            closeUSIMenu();
                          }}
                        >
                          {$t('international_student')}
                        </MenuItem>
                      </>
                    )}
                </div>
              </Menu>
            </Grid>
            <Grid item xs={twoColumn ? 6 : 12} md={twoColumn ? 8 : 12}>
              <div className="mt-1">{getUSIStatusMsg()}</div>
            </Grid>
            {usiVerificationResult && usiVerificationResult.errorMessage && (
            <Grid item xs={12}>
              <div className={`errorColor ${classes.verificationError}`}>{usiVerificationResult.errorMessage}</div>
            </Grid>
              )}
          </Grid>
          <Grid item xs={12}>
            <div className="mt-1 centeredFlex">
              <FormControlLabel
                className="checkbox pr-3"
                control={
                  <FormField type="checkbox" name="student.isOverseasClient" color="secondary" />
                }
                label={$t('overseas_student')}
              />
            </div>
          </Grid>
          {values.student && values.student.isOverseasClient && (
            <>
              {countries && (
                <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
                  <FormField
                    type="select"
                    selectValueMark="id"
                    selectLabelMark="name"
                    name="student.countryOfResidency"
                    label={$t('country_of_residency')}
                    returnType="object"
                    items={countries}
                  />
                </Grid>
              )}
              <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
                <FormField type="text" name="student.passportNumber" label={$t('passport_number')} />
              </Grid>
              <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
                <FormField type="text" name="student.visaType" label={$t('visa_type')} />
              </Grid>
              <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
                <FormField type="text" name="student.visaNumber" label={$t('visa_number')} />
              </Grid>
              <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
                <FormField
                  type="date"
                  name="student.visaExpiryDate"
                  label={$t('visa_expiry_date')}
                />
              </Grid>
              <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
                <FormField
                  type="text"
                  name="student.medicalInsurance"
                  label={$t('overseas_health_care_cover')}
                />
              </Grid>
            </>
          )}
          <Grid item xs={12}>
            <div className="mt-1 mb-2 centeredFlex">
              <FormControlLabel
                className="checkbox pr-3"
                control={<FormField type="checkbox" name="student.feeHelpEligible" color="secondary" />}
                label={$t('vet_student_loan_eligible')}
              />
            </div>
          </Grid>
          {values.student && values.student.feeHelpEligible && (
            <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
              <FormField
                type="text"
                name="tfn"
                label={$t('tax_file_number')}
                max="9"
                InputProps={{
                  inputComponent: TFNInputMask
                }}
                validate={validateTFN}
                parse={parseTFN}
                format={formatTFN}
                debounced={false}
              />
            </Grid>
          )}
        </Grid>
      </ExpandableContainer>
    </div>
  ) : null;
};

const mapStateToProps = (state: State) => ({
  countries: state.countries,
  languages: state.languages,
  isVerifyingUSI: state.contacts.verifyingUSI,
  usiVerificationResult: state.contacts.usiVerificationResult,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  clearUSIVerificationResult: () => dispatch(clearUSIVerificationResult()),
  verifyUSI: (firstName: string, lastName: string, birthDate: string, usiCode: string) =>
    dispatch(verifyUSI(firstName, lastName, birthDate, usiCode))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(ContactsVET, styles));