/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { ComponentClass } from "react";
import { Grid, Typography } from "@mui/material";
import Divider from "@mui/material/Divider";
import { withRouter } from "react-router";
import DeleteForever from "@mui/icons-material/DeleteForever";
import { DragDropContext, Droppable } from "react-beautiful-dnd";
import {
 arrayRemove, change, Field, Form, getFormSyncErrors, getFormValues, initialize, reduxForm 
} from "redux-form";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { ForbiddenTagNames, Tag } from "@api/model";
import { createStyles, withStyles } from "@mui/styles";
import { alpha } from "@mui/material/styles";
import FormField from "../../../common/components/form/formFields/FormField";
import { validateAqlFilterOrTagName, validateSingleMandatoryField } from "../../../common/utils/validation";
import { State } from "../../../reducers/state";
import RouteChangeConfirm from "../../../common/components/dialog/confirm/RouteChangeConfirm";
import TagRequirementsMenu from "../components/TagRequirementsMenu";
import { getDeepValue } from "../../../common/utils/common";
import { createTag, deleteTag, updateTag } from "../actions";
import AppBarActions from "../../../common/components/form/AppBarActions";
import TagRequirementItem from "../components/TagRequirementItem";
import { getManualLink } from "../../../common/utils/getManualLink";
import { setNextLocation, showConfirm } from "../../../common/actions";
import { COLORS, getAllTags } from "../utils";
import { ShowConfirmCaller } from "../../../model/common/Confirm";
import AddButton from "../../../common/components/icons/AddButton";
import { onSubmitFail } from "../../../common/utils/highlightFormClassErrors";
import AppBarContainer from "../../../common/components/layout/AppBarContainer";
import { getPluralSuffix } from "../../../common/utils/strings";
import TagItem from "../components/TagItem";
import { AppTheme } from "../../../model/common/Theme";
import { FormTag } from "../../../model/tags";

const styles = (theme: AppTheme) => createStyles({
  dragIcon: {
    margin: theme.spacing(0, 2),
    color: theme.palette.action.focus,
    "&:hover": {
      color: theme.palette.action.active
    }
  },
  actionButton: {
    marginRight: "10px"
  },
  actionIcon: {
    color: theme.palette.action.focus,
    fontSize: "20px"
  },
  actionIconInactive: {
    color: theme.palette.action.hover,
    fontSize: "20px"
  },
  card: {
    borderRadius: `${theme.shape.borderRadius}px`,
    padding: theme.spacing(0.25, 0),
    margin: theme.spacing(1, 0),
    cursor: "pointer",
    backgroundColor: alpha(theme.palette.text.primary, 0.025),
    "&:hover $actionIcon": {
      color: theme.palette.action.active
    },
    "&:hover $actionIconInactive": {
      color: theme.palette.action.focus
    }
  },
  cardGrid: {
    gridTemplateColumns: "auto auto 1fr 1fr auto auto auto",
    display: "grid",
    alignItems: "center",
  },
  dragOver: {
    boxShadow: theme.shadows[2]
  },
  tagColorDot: {
    width: "1em",
    height: "1em",
    borderRadius: "100%"
  },
  legend: {
    gridTemplateColumns: "1fr 1fr 108px",
    display: "grid",
    alignItems: "center",
    paddingLeft: "94px"
  },
  fieldEditable: {
    paddingRight: theme.spacing(2),
    position: "relative",
    top: 2
  },
  nameEditable: {
    fontSize: "14px",
    fontWeight: 500
  },
  urlEditable: {
    fontSize: "14px",
  }
});

const manualUrl = getManualLink("tagging");

interface Props {
  rootTag?: FormTag;
  tags?: FormTag[];
  isNew?: boolean;
  redirectOnDelete?: () => void;
  openConfirm?: ShowConfirmCaller;
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
  syncErrors?: any;
}

const setWeight = items =>
  items.map((i, index) => {
    let item = { ...i, weight: index + 1 };

    delete item.dragIndex;
    delete item.parent;

    if (item.id.toString().includes("new")) {
      item.id = null;
    }

    if (item.childTags.length) {
      item = { ...item, childTags: setWeight(item.childTags) };
    }

    return item;
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

const validatTagsNames = val => (val?.some(i => !i.name) ? "Name is mandatory" : undefined);

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
    const {
      rootTag, submitSucceeded, fetch, nextLocation, setNextLocation, dirty, history
    } = this.props;

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

    const tags = { ...clone, childTags: setWeight(clone.childTags) };

    this.isPending = true;

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      if (isNew) {
        onCreate(tags);
      } else {
        onUpdate(tags.id, tags);
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
      let destinationPathIndex = -1;

      if (!combine) {
        const destinationPathMatch = destinationPath.match(/\[[0-9]+]$/);
        if (destinationPathMatch && destinationPathMatch.length > 0) {
          destinationPathIndex = Number(destinationPathMatch[0].replace(/[\[\]]/g, ""));
        }
      }

      const insertIndex = !combine && destinationPathIndex;

      const removePath = getDeepValue(clone, draggableId.replace(/\[[0-9]+]$/, ""));

      const draggableIdMatch = draggableId.match(/\[[0-9]+]$/);

      if (draggableIdMatch && draggableIdMatch.length > 0) {
        const removeIndex = Number(draggableIdMatch[0].replace(/[\[\]]/g, ""));
        removePath && removePath.splice(removeIndex, 1);
      }

      combine ? insertPath.push(insertValue) : insertPath && insertPath.splice(insertIndex, 0, insertValue);

      setDragIndex(getAllTags([clone]));
      dispatch(change("TagsForm", "childTags", clone.childTags));
    }
  };

  addTag = () => {
    const { values, dispatch } = this.props;

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
      color: COLORS[Math.floor(Math.random() * COLORS.length)],
    };

    const clone = JSON.parse(JSON.stringify(values));

    clone && clone.childTags && clone.childTags.splice(0, 0, newTag);

    setDragIndex(getAllTags([clone]));

    dispatch(change("TagsForm", "childTags", clone.childTags));

    this.counter++;
  };

  changeVisibility = (parent, index, item: Tag) => {
    const { dispatch } = this.props;

    dispatch(change("TagsForm", parent ? parent + ".status" : "status", item.status === "Private" ? "Show on website" : "Private"));
  }

  removeChildTag = (parent, index, item) => {
    const { dispatch, values, openConfirm } = this.props;

    const confirmMessage = item.childrenCount
      ? `Deleting this tag will automatically delete ${item.childrenCount} 
    children tag${getPluralSuffix(item.childrenCount)}. ${
          item.taggedRecordsCount ? item.taggedRecordsCount + " records will be untagged. " : ""
        }After saving the records it cannot be undone`
      : "You are about to delete tag. After saving the records it cannot be undone";

    const onConfirm = () => {
      const clone = JSON.parse(JSON.stringify(values));

      const removePath = getDeepValue(clone, parent.replace(/\[[0-9]+]$/, ""));

      removePath && removePath.splice(index, 1);

      setDragIndex(getAllTags([clone]));

      dispatch(change("TagsForm", "childTags", clone.childTags));
    };

    openConfirm({ onConfirm, confirmMessage, confirmButtonText: "DELETE" });
  };

  removeRequirement = index => {
    this.props.dispatch(arrayRemove("TagsForm", "requirements", index));
  };

  validateTagName = (value, v, props, path) => {
    const { values } = props;

    const group = getDeepValue(values, path.replace(/\[[0-9]+]$/, ""));

    const match = group.filter(i => i.name && i.id !== v.id && i.name.trim() === value.trim());

    return match.length === 2 ? "The tag name is not unique within its parent tag" : undefined;
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
      className,
      handleSubmit,
      dirty,
      invalid,
      rootTag,
      values,
      isNew,
      openConfirm,
      dispatch,
      syncErrors,
      form,
      classes
    } = this.props;

    return (
      <Form onSubmit={handleSubmit(this.onSave)} className={className}>
        {!this.disableConfirm && dirty && <RouteChangeConfirm form={form} when={dirty} />}

        <AppBarContainer
          values={values}
          manualUrl={manualUrl}
          getAuditsUrl='audit?search=~"Tag"'
          disabled={!dirty}
          invalid={invalid}
          title={(isNew && (!values || !values.name || values.name.trim().length === 0))
              ? "New"
              : values && values.name.trim()}
          createdOn={() => (rootTag.created ? new Date(rootTag.created) : null)}
          modifiedOn={() => (rootTag.modified ? new Date(rootTag.modified) : null)}
          disableInteraction={rootTag.system}
          opened={isNew || Object.keys(syncErrors).includes("name")}
          containerClass="p-3"
          fields={(
            <Grid item xs={8}>
              <FormField
                name="name"
                label="Name"
                margin="none"
                validate={[validateSingleMandatoryField, this.validateRootTagName, validateAqlFilterOrTagName]}
                disabled={rootTag.system}
              />
            </Grid>
          )}
          actions={!isNew && !rootTag.system && (
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
        >
          <Grid container>
            <Grid item sm={12} lg={11} xl={8}>
              <Grid container columnSpacing={3}>
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
                <div className="heading">Tags</div>
                <AddButton onClick={this.addTag} />
              </div>

              <div className={classes.legend}>
                <Typography variant="caption" color="textSecondary">Name</Typography>
                <Typography variant="caption" color="textSecondary">URL path</Typography>
                <Typography variant="caption" color="textSecondary" textAlign="center">Website visibility</Typography>
              </div>

              <DragDropContext onDragEnd={this.onDragEnd}>
                <Droppable droppableId="ROOT" isCombineEnabled>
                  {provided => (
                    <div ref={provided.innerRef}>
                      {values && (
                        <TagItem
                          item={values}
                          classes={classes}
                          validatTagsNames={validatTagsNames}
                          onDelete={this.removeChildTag}
                          validateName={this.validateTagName}
                          validateShortName={this.validateTagShortName}
                          validateRootTagName={this.validateRootTagName}
                          changeVisibility={this.changeVisibility}
                        />
                      )}
                      {provided.placeholder}
                    </div>
                    )}
                </Droppable>
              </DragDropContext>
            </Grid>
          </Grid>
        </AppBarContainer>
      </Form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("TagsForm")(state),
  syncErrors: getFormSyncErrors("TagsForm")(state),
  fetch: state.fetch,
  nextLocation: state.nextLocation,
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  onUpdate: (id: number, tag: Tag) => dispatch(updateTag(id, tag)),
  onCreate: (tag: Tag) => dispatch(createTag(tag)),
  onDelete: (id: number) => dispatch(deleteTag(id)),
  openConfirm: props => dispatch(showConfirm(props)),
  setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
});

const TagsForm = reduxForm({
  form: "TagsForm",
  onSubmitFail
})(connect(mapStateToProps, mapDispatchToProps)(withStyles(styles)(withRouter(TagsFormBase))));

export default TagsForm as ComponentClass<Props>;
