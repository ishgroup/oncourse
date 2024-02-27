/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Tag } from "@api/model";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import React, { useEffect } from "react";
import { Form, getFormValues, InjectedFormProps } from "redux-form";
import { showConfirm } from "../../../common/actions";
import RouteChangeConfirm from "../../../common/components/dialog/RouteChangeConfirm";
import AppBarContainer from "../../../common/components/layout/AppBarContainer";
import { useAppDispatch, useAppSelector } from "../../../common/utils/hooks";
import { EntityName } from "../../../model/entities/common";
import { SpecialTypeTagsFormValues } from "../../../model/tags";
import { EmptyTag } from "../../tags/constants";
import { getSpecialTagTypes, postSpecialTagTypes } from "../actions";
import { getSpecialTagTypeByEntity } from "../utils";
import SpecialTagType from "./SpecialTagType";

interface Props {
  title: string;
  entity: EntityName;
}

function SpecialTagTypeForm(
  {
    handleSubmit, dirty, invalid, form, array, title, entity
  }: Props & InjectedFormProps<SpecialTypeTagsFormValues>
) {
  const dispatch = useAppDispatch();
  const values = useAppSelector(state => getFormValues(form)(state)) as SpecialTypeTagsFormValues;

  useEffect(() => {
    dispatch(getSpecialTagTypes(entity));
  }, []);

  const onSave = ({ types }) => {
    dispatch(postSpecialTagTypes(types));
  };

  const onAddNew = () => {
    array.unshift('types', { ...EmptyTag, specialType: getSpecialTagTypeByEntity(entity) } satisfies Tag);
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
        cancelButtonText: 'Delete',
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
        onAddMenu={onAddNew}
      >
        {values?.types?.length ? <Grid container className="mt-2">
          <Grid item sm={12} lg={10}>
            <Grid container columnSpacing={3}>
              {values.types.map((t, index) => <SpecialTagType key={index} index={index} system={t.system} onDelete={onDelete} />)}
            </Grid>
          </Grid>
        </Grid> : <div className="noRecordsMessage h-100">
          <Typography variant="h6" color="inherit" align="center">
            No data
          </Typography>
        </div>}
      </AppBarContainer>
    </Form>
  );
}

export default SpecialTagTypeForm;