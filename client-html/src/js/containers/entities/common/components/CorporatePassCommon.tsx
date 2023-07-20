/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import * as React from "react";
import { createStyles, withStyles } from "@mui/styles";
import { DiscountCorporatePass } from "@api/model";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { change } from "redux-form";
import clsx from "clsx";
import NestedList, { NestedListItem } from "../../../../common/components/form/nestedList/NestedList";
import { AppTheme } from "../../../../../ish-ui/model/Theme";
import { State } from "../../../../reducers/state";
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from "../../../../common/actions/CommonPlainRecordsActions";
import { PLAIN_LIST_MAX_PAGE_SIZE } from "../../../../constants/Config";

const styles = createStyles(({ spacing }: AppTheme) => ({
  marginBottomTriple: {
    marginBottom: spacing(3)
  },
  compact: {
    marginBottom: spacing(2),
    maxWidth: spacing(50)
  }
}));

const corporatePassToNestedListItem = (items: DiscountCorporatePass[]): NestedListItem[] => (items && items.length > 0
  ? items.map(corpPass => ({
    id: corpPass.id.toString(),
    entityId: corpPass.id,
    primaryText: corpPass.contactFullName,
    secondaryText: ``,
    link: `/corporatePass/${corpPass.id}`,
    active: true
  }))
  : []);

class CorporatePassCommon extends React.PureComponent<any, any> {
  onAddCorporatePasses = (items: NestedListItem[]) => {
    const {
      values, dispatch, form, foundCorporatePassItems, path
    } = this.props;
    const passes = values[path].concat(
      items.map(item => foundCorporatePassItems.find(c => item.entityId === c.id))
    );
    dispatch(change(form, path, passes));
  };

  onDeleteCorporatePass = (item: NestedListItem) => {
    const {
     values, dispatch, form, path
    } = this.props;
    const passes = values[path].filter(c => item.entityId !== c.id);
    dispatch(change(form, path, passes));
  };

  onDeleteAllCorporatePasses = () => {
    const { dispatch, form, path } = this.props;
    dispatch(change(form, path, []));
  };

  render() {
    const {
      twoColumn,
      classes,
      values,
      foundCorporatePassItems,
      searchCorporatePasses,
      clearCorporatePasses,
      pending,
      passErrors,
      title = "CORPORATE PASS",
      titleCaption,
      path,
      className
    } = this.props;

    const corporatePassItems = values && values[path] ? corporatePassToNestedListItem(values[path]) : [];

    return (
      <div className={clsx("pl-3 pr-3", className)}>
        <div className={twoColumn ? classes.compact : classes.marginBottomTriple}>
          <NestedList
            formId={values.id}
            title={title}
            titleCaption={titleCaption}
            searchPlaceholder="Add corporate pass"
            values={corporatePassItems}
            searchValues={corporatePassToNestedListItem(foundCorporatePassItems)}
            onSearch={searchCorporatePasses}
            clearSearchResult={clearCorporatePasses}
            pending={pending}
            onAdd={this.onAddCorporatePasses}
            onDelete={this.onDeleteCorporatePass}
            onDeleteAll={this.onDeleteAllCorporatePasses}
            sort={(a, b) =>
              (`${a.contact.lastName},${a.contact.firstName}` > `${b.contact.lastName},${b.contact.firstName}` ? 1 : -1)}
            searchType="withToggle"
            aqlEntities={["CorporatePass"]}
            aqlQueryError={passErrors}
            usePaper
          />
        </div>
      </div>
    );
  }
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  searchCorporatePasses: (search: string) => {
    dispatch(setCommonPlainSearch("CorporatePass", `${search ? `${search} and ` : ""}(expiryDate is null or expiryDate >= today)`));
    dispatch(getCommonPlainRecords("CorporatePass", 0, "contact.fullName", null, null, PLAIN_LIST_MAX_PAGE_SIZE, items => items.map(i => ({
      id: i.id,
      contactFullName: i["contact.fullName"]
    }))));
  },
  clearCorporatePasses: (pending: boolean) => dispatch(clearCommonPlainRecords("CorporatePass", pending))
});

const mapStateToProps = (state: State) => ({
  foundCorporatePassItems: state.plainSearchRecords["CorporatePass"].items,
  pending: state.plainSearchRecords["CorporatePass"].loading,
  passErrors: state.plainSearchRecords["CorporatePass"].error,
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(styles)(CorporatePassCommon));
