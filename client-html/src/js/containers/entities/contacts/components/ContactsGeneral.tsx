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
import withStyles from "@material-ui/core/styles/withStyles";
import createStyles from "@material-ui/core/styles/createStyles";
import { FormControlLabel, Grid } from "@material-ui/core";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import Button from "@material-ui/core/Button";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import Checkbox from "@material-ui/core/Checkbox";
import Typography from "@material-ui/core/Typography";
import Link from "@material-ui/core/Link";
import ExitToApp from "@material-ui/icons/ExitToApp";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../reducers/state";
import {
  greaterThanNullValidation,
  validateEmail,
  validatePhoneNumber
} from "../../../../common/utils/validation";
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

const NO_MARKETING_MSG = "(no marketing)";
const UNDELIVERABLE_MSG = "(undeliverable)";
const NO_MARKETING_AND_UNDELIVERABLE_MSG = "(no marketing and undeliverable)";

const TutorInitial: Tutor = {
  wwChildrenStatus: "Not checked"
};

const styles = theme => createStyles({
    avatarWrapper: {
      "&  img": {
        width: "100%"
      }
    },
    exitToApp: {
      fontSize: "1.2rem",
      top: "5px"
    },
    profileThumbnail: {
      "&:hover $profileEditIcon": {
        color: theme.palette.primary.main,
        fill: theme.palette.primary.main
      }
    },
    profileEditIcon: {
      fontSize: "14px",
      color: theme.palette.divider,
      position: "absolute",
      bottom: 5,
      right: -10
    }
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

const validateABN = v => (!v || (!isNaN(parseFloat(v)) && isFinite(v)) ? undefined : "Business Number (ABN) should be numeric.");

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

const ContactsGeneral: React.FC<ContactsGeneralProps> = props => {
  const {
    classes,
    twoColumn,
    values,
    initialValues,
    form,
    dispatch,
    showConfirm,
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
    syncErrors
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

  return (
    <div className="p-3">
      <Grid container className="mb-3">
        <Grid item xs={twoColumn ? 2 : 12}>
          <Field
            name="profilePicture"
            label="Profile picture"
            component={AvatarRenderer}
            props={{
              form,
              classes,
              dispatch,
              showConfirm,
              email: values.email
            }}
          />
        </Grid>
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
        <Grid item xs={12}>
          <ButtonGroup aria-label="full width outlined button group" className="mt-1">
            <Button color={isStudent ? "primary" : "default"} disabled={isCompany} onClick={toggleStudentRole}>
              Student
            </Button>
            <Button color={isTutor ? "primary" : "default"} onClick={toggleTutorRole}>
              Tutor
            </Button>
            <Button color={isCompany ? "primary" : "default"} disabled={isStudent} onClick={toggleCompanyRole}>
              Company
            </Button>
          </ButtonGroup>
        </Grid>
      </Grid>
      <Grid container className="flex-nowrap align-items-center mb-1">
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
        <Grid container className="pt-2 pb-2">
          <Grid item xs={12} className="mb-2">
            <TimetableButton onClick={onCalendarClick} />
          </Grid>
        </Grid>
      )}
      <Grid container>
        <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
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
        <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
          <FormField type="text" name="suburb" label="Suburb" />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
          <FormField type="text" name="state" label="State" />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
          <FormField type="text" name="postcode" label="Postcode" />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
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
        <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
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
        <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
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
        <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
          <FormField type="text" name="message" label="Message (alert for operator)" />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
          <FormField type="text" name="homePhone" label="Home phone" validate={validatePhoneNumber} />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
          <FormField type="text" name="workPhone" label="Work phone" validate={validatePhoneNumber} />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
          <FormField type="text" name="fax" label="Fax" />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
          <FormField type="text" name="abn" label="Business number (ABN)" validate={validateABN} />
        </Grid>
        {!isCompany ? (
          <>
            <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
              <FormField
                type="date"
                name="birthDate"
                label="Date of birth"
                disabled={usiLocked}
                validate={validateBirthDate}
              />
            </Grid>
            <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
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
            <Grid item xs={twoColumn ? 6 : 12} lg={twoColumn ? 4 : 12}>
              <FormField type="text" name="honorific" label="Honorific" />
            </Grid>
          </>
        ) : null}
        <Grid item xs={12}>
          <CustomFields
            entityName="Contact"
            fieldName="customFields"
            entityValues={values}
            dispatch={dispatch}
            form={form}
          />
        </Grid>
        {values.student && (
          <>
            <Grid item xs={12}>
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
            <Grid item xs={12} className="pb-2 pt-2">
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
  concessionTypes: state.contacts.contactsConcessionTypes
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(withStyles(styles)((props: any) => (props.values ? <ContactsGeneral {...props} /> : null))) as React.FC<
  ContactsGeneralProps
>;
