/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Site } from '@api/model';
import { Grid } from '@mui/material';
import $t from '@t';
import * as React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { change } from 'redux-form';
import {
  clearCommonPlainRecords,
  getCommonPlainRecords,
  setCommonPlainSearch
} from '../../../../common/actions/CommonPlainRecordsActions';
import NestedList, { NestedListItem } from '../../../../common/components/form/nestedList/NestedList';
import { PLAIN_LIST_MAX_PAGE_SIZE } from '../../../../constants/Config';
import { State } from '../../../../reducers/state';

class WaitingListSites extends React.PureComponent<any, any> {
  sitesToNestedListItems = (sites: Site[]) =>
    sites.map(site => ({
        id: site.id.toString(),
        entityId: site.id,
        primaryText: site.name,
        secondaryText: `${site.suburb ? site.suburb + ", " : ""} ${site.postcode ? site.postcode : ""}`,
        link: `/site/${site.id}`,
        active: true
      }));

  onAddSite = (items: NestedListItem[]): void => {
    const {
 dispatch, form, values, foundQuickSearchSites
} = this.props;
    const updated = (values.sites ? values.sites : []).concat(
      items.map(item => foundQuickSearchSites.find(site => site.id === item.entityId))
    );
    dispatch(change(form, "sites", updated));
  };

  onDeleteSite = (item: NestedListItem): void => {
    const { dispatch, form, values } = this.props;
    dispatch(
      change(
        form,
        "sites",
        values.sites.filter(site => site.id !== item.entityId)
      )
    );
  };

  onDeleteAllSites = (): void => {
    const { dispatch, form } = this.props;
    dispatch(change(form, "sites", []));
  };

  render() {
    const {
      foundQuickSearchSites,
      pendingQuickSearchSites,
      getQuickSearchSites,
      submitSucceeded,
      twoColumn,
      values,
      clearSites,
      errorQuickSearchSites
    } = this.props;

    return (
      <Grid container columnSpacing={3} className="pl-3 pr-3 pb-2">
        <Grid item xs={twoColumn ? 6 : 12}>
          <NestedList
            formId={values && values.id}
            title={$t('sites')}
            searchPlaceholder="Find sites"
            values={values && values.sites ? this.sitesToNestedListItems(values.sites) : []}
            searchValues={foundQuickSearchSites ? this.sitesToNestedListItems(foundQuickSearchSites) : []}
            pending={pendingQuickSearchSites}
            onAdd={this.onAddSite}
            onDelete={this.onDeleteSite}
            onDeleteAll={this.onDeleteAllSites}
            onSearch={getQuickSearchSites}
            clearSearchResult={clearSites}
            sort={(a, b) =>
              (a.name.toLowerCase() > b.name.toLowerCase() ? 1 : a.name.toLowerCase() < b.name.toLowerCase() ? -1 : 0)}
            resetSearch={submitSucceeded}
            aqlEntities={["Site"]}
            aqlQueryError={errorQuickSearchSites}
            usePaper
          />
        </Grid>
      </Grid>
    );
  }
}

const mapStateToProps = (state: State) => ({
  foundQuickSearchSites: state.plainSearchRecords["Site"].items,
  pendingQuickSearchSites: state.plainSearchRecords["Site"].loading,
  errorQuickSearchSites: state.plainSearchRecords["Site"].error
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getQuickSearchSites: (search: string) => {
    dispatch(setCommonPlainSearch("Site", search));
    dispatch(getCommonPlainRecords("Site", 0, "name,suburb,postcode", null, null, PLAIN_LIST_MAX_PAGE_SIZE));
  },
  clearSites: () => dispatch(clearCommonPlainRecords("Site"))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(WaitingListSites);
