/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect } from "react";
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid";
import { change } from "redux-form";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { Contact } from "@api/model";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { defaultContactName, getContactFullName, openContactLink } from "../utils";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import ContactSelectItemRenderer from "./ContactSelectItemRenderer";
import { State } from "../../../../reducers/state";
import {
 clearContacts, clearContactsSearch, getContacts, setContactsSearch
} from "../actions";

const RelationsHeaderBase: React.FunctionComponent<any> = React.memo((props: any) => {
  const { row, relationTypes } = props;
  const { relationId, relatedContactName } = row;
  const isReverseRelation = String(relationId).includes("r");
  const filteredRelations = Array.isArray(relationTypes) ? relationTypes.filter(r => r.isReverseRelation === isReverseRelation) : [];
  const relation = filteredRelations.find(r => r.value === String(parseInt(relationId, 10)));
  const relationName = relation && relation.label;

  return (
    <div className="w-100 d-grid gridTemplateColumns-1fr">
      <Typography variant="subtitle2" noWrap>
        {relationName && relatedContactName ? `${relationName} of ${defaultContactName(relatedContactName)}` : ""}
      </Typography>
    </div>
  );
});

export const RelationsHeader = RelationsHeaderBase;

export const RelationsContentBase: React.FunctionComponent<any> = React.memo((props: any) => {
  const {
    row,
    classes,
    relationTypes,
    item,
    contacts,
    contactsLoading,
    contactsRowsCount,
    getContacts,
    setContactsSearch,
    clearContactsSearch,
    form,
    dispatch,
    contactFullName,
    clearContacts
  } = props;

  const { relationId } = row;

  const isReverseRelation = String(relationId).includes("r");

  useEffect(() => {
    setContactsSearch("");
    return () => clearContactsSearch();
  }, []);

  const getUniqueRelationTypeItems = useCallback(() => {
    let uniqueLabels = [];
    const sameNameLabels = [];

    relationTypes.forEach(r => {
      if (!uniqueLabels.includes(r.label)) {
        uniqueLabels.push(r.label);
      } else {
        sameNameLabels.push(r.label);
      }
    });

    uniqueLabels = uniqueLabels.filter(l => !sameNameLabels.includes(l));

    return relationTypes
      .map(r => {
        const isSameNameLabel = sameNameLabels.includes(r.label);

        if (isSameNameLabel) {
          if (r.isReverseRelation === isReverseRelation) {
            return undefined;
          }
        }

        return {
          label: r.label,
          value: r.isReverseRelation ? String(r.value) : r.value + "r"
        };
      })
      .filter(r => !!r);
  }, [relationTypes, row, row.relationId]);

  const onRelatedContactChange = useCallback(
    (contact: Contact) => {
      dispatch(change(form, `${item}.relatedContactName`, getContactFullName(contact)));
    },
    [form]
  );

  return (
    <Grid container>
      <Grid item xs={12} className={classes.select1}>
        <FormField
          type="remoteDataSearchSelect"
          name={`${item}.relatedContactId`}
          label="Related contact"
          selectValueMark="id"
          selectLabelCondition={getContactFullName}
          defaultDisplayValue={defaultContactName(row.relatedContactName)}
          onInnerValueChange={onRelatedContactChange}
          labelAdornment={
            <LinkAdornment linkHandler={openContactLink} link={row.relatedContactId} disabled={!row.relatedContactId} />
          }
          items={contacts || []}
          onSearchChange={setContactsSearch}
          onLoadMoreRows={getContacts}
          onClearRows={clearContacts}
          loading={contactsLoading}
          remoteRowCount={contactsRowsCount}
          itemRenderer={ContactSelectItemRenderer}
          rowHeight={55}
          required
        />
      </Grid>

      <Grid item xs={12}>
        <FormField
          type="select"
          name={`${item}.relationId`}
          label="Contact above is"
          items={getUniqueRelationTypeItems()}
          required
        />
      </Grid>
      <Grid item xs={12} className="mb-2">
        <Typography variant="caption" color="textSecondary">{`of ${contactFullName}`}</Typography>
      </Grid>
    </Grid>
  );
});

const mapStateToProps = (state: State) => ({
  contacts: state.contacts.items,
  contactsLoading: state.contacts.loading,
  contactsRowsCount: state.contacts.rowsCount
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    getContacts: (offset?: number) => dispatch(getContacts(offset, null, true)),
    clearContacts: () => dispatch(clearContacts()),
    setContactsSearch: (search: string) => dispatch(setContactsSearch(search)),
    clearContactsSearch: () => dispatch(clearContactsSearch())
  });

export const RelationsContent = connect<any, any, any>(mapStateToProps, mapDispatchToProps)(RelationsContentBase);
