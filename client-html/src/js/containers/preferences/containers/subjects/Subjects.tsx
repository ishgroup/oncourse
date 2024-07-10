/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { TreeData } from "@atlaskit/tree";
import Typography from "@mui/material/Typography";
import clsx from "clsx";
import { makeAppStyles } from "ish-ui";
import React, { useEffect, useRef, useState } from "react";
import { change, Form, FormErrors, getFormSyncErrors, getFormValues, InjectedFormProps, reduxForm } from "redux-form";
import AppBarContainer from "src/js/common/components/layout/AppBarContainer";
import { showConfirm } from "../../../../common/actions";
import RouteChangeConfirm from "../../../../common/components/dialog/RouteChangeConfirm";
import { getInvalidValueOdjects } from "../../../../common/utils/common";
import { onSubmitFail } from "../../../../common/utils/highlightFormErrors";
import { useAppDispatch, useAppSelector } from "../../../../common/utils/hooks";
import { SPECIAL_TYPES_DISPLAY_KEY } from "../../../../constants/Config";
import { SUBJECTS_ENTITY_FORM_NAME } from "../../../../constants/Forms";
import { FormTag } from "../../../../model/tags";
import { updateTag } from "../../../tags/actions";
import { treeDataToTags } from "../../../tags/components/Trees";
import { EmptyTag } from "../../../tags/constants";
import { styles } from "../../../tags/styles/TagItemsStyles";
import { getAllTags, rootTagToServerModel } from "../../../tags/utils";
import { validateTagsForm } from "../../../tags/utils/validation";
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
  const counter = useRef(1);

  const classes = useStyles();

  const dispatch = useAppDispatch();
  const [editingIds, setEditingIds] = useState<number[]>([]);
  const disabled = useAppSelector(state => state.userPreferences[SPECIAL_TYPES_DISPLAY_KEY] !== 'true');
  const values = useAppSelector(state => getFormValues(form)(state)) as FormTag;
  const syncErrors: FormErrors<FormTag> = useAppSelector(state => getFormSyncErrors(form)(state));

  useEffect(() => {
    if (Object.keys(syncErrors || {}).length) {
      getInvalidValueOdjects(syncErrors, values).forEach(t => setEditingIds(prev => Array.from(new Set([...prev, t.id]))));
    }
  }, [syncErrors]);

  useEffect(() => {
    dispatch(getSubjects());
  }, []);

  const onSave = values => {
    dispatch(updateTag(form, rootTagToServerModel(values)));
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
      array.remove(item.parent.replace(/\[[0-9]+]$/, ""), parseInt(item.parent.match(/\[([0-9]+)]$/)[1]));
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

  const onAddNew = () => {
    const id = `new${counter.current}`;
    const newSubject = { ...EmptyTag, id };
    array.unshift('childTags', newSubject);
    counter.current++;
    setTimeout(() => {
      const domNode = document.querySelector(`[data-draggable-id="${id}"]`);
      if (domNode) domNode.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }, 200);
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
            syncErrors={syncErrors}
          />
        )}
      </AppBarContainer>
    </Form>
  );
}

const Decorated = reduxForm({
  onSubmitFail,
  shouldError: () => true,
  validate: validateTagsForm,
  initialValues: { ...EmptyTag },
  form: SUBJECTS_ENTITY_FORM_NAME
})(Subjects);

export default Decorated;