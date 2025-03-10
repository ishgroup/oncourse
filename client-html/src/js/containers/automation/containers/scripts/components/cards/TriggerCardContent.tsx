/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Script, TagRequirementType, TriggerType } from '@api/model';
import * as Entities from '@aql/queryLanguageModel';
import Help from '@mui/icons-material/Help';
import IconButton from '@mui/material/IconButton';
import $t from '@t';
import { mapSelectItems, SelectItemDefault, TagInputList } from 'ish-ui';
import React, { useEffect, useState } from 'react';
import { Dispatch } from 'redux';
import { change } from 'redux-form';
import { IAction } from '../../../../../../common/actions/IshAction';
import instantFetchErrorHandler from '../../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { useAppSelector } from '../../../../../../common/utils/hooks';
import { CatalogItemType } from '../../../../../../model/common/Catalog';
import { getEntityTags } from '../../../../../tags/actions';
import TagsService from '../../../../../tags/services/TagsService';
import { AQL_ENTITY_ITEMS } from '../../../../constants';

// Filter AbstractInvoice and include Quote
const AllEntities = [
  ...AQL_ENTITY_ITEMS, 
  { label: "ContactTagRelation", value: "ContactTagRelation" },
  { label: "Quote", value: "Quote" }
  ]
  .filter(e => e.value !== "AbstractInvoice");

const TagableEntities = Object.keys(TagRequirementType).map(mapSelectItems);

interface Props {
  TriggerTypeItems: SelectItemDefault[];
  ScheduleTypeItems: SelectItemDefault[];
  enableEntityNameField: boolean;
  values: Script;
  isInternal: boolean;
  dispatch: Dispatch<IAction>
  form: string;
  timeZone: string;
  classes: any;
  checklists: CatalogItemType[];
}

const ChecklistsTriggers: TriggerType[] = [
  'Checklist task checked',
  'Checklist completed'
];

const TagTriggers: TriggerType[] = [
  'Tag added',
  'Tag removed'
];

const TriggerCardContent = (props: Props) => {
  const {
    TriggerTypeItems, ScheduleTypeItems, enableEntityNameField, values, isInternal, dispatch, form, timeZone, checklists
  } = props;

  const [entityItems, setEntityItems] = useState(AllEntities);

  const entityTags = useAppSelector(state => state.tags.entityTags[values.trigger.entityName]);

  const onTriggerChange = (e, newType: TriggerType) => {
    if (
      (ChecklistsTriggers.includes(values.trigger.type) && !ChecklistsTriggers.includes(newType))
      || (!ChecklistsTriggers.includes(values.trigger.type) && ChecklistsTriggers.includes(newType))
    ) {
      dispatch(change(form, "trigger.entityName", null));
    }
    dispatch(change(form, "trigger.parameterId", null));
  };

  const onTagChange = tag => {
    dispatch(change(form, 'trigger.parameterId', tag[tag.length - 1] || null));
  };

  useEffect(() => {
    if (values.trigger.entityAttribute) {
      dispatch(change(form, "trigger.entityAttribute", null));
    }
  }, [values.trigger.type, values.trigger.entityName]);

  useEffect(() => {
    if (values.trigger.entityName) {
      dispatch(getEntityTags(values.trigger.entityName));
    }
  }, [values.trigger.entityName]);

  useEffect(() => {
    setEntityItems(ChecklistsTriggers.includes(values?.trigger?.type)
      ? TagableEntities
      : AllEntities);
  }, [values.trigger.type]);

  useEffect(() => {
    if (ChecklistsTriggers.includes(values.trigger.type) && typeof values.trigger.parameterId === "number") {
      TagsService.getTag(values.trigger.parameterId)
        .then(checklist => {
          const updatedEntities = checklist.requirements.map(r => ({ value: r.type, label: r.type }));
          setEntityItems(updatedEntities);
  
          if (values.trigger.entityName && !updatedEntities.some(e => e.value === values.trigger.entityName)) {
            dispatch(change(form, "trigger.entityName", null));
          }
        })
        .catch(e => instantFetchErrorHandler(dispatch, e));
    } else {
      setEntityItems(ChecklistsTriggers.includes(values?.trigger?.type) || TagTriggers.includes(values.trigger.type)
        ? TagableEntities
        : AllEntities);
    }
  }, [values.trigger.parameterId, values.trigger.type]);

  const entityNotRequired = [...ChecklistsTriggers, "On demand"].includes(values.trigger.type);

  const isScheduleOrOnDemand = ["On demand", "Schedule"].includes(values.trigger.type);

  return (
    <div className="pt-2 centeredFlex mb-2">
      <FormField
        type="select"
        name="trigger.type"
        label={$t('trigger_type')}
        className="flex-fill"
        items={
          isScheduleOrOnDemand && isInternal
          ? [{ label: "On demand", value: "On demand" }, { label: "Schedule", value: "Schedule" }]
          : TriggerTypeItems
        }
        disabled={isInternal && !isScheduleOrOnDemand}
        onChange={onTriggerChange}
        debounced={false}
        required
      />

      {
        ChecklistsTriggers.includes(values.trigger.type) && (
          <FormField
            type="select"
            name="trigger.parameterId"
            label={$t('checklist')}
            className="pl-2 flex-fill"
            disabled={isInternal}
            items={checklists}
            selectValueMark="id"
            selectLabelMark="title"
            allowEmpty
          />
        )
      }

      {
        TagTriggers.includes(values.trigger.type) && (
          <TagInputList
            input={{
              onChange: onTagChange,
              value: values.trigger.parameterId ? [values.trigger.parameterId] : []
            }}
            meta={{}}
            label={$t('tag')}
            className="pl-2 flex-fill"
            tags={entityTags}
          />
        )
      }

      {enableEntityNameField && (
        <FormField
          type="select"
          name="trigger.entityName"
          label={$t('entity_name')}
          required={!entityNotRequired}
          className="pl-2 flex-fill"
          disabled={isInternal}
          items={entityItems}
          allowEmpty={entityNotRequired}
          sort
        />
      )}

      {values.trigger.type === "On edit" && values.trigger.entityName
        && (
          <FormField
            type="select"
            name="trigger.entityAttribute"
            label={$t('entity_attribute')}
            className="pl-2 flex-fill"
            disabled={isInternal}
            items={Object.keys(Entities[values.trigger.entityName]).map(mapSelectItems)}
            allowEmpty
          />
      )}

      {values.trigger.type === "Schedule" && (
        <>
          <FormField
            type="select"
            name="trigger.cron.scheduleType"
            label={`Schedule type ${(timeZone ? '(' + timeZone + ' time)' : '' )}`}
            items={ScheduleTypeItems}
            required
            className="pl-2 flex-fill"
          />

          {values.trigger.cron?.scheduleType === "Custom" && (
            <FormField
              type="text"
              name="trigger.cron.custom"
              label={$t('cron_schedule')}
              className="pl-2 flex-fill"
              labelAdornment={(
                <span>
                  <a target="_blank" href="http://www.cronmaker.com/" rel="noreferrer">
                    <IconButton
                      classes={{
                        root: "inputAdornmentButton"
                      }}
                    >
                      <Help className="inputAdornmentIcon" />
                    </IconButton>
                  </a>
                </span>
              )}
              required
            />
          )}
        </>
      )}
    </div>
  );
};

export default TriggerCardContent;