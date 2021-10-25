/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useCallback, useEffect, useMemo, useState
} from "react";
import {
  ConcessionType,
  Contact,
  ContactGender,
  ContactRelationType,
  Student,
  StudentConcession,
  Tag,
  Tutor
} from "@api/model";
import {
 arrayInsert, arrayRemove, change, Field, getFormInitialValues
} from "redux-form";
import { connect } from "react-redux";
import clsx from "clsx";
import withStyles from "@mui/styles/withStyles";
import createStyles from "@mui/styles/createStyles";
import { FormControlLabel, Grid } from "@mui/material";
import ButtonGroup from "@mui/material/ButtonGroup";
import Button from "@mui/material/Button";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Checkbox from "@mui/material/Checkbox";
import Typography from "@mui/material/Typography";
import Link from "@mui/material/Link";
import ExitToApp from "@mui/icons-material/ExitToApp";
import Divider from '@mui/material/Divider';
import FormField from "../../../../common/components/form/formFields/FormField";
import { State } from "../../../../reducers/state";
import { greaterThanNullValidation, validateEmail, validatePhoneNumber } from "../../../../common/utils/validation";
import AvatarRenderer from "./AvatarRenderer";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import { SettingsAdornment } from "../../../../common/components/form/FieldAdornments";
import MinifiedEntitiesList from "../../../../common/components/form/minifiedEntitiesList/MinifiedEntitiesList";
import { MembershipContent, MembershipHeader } from "./MembershipLines";
import { RelationsContent, RelationsHeader } from "./RelationsLines";
import { ConcessionsContent, ConcessionsHeader } from "./ConcessionsLines";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import { getContactFullName } from "../utils";
import { openInternalLink } from "../../../../common/utils/links";
import TimetableButton from "../../../../common/components/buttons/TimetableButton";
import { EditViewProps } from "../../../../model/common/ListView";
import { mapSelectItems } from "../../../../common/utils/common";
import { StyledCheckbox } from "../../../../common/components/form/formFields/CheckboxField";
import ExpandableContainer from "../../../../common/components/layout/expandable/ExpandableContainer";
import { AppTheme } from "../../../../model/common/Theme";
import FullScreenStickyHeader
  from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";

const NO_MARKETING_MSG = "(no marketing)";
const UNDELIVERABLE_MSG = "(undeliverable)";
const NO_MARKETING_AND_UNDELIVERABLE_MSG = "(no marketing and undeliverable)";

const TutorInitial: Tutor = {
  wwChildrenStatus: "Not checked"
};

const styles = (theme: AppTheme) => createStyles({
  contactGeneralWrapper: {
    paddingTop: `${theme.spacing(10)} !important`,
  },
  exitToApp: {
    fontSize: "1.2rem",
    top: "5px"
  },
  customCheckbox: {
    margin: 0,
    height: "24px",
    width: "30px",
  },
});

interface ContactsGeneralProps extends EditViewProps<Contact> {
  classes?: any;
  initialValues?: Contact;
  isStudent?: boolean;
  isTutor?: boolean;
  isCompany?: boolean;
  setIsStudent?: any;
  setIsTutor?: any;
  setIsCompany?: any;
  tags?: any;
  countries?: any;
  relationTypes?: ContactRelationType[];
  concessionTypes?: ConcessionType[];
  usiLocked?: boolean;
  fullScreenEditView?: boolean;
}

const contactGenderItems = Object.keys(ContactGender).map(mapSelectItems);

export const studentInitial: Student = {
  labourForceStatus: "Not stated",
  englishProficiency: "Not stated",
  highestSchoolLevel: "Not stated",
  citizenship: "No information",
  feeHelpEligible: false,
  indigenousStatus: "Not stated",
  priorEducationCode: "Not stated",
  isOverseasClient: false,
  disabilityType: "Not stated",
  clientIndustryEmployment: "Not Stated",
  clientOccupationIdentifier: "Not Stated"
};

const validateBirthDate = v => (!v || new Date(v).getTime() - Date.now() < 0 ? undefined : "Date of birth cannot be in future.");

const validateABN = v => (!v || (!Number.isNaN(parseFloat(v)) && Number.isFinite(v)) ? undefined : "Business Number (ABN) should be numeric.");

const filterStudentTags = (tag: Tag) => {
  const { requirements } = tag;
  if (Array.isArray(requirements)) {
    return requirements.some(r => r.type === "Student") || requirements.some(r => r.type === "Contact");
  }
  return true;
};

const filterTutorTags = (tag: Tag) => {
  const { requirements } = tag;
  if (Array.isArray(requirements)) {
    return (
      requirements.some(r => r.type === "Tutor")
      || requirements.some(r => r.type === "Contact")
      || requirements.some(r => r.type === "Course")
    );
  }
  return true;
};

const filterCompanyTags = (tag: Tag) => {
  const { requirements } = tag;
  if (Array.isArray(requirements)) {
    return !(requirements.some(r => r.type === "Student") || requirements.some(r => r.type === "Tutor"));
  }
  return true;
};

export const ProfileHeading: React.FC<any> = props => {
  const {
    form,
    dispatch,
    showConfirm,
    values,
    twoColumn,
    isCompany,
    usiLocked,
    hide,
    otherClasses,
    isScrolling,
  } = props;

  return (
    <FullScreenStickyHeader
      hide={hide}
      otherClasses={otherClasses}
      isScrolling={isScrolling}
      twoColumn={twoColumn}
      avatar={aClasses => (
        <Field
          name="profilePicture"
          label="Profile picture"
          component={AvatarRenderer}
          props={{
            form,
            classes: aClasses,
            dispatch,
            showConfirm,
            email: values.email,
            avatarSize: isScrolling ? 40 : 90,
            disabled: isScrolling,
            twoColumn,
          }}
        />
      )}
      title={(
        <>
          {values && !isCompany && values.title && values.title.trim().length > 0 ? `${values.title} ` : ""}
          {values ? (!isCompany ? getContactFullName(values) : values.lastName) : ""}
        </>
      )}
      fields={(
        <>
          {!isCompany && (
            <>
              <Grid item xs={twoColumn ? 2 : 6}>
                <FormField type="text" name="title" label="Title" />
              </Grid>
              <Grid item xs={twoColumn ? 2 : 6}>
                <FormField type="text" name="firstName" label="First name" disabled={usiLocked} required />
              </Grid>
              <Grid item xs={twoColumn ? 2 : 6}>
                <FormField type="text" name="middleName" label="Middle name" />
              </Grid>
            </>
          )}
          <Grid item xs={twoColumn ? 4 : 6}>
            <FormField type="text" name="lastName" label={isCompany ? "Company name" : "Last name"} disabled={usiLocked} required />
          </Grid>
        </>
      )}
    />
  );
};

const ContactsGeneral: React.FC<ContactsGeneralProps> = props => {
  const {
    classes,
    twoColumn,
    values,
    initialValues,
    form,
    dispatch,
    isStudent,
    isTutor,
    isCompany,
    setIsStudent,
    setIsTutor,
    setIsCompany,
    tags,
    countries,
    relationTypes,
    concessionTypes,
    usiLocked,
    isNew,
    syncErrors,
    tabIndex,
    expanded,
    setExpanded,
  } = props;

  const isInitiallyStudent = initialValues && !!initialValues.student;
  const isInitiallyTutor = initialValues && !!initialValues.tutor;
  const isInitiallyCompany = initialValues && !!initialValues.isCompany;

  const toggleStudentRole = useCallback(() => {
    if (!isInitiallyStudent) {
      setIsStudent(!isStudent);
      dispatch(change(form, "student", isStudent ? null : { ...studentInitial }));
    }
    if (isNew) {
      dispatch(change(form, "student", isStudent ? null : { ...studentInitial }));
    }
  }, [isInitiallyStudent, isNew, isStudent]);

  const toggleTutorRole = useCallback(() => {
    if (!isInitiallyTutor) {
      setIsTutor(!isTutor);
      dispatch(change(form, "tutor", isTutor ? null : { ...TutorInitial }));
    }
    if (isNew) {
      dispatch(change(form, "tutor", isTutor ? null : { ...TutorInitial }));
    }
  }, [isInitiallyTutor, isNew, isTutor]);

  const toggleCompanyRole = useCallback(() => {
    if (!isInitiallyCompany) {
      setIsCompany(!isCompany);
      dispatch(change(form, "isCompany", isCompany ? null : true));
    }
    if (isNew) {
      dispatch(change(form, "isCompany", isCompany ? null : true));
    }
  }, [isInitiallyCompany, isNew, isCompany]);

  useEffect(() => {
    setIsCompany(isInitiallyCompany);
    setIsStudent(isInitiallyStudent);
    setIsTutor(isInitiallyTutor);
  }, [isInitiallyCompany, isInitiallyStudent, isInitiallyTutor]);

  const [showPostalSettingsMenu, setPostalSettingsMenu] = useState(null);
  const [showSmsSettingsMenu, setSmsSettingsMenu] = useState(null);
  const [showEmailSettingsMenu, setEmailSettingsMenu] = useState(null);

  const validateTagList = useCallback((value, allValues, props) => validateTagsList(tags, value, allValues, props), [tags]);

  const onCalendarClick = () => {
    openInternalLink(
      `/timetable/search?query=attendance.student.contact.id=${values.id}&title=Timetable for ${getContactFullName(
        values
      )}`
    );
  };

  const isUndeliverablePostal = useCallback(() => values && values.deliveryStatusPost >= 6, [
    values && values.deliveryStatusPost
  ]);

  const isUndeliverableSms = useCallback(() => values && values.deliveryStatusSms >= 6, [
    values && values.deliveryStatusSms
  ]);

  const isUndeliverableEmail = useCallback(() => values && values.deliveryStatusEmail >= 6, [
    values && values.deliveryStatusEmail
  ]);

  const handleUndeliverablePostalCheck = () => {
    dispatch(change(form, "deliveryStatusPost", isUndeliverablePostal() ? 0 : 6));
  };

  const handleUndeliverableSmsCheck = () => {
    dispatch(change(form, "deliveryStatusSms", isUndeliverableSms() ? 0 : 6));
  };

  const handleUndeliverableEmailCheck = () => {
    dispatch(change(form, "deliveryStatusEmail", isUndeliverableEmail() ? 0 : 6));
  };

  const setMarketingLabel = (fieldName = ""): string => {
    if (!values) return "";

    if (fieldName === "street") {
      const { allowPost } = values;

      if (!allowPost && isUndeliverablePostal()) {
        return `Street ${NO_MARKETING_AND_UNDELIVERABLE_MSG}`;
      }
      if (!allowPost) {
        return `Street ${NO_MARKETING_MSG}`;
      }
      if (isUndeliverablePostal()) {
        return `Street ${UNDELIVERABLE_MSG}`;
      }

      return "Street";
    }

    if (fieldName === "mobilePhone") {
      const { allowSms } = values;

      if (!allowSms && isUndeliverableSms()) {
        return `Mobile phone ${NO_MARKETING_AND_UNDELIVERABLE_MSG}`;
      }
      if (!allowSms) {
        return `Mobile phone  ${NO_MARKETING_MSG}`;
      }
      if (isUndeliverableSms()) {
        return `Mobile phone ${UNDELIVERABLE_MSG}`;
      }

      return "Mobile phone";
    }

    if (fieldName === "email") {
      const { allowEmail } = values;

      if (!allowEmail && isUndeliverableEmail()) {
        return `Email ${NO_MARKETING_AND_UNDELIVERABLE_MSG}`;
      }
      if (!allowEmail) {
        return `Email ${NO_MARKETING_MSG}`;
      }
      if (isUndeliverableEmail()) {
        return `Email ${UNDELIVERABLE_MSG}`;
      }

      return "Email";
    }

    return fieldName;
  };

  const membershipsCount = useMemo(() => (values.memberships && values.memberships.length) || 0, [values.memberships]);
  const relationsCount = useMemo(() => (values.relations && values.relations.length) || 0, [values.relations]);
  const concessionsCount = useMemo(
    () => (values.student && values.student.concessions && values.student.concessions.length) || 0,
    [values.student && values.student.concessions]
  );

  const deleteRelation = useCallback(
    index => {
      dispatch(arrayRemove(form, "relations", index));
    },
    [values && values.relations, form]
  );

  const addNewRelation = useCallback(() => {
    dispatch(
      arrayInsert(form, "relations", 0, {
        id: null,
        relationId: null,
        relatedContactId: null,
        relatedContactName: null
      })
    );
  }, [values && values.id, form]);

  const RelationsHeaderLine = useCallback(
    props => <RelationsHeader relationTypes={relationTypes} contactId={values.id} {...props} />,
    [values && values.id, relationTypes]
  );
  const RelationsContentLine = useCallback(
    props => (
      <RelationsContent
        form={form}
        dispatch={dispatch}
        relationTypes={relationTypes}
        contactId={values.id}
        contactFullName={getContactFullName(values)}
        {...props}
      />
    ),
    [values && values.firstName, values && values.lastName, values && values.id, form, relationTypes]
  );

  const deleteConcession = useCallback(
    index => {
      dispatch(arrayRemove(form, "student.concessions", index));
    },
    [values && values.student && values.student.concessions, form]
  );

  const addNewConcession = useCallback(() => {
    const newLine: StudentConcession = {
      number: null,
      expiresOn: null,
      type: null
    };

    dispatch(arrayInsert(form, "student.concessions", 0, newLine));
  }, [values && values.id, form]);

  const ConcessionsHeaderLine = useCallback(props => <ConcessionsHeader {...props} />, [
    values.student && values.student.concessions
  ]);
  const ConcessionsContentLine = useCallback(
    props => <ConcessionsContent concessionTypes={concessionTypes} {...props} />,
    []
  );

  const getFilteredTags = useCallback(() => {
    if (Array.isArray(tags)) {
      if (isStudent && isTutor) {
        return Array.from(new Set([...tags.filter(filterStudentTags), ...tags.filter(filterTutorTags)]));
      }
      if (isStudent) {
        return tags.filter(filterStudentTags);
      }
      if (isTutor) {
        return tags.filter(filterTutorTags);
      }
      if (isCompany) {
        return tags.filter(filterCompanyTags);
      }
    }

    return [];
  }, [tags, isStudent, isTutor, isCompany]);

  const filteredTags = getFilteredTags();

  const gridItemProps: any = {
    xs: twoColumn ? 6 : 12,
    lg: twoColumn ? 4 : 12
  };

  return (
    <div className={clsx("p-3", twoColumn && classes.contactGeneralWrapper)}>
      <ProfileHeading {...props} hide={twoColumn} />
      <Grid container columnSpacing={3}>
        <Grid item xs={12} md={twoColumn ? 7 : 12}>
          <Typography variant="caption" display="block" gutterBottom>
            Type
          </Typography>
          <ButtonGroup aria-label="full width outlined button group" className="mb-3 mt-1">
            <Button color={isStudent ? "primary" : "inherit"} disabled={isCompany} onClick={toggleStudentRole}>
              <StyledCheckbox
                checked={isStudent}
                color="primary"
                className={classes.customCheckbox}
              />
              Student
            </Button>
            <Button color={isTutor ? "primary" : "inherit"} onClick={toggleTutorRole}>
              <StyledCheckbox
                checked={isTutor}
                color="primary"
                className={classes.customCheckbox}
              />
              Tutor
            </Button>
            <Button color={isCompany ? "primary" : "inherit"} disabled={isStudent} onClick={toggleCompanyRole}>
              <StyledCheckbox
                checked={isCompany}
                color="primary"
                disabled={isStudent}
                className={classes.customCheckbox}
              />
              Company
            </Button>
          </ButtonGroup>
        </Grid>
      </Grid>
      <Grid container columnSpacing={3} className="flex-nowrap align-items-center mb-1">
        <Grid item xs={12}>
          <FormField
            type="tags"
            name="tags"
            tags={filteredTags}
            validate={tags && tags.length ? validateTagList : undefined}
          />
        </Grid>
      </Grid>
      {isStudent && (
        <>
          <Divider className="mt-3 mb-3" />
          <Grid container columnSpacing={3}>
            <Grid item xs={12}>
              <TimetableButton onClick={onCalendarClick} />
            </Grid>
          </Grid>
        </>
      )}
      <Divider className="mt-3 mb-3" />
      <Grid container columnSpacing={3} rowSpacing={3}>
        <Grid item xs={12}>
          <ExpandableContainer index={tabIndex} expanded={expanded} setExpanded={setExpanded} header="Contact">
            <Grid container columnSpacing={3} rowSpacing={3}>
              <Grid item {...gridItemProps}>
                <FormField
                  type="text"
                  name="street"
                  label={setMarketingLabel("street")}
                  validate={greaterThanNullValidation}
                  labelAdornment={<SettingsAdornment clickHandler={e => setPostalSettingsMenu(e.currentTarget)} />}
                />
                <Menu
                  id="postalSettingsMenu"
                  anchorEl={showPostalSettingsMenu}
                  open={Boolean(showPostalSettingsMenu)}
                  onClose={() => setPostalSettingsMenu(null)}
                  disableAutoFocusItem
                >
                  <MenuItem>
                    <FormControlLabel
                      className="checkbox pr-3"
                      control={<FormField type="checkbox" name="allowPost" color="secondary" />}
                      label="Accept postal marketing material"
                    />
                  </MenuItem>
                  <MenuItem>
                    <FormControlLabel
                      className="checkbox pr-3"
                      control={<Checkbox checked={isUndeliverablePostal()} onClick={handleUndeliverablePostalCheck} />}
                      label="Undeliverable"
                    />
                  </MenuItem>
                </Menu>
              </Grid>
              <Grid item {...gridItemProps}>
                <FormField type="text" name="suburb" label="Suburb" />
              </Grid>
              <Grid item {...gridItemProps}>
                <FormField type="text" name="state" label="State" />
              </Grid>
              <Grid item {...gridItemProps}>
                <FormField type="text" name="postcode" label="Postcode" />
              </Grid>
              <Grid item {...gridItemProps}>
                {countries && (
                  <FormField
                    type="searchSelect"
                    selectValueMark="id"
                    selectLabelMark="name"
                    name="country"
                    label="Country"
                    returnType="object"
                    items={countries}
                  />
                )}
              </Grid>
              <Grid item {...gridItemProps}>
                <FormField
                  type="text"
                  name="mobilePhone"
                  label={setMarketingLabel("mobilePhone")}
                  validate={validatePhoneNumber}
                  labelAdornment={<SettingsAdornment clickHandler={e => setSmsSettingsMenu(e.currentTarget)} />}
                />
                <Menu
                  id="smsSettingsMenu"
                  anchorEl={showSmsSettingsMenu}
                  open={Boolean(showSmsSettingsMenu)}
                  onClose={() => setSmsSettingsMenu(null)}
                  disableAutoFocusItem
                >
                  <MenuItem>
                    <FormControlLabel
                      className="checkbox pr-3"
                      control={<FormField type="checkbox" name="allowSms" color="secondary" />}
                      label="Accept sms marketing material"
                    />
                  </MenuItem>
                  <MenuItem>
                    <FormControlLabel
                      className="checkbox pr-3"
                      control={<Checkbox checked={isUndeliverableSms()} onClick={handleUndeliverableSmsCheck} />}
                      label="Undeliverable"
                    />
                  </MenuItem>
                </Menu>
              </Grid>
              <Grid item {...gridItemProps}>
                <FormField
                  type="text"
                  name="email"
                  label={setMarketingLabel("email")}
                  labelAdornment={<SettingsAdornment clickHandler={e => setEmailSettingsMenu(e.currentTarget)} />}
                  validate={validateEmail}
                />
                <Menu
                  id="emailSettingsMenu"
                  anchorEl={showEmailSettingsMenu}
                  open={Boolean(showEmailSettingsMenu)}
                  onClose={() => setEmailSettingsMenu(null)}
                  disableAutoFocusItem
                >
                  <MenuItem>
                    <FormControlLabel
                      className="checkbox pr-3"
                      control={<FormField type="checkbox" name="allowEmail" color="secondary" />}
                      label="Accept email marketing material"
                    />
                  </MenuItem>
                  <MenuItem>
                    <FormControlLabel
                      className="checkbox pr-3"
                      control={<Checkbox checked={isUndeliverableEmail()} onClick={handleUndeliverableEmailCheck} />}
                      label="Undeliverable"
                    />
                  </MenuItem>
                </Menu>
              </Grid>
              <Grid item {...gridItemProps}>
                <FormField type="text" name="message" label="Message (alert for operator)" />
              </Grid>
              <Grid item {...gridItemProps}>
                <FormField type="text" name="homePhone" label="Home phone" validate={validatePhoneNumber} />
              </Grid>
              <Grid item {...gridItemProps}>
                <FormField type="text" name="workPhone" label="Work phone" validate={validatePhoneNumber} />
              </Grid>
              <Grid item {...gridItemProps}>
                <FormField type="text" name="fax" label="Fax" />
              </Grid>
              <Grid item {...gridItemProps}>
                <FormField type="text" name="abn" label="Business number (ABN)" validate={validateABN} />
              </Grid>
              {!isCompany ? (
                <>
                  <Grid item {...gridItemProps}>
                    <FormField
                      type="date"
                      name="birthDate"
                      label="Date of birth"
                      disabled={usiLocked}
                      validate={validateBirthDate}
                    />
                  </Grid>
                  <Grid item {...gridItemProps}>
                    <FormField
                      type="select"
                      name="gender"
                      label="Gender"
                      items={contactGenderItems}
                      className={classes.selectField}
                      placeholder="Not stated"
                      allowEmpty
                    />
                  </Grid>
                  <Grid item {...gridItemProps}>
                    <FormField type="text" name="honorific" label="Honorific" />
                  </Grid>
                </>
              ) : null}

              <CustomFields
                entityName="Contact"
                fieldName="customFields"
                entityValues={values}
                dispatch={dispatch}
                form={form}
                gridItemProps={gridItemProps}
              />

              {values.student && (
                <>
                  <Grid item xs={twoColumn ? 6 : 12}>
                    <FormField type="multilineText" name="student.specialNeeds" label="Special needs" />
                  </Grid>
                  <Grid item xs={12}>
                    {values.student.waitingLists && values.student.waitingLists.length !== 0 ? (
                      <Typography display="inline" variant="body1">
                        {`Student is on waiting list for: ${values.student.waitingLists.map(v => `"${v}"`).join(", ")}`}
                        <Link
                          href={`${window.location.origin}/waitingList?search=student.contact.id = ${values.id}`}
                          target="_blank"
                          color="textSecondary"
                          underline="none"
                          className="d-inline"
                        >
                          <ExitToApp className={clsx("ml-1 relative", classes.exitToApp)} />
                        </Link>
                      </Typography>
                    ) : (
                      <Typography display="inline" variant="body1" className="pt-2">
                        Student is not on any waiting list
                      </Typography>
                    )}
                  </Grid>
                </>
              )}
            </Grid>
          </ExpandableContainer>
        </Grid>
        {values.student && (
          <>
            <Grid item xs={12} className="pb-2 pt-2">
              <Divider className="mt-3 mb-3" />
              <MinifiedEntitiesList
                name="student.concessions"
                header="Concessions"
                oneItemHeader="Concession"
                count={concessionsCount}
                FieldsContent={ConcessionsContentLine}
                HeaderContent={ConcessionsHeaderLine}
                onAdd={addNewConcession}
                onDelete={deleteConcession}
                syncErrors={syncErrors}
                accordion
              />
            </Grid>
          </>
        )}
        <Grid item xs={12} className="pb-2 pt-2">
          <MinifiedEntitiesList
            name="memberships"
            header="Memberships"
            oneItemHeader="Membership"
            count={membershipsCount}
            FieldsContent={MembershipContent}
            HeaderContent={MembershipHeader}
            syncErrors={syncErrors}
            twoColumn={twoColumn}
            accordion
          />
        </Grid>
        <Grid item xs={12} className="pb-2 pt-2">
          <MinifiedEntitiesList
            name="relations"
            header="Relations"
            oneItemHeader="Relation"
            count={relationsCount}
            FieldsContent={RelationsContentLine}
            HeaderContent={RelationsHeaderLine}
            onAdd={addNewRelation}
            onDelete={deleteRelation}
            syncErrors={syncErrors}
            accordion
          />
        </Grid>
      </Grid>
    </div>
  );
};

const mapStateToProps = (state: State, props) => ({
  initialValues: getFormInitialValues(props.form)(state),
  tags: state.tags.entityTags["Contact"],
  countries: state.countries,
  relationTypes: state.contacts.contactsRelationTypes,
  concessionTypes: state.contacts.contactsConcessionTypes,
  fullScreenEditView: state.list.fullScreenEditView
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(withStyles(styles)((props: any) => (props.values ? <ContactsGeneral {...props} /> : null))) as React.FC<
  ContactsGeneralProps
>;
