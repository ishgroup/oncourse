/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useState } from "react";
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
} from "@api/model";
import { change } from "redux-form";
import { connect } from "react-redux";
import withStyles from "@mui/styles/withStyles";
import createStyles from "@mui/styles/createStyles";
import FormControlLabel from "@mui/material/FormControlLabel";
import Grid from "@mui/material/Grid";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Link from "@mui/material/Link";
import ExitToApp from "@mui/icons-material/ExitToApp";
import Chip from "@mui/material/Chip";
import { Dispatch } from "redux";
import FormField from "../../../../common/components/form/formFields/FormField";
import { State } from "../../../../reducers/state";
import { validateNonNegative } from "../../../../common/utils/validation";
import { SettingsAdornment } from  "ish-ui";
import { clearUSIVerificationResult, verifyUSI } from "../actions";
import { EditViewProps } from "../../../../model/common/ListView";
import { usePrevious } from "../../../../common/utils/hooks";
import { mapSelectItems } from "../../../../common/utils/common";
import ExpandableContainer from "../../../../common/components/layout/expandable/ExpandableContainer";
import { formatTFN, parseTFN, validateTFN } from "../../../../common/utils/validation/tfnValidation";
import { TFNInputMask } from "./ContactsTutor";

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

const styles = () => createStyles({
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

  const validateUSI = useCallback((value) => {
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
  },[values?.birthDate, values?.student?.usiStatus]);

  const getUSIStatusMsg = () => {
    if (!values) return "";

    if (isVerifyingUSI) return <Chip label="Verifying USI..." />;

    if (usiVerificationResult && usiVerificationResult.verifyStatus === "Disabled") {
      return <Chip label="Upgrade for automatic verification" color="secondary" onClick={openUpgradeLink} />;
    }

    if (values.student && values.student.usiStatus) {
      const status = values.student.usiStatus;

      if (status === "Not supplied") {
        if (values.student.usi) {
          return <Chip label="Not verified" className="errorBackgroundColor text-white" />;
        }
      }

      if (status === "Not verified") return <Chip label="Verification failed" className="errorBackgroundColor text-white" />;
      if (status === "Verified") return <Chip label="Verified" className="successBackgroundColor text-white" />;
      if (status === "Exemption") return <Chip label="Student has exemption" className="successBackgroundColor text-white" />;
      if (status === "International") return <Chip label="International student" className="successBackgroundColor text-white" />;
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
                label="Country of birth"
                returnType="object"
                items={countries}
                allowEmpty
              />
            </Grid>
          )}
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField type="text" name="student.townOfBirth" label="Town of birth" />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField type="select" name="student.indigenousStatus" label="Indigenous status" items={indigenousStatuses} />
          </Grid>
          {languages && (
            <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
              <FormField
                type="select"
                selectValueMark="id"
                selectLabelMark="name"
                name="student.language"
                label="Language spoken at home"
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
              label="Proficiency in spoken English"
              items={englishProficiencies}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="student.highestSchoolLevel"
              label="Highest school level"
              items={schoolLevels}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="number"
              name="student.yearSchoolCompleted"
              label="Achieved in year"
              validate={[validateNonNegative, validateYearSchoolCompleted]}
              parse={parseIntValue}
              debounced={false}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="student.priorEducationCode"
              label="Prior educational achievement"
              items={priorEducations}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="student.labourForceStatus"
              label="Employment category"
              items={avetmissStudentLabourStatuses}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="student.isStillAtSchool"
              label="Still at school"
              items={stillAtSchoolItems}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="student.disabilityType"
              label="Disability type"
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
                label="Disability support requested"
              />
            </div>
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="student.clientIndustryEmployment"
              label="Client industry of employment (VIC)"
              items={industriesOfEmployment}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="select"
              name="student.clientOccupationIdentifier"
              label="Client occupation identifier (VIC)"
              items={occupationIdentifiers}
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name="student.uniqueLearnerIdentifier"
              label="Government student number (VIC/QLD)"
            />
          </Grid>
          <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
            <FormField
              type="text"
              name="student.chessn"
              label="Commonwealth higher education support number (CHESSN)"
            />
          </Grid>
          <Grid item container xs={12}>
            <Grid item xs={twoColumn ? 6 : 12} md={twoColumn ? 4 : 12}>
              <FormField
                type="text"
                name="student.usi"
                label="Unique student identifier (USI)"
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
                      Remove special USI status
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
                            <span>Create a USI...</span>
                            {' '}
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
                            Update USI
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
                          Student has exemption
                        </MenuItem>
                        <MenuItem
                          onClick={() => {
                            clearUSIVerificationResult();
                            setUSIStatus("International");
                            dispatch(change(form, "student.usi", "INTOFF"));
                            closeUSIMenu();
                          }}
                        >
                          International student
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
                label="Overseas student"
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
                    label="Country of residency"
                    returnType="object"
                    items={countries}
                  />
                </Grid>
              )}
              <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
                <FormField type="text" name="student.passportNumber" label="Passport number" />
              </Grid>
              <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
                <FormField type="text" name="student.visaType" label="Visa type" />
              </Grid>
              <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
                <FormField type="text" name="student.visaNumber" label="Visa number" />
              </Grid>
              <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
                <FormField
                  type="date"
                  name="student.visaExpiryDate"
                  label="Visa expiry date"
                />
              </Grid>
              <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
                <FormField
                  type="text"
                  name="student.medicalInsurance"
                  label="Overseas health care cover"
                />
              </Grid>
            </>
          )}
          <Grid item xs={12}>
            <div className="mt-1 mb-2 centeredFlex">
              <FormControlLabel
                className="checkbox pr-3"
                control={<FormField type="checkbox" name="student.feeHelpEligible" color="secondary" />}
                label="VET student loan eligible"
              />
            </div>
          </Grid>
          {values.student && values.student.feeHelpEligible && (
            <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
              <FormField
                type="text"
                name="tfn"
                label="Tax file number"
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

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(ContactsVET));