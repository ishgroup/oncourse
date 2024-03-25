/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Tag } from "@api/model";
import { TreeData } from "@atlaskit/tree";
import Typography from "@mui/material/Typography";
import clsx from "clsx";
import { makeAppStyles } from "ish-ui";
import React, { useEffect, useState } from "react";
import { change, Form, getFormValues, InjectedFormProps, reduxForm } from "redux-form";
import AppBarContainer from "src/js/common/components/layout/AppBarContainer";
import { showConfirm } from "../../../../common/actions";
import RouteChangeConfirm from "../../../../common/components/dialog/RouteChangeConfirm";
import { getDeepValue } from "../../../../common/utils/common";
import { onSubmitFail } from "../../../../common/utils/highlightFormErrors";
import { useAppDispatch, useAppSelector } from "../../../../common/utils/hooks";
import { SPECIAL_TYPES_DISPLAY_KEY } from "../../../../constants/Config";
import { SUBJECTS_ENTITY_FORM_NAME } from "../../../../constants/Forms";
import { FormTag } from "../../../../model/tags";
import { treeDataToTags } from "../../../tags/components/Trees";
import { EmptyTag } from "../../../tags/constants";
import { styles } from "../../../tags/styles/TagItemsStyles";
import { getAllTags } from "../../../tags/utils";
import { getSubjects } from "./actions";
import SubjectsTree from "./SubjectsTree";

const useStyles = makeAppStyles(theme => {
  const tagStyles = styles(theme);
  
  return {
    ...tagStyles,
    legend: {
      ...tagStyles.legend,
      paddingLeft: '56px'
    }
  };
});

function Subjects(
  {
    handleSubmit, dirty, invalid, form, array
  }: InjectedFormProps<FormTag>
) {
  const classes = useStyles();

  const dispatch = useAppDispatch();
  const [editingIds, setEditingIds] = useState<number[]>([]);
  const disabled = useAppSelector(state => state.userPreferences[SPECIAL_TYPES_DISPLAY_KEY] !== 'true');
  const values = useAppSelector(state => getFormValues(form)(state)) as FormTag;

  useEffect(() => {
    dispatch(getSubjects());
  }, []);

  const onSave = () => {
    
  };

  const onAddNew = () => {
    array.unshift('types', { ...EmptyTag } satisfies Tag);
    const domNode = document.getElementById("special-tag-type-0");
    if (domNode) domNode.scrollIntoView({ behavior: "smooth" });
  };
  
  const changeVisibility = (item: FormTag) => {
    dispatch(change(form, item.parent ? item.parent + ".status" : "status", item.status === "Private" ? "Show on website" : "Private"));
    dispatch(change(form, "refreshFlag", !values.refreshFlag));
  };
  
  const removeChildTag = (item: FormTag) => {
    const confirmMessage = item.childrenCount
      ? `Deleting this Subject will automatically delete ${item.childrenCount} 
    child${item.childrenCount !== 1 ? 'ren' : ''}. ${
        item.taggedRecordsCount ? item.taggedRecordsCount + " record relations will be removed. " : ""
      }After pressing Save it cannot be undone`
      : `You are about to delete Subject. After pressing Save it cannot be undone`;

    const onConfirm = () => {
      const clone = JSON.parse(JSON.stringify(values));

      if (item.parent) {
        const removePath = getDeepValue(clone, item.parent.replace(/\[[0-9]+]$/, ""));

        if (removePath) {
          const deleteItem = item.parent.match(/\[(\d+)]$/);
          if (deleteItem && deleteItem.length > 0) removePath.splice(Number(deleteItem[1]), 1);
        }
      }

      dispatch(change(form, "childTags", clone.childTags));
      dispatch(change(form, "refreshFlag", !values.refreshFlag));
    };

    dispatch(showConfirm({ onConfirm, confirmMessage, confirmButtonText: "DELETE" }));
  };
  
  const setEditingId = editingId => {
    setEditingIds(prev => prev.includes(editingId) ? prev.filter(id => id !== editingId) : prev.concat(editingId));
  };

  const onDrop = (tagsTree: TreeData) => {
    dispatch(change(form, "childTags", treeDataToTags(tagsTree, getAllTags([values]))));
    dispatch(change(form, "refreshFlag", !values.refreshFlag));
  };

  return (
    <Form className="container" noValidate autoComplete="off" onSubmit={handleSubmit(onSave)} role={form}>
      <RouteChangeConfirm form={form} when={dirty} />
      <AppBarContainer
        disabled={!dirty}
        invalid={invalid}
        title='Subjects'
        disableInteraction
        onAddMenu={!disabled && onAddNew}
      >
        <div className={clsx(classes.legend, 'mt-3')}>
          <Typography variant="caption" color="textSecondary">Name</Typography>
          <Typography variant="caption" color="textSecondary">URL path</Typography>
          <Typography variant="caption" color="textSecondary" textAlign="center">Website visibility</Typography>
        </div>

        {values && (
          <SubjectsTree
            rootTag={values}
            classes={classes}
            onDelete={removeChildTag}
            changeVisibility={changeVisibility}
            setEditingId={setEditingId}
            onDrop={onDrop}
            editingIds={editingIds}
            syncErrors={{}}
          />
        )}
      </AppBarContainer>
    </Form>
  );
}

const Decorated = reduxForm({
  onSubmitFail,
  initialValues: { ...EmptyTag },
  form: SUBJECTS_ENTITY_FORM_NAME
})(Subjects);

export default Decorated;