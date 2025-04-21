/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ConcessionType, Discount, DiscountMembership } from '@api/model';
import { Collapse, FormControlLabel, Grid } from '@mui/material';
import $t from '@t';
import clsx from 'clsx';
import { CheckboxField, LinkAdornment, normalizeNumber } from 'ish-ui';
import * as React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { change } from 'redux-form';
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from '../../../../common/actions/CommonPlainRecordsActions';
import FormField from '../../../../common/components/form/formFields/FormField';
import { PanelItemChangedMessage } from '../../../../common/components/form/nestedList/components/PaperListRenderer';
import NestedList, {
  NestedListItem,
  NestedListPanelItem
} from '../../../../common/components/form/nestedList/NestedList';
import Subtitle from '../../../../common/components/layout/Subtitle';
import { greaterThanZeroIncludeValidation, validateSingleMandatoryField } from '../../../../common/utils/validation';
import { PLAIN_LIST_MAX_PAGE_SIZE } from '../../../../constants/Config';
import { State } from '../../../../reducers/state';
import CourseItemRenderer from '../../courses/components/CourseItemRenderer';
import { courseFilterCondition, openCourseLink } from '../../courses/utils';

interface DiscountStudentsProps {
  twoColumn?: boolean;
  values?: Discount;
  dispatch: Dispatch<any>;
  form: string;
  // concession types
  clearConcessionTypeSearch: (pending: boolean) => void;
  searchConcessionTypes: (search: string) => void;
  foundConcessionTypes: ConcessionType[];
  concessionTypePending: boolean;
  concessionTypeError?: boolean;
  membershipError?: boolean;
  // memberships
  clearMembershipSearch: (pending: boolean) => void;
  searchMemberships: (search: string) => void;
  foundMemberships: any[];
  membershipPending: boolean;
  submitSucceeded?: boolean;
  contactRelationTypes?: DiscountContactRelationType[];
}

interface DiscountStudentsState {
  limited?: boolean;
}

interface DiscountContactRelationType extends NestedListPanelItem {
}

const concessionTypesField = "discountConcessionTypes";

const plainMembershipToNestedListItem = (items: any[]): NestedListItem[] => (items && items.length > 0
  ? items.map(membership => ({
    id: membership.id.toString(),
    entityId: Number(membership.id),
    primaryText: membership.name,
    secondaryText: `(${membership.sku})`,
    link: `/membership/${membership.id}`,
    panelItemIds: JSON.parse(membership["discountMemberships.discountMembershipRelationTypes.contactRelationType.id"])?.flatMap(m => m),
    active: true
  }))
  : []);

const membershipToNestedListItem = (items: DiscountMembership[]): NestedListItem[] => (items && items.length > 0
  ? items.map(membership => ({
    id: membership.productId.toString(),
    entityId: Number(membership.productId),
    primaryText: membership.productName,
    secondaryText: `(${membership.productSku})`,
    link: `/membership/${membership.productId}`,
    panelItemIds: membership.contactRelations,
    active: true
  }))
  : []);

const concessionsToNestedListItems = (items: ConcessionType[]): NestedListItem[] => (items && items.length > 0
    ? items.map(concessionType => ({
        id: concessionType.id.toString(),
        entityId: concessionType.id,
        primaryText: concessionType.name,
        secondaryText: concessionType.allowOnWeb ? "Available on website" : "",
        link: `/preferences/concessionTypes`,
        active: true
      }))
    : []);

const validatePostcode = value => (value && value.length > 500 ? "Up to 500 chars" : undefined);

class DiscountStudents extends React.PureComponent<DiscountStudentsProps, DiscountStudentsState> {
  constructor(props) {
    super(props);
    const limited = this.isLimitedForCertainStudents(this.props.values);
    this.state = { limited };
  }

  componentDidUpdate(prevProps): void {
    const limited = this.isLimitedForCertainStudents(this.props.values);
    if (prevProps.values.id !== this.props.values.id && limited !== this.isLimitedForCertainStudents(prevProps.values)) {
      this.setState({ ...this.state, limited });
    }
  }

  isLimitedForCertainStudents = (values: Discount): boolean => !!(
      values
      && (values.studentAge
        || values.courseIdMustEnrol
        || values.minEnrolmentsForAnyCourses
        || values.studentPostcode
        || values.limitPreviousEnrolment
        || values.studentEnrolledWithinDays
        || (values.discountMemberships && values.discountMemberships.length > 0)
        || (values.discountConcessionTypes && values.discountConcessionTypes.length > 0))
    );

  onAddMemberships = (items: NestedListItem[]) => {
    const {
      values, dispatch, form, foundMemberships
    } = this.props;

    const toAdd = items.map(item => {
      const membership = foundMemberships.find(m => m.id === item.entityId);

      return {
        productId: item.entityId,
        productName: membership.name,
        productSku: membership.sku,
        contactRelations: item.panelItemIds
      };
    });

    if (toAdd) {
      dispatch(change(form, "discountMemberships", values.discountMemberships.concat(toAdd)));
    }
  };

  onDeleteAllMemberships = () => {
    const { dispatch, form } = this.props;
    dispatch(change(form, "discountMemberships", []));
  };

  onDeleteMembership = (item: NestedListItem) => {
    const { values, dispatch, form } = this.props;
    const memberships = values.discountMemberships.filter(ct => item.entityId !== ct.productId);
    dispatch(change(form, "discountMemberships", memberships));
  };

  onAddConcessionTypes = (items: NestedListItem[]) => {
    const {
      values, dispatch, form, foundConcessionTypes
    } = this.props;
    const concessionTypes = values.discountConcessionTypes.concat(
      items.map(item => foundConcessionTypes.find(ct => item.id === ct.id.toString()))
    );
    dispatch(change(form, concessionTypesField, concessionTypes));
  };

  onDeleteAllConcessionTypes = () => {
    const { dispatch, form } = this.props;
    dispatch(change(form, concessionTypesField, []));
  };

  onDeleteConcessionType = (item: NestedListItem) => {
    const { values, dispatch, form } = this.props;
    const concessionTypes = values.discountConcessionTypes.filter(ct => item.id !== ct.id.toString());
    dispatch(change(form, concessionTypesField, concessionTypes));
  };

  onChangeCertainStudents = e => {
    const { dispatch, form } = this.props;
    if (e === false) {
      dispatch(change(form, "discountConcessionTypes", []));
      dispatch(change(form, "discountMemberships", []));
      dispatch(change(form, "studentAgeUnder", null));
      dispatch(change(form, "studentAge", null));
      dispatch(change(form, "studentEnrolledWithinDays", null));
      dispatch(change(form, "studentPostcode", null));
      dispatch(change(form, "limitPreviousEnrolment", false));

      this.setState({ ...this.state, limited: e });
    } else {
      this.setState({ ...this.state, limited: e });
    }
  };

  onChangeStudentAgeUnder = e => {
    const { dispatch, form } = this.props;
    if (e === null) {
      dispatch(change(form, "studentAge", null));
      this.setState({ ...this.state });
    } else {
      this.setState({ ...this.state });
    }
  };

  onChangePanelItem = (message: PanelItemChangedMessage) => {
    const { values, dispatch, form } = this.props;
    const membershipId = values.discountMemberships.findIndex(
      membership => membership.productId === message.item.entityId
    );
    const contactRelations = values.discountMemberships[membershipId].contactRelations
      ? values.discountMemberships[membershipId].contactRelations
      : [];
    dispatch(
      change(
        form,
        `discountMemberships[${membershipId}].contactRelations`,
        message.checked
          ? [...contactRelations, message.panelItemId].sort()
          : contactRelations.filter(e => e !== message.panelItemId).sort()
      )
    );
  };

  onCouresMastEnrolChange = course => {
    const { dispatch, form } = this.props;
    dispatch(change(form, "courseNameMustEnrol", course ? course.name : null));
  };

  render() {
    const {
      values,
      clearConcessionTypeSearch,
      searchConcessionTypes,
      foundConcessionTypes,
      clearMembershipSearch,
      searchMemberships,
      concessionTypePending,
      concessionTypeError,
      foundMemberships,
      membershipError,
      membershipPending,
      submitSucceeded,
      twoColumn,
      contactRelationTypes
    } = this.props;

    const concessionTypes = values && values.discountConcessionTypes ? concessionsToNestedListItems(values.discountConcessionTypes) : [];

    return (
      <div className="pt-3 pl-3 pr-3">
        <div className="mb-2">
          <Subtitle label={$t('students2')} />
        </div>
        <FormControlLabel
          className="checkbox pr-3 mb-2"
          control={(
            <CheckboxField
              input={{
                name: "restrictStudents",
                value: this.state.limited ? this.state.limited : undefined,
                onChange: this.onChangeCertainStudents
              }}
              color="secondary"
            />
          )}
          label={$t('restrict_this_discount_to_certain_students')}
        />
        <Collapse in={this.state.limited}>
          <Grid container rowSpacing={2} columnSpacing={3}>
            <Grid item xs={twoColumn ? 6 : 12}>
              <FormField
                type="number"
                name="studentEnrolledWithinDays"
                label={$t('enrolled_within_days')}
                parse={normalizeNumber}
                validate={greaterThanZeroIncludeValidation}
                debounced={false}
              />
            </Grid>
            <Grid item xs={twoColumn ? 3 : 12}>
              <FormField
                type="select"
                name="studentAgeUnder"
                onChange={this.onChangeStudentAgeUnder}
                label={$t('age')}
                items={[
                  {
                    value: true,
                    label: "Under"
                  },
                  {
                    value: false,
                    label: "Over"
                  }
                ]}
                allowEmpty
              />
            </Grid>
            {typeof values.studentAgeUnder === "boolean" && (
              <Grid item xs={twoColumn ? 3 : 12}>
                <FormField
                  type="number"
                  name="studentAge"
                  label={$t('years')}
                  parse={normalizeNumber}
                  validate={[validateSingleMandatoryField, greaterThanZeroIncludeValidation]}
                  debounced={false}
                />
              </Grid>
            )}
            <Grid item xs={twoColumn ? 6 : 12}>
              <FormField
                type="text"
                name="studentPostcode"
                label={$t('postcode')}
                validate={validatePostcode}
              />
            </Grid>
            <Grid item xs={twoColumn ? 6 : 12}>
              <FormControlLabel
                className="checkbox"
                control={<FormField type="checkbox" name="limitPreviousEnrolment" color="secondary" />}
                label={$t('limit_to_students_previously_enrolled_in_same_cour')}
              />
            </Grid>
            <Grid item xs={twoColumn ? 6 : 12}>
              <FormField
                type="remoteDataSelect"
                entity="Course"
                aqlFilter="currentlyOffered is true"
                name="courseIdMustEnrol"
                label={$t('must_have_completed_enrolment_in')}
                selectValueMark="id"
                selectLabelMark="name"
                selectFilterCondition={courseFilterCondition}
                selectLabelCondition={courseFilterCondition}
                defaultValue={values?.courseNameMustEnrol}
                labelAdornment={(
                  <LinkAdornment
                    linkHandler={openCourseLink}
                    link={values && values.courseIdMustEnrol}
                    disabled={!values || !values.courseIdMustEnrol}
                  />
                )}
                itemRenderer={CourseItemRenderer}
                onInnerValueChange={this.onCouresMastEnrolChange}
                rowHeight={55}
                allowEmpty
              />
            </Grid>
            <Grid item xs={twoColumn ? 6 : 12}>
              <FormField
                type="number"
                name="minEnrolmentsForAnyCourses"
                label={$t('minimal_completed_enrolments_in_any_course')}
                parse={normalizeNumber}
                validate={greaterThanZeroIncludeValidation}
                debounced={false}
              />
            </Grid>
          </Grid>
          <div className={clsx("mb-2 mt-2", twoColumn && "mw-400")}>
            <NestedList
              formId={values.id}
              title={$t('limit_to_students_with_concession')}
              titleCaption="This discount will only be available with the following concession types"
              searchPlaceholder="Find concession types"
              values={concessionTypes}
              searchValues={concessionsToNestedListItems(foundConcessionTypes)}
              onSearch={searchConcessionTypes}
              clearSearchResult={clearConcessionTypeSearch}
              aqlQueryError={concessionTypeError}
              pending={concessionTypePending}
              onAdd={this.onAddConcessionTypes}
              onDelete={this.onDeleteConcessionType}
              onDeleteAll={this.onDeleteAllConcessionTypes}
              sort={(a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1)}
              resetSearch={submitSucceeded}
              searchType="withToggle"
              aqlEntities={["ConcessionType"]}
              usePaper
            />
          </div>
          <div className={twoColumn ? "mb-2 mw-400" : "mb-2"}>
            <NestedList
              formId={values.id}
              title={$t('limit_to_students_with_membership')}
              titleCaption="This discount will only apply to classes commencing while one of these memberships is valid"
              searchPlaceholder="Add memberships"
              values={membershipToNestedListItem(values.discountMemberships)}
              searchValues={plainMembershipToNestedListItem(foundMemberships)}
              aqlQueryError={membershipError}
              onSearch={searchMemberships}
              clearSearchResult={clearMembershipSearch}
              pending={membershipPending}
              onAdd={this.onAddMemberships}
              onDelete={this.onDeleteMembership}
              onDeleteAll={this.onDeleteAllMemberships}
              onChangePanelItem={this.onChangePanelItem}
              sort={(a, b) => (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1)}
              resetSearch={submitSucceeded}
              inlineSecondaryText
              panelItems={contactRelationTypes}
              panelCaption="Also apply to"
              searchType="withToggle"
              aqlEntities={["Product"]}
              usePaper
            />
          </div>
        </Collapse>
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  foundConcessionTypes: state.plainSearchRecords["ConcessionType"].items,
  concessionTypePending: state.plainSearchRecords["ConcessionType"].loading,
  concessionTypeError: state.plainSearchRecords["ConcessionType"].error,
  foundMemberships: state.plainSearchRecords["MembershipProduct"].items,
  membershipPending: state.plainSearchRecords["MembershipProduct"].loading,
  membershipError: state.plainSearchRecords["MembershipProduct"].error,
  contactRelationTypes: state.discounts.contactRelationTypes
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  searchConcessionTypes: (search: string) => {
    dispatch(setCommonPlainSearch("ConcessionType", search));
    dispatch(getCommonPlainRecords("ConcessionType", 0, "name,isEnabled", null, null, PLAIN_LIST_MAX_PAGE_SIZE, item => item.map(i => ({
      id: i.id,
      name: i.name,
      allowOnWeb: i.isEnabled
    }))));
  },
  clearConcessionTypeSearch: (pending: boolean) => dispatch(clearCommonPlainRecords("ConcessionType", pending)),
  searchMemberships: (search: string) => {
    dispatch(setCommonPlainSearch("MembershipProduct", search));
    dispatch(getCommonPlainRecords("MembershipProduct", 0, "name,sku,discountMemberships.discountMembershipRelationTypes.contactRelationType.id", null, null, PLAIN_LIST_MAX_PAGE_SIZE));
  },
  clearMembershipSearch: (pending: boolean) => dispatch(clearCommonPlainRecords("MembershipProduct", pending))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(DiscountStudents);