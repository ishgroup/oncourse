/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import clsx from "clsx";
import { change } from "redux-form";
import { FormControlLabel, Theme } from "@mui/material";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { ConcessionType, Discount, DiscountMembership } from "@api/model";
import Collapse from "@mui/material/Collapse";
import { createStyles, withStyles } from "@mui/styles";
import FormField from "../../../../common/components/form/formFields/FormField";
import Subtitle from "../../../../common/components/layout/Subtitle";
import { CheckboxField } from "../../../../common/components/form/formFields/CheckboxField";
import NestedList, {
  NestedListItem,
  NestedListPanelItem
} from "../../../../common/components/form/nestedList/NestedList";
import { State } from "../../../../reducers/state";
import { PanelItemChangedMessage } from "../../../../common/components/form/nestedList/components/PaperListRenderer";
import { greaterThanZeroIncludeValidation, validateSingleMandatoryField } from "../../../../common/utils/validation";
import { normalizeNumber } from "../../../../common/utils/numbers/numbersNormalizing";
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from "../../../../common/actions/CommonPlainRecordsActions";
import { PLAIN_LIST_MAX_PAGE_SIZE } from "../../../../constants/Config";

interface DiscountStudentsProps {
  classes: any;
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

const styles = createStyles(({ spacing }: Theme) => ({
  studentsAttributes: {
    gridColumnGap: spacing(2)
  }
}));

const validatePostcode = value => (value && value.length > 500 ? "Up to 500 chars" : undefined);

class DiscountStudents extends React.PureComponent<DiscountStudentsProps, DiscountStudentsState> {
  constructor(props) {
    super(props);
    const limited = this.isLimitedForCertainStudents(this.props.values);
    this.state = { limited };
  }

  componentDidUpdate(prevProps): void {
    const limited = this.isLimitedForCertainStudents(this.props.values);
    if (limited !== this.isLimitedForCertainStudents(prevProps.values)) {
      this.setState({ ...this.state, limited });
    }
  }

  isLimitedForCertainStudents = (values: Discount): boolean => !!(
      values
      && (values.studentAge
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

  render() {
    const {
      classes,
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
          <Subtitle label="STUDENTS" />
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
              meta={{ error: null, invalid: false, touched: false }}
              color="secondary"
              fullWidth
            />
          )}
          label="Restrict this discount to certain students"
        />
        <Collapse in={this.state.limited}>
          <FormField
            type="number"
            name="studentEnrolledWithinDays"
            label="Enrolled within (days)"
            parse={normalizeNumber}
            validate={greaterThanZeroIncludeValidation}
            className="mb-2"
            debounced={false}
          />
          <div className={clsx("d-grid justify-content-start gridAutoFlow-column", classes.studentsAttributes)}>
            <FormField
              type="select"
              name="studentAgeUnder"
              onChange={this.onChangeStudentAgeUnder}
              label="Age"
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
              className="mb-2"
              allowEmpty
            />
            {typeof values.studentAgeUnder === "boolean" && (
              <FormField
                type="number"
                name="studentAge"
                label="(years)"
                parse={normalizeNumber}
                validate={[validateSingleMandatoryField, greaterThanZeroIncludeValidation]}
                className="mb-2"
                debounced={false}
              />
            )}
          </div>

          <FormField
            type="text"
            name="studentPostcode"
            label="Postcode"
            validate={validatePostcode}
          />

          <FormControlLabel
            className="checkbox pr-3 mb-2"
            control={<FormField type="checkbox" name="limitPreviousEnrolment" color="secondary" fullWidth />}
            label="Limit to students previously enrolled in same course"
          />

          <div className={twoColumn ? "mb-2 mw-400" : "mb-2"}>
            <NestedList
              formId={values.id}
              title="LIMIT TO STUDENTS WITH CONCESSION"
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
              title="LIMIT TO STUDENTS WITH MEMBERSHIP"
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

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(DiscountStudents));
