/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Contact, ContactGender, StudentCitizenship } from '@api/model';
import OpenInNew from '@mui/icons-material/OpenInNew';
import { Alert, FormControlLabel, Grid, IconButton, Typography } from '@mui/material';
import Checkbox from '@mui/material/Checkbox';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import $t from '@t';
import { mapSelectItems, openInternalLink, SettingsAdornment } from 'ish-ui';
import React, { useCallback, useState } from 'react';
import { connect } from 'react-redux';
import { change } from 'redux-form';
import FormField from '../../../../common/components/form/formFields/FormField';
import ExpandableContainer from '../../../../common/components/layout/expandable/ExpandableContainer';
import { greaterThanNullValidation, validateEmail, validatePhoneNumber } from '../../../../common/utils/validation';
import { EditViewProps } from '../../../../model/common/ListView';
import { State } from '../../../../reducers/state';
import CustomFields from '../../customFieldTypes/components/CustomFieldsTypes';

const NO_MARKETING_MSG = "(no marketing)";
const UNDELIVERABLE_MSG = "(undeliverable)";
const NO_MARKETING_AND_UNDELIVERABLE_MSG = "(no marketing and undeliverable)";

const contactGenderItems = Object.keys(ContactGender).map(mapSelectItems);
const studentCitizenships = Object.keys(StudentCitizenship).map(mapSelectItems);

const validateBirthDate = v => (!v || new Date(v).getTime() - Date.now() < 0 ? undefined : "Date of birth cannot be in future.");

const validateABN = v => (!v || (new RegExp(/^\d+$/)).test(v) ? undefined : "Business Number (ABN) should be numeric.");

interface ContactDetailsProps extends EditViewProps<Contact> {
  classes?: any;
  isStudent?: boolean;
  noCustomFields?: boolean;
  isTutor?: boolean;
  isCompany?: boolean;
  setIsStudent?: any;
  setIsTutor?: any;
  setIsCompany?: any;
  tags?: any;
  countries?: any;
  usiLocked?: boolean;
  fullScreenEditView?: boolean;
  namePrefix?: string;
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
    countries,
    usiLocked,
    isCompany,
    namePrefix,
    noCustomFields
  } = props;

  const getName = (name: string) => namePrefix ? `${namePrefix}.${name}` : name;

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
    dispatch(change(form, getName("deliveryStatusPost"), isUndeliverablePostal() ? 0 : 6));
  };

  const handleUndeliverableSmsCheck = () => {
    dispatch(change(form, getName("deliveryStatusSms"), isUndeliverableSms() ? 0 : 6));
  };

  const handleUndeliverableEmailCheck = () => {
    dispatch(change(form, getName("deliveryStatusEmail"), isUndeliverableEmail() ? 0 : 6));
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
                    label={$t('accept_postal_marketing_material')}
                  />
                </MenuItem>
                <MenuItem>
                  <FormControlLabel
                    className="checkbox pr-3"
                    control={<Checkbox checked={isUndeliverablePostal()} onClick={handleUndeliverablePostalCheck} />}
                    label={$t('undeliverable')}
                  />
                </MenuItem>
              </Menu>
            </Grid>
            <Grid item {...gridItemProps}>
              <FormField type="text" name="suburb" label={$t('suburb')} />
            </Grid>
            <Grid item {...gridItemProps}>
              <FormField type="text" name="state" label={$t('state')} />
            </Grid>
            <Grid item {...gridItemProps}>
              <FormField type="text" name="postcode" label={$t('postcode')} />
            </Grid>
            <Grid item {...gridItemProps}>
              {countries && (
                <FormField
                  type="select"
                  selectValueMark="id"
                  selectLabelMark="name"
                  name="country"
                  label={$t('country')}
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
                    label={$t('accept_sms_marketing_material')}
                  />
                </MenuItem>
                <MenuItem>
                  <FormControlLabel
                    className="checkbox pr-3"
                    control={<Checkbox checked={isUndeliverableSms()} onClick={handleUndeliverableSmsCheck} />}
                    label={$t('undeliverable')}
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
                    label={$t('accept_email_marketing_material')}
                  />
                </MenuItem>
                <MenuItem>
                  <FormControlLabel
                    className="checkbox pr-3"
                    control={<Checkbox checked={isUndeliverableEmail()} onClick={handleUndeliverableEmailCheck} />}
                    label={$t('undeliverable')}
                  />
                </MenuItem>
              </Menu>
            </Grid>
            <Grid item {...gridItemProps}>
              <FormField type="multilineText" name="message" label={$t('message_alert_for_operator')} />
            </Grid>
            <Grid item {...gridItemProps}>
              <FormField type="phone" name="homePhone" label={$t('home_phone')} validate={validatePhoneNumber} />
            </Grid>
            <Grid item {...gridItemProps}>
              <FormField type="phone" name="workPhone" label={$t('work_phone')} validate={validatePhoneNumber} />
            </Grid>
            <Grid item {...gridItemProps}>
              <FormField type="phone" name="fax" label={$t('fax2')} />
            </Grid>
            <Grid item {...gridItemProps}>
              <FormField type="text" name="abn" label={$t('business_number_abn')} validate={validateABN} />
            </Grid>
            {!isCompany ? (
              <>
                <Grid item {...gridItemProps}>
                  <FormField
                    type="date"
                    name="birthDate"
                    label={$t('date_of_birth')}
                    disabled={usiLocked}
                    validate={validateBirthDate}
                  />
                </Grid>
                <Grid item {...gridItemProps}>
                  <FormField
                    type="select"
                    name="gender"
                    label={$t('gender')}
                    items={contactGenderItems}
                    placeholder={$t('not_stated')}
                    allowEmpty
                  />
                </Grid>
                <Grid item {...gridItemProps}>
                  <FormField type="text" name="honorific" label={$t('honorific')} />
                </Grid>
              </>
            ) : null}

            {!noCustomFields && (
              <CustomFields
                entityName="Contact"
                fieldName="customFields"
                entityValues={values}
                form={form}
                gridItemProps={gridItemProps}
              />
            )}

            {values.student && !noCustomFields && (
              <CustomFields
                entityName="Student"
                fieldName="student.customFields"
                entityValues={values}
                form={form}
                gridItemProps={gridItemProps}
              />
            )}

            {values.student && (
              <>
                <Grid item {...gridItemProps} className="mb-2">
                  <Alert severity="info">
                    {values.student.waitingLists && values.student.waitingLists.length !== 0 ? (
                      <Typography className="centeredFlex" variant="body1">
                        {`Student is on waiting list for: ${values.student.waitingLists.map(v => `"${v}"`).join(", ")}`}
                        <IconButton
                          size="small"
                          onClick={() => openInternalLink(`/waitingList?search=student.contact.id = ${values?.id}`)}
                        >
                          <OpenInNew color="primary" fontSize="inherit" />
                        </IconButton>
                      </Typography>
                    ) : (
                      <Typography display="inline" variant="body1" className="pt-2">
                        {$t('student_is_not_on_any_waiting_list')}
                      </Typography>
                    )}
                  </Alert>
                </Grid>
                <Grid item {...gridItemProps}>
                  <FormField type="multilineText" name="student.specialNeeds" label={$t('special_needs')} />
                </Grid>
                <Grid item {...gridItemProps}>
                  <FormField
                    type="select"
                    name="student.citizenship"
                    label={$t('citizenship_status')}
                    items={studentCitizenships}
                  />
                </Grid>
              </>
            )}
          </Grid>
        </ExpandableContainer>
      </Grid>

    </Grid>
  ) : null;
};

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Contact"],
  countries: state.countries,
  fullScreenEditView: state.list.fullScreenEditView
});

export default connect<any, any, any>(mapStateToProps)(ContactDetails);