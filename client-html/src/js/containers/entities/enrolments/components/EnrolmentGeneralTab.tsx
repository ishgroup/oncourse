/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { change, FieldArray } from "redux-form";
import Grid from "@material-ui/core/Grid";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import FormGroup from "@material-ui/core/FormGroup";
import Divider from "@material-ui/core/Divider";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import {
  AssessmentClass,
  ClassFundingSource,
  Enrolment,
  EnrolmentExemptionType,
  EnrolmentStudyReason,
  FundingSource, GradingType,
  PaymentSource,
  Tag
} from "@api/model";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../reducers/state";
import { validateTagsList } from "../../../../common/components/form/simpleTagListComponent/validateTagsList";
import { formatFundingSourceId } from "../../common/utils";
import { contactLabelCondition, defaultContactName, openContactLink } from "../../contacts/utils";
import {
  validateAssociatedCourseIdentifier,
  validateCharacter,
  validateOutcomeIdTrainingOrg,
  validateVetClientID,
  validateVetFundingSourceState,
  validateVetPurchasingContractIdentifier,
  validateVetTrainingContractID
} from "../../../../common/utils/validation";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import AppBarHelpMenu from "../../../../common/components/form/AppBarHelpMenu";
import { setSelectedContact } from "../../invoices/actions";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import { mapSelectItems } from "../../../../common/utils/common";
import { EditViewProps } from "../../../../model/common/ListView";
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import NestedEntity from "../../../../common/components/form/nestedEntity/NestedEntity";
import Uneditable from "../../../../common/components/form/Uneditable";
import EnrolmentSubmissions from "./EnrolmentSubmissions";
import FormSubmitButton from "../../../../common/components/form/FormSubmitButton";

const validateCricosConfirmation = value => validateCharacter(value, 32, "Confirmation of Enrolment");

const paymentSourceItems = Object.keys(PaymentSource).map(mapSelectItems);

const enrolmentStudyReasonItems = Object.keys(EnrolmentStudyReason).map(mapSelectItems);

const enrolmentExemptionTypeItems = Object.keys(EnrolmentExemptionType).map(mapSelectItems);

const fundingSourceValues = Object.keys(ClassFundingSource).map(mapSelectItems);

interface Props extends Partial<EditViewProps> {
  values?: Enrolment;
  contracts?: FundingSource[];
  tags?: Tag[];
  setSelectedContact?: AnyArgFunction;
  gradingTypes?: GradingType[];
}

const EnrolmentGeneralTab: React.FC<Props> = props => {
  const {
    isNew,
    form,
    showConfirm,
    tags,
    twoColumn,
    values,
    dispatch,
    setSelectedContact,
    contracts,
    manualLink,
    rootEntity,
    onCloseClick,
    invalid,
    dirty,
    gradingTypes
  } = props;

  const onContactChange = useCallback(
    value => {
      setSelectedContact(value);

      dispatch(change(form, "studentName", contactLabelCondition(value)));
    },
    [form]
  );

  const validateAssesments = useCallback((value: AssessmentClass[], allValues: Enrolment) => {
    let error;

    if (Array.isArray(value) && value.length) {
      value.forEach(a => {
        const gradeType: GradingType = gradingTypes?.find(g => g.id === a.gradingTypeId);
        const submission = allValues.submissions.find(s => s.assessmentId === a.id);

        if (gradeType
          && submission
          && typeof submission.grade === "number"
          && (submission.grade > gradeType.maxValue || submission.grade < gradeType.minValue)) {
          error = "Some assessments grades are invalid";
        }
      });
    }
    return error;
  }, [gradingTypes]);

  const validateTagList = useCallback((value, allValues, props) => validateTagsList(tags, value, allValues, props), []);

  const invoiceTypes = useMemo(
    () => (values.id
        ? [
            {
              name: "Invoices",
              count: values.invoicesCount,
              link: `/invoice?search=invoiceLines.enrolment.id=${values.id}`
            }
          ]
        : []),
    [values.invoicesCount, values.id]
  );

  const outcomeTypes = useMemo(
    () => (values.id
        ? [
            {
              name: "Outcomes",
              count: values.outcomesCount,
              link: `/outcome?search=enrolment.id=${values.id}`
            }
          ]
        : []),
    [values.outcomesCount, values.id]
  );

  const outcomesAddLink = useMemo(() => `/outcome/new?search=enrolment.id=${values.id}`, [values.id]);

  return (
    <>
      {twoColumn && (
        <CustomAppBar noDrawer>
          <div className="flex-fill">
            <Typography className="appHeaderFontSize" color="inherit">
              {defaultContactName(values.studentName)}
            </Typography>
          </div>
          <div>
            {manualLink && (
              <AppBarHelpMenu
                created={values ? new Date(values.createdOn) : null}
                modified={values ? new Date(values.modifiedOn) : null}
                auditsUrl={`audit?search=~"${rootEntity}" and entityId in (${values ? values.id : 0})`}
                manualUrl={manualLink}
              />
            )}

            <Button onClick={onCloseClick} className="closeAppBarButton">
              Close
            </Button>
            <FormSubmitButton
              disabled={(!isNew && !dirty)}
              invalid={invalid}
            />
          </div>
        </CustomAppBar>
      )}
      <Grid container className="pt-3 pl-3 pr-3">
        <Grid item xs={12}>
          <FormField
            type="tags"
            name="tags"
            tags={tags}
            validate={tags && tags.length ? validateTagList : undefined}
          />
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField
            type="remoteDataSearchSelect"
            entity="Contact"
            aqlFilter="isStudent is true"
            name="studentContactId"
            label="Student"
            selectValueMark="id"
            selectLabelCondition={contactLabelCondition}
            defaultDisplayValue={values && defaultContactName(values.studentName)}
            labelAdornment={(
              <LinkAdornment
                linkHandler={openContactLink}
                link={values.studentContactId}
                disabled={!values.studentContactId}
              />
            )}
            onInnerValueChange={onContactChange}
            itemRenderer={ContactSelectItemRenderer}
            disabled={!isNew}
            rowHeight={55}
            required
          />
        </Grid>

        <Grid item xs={twoColumn ? 8 : 12}>
          <Uneditable
            value={values && values.courseClassName}
            label="Class"
            url={`/class/${values.courseClassId}`}
          />
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField type="text" name="displayStatus" label="Status" disabled />
        </Grid>

        <Grid item xs={twoColumn ? 8 : 12}>
          <FormField
            type="select"
            name="source"
            label="Source"
            items={paymentSourceItems}
            disabled={!isNew}
          />
        </Grid>

        <Grid item xs={12} className="pt-2 pb-3">
          <Divider />
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12}>
          {contracts && (
            <FormField
              type="select"
              selectValueMark="id"
              selectLabelMark="name"
              name="relatedFundingSourceId"
              label="Funding contract"
              items={contracts}
              format={formatFundingSourceId}
            />
          )}
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField
            type="select"
            name="studyReason"
            label="Study reason"
            items={enrolmentStudyReasonItems}
          />
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField
            type="select"
            name="vetFeeExemptionType"
            label="Fee exemption/concession type"
            items={enrolmentExemptionTypeItems}
          />
        </Grid>

        <Grid item xs={twoColumn ? 4 : false}>
          <FormField
            type="select"
            name="fundingSource"
            label="Default funding source national"
            items={fundingSourceValues}
          />
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField
            type="text"
            name="vetFundingSourceStateID"
            label="Default funding source - State"
            validate={validateVetFundingSourceState}
            fullWidth
          />
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12}>
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="vetIsFullTime" />}
            label="Full time flag (QLD)"
          />
        </Grid>

        <Grid item xs={12} className="pt-2 pb-3">
          <Divider />
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12}>
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="vetInSchools" />}
            label="VET in schools enrolment"
          />
        </Grid>
        <Grid item xs={twoColumn ? 8 : 12} className="mb-2">
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="suppressAvetmissExport" />}
            label="Do not report for AVETMISS"
          />
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField
            type="text"
            name="associatedCourseIdentifier"
            label="Associated Course Identifier - (SA - SACE Student ID)"
            validate={validateAssociatedCourseIdentifier}
            fullWidth
          />
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField
            type="text"
            name="vetPurchasingContractID"
            label="Default purchasing contract identifier (NSW Commitment ID)"
            validate={validateVetPurchasingContractIdentifier}
            fullWidth
          />
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField
            type="text"
            name="outcomeIdTrainingOrg"
            label="Outcome identifier Training Organisation"
            validate={validateOutcomeIdTrainingOrg}
            fullWidth
          />
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField
            type="text"
            name="vetClientID"
            label="Client identifier: apprenticeships"
            validate={validateVetClientID}
            fullWidth
          />
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12}>
          <FormField
            type="text"
            name="vetTrainingContractID"
            label="Training contract: apprenticeships"
            validate={validateVetTrainingContractID}
            fullWidth
          />
        </Grid>

        <Grid item xs={twoColumn ? 4 : 12} className="mb-2">
          <FormField
            type="text"
            name="cricosConfirmation"
            label="CRICOS: Confirmation of Enrolment (CoE)"
            validate={validateCricosConfirmation}
            fullWidth
          />
        </Grid>

        <FormGroup className="mb-2">
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="eligibilityExemptionIndicator" />}
            label="Eligibility exemption  inidicator (Vic)"
          />
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="vetFeeIndicator" />}
            label="VET FEE HELP Indicator (Vic)"
          />
          <FormControlLabel
            className="checkbox"
            control={<FormField type="checkbox" name="trainingPlanDeveloped" />}
            label="Training plan developed (NSW)"
          />
        </FormGroup>

        {!isNew && (
          <>
            <Grid item xs={12} className="pt-2 pb-3">
              <Divider />
            </Grid>
            <Grid item xs={12}>
              <NestedEntity
                entityTypes={invoiceTypes}
                dirty={dirty}
                showConfirm={showConfirm}
                twoColumn={twoColumn}
                isNew={isNew}
              />
            </Grid>
            <Grid item xs={12}>
              <NestedEntity
                entityTypes={outcomeTypes}
                dirty={dirty}
                showConfirm={showConfirm}
                twoColumn={twoColumn}
                isNew={isNew}
                addLink={outcomesAddLink}
              />
            </Grid>
            <Grid item xs={12} className="pb-3">
              <Divider />
            </Grid>
          </>
        )}

        <Grid container>
          <Grid item xs={12} className="mb-2">
            <CustomFields
              entityName="Enrolment"
              fieldName="customFields"
              entityValues={values}
              dispatch={dispatch}
              form={form}
            />
          </Grid>
        </Grid>

        <FieldArray
          name="assessments"
          component={EnrolmentSubmissions}
          values={values as any}
          gradingTypes={gradingTypes}
          dispatch={dispatch}
          validate={validateAssesments}
        />
      </Grid>
    </>
  );
};

const mapStateToProps = (state: State) => ({
  tags: state.tags.entityTags["Enrolment"],
  contracts: state.export.contracts,
  gradingTypes: state.preferences.gradingTypes
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setSelectedContact: (selectedContact: any) => dispatch(setSelectedContact(selectedContact)),
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(EnrolmentGeneralTab);
