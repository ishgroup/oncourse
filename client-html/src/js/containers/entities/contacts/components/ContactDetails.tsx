/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Typography from "@mui/material/Typography";
import React, {
  useCallback, useMemo, useState
} from "react";
import {
  ConcessionType,
  Contact, ContactGender, ContactRelationType, StudentCitizenship, StudentConcession
} from "@api/model";
import {
  arrayInsert, arrayRemove, change
} from "redux-form";
import { connect } from "react-redux";
import {
 Alert, FormControlLabel, Grid, IconButton
} from "@mui/material";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Checkbox from "@mui/material/Checkbox";
import OpenInNew from "@mui/icons-material/OpenInNew";
import Divider from "@mui/material/Divider";
import FormField from "../../../../common/components/form/formFields/FormField";
import { State } from "../../../../reducers/state";
import { getContactFullName } from "../utils";
import { EditViewProps } from "../../../../model/common/ListView";
import ExpandableContainer from "../../../../common/components/layout/expandable/ExpandableContainer";
import { greaterThanNullValidation, validateEmail, validatePhoneNumber } from "../../../../common/utils/validation";
import { SettingsAdornment } from "../../../../common/components/form/FieldAdornments";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import MinifiedEntitiesList from "../../../../common/components/form/minifiedEntitiesList/MinifiedEntitiesList";
import { MembershipContent, MembershipHeader } from "./MembershipLines";
import { RelationsContent, RelationsHeader } from "./RelationsLines";
import { ConcessionsContent, ConcessionsHeader } from "./ConcessionsLines";
import { mapSelectItems } from "../../../../common/utils/common";
import { openInternalLink } from "../../../../common/utils/links";

const NO_MARKETING_MSG = "(no marketing)";
const UNDELIVERABLE_MSG = "(undeliverable)";
const NO_MARKETING_AND_UNDELIVERABLE_MSG = "(no marketing and undeliverable)";

const contactGenderItems = Object.keys(ContactGender).map(mapSelectItems);
const studentCitizenships = Object.keys(StudentCitizenship).map(mapSelectItems);

const validateBirthDate = v => (!v || new Date(v).getTime() - Date.now() < 0 ? undefined : "Date of birth cannot be in future.");

const validateABN = v => (!v || (new RegExp(/^\d+$/)).test(v) ? undefined : "Business Number (ABN) should be numeric.");

interface ContactDetailsProps extends EditViewProps<Contact> {
  relationTypes?: ContactRelationType[];
  classes?: any;
  isStudent?: boolean;
  isTutor?: boolean;
  isCompany?: boolean;
  setIsStudent?: any;
  setIsTutor?: any;
  setIsCompany?: any;
  tags?: any;
  countries?: any;
  concessionTypes?: ConcessionType[];
  usiLocked?: boolean;
  fullScreenEditView?: boolean;
}

const ContactDetails: React.FC<ContactDetailsProps> = props => {
  const {
    values,
    tabIndex,
    twoColumn,
    expanded,
    setExpanded,
    dispatch,
    form,
    syncErrors,
    relationTypes,
    countries,
    concessionTypes,
    usiLocked,
    isCompany,
  } = props;

  const [showPostalSettingsMenu, setPostalSettingsMenu] = useState(null);
  const [showSmsSettingsMenu, setSmsSettingsMenu] = useState(null);
  const [showEmailSettingsMenu, setEmailSettingsMenu] = useState(null);

  const isUndeliverablePostal = useCallback(() => values && values.deliveryStatusPost >= 6, [
    values && values.deliveryStatusPost
  ]);

  const isUndeliverableSms = useCallback(() => values && values.deliveryStatusSms >= 6, [
    values && values.deliveryStatusSms
  ]);

  const isUndeliverableEmail = useCallback(() => values && values.deliveryStatusEmail >= 6, [
    values && values.deliveryStatusEmail
  ]);

  const gridItemProps: any = {
    xs: twoColumn ? 6 : 12,
    lg: twoColumn ? 4 : 12
  };

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

  return values ? (
    <Grid container className="pt-2 pl-3 pr-3">
      <Grid item xs={12}>
        <ExpandableContainer
          index={tabIndex}
          expanded={expanded}
          setExpanded={setExpanded}
          formErrors={syncErrors}
          header="Contact"
        >
          <Grid container columnSpacing={3} rowSpacing={2} className="mb-2">
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
                  type="select"
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
                type="phone"
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
              <FormField type="multilineText" name="message" label="Message (alert for operator)" />
            </Grid>
            <Grid item {...gridItemProps}>
              <FormField type="phone" name="homePhone" label="Home phone" validate={validatePhoneNumber} />
            </Grid>
            <Grid item {...gridItemProps}>
              <FormField type="phone" name="workPhone" label="Work phone" validate={validatePhoneNumber} />
            </Grid>
            <Grid item {...gridItemProps}>
              <FormField type="phone" name="fax" label="Fax" />
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
              form={form}
              gridItemProps={gridItemProps}
            />

            {values.student && (
              <>
                <Grid item {...gridItemProps} className="mb-2">
                  <Alert severity="info">
                    {values.student.waitingLists && values.student.waitingLists.length !== 0 ? (
                      <Typography className="centeredFlex" variant="body1">
                        {`Student is on waiting list for: ${values.student.waitingLists.map(v => `"${v}"`).join(", ")}`}
                        <IconButton
                          size="small"
                          onClick={() => openInternalLink(`/waitingList?search=student.contact.id = ${values.id}`)}
                        >
                          <OpenInNew color="primary" fontSize="inherit" />
                        </IconButton>
                      </Typography>
                    ) : (
                      <Typography display="inline" variant="body1" className="pt-2">
                        Student is not on any waiting list
                      </Typography>
                    )}
                  </Alert>
                </Grid>
                <Grid item {...gridItemProps}>
                  <FormField type="multilineText" name="student.specialNeeds" label="Special needs" />
                </Grid>
                <Grid item {...gridItemProps}>
                  <FormField
                    type="select"
                    name="student.citizenship"
                    label="Citizenship status"
                    items={studentCitizenships}
                  />
                </Grid>
              </>
            )}
          </Grid>
        </ExpandableContainer>
      </Grid>
      {values.student && (
        <>
          <Grid item xs={12} className="pb-1">
            <Divider className="mb-1" />
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
      <Grid item xs={12} className="pb-1">
        <Divider className="mb-1" />
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
      <Grid item xs={12} className="pb-1">
        <Divider className="mb-1" />
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
  ) : null;
};

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Contact"],
  countries: state.countries,
  relationTypes: state.contacts.contactsRelationTypes,
  concessionTypes: state.contacts.contactsConcessionTypes,
  fullScreenEditView: state.list.fullScreenEditView
});

export default connect<any, any, any>(mapStateToProps)(ContactDetails);
