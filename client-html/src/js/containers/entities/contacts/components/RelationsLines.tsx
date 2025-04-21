/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Contact } from '@api/model';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import $t from '@t';
import React, { useCallback } from 'react';
import { change } from 'redux-form';
import { ContactLinkAdornment } from '../../../../common/components/form/formFields/FieldAdornments';
import FormField from '../../../../common/components/form/formFields/FormField';
import { getContactFullName } from '../utils';
import ContactSelectItemRenderer from './ContactSelectItemRenderer';

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
        {relationName && relatedContactName ? `${relationName} of ${relatedContactName}` : ""}
      </Typography>
    </div>
  );
});

export const RelationsHeader = RelationsHeaderBase;

export const RelationsContent: React.FunctionComponent<any> = React.memo((props: any) => {
  const {
    row,
    classes,
    relationTypes,
    item,
    form,
    dispatch,
    contactFullName
  } = props;

  const { relationId } = row;

  const isReverseRelation = String(relationId).includes("r");

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

  // @ts-ignore
  return (
    <Grid container columnSpacing={3} rowSpacing={2}>
      <Grid item xs={12} className={classes.select1}>
        <FormField
          type="remoteDataSelect"
          entity="Contact"
          name={`${item}.relatedContactId`}
          label={$t('related_contact')}
          selectValueMark="id"
          selectLabelCondition={getContactFullName}
          defaultValue={row.relatedContactName}
          onInnerValueChange={onRelatedContactChange}
          labelAdornment={
            <ContactLinkAdornment id={row?.relatedContactId} />
          }
          itemRenderer={ContactSelectItemRenderer}
          rowHeight={55}
          required
        />
      </Grid>

      <Grid item xs={12}>
        <FormField
          type="select"
          name={`${item}.relationId`}
          label={$t('contact_above_is')}
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
