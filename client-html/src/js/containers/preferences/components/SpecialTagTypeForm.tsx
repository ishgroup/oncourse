/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Tag } from '@api/model';
import DragIndicator from '@mui/icons-material/DragIndicator';
import { Card, Grid } from '@mui/material';
import Typography from '@mui/material/Typography';
import $t from '@t';
import clsx from 'clsx';
import { makeAppStyles } from 'ish-ui';
import React, { useEffect, useMemo } from 'react';
import { DragDropContext, Draggable, Droppable } from 'react-beautiful-dnd-next';
import { change, FieldArray, Form, getFormValues, InjectedFormProps } from 'redux-form';
import { WrappedFieldArrayProps } from 'redux-form/lib/FieldArray';
import { showConfirm } from '../../../common/actions';
import RouteChangeConfirm from '../../../common/components/dialog/RouteChangeConfirm';
import AppBarContainer from '../../../common/components/layout/AppBarContainer';
import { reorder } from '../../../common/utils/DnD';
import { useAppDispatch, useAppSelector } from '../../../common/utils/hooks';
import { SPECIAL_TYPES_DISPLAY_KEY } from '../../../constants/Config';
import { EntityName } from '../../../model/entities/common';
import { SpecialTypeTagsFormValues } from '../../../model/tags';
import { EmptyTag } from '../../tags/constants';
import { getSpecialTagTypes, postSpecialTagTypes } from '../actions';
import { styles } from '../styles/dragablePreferenceItemStyles';
import { cardsFormStyles } from '../styles/formCommonStyles';
import { getSpecialTagTypeByEntity } from '../utils';
import SpecialTagType from './SpecialTagType';

interface Props {
  title: string;
  entity: EntityName;
}

const useStyles = makeAppStyles()(theme => ({ ...cardsFormStyles(theme) as any, ...styles(theme) as any }));

const onDragEnd = (result, items, dispatch, form) => {
  // dropped outside the list
  if (!result.destination) {
    return;
  }

  // dropped on the same position
  if (result.source.index === result.destination.index) {
    return;
  }

  const reordered = reorder<Tag>(items, result.source.index, result.destination.index)
    .map((t, weight) => ({ ...t, weight }));

  dispatch(change(form, "types", reordered));
};

function SpecialTagTypeRender({  fields, meta: { form }, classes, onDelete, disabled, dispatch }: WrappedFieldArrayProps & { classes, onDelete, dispatch, disabled }) {
  return  <DragDropContext onDragEnd={result => onDragEnd(result, fields.getAll(), dispatch, form)}>
    <Droppable droppableId="droppableCustomFields">
      {provided => (
        <div ref={provided.innerRef} className={classes.container}>
          {fields.map((field, index) => {
            return (
              <Draggable key={index} draggableId={String(index + 1)} index={index}>
                {provided => (
                  <div
                    id={`custom-field-${index}`}
                    key={index}
                    ref={provided.innerRef}
                    {...provided.draggableProps}
                  >
                    <Card className="card d-flex">
                      <div className="centeredFlex mr-2" {...provided.dragHandleProps}>
                        <DragIndicator className={clsx("dndActionIcon", classes.dragIcon)} />
                      </div>
                      <SpecialTagType field={field} disabled={disabled} key={index} index={index} onDelete={onDelete} />
                    </Card>
                  </div>
                )}
              </Draggable>
            );
          })}
          {provided.placeholder}
        </div>
      )}
    </Droppable>
  </DragDropContext>;

}

function SpecialTagTypeForm(
  {
    handleSubmit, dirty, invalid, form, array, title, entity
  }: Props & InjectedFormProps<SpecialTypeTagsFormValues>
) {
  
  const { classes }: any = useStyles();
  
  const dispatch = useAppDispatch();
  const disabled = useAppSelector(state => state.userPreferences[SPECIAL_TYPES_DISPLAY_KEY] !== 'true');
  const values = useAppSelector(state => getFormValues(form)(state)) as SpecialTypeTagsFormValues;

  const specialType = useMemo(() => getSpecialTagTypeByEntity(entity), [entity]);

  useEffect(() => {
    dispatch(getSpecialTagTypes(entity));
  }, []);

  const onSave = ({ types }) => {
    dispatch(postSpecialTagTypes(types, specialType));
  };

  const onAddNew = () => {
    array.unshift('types', { ...EmptyTag } satisfies Tag);
    const domNode = document.getElementById("special-tag-type-0");
    if (domNode) domNode.scrollIntoView({ behavior: "smooth" });
  };

  const onDelete = index => {
    const onConfirm = () => {
      array.remove('types', index);
    };
    
    if (values.types[index]?.id) {
      dispatch(showConfirm({
        confirmMessage: 'All created relations will be removed as well',
        confirmButtonText: 'Delete',
        onConfirm
      }));
    } else {
      onConfirm();
    }
  };

  return (
    <Form className="container" noValidate autoComplete="off" onSubmit={handleSubmit(onSave)} role={form}>
      <RouteChangeConfirm form={form} when={dirty} />
      <AppBarContainer
        disabled={!dirty}
        invalid={invalid}
        title={title}
        disableInteraction
        onAddMenu={!disabled && onAddNew}
      >
        <Grid container className="mt-2">
          <Grid size={{ xs: 12, lg: 10 }}>
            {values?.types?.length ? (
              <FieldArray
                name="types"
                component={SpecialTagTypeRender}
                props={{
                  disabled,
                  onDelete,
                  classes,
                  dispatch
                }}
              />) : (<div className="noRecordsMessage h-100">
                <Typography variant="h6" color="inherit" align="center">
                  {$t('no_data')}
                </Typography>
              </div>
            )}
          </Grid>
        </Grid>
      </AppBarContainer>
    </Form>
  );
}

export default SpecialTagTypeForm;