/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { TagRequirement } from "@api/model";
import DeleteForever from "@mui/icons-material/DeleteForever";
import { Divider, Grid, Typography } from "@mui/material";
import { AddButton, ColorPicker } from "ish-ui";
import React, { useEffect } from "react";
import { arrayInsert, arrayPush, Field, Form, initialize } from "redux-form";
import AppBarActions from "../../../common/components/appBar/AppBarActions";
import RouteChangeConfirm from "../../../common/components/dialog/RouteChangeConfirm";
import FormField from "../../../common/components/form/formFields/FormField";
import AppBarContainer from "../../../common/components/layout/AppBarContainer";
import { getManualLink } from "../../../common/utils/getManualLink";
import { useAppDispatch } from "../../../common/utils/hooks";
import { FormTag } from "../../../model/tags";
import { getTagRequest } from "../actions";
import ChecklistRequirementItem from "../components/ChecklistRequirementItem";
import TagRequirementItem from "../components/TagRequirementItem";
import TagRequirementsMenu from "../components/TagRequirementsMenu";
import { ChecklistTree, TagTree } from "../components/Trees";
import { EmptyTag, TAGS_FORM_NAME } from "../constants";
import { TagsFormBase, TagsFormWrapper } from "./TagsFormBase";

const manualUrl = getManualLink("tagging");

class TagsFormRenderer extends TagsFormBase {
  render() {
    const {
      className,
      handleSubmit,
      dirty,
      invalid,
      values,
      openConfirm,
      dispatch,
      syncErrors,
      form,
      classes
    } = this.props;

    const isNew = !values?.id;

    const { editingIds } = this.state;

    return values ? (
      <Form onSubmit={handleSubmit(this.onSave)} className={className}>
        <RouteChangeConfirm form={form} when={dirty} />

        <AppBarContainer
          values={values}
          manualUrl={manualUrl}
          getAuditsUrl='audit?search=~"Tag"'
          disabled={!dirty}
          invalid={invalid}
          title={(isNew && (!values || !values.name || values.name.trim().length === 0))
            ? "New"
            : values && values.name.trim()}
          createdOn={() => (values.created ? new Date(values.created) : null)}
          modifiedOn={() => (values.modified ? new Date(values.modified) : null)}
          disableInteraction={values.system}
          opened={isNew || Object.keys(syncErrors).includes("name")}
          containerClass="p-3"
          fields={(
            <FormField
              type="text"
              name="name"
              label="Name"
              disabled={values.system}
              className="flex-fill"
            />
          )}
          actions={!isNew && !values.system && (
            <AppBarActions
              actions={[
                {
                  action: () => this.onDelete(values),
                  icon: <DeleteForever/>,
                  confirmText: "Tag will be deleted permanently",
                  tooltip: "Delete Tag",
                  confirmButtonText: "DELETE"
                }
              ]}
            />
          )}
        >
          <Grid container>
            <Grid item sm={12} lg={11} xl={8}>
              <div className="centeredFlex">
                {values && (
                  <Field
                    name="requirements"
                    label="Available for"
                    component={TagRequirementsMenu}
                    items={values.requirements}
                    rootID={values.id}
                    validate={this.validateRequirements}
                    system={values.system}
                  />
                )}
              </div>

              {values
              && values.requirements?.map((i, index) => (
                <TagRequirementItem
                  parent={`requirements[${index}]`}
                  key={i.type}
                  item={i}
                  index={index}
                  onDelete={this.removeRequirement}
                  disabled={values.system}
                  openConfirm={openConfirm}
                  dispatch={dispatch}
                />
              ))}

              <Divider className="mt-2 mb-2" />

              <div className="centeredFlex">
                <div className="heading">Tags</div>
                <AddButton onClick={this.addTag} />
              </div>

              <div className={classes.legend}>
                <Typography variant="caption" color="textSecondary">Name</Typography>
                <Typography variant="caption" color="textSecondary">URL path</Typography>
                <Typography variant="caption" color="textSecondary" textAlign="center">Website visibility</Typography>
              </div>

              {values && (
                <TagTree
                  rootTag={values}
                  classes={classes}
                  onDelete={this.removeChildTag}
                  changeVisibility={this.changeVisibility}
                  setEditingId={this.setEditingId}
                  onDrop={this.onDrop}
                  editingIds={editingIds}
                  syncErrors={syncErrors}
                />
              )}
            </Grid>
          </Grid>
        </AppBarContainer>
      </Form>
    ) : null;
  }
}

class ChecklistsFormRenderer extends TagsFormBase {
  addTag = () => {
    const { dispatch } = this.props;

    const newTag: FormTag = {
      ...EmptyTag,
      color: null,
      type: "Checklist",
      id: ("new" + this.counter) as any,
    };

    dispatch(arrayPush(TAGS_FORM_NAME, "childTags", newTag));

    this.counter++;
  };

  render() {
    const {
      className,
      handleSubmit,
      dirty,
      invalid,
      values,
      syncErrors,
      form,
      classes,
      dispatch
    } = this.props;

    const { editingIds } = this.state;

    const isNew = !values?.id;

    return values ? (
      <Form onSubmit={handleSubmit(this.onSave)} className={className}>
        <RouteChangeConfirm form={form} when={dirty} />

        <AppBarContainer
          values={values}
          manualUrl={manualUrl}
          getAuditsUrl='audit?search=~"Tag"'
          disabled={!dirty}
          invalid={invalid}
          title={(
            <div className="centeredFlex">
              <span className="mr-2">
                <div className={classes.tagColorDot} style={{ background: "#" + values.color }} />
              </span>
              {(isNew && (!values || !values.name || values.name.trim().length === 0))
              ? "New"
              : values && values.name.trim()}
            </div>
          )}
          createdOn={() => (values.created ? new Date(values.created) : null)}
          modifiedOn={() => (values.modified ? new Date(values.modified) : null)}
          disableInteraction={values.system}
          opened={isNew || Object.keys(syncErrors).includes("name")}
          containerClass="p-3"
          fields={(
            <div className="centeredFlex">
              <span className="mr-2">
                <Field name="color" component={ColorPicker} placement="bottom" />
              </span>
              <FormField
                type="text"
                name="name"
                label="Name"
                className="flex-fill"
                disabled={values.system}
              />
            </div>
          )}
          actions={!isNew && !values.system && (
            <AppBarActions
              actions={[
                {
                  action: () => this.onDelete(values),
                  icon: <DeleteForever/>,
                  confirmText: "Tag will be deleted permanently",
                  tooltip: "Delete Tag",
                  confirmButtonText: "DELETE"
                }
              ]}
            />
          )}
        >
          <Grid container>
            <Grid item sm={12} lg={11} xl={8}>
              <Grid container columnSpacing={3}>
                <Grid item xs={12} md={8}>
                  <div className="centeredFlex">
                    {values && (
                      <Field
                        name="requirements"
                        label="Checklist visible on"
                        component={TagRequirementsMenu}
                        items={values.requirements}
                        rootID={values.id}
                        validate={this.validateRequirements}
                        system={values.system}
                      />
                    )}
                  </div>

                  {values
                    && values.requirements?.map((i, index) => (
                      <ChecklistRequirementItem
                        parent={`requirements[${index}]`}
                        key={i.type}
                        item={i}
                        index={index}
                        onDelete={this.removeRequirement}
                        disabled={values.system}
                        dispatch={dispatch}
                        form={form}
                      />
                  ))}
                </Grid>

                <Grid item xs={false} md={4} />
              </Grid>

              <Divider className="mt-2 mb-2" />

              <div className="centeredFlex">
                <div className="heading">Add a task</div>
                <AddButton onClick={this.addTag} />
              </div>

              {values && (
                <ChecklistTree
                  rootTag={values}
                  classes={classes}
                  onDelete={this.removeChildTag}
                  setEditingId={this.setEditingId}
                  onDrop={this.onDrop}
                  editingIds={editingIds}
                  syncErrors={syncErrors}
                />
              )}
            </Grid>
          </Grid>
        </AppBarContainer>
      </Form>
    ) : null;
  }
}

export const ChecklistsForm = ({ match: { params: { id } }, history }) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    if (id === "new") {
      dispatch(initialize(TAGS_FORM_NAME, {
        ...EmptyTag,
        type: "Checklist"
      }));

      const params = new URLSearchParams(history.location.search);

      const entity = params.get("entity");

      if (entity) {
        params.delete("entity");
        
        dispatch(arrayInsert(TAGS_FORM_NAME, "requirements", 0, {
          id: null,
          type: entity === "AbstractInvoice" ? "Invoice" : entity,
          mandatory: false,
          limitToOneTag: false,
          system: false
        } as TagRequirement));

        history.replace({
          pathname: history.location.pathname,
          search: decodeURIComponent(params.toString())
        });
      }
    } else {
      dispatch(getTagRequest(TAGS_FORM_NAME, id));
    }
  }, [id]);

  return <TagsFormWrapper history={history} Root={ChecklistsFormRenderer} />;
};

export const TagsForm = ({ match: { params: { id } }, history }) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    if (id === "new") {
      dispatch(initialize(TAGS_FORM_NAME, EmptyTag));
    } else {
      dispatch(getTagRequest(TAGS_FORM_NAME, id));
    }
  }, [id]);

  return <TagsFormWrapper history={history} Root={TagsFormRenderer} />;
};