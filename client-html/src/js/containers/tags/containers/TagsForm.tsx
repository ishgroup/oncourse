/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { ComponentClass } from "react";
import { withStyles, Typography, Grid } from "@material-ui/core";
import Divider from "@material-ui/core/Divider";
import { withRouter } from "react-router";
import DeleteForever from "@material-ui/icons/DeleteForever";
import { DragDropContext, Droppable } from "react-beautiful-dnd";
import {
  Form, Field, initialize, change, arrayRemove, reduxForm, getFormValues
} from "redux-form";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { ForbiddenTagNames, Tag } from "@api/model";
import { AddCircle } from "@material-ui/icons";
import IconButton from "@material-ui/core/IconButton";
import Button from "../../../common/components/buttons/Button";
import FormField from "../../../common/components/form/form-fields/FormField";
import HeaderTextField from "../../../common/components/form/form-fields/HeaderTextField";
import { validateSingleMandatoryField, validateTagName } from "../../../common/utils/validation";
import AppBarHelpMenu from "../../../common/components/form/AppBarHelpMenu";
import CustomAppBar from "../../../common/components/layout/CustomAppBar";
import { State } from "../../../reducers/state";
import RouteChangeConfirm from "../../../common/components/dialog/confirm/RouteChangeConfirm";
import TagRequirementsMenu from "../components/TagRequirementsMenu";
import TagItem from "../components/TagItem";
import { getDeepValue } from "../../../common/utils/common";
import {
  createTag, deleteTag, updateTag, updateTagEditViewState
} from "../actions";
import AppBarActions from "../../../common/components/form/AppBarActions";
import TagRequirementItem from "../components/TagRequirementItem";
import { getManualLink } from "../../../common/utils/getManualLink";
import TagItemEditView from "../components/TagItemEditView";
import { setNextLocation, showConfirm } from "../../../common/actions";
import { getAllTags } from "../utils";

const styles = () => ({
  noTransform: {
    transform: "none !important"
  }
});

const manualUrl = getManualLink("tagging");

interface FormTag extends Tag {
  parent: string;
  dragIndex: number;
}

interface Props {
  rootTag?: FormTag;
  tags?: FormTag[];
  isNew?: boolean;
  redirectOnDelete?: () => void;
  openConfirm?: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string) => void;
}

interface FormProps extends Props {
  values: any;
  classes: any;
  dispatch: any;
  className: string;
  form: string;
  handleSubmit: any;
  dirty: boolean;
  asyncValidating: boolean;
  invalid: boolean;
  submitSucceeded: boolean;
  fetch: any;
  asyncErrors: any;
  onUpdate: (id: number, tag: Tag) => void;
  onCreate: (tag: Tag) => void;
  onDelete: (id: number) => void;
  openTagEditView: (item: Tag) => void;
  closeTagEditView: () => void;
  history: any;
  nextLocation: string;
  setNextLocation: (nextLocation: string) => void;
  theme?: any;
}

const setWeight = items =>
  items.map((i, index) => {
    i.weight = index + 1;

    delete i.dragIndex;
    delete i.parent;

    if (i.id.toString().includes("new")) {
      i.id = null;
    }

    if (i.childTags.length) {
      setWeight(i.childTags);
    }

    return i;
  });

const checkParentDrop = (values, path, dragID) => {
  const match = getDeepValue(values, path).id === dragID;

  const regex = /.childTags\[[0-9]+]$/;

  if (!match && path.match(regex)) {
    return checkParentDrop(values, path.replace(regex, ""), dragID);
  }

  return match;
};

const setDragIndex = tags => {
  tags.forEach((i, index) => {
    i.dragIndex = index;
    delete i.parent;
  });
};

const getPathByDragIndex = (index, tags) => {
  let parent = "";

  tags.forEach(t => {
    if (!parent && t.dragIndex === index) {
      parent = t.parent;
      return;
    }

    if (!parent && t.childTags.length) {
      parent = getPathByDragIndex(index, t.childTags);
    }
  });

  return parent;
};

const validatTagsNames = val => (val.some(i => !i.name) ? "Name is mandatory" : undefined);

class TagsFormBase extends React.PureComponent<FormProps, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending;

  private disableConfirm;

  private counter;

  constructor(props) {
    super(props);

    // New Tags counter
    this.counter = 1;

    // Initializing form with values

    if (props.rootTag) {
      setDragIndex(getAllTags([props.rootTag]));

      props.dispatch(initialize("TagsForm", props.rootTag));
    }
  }

  componentDidUpdate(prevProps) {
    const { rootTag, submitSucceeded, fetch, nextLocation, setNextLocation, dirty, history } = this.props;

    if (rootTag && (!prevProps.rootTag || prevProps.rootTag.id !== rootTag.id || submitSucceeded)) {
      setDragIndex(getAllTags([rootTag]));

      this.props.dispatch(initialize("TagsForm", rootTag));
    }

    if (this.isPending && fetch && fetch.success === false) {
      this.isPending = false;
      this.rejectPromise();
    }
    if (this.isPending && fetch && fetch.success) {
      this.isPending = false;
      this.resolvePromise();
    }

    if (nextLocation && !dirty && !this.isPending) {
      history.push(nextLocation);
      setNextLocation('');
    }
  }

  onSave = values => {
    const { onUpdate, onCreate, isNew } = this.props;

    const clone = JSON.parse(JSON.stringify(values));

    if (!clone.weight) clone.weight = 1;

    delete clone.dragIndex;
    delete clone.parent;

    setWeight(clone.childTags);

    this.isPending = true;

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      if (isNew) {
        onCreate(clone);
      } else {
        onUpdate(clone.id, clone);
      }
    });
  };

  onDelete = id => {
    const { onDelete, redirectOnDelete } = this.props;

    this.isPending = true;
    this.disableConfirm = true;

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      onDelete(id);
    }).then(() => {
      redirectOnDelete();
      this.disableConfirm = false;
    });
  };

  onDragEnd = args => {
    const { combine, draggableId, destination } = args;

    const { values, dispatch } = this.props;

    if (destination || combine) {
      const destinationPath = combine
        ? combine.draggableId === "ROOT"
          ? ""
          : combine.draggableId
        : getPathByDragIndex(destination.index, values.childTags);

      if (
        destinationPath.length > draggableId.length
        && checkParentDrop(values, destinationPath, getDeepValue(values, draggableId).id)
      ) {
        // parent dropped inside itself or children
        return;
      }

      if (
        !combine
        && destinationPath.replace(/childTags\[[0-9]+]$/, "") !== draggableId.replace(/childTags\[[0-9]+]$/, "")
      ) {
        // dropped inside different parent
        return;
      }

      const clone = JSON.parse(JSON.stringify(values));

      const insertPath = getDeepValue(
        clone,
        combine
          ? destinationPath
            ? destinationPath + ".childTags"
            : "childTags"
          : destinationPath.replace(/\[[0-9]+]$/, "")
      );

      const insertValue = getDeepValue(clone, draggableId);

      const insertIndex = !combine && Number(destinationPath.match(/\[[0-9]+]$/)[0].replace(/[\[\]]/g, ""));

      const removePath = getDeepValue(clone, draggableId.replace(/\[[0-9]+]$/, ""));

      const removeIndex = Number(draggableId.match(/\[[0-9]+]$/)[0].replace(/[\[\]]/g, ""));

      removePath.splice(removeIndex, 1);
      combine ? insertPath.push(insertValue) : insertPath.splice(insertIndex, 0, insertValue);

      setDragIndex(getAllTags([clone]));
      dispatch(change("TagsForm", "childTags", clone.childTags));
    }
  };

  addTag = () => {
    const { values, dispatch, theme } = this.props;

    const newTag: FormTag = {
      id: ("new" + this.counter) as any,
      name: "",
      status: "Private",
      system: false,
      urlPath: null,
      content: "",
      parent: "ROOT",
      dragIndex: 0,
      weight: 1,
      taggedRecordsCount: 0,
      created: null,
      modified: null,
      requirements: [],
      childTags: [],
      color: theme.palette.primary.main.replace("#", "")
    };

    const clone = JSON.parse(JSON.stringify(values));

    clone.childTags.splice(0, 0, newTag);

    setDragIndex(getAllTags([clone]));

    dispatch(change("TagsForm", "childTags", clone.childTags));

    this.counter++;
  };

  removeChildTag = (parent, index, item) => {
    const { dispatch, values, openConfirm } = this.props;

    const message = item.childrenCount
      ? `Deleting this tag will automatically delete ${item.childrenCount} 
    children tag${item.childrenCount === 1 ? "" : "s"}. ${
          item.taggedRecordsCount ? item.taggedRecordsCount + " records will be untagged. " : ""
        }After saving the records it cannot be undone`
      : "You are about to delete tag. After saving the records it cannot be undone";

    const onConfirm = () => {
      const clone = JSON.parse(JSON.stringify(values));

      const removePath = getDeepValue(clone, parent.replace(/\[[0-9]+]$/, ""));

      removePath.splice(index, 1);

      setDragIndex(getAllTags([clone]));

      dispatch(change("TagsForm", "childTags", clone.childTags));
    };

    openConfirm(onConfirm, message, "DELETE");
  };

  removeRequirement = index => {
    this.props.dispatch(arrayRemove("TagsForm", "requirements", index));
  };

  validateTagName = (value, v, props, path) => {
    const { values } = this.props;

    const group = getDeepValue(values, path.replace(/\[[0-9]+]$/, ""));

    const match = group.filter(i => i.name && i.id !== v.id && i.name.trim() === value.trim());

    return match.length ? "The tag name is not unique within its parent tag" : undefined;
  };

  validateTagShortName = (value, v, path) => {
    const { values } = this.props;

    if (!value) return undefined;

    if (ForbiddenTagNames[value.toLowerCase()]) {
      return "This name is reserved by the onCourse system and cannot be used.";
    }

    if (value.includes('"')) {
      return "Double quotes are not permitted in the short name.";
    }

    if (value.includes("+")) {
      return "Plus sign is not permitted in the short name.";
    }

    if (value.includes("\\")) {
      return "Backslash is not permitted in the short name.";
    }

    if (value.includes("/")) {
      return "Forward slash is not permitted in the short name.";
    }

    if (value.startsWith("template-") || value.match(/\/[0-9]/)) {
      return "This short name is not allowed.";
    }

    if (!path) {
      return undefined;
    }

    const group = getDeepValue(values, path.replace(/\[[0-9]+]$/, ""));

    const match = group && group.filter(i => i.id !== v.id && i.urlPath && i.urlPath.trim() === value.trim());

    return match.length ? "The node name is not unique" : undefined;
  };

  validateRootTagName = value => {
    const { tags, rootTag } = this.props;

    const matches = tags.filter(i => i.name.trim() === value.trim() && rootTag.id !== i.id);

    return matches.length > 0 ? "The tag name is not unique" : undefined;
  };

  validateRequirements = value => (value.length ? undefined : "At least one table should be selected before the tag record can be saved");

  render() {
    const {
      classes,
      className,
      handleSubmit,
      dirty,
      invalid,
      rootTag,
      values,
      openTagEditView,
      closeTagEditView,
      isNew,
      openConfirm,
      dispatch,
      form
    } = this.props;

    return (
      <>
        <Form onSubmit={handleSubmit(this.onSave)} className={className}>
          {!this.disableConfirm && dirty && <RouteChangeConfirm form={form} when={dirty} />}

          <CustomAppBar>
            <Grid container>
              <Grid item xs={12} className="centeredFlex">
                <FormField
                  type="headerText"
                  name="name"
                  placeholder="Name"
                  margin="none"
                  className={classes.HeaderTextField}
                  listSpacing={false}
                  validate={[validateSingleMandatoryField, this.validateRootTagName, validateTagName]}
                  disabled={rootTag.system}
                />

                <div className="flex-fill" />

                {!isNew && !rootTag.system && (
                  <AppBarActions
                    actions={[
                      {
                        action: () => this.onDelete(rootTag.id),
                        icon: <DeleteForever />,
                        confirmText: "Tag will be deleted permanently",
                        tooltip: "Delete Tag",
                        confirmButtonText: "DELETE"
                      }
                    ]}
                  />
                )}

                <AppBarHelpMenu
                  created={rootTag.created ? new Date(rootTag.created) : null}
                  modified={rootTag.modified ? new Date(rootTag.modified) : null}
                  auditsUrl={'audit?search=~"Tag"'}
                  manualUrl={manualUrl}
                />

                <Button
                  text="Save"
                  type="submit"
                  size="small"
                  variant="text"
                  className="appBarSaveButton"
                  disabled={!dirty || invalid}
                  rootClasses="whiteAppBarButton"
                  disabledClasses="whiteAppBarButtonDisabled"
                />
              </Grid>
            </Grid>
          </CustomAppBar>

          <Grid container spacing={5}>
            <Grid item sm={12} lg={11} xl={8}>
              <Grid container>
                <Grid item xs={12} md={8}>
                  <div className="centeredFlex">
                    {values && (
                      <Field
                        name="requirements"
                        label="Available for"
                        component={TagRequirementsMenu}
                        items={values.requirements}
                        rootID={values.id}
                        validate={this.validateRequirements}
                        system={rootTag.system}
                      />
                    )}
                  </div>

                  {values
                    && values.requirements.map((i, index) => (
                      <TagRequirementItem
                        parent={`requirements[${index}]`}
                        key={index}
                        item={i}
                        index={index}
                        onDelete={this.removeRequirement}
                        disabled={values.system}
                        openConfirm={openConfirm}
                        dispatch={dispatch}
                      />
                    ))}
                </Grid>

                <Grid item xs={false} md={4} />
              </Grid>

              <Divider className="mt-2 mb-2" />

              <div className="centeredFlex">
                <Typography className="heading">Tags</Typography>
                <IconButton onClick={this.addTag}>
                  <AddCircle className="addButtonColor" width={20} />
                </IconButton>
              </div>

              <DragDropContext onDragEnd={this.onDragEnd}>
                <Droppable droppableId="ROOT" isCombineEnabled>
                  {provided => (
                    <div ref={provided.innerRef}>
                      {values && (
                        <TagItem
                          noTransformClass={classes.noTransform}
                          item={values}
                          onDelete={this.removeChildTag}
                          openTagEditView={openTagEditView}
                          validatTagsNames={validatTagsNames}
                        />
                      )}

                      {provided.placeholder}
                    </div>
                  )}
                </Droppable>
              </DragDropContext>
            </Grid>
          </Grid>
        </Form>

        <TagItemEditView
          validateName={this.validateTagName}
          validateShortName={this.validateTagShortName}
          validateRootName={this.validateRootTagName}
          onClose={closeTagEditView}
        />
      </>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("TagsForm")(state),
  fetch: state.fetch,
  nextLocation: state.nextLocation,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onUpdate: (id: number, tag: Tag) => dispatch(updateTag(id, tag)),
  onCreate: (tag: Tag) => dispatch(createTag(tag)),
  onDelete: (id: number) => dispatch(deleteTag(id)),
  openTagEditView: (item: Tag, parent: string) => dispatch(updateTagEditViewState(item, true, parent)),
  closeTagEditView: () => dispatch(updateTagEditViewState({}, false, null)),
  openConfirm: (onConfirm: any, confirmMessage?: string, confirmButtonText?: any, onCancel?: any, title?: string) =>
    dispatch(showConfirm(onConfirm, confirmMessage, confirmButtonText, onCancel, title)),
  setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
});

const TagsForm = reduxForm({
  form: "TagsForm"
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles, { withTheme: true })(withRouter(TagsFormBase))));

export default TagsForm as ComponentClass<Props>;
