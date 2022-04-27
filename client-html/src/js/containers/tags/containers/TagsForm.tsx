/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { ComponentClass } from "react";
import { Grid, Typography } from "@mui/material";
import Divider from "@mui/material/Divider";
import { withRouter } from "react-router";
import DeleteForever from "@mui/icons-material/DeleteForever";
import {
  arrayRemove,
  change,
  Field,
  Form,
  getFormSyncErrors,
  getFormValues,
  initialize,
  reduxForm
} from "redux-form";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { Tag } from "@api/model";
import { createStyles, withStyles } from "@mui/styles";
import { alpha } from "@mui/material/styles";
import { TreeData } from "@atlaskit/tree";
import FormField from "../../../common/components/form/formFields/FormField";
import { State } from "../../../reducers/state";
import RouteChangeConfirm from "../../../common/components/dialog/confirm/RouteChangeConfirm";
import TagRequirementsMenu from "../components/TagRequirementsMenu";
import { getDeepValue } from "../../../common/utils/common";
import { createTag, deleteTag, updateTag } from "../actions";
import AppBarActions from "../../../common/components/form/AppBarActions";
import TagRequirementItem from "../components/TagRequirementItem";
import { getManualLink } from "../../../common/utils/getManualLink";
import { setNextLocation, showConfirm } from "../../../common/actions";
import { COLORS } from "../utils";
import { ShowConfirmCaller } from "../../../model/common/Confirm";
import AddButton from "../../../common/components/icons/AddButton";
import { onSubmitFail } from "../../../common/utils/highlightFormClassErrors";
import AppBarContainer from "../../../common/components/layout/AppBarContainer";
import { getPluralSuffix } from "../../../common/utils/strings";
import { AppTheme } from "../../../model/common/Theme";
import { FormTag } from "../../../model/tags";
import TagsTree from "../components/TagsTree";
import { validate } from "../utils/validation";

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
  cardRoot: {
    paddingTop: theme.spacing(1),
  },
  card: {
    zIndex: 1,
    borderRadius: `${theme.shape.borderRadius}px`,
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
    paddingLeft: "94px",
    marginBottom: theme.spacing(1)
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
  },
  placeholder: {
    border: `2px dashed ${theme.palette.action.focus}`,
    borderRadius: `${theme.shape.borderRadius}px`,
    position: "absolute",
    boxSizing: "border-box",
    zIndex: 0
  }
});

const manualUrl = getManualLink("tagging");

interface Props {
  rootTag?: FormTag;
  tags?: Tag[];
  isNew?: boolean;
  redirectOnDelete?: () => void;
  openConfirm?: ShowConfirmCaller;
}

interface FormProps extends Props {
  values: FormTag;
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

const treeItemDataToTag = (id: number | string, tree: TreeData): Tag => {
  const tag = tree.items[id].data;
  tag.childTags = tree.items[id].children.map(id => treeItemDataToTag(id, tree));
  return tag;
};

const treeDataToTags = (tree: TreeData): Tag[] => tree.items[tree.rootId].children.map(id => treeItemDataToTag(id, tree));

interface FormState {
  editingId: number;
}

class TagsFormBase extends React.PureComponent<FormProps, FormState> {
  private resolvePromise;

  private rejectPromise;

  private isPending;

  private disableConfirm;

  private counter;

  state = {
    editingId: null
  }

  constructor(props) {
    super(props);

    // New Tags counter
    this.counter = 1;

    // Initializing form with values

    if (props.rootTag) {
      props.dispatch(initialize("TagsForm", props.rootTag));
    }
  }

  componentDidUpdate(prevProps) {
    const {
      rootTag, submitSucceeded, fetch, nextLocation, setNextLocation, dirty, history
    } = this.props;

    if (rootTag && (!prevProps.rootTag || prevProps.rootTag.id !== rootTag.id || submitSucceeded)) {
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

  setEditingId = editingId => {
    this.setState({
      editingId
    });
  }

  onSave = values => {
    const { onUpdate, onCreate, isNew } = this.props;

    const clone = JSON.parse(JSON.stringify(values));

    if (!clone.weight) clone.weight = 1;

    delete clone.dragIndex;
    delete clone.parent;
    delete clone.refreshFlag;

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

    dispatch(change("TagsForm", "childTags", clone.childTags));

    this.counter++;
  };

  changeVisibility = (item: FormTag) => {
    const { dispatch, values } = this.props;
    dispatch(change("TagsForm", item.parent ? item.parent + ".status" : "status", item.status === "Private" ? "Show on website" : "Private"));
    dispatch(change("TagsForm", "refreshFlag", !values.refreshFlag));
  }

  removeChildTag = (item: FormTag) => {
    const { dispatch, values, openConfirm } = this.props;

    const confirmMessage = item.childrenCount
      ? `Deleting this tag will automatically delete ${item.childrenCount} 
    children tag${getPluralSuffix(item.childrenCount)}. ${
          item.taggedRecordsCount ? item.taggedRecordsCount + " records will be untagged. " : ""
        }After saving the records it cannot be undone`
      : "You are about to delete tag. After saving the records it cannot be undone";

    const onConfirm = () => {
      const clone = JSON.parse(JSON.stringify(values));

      const removePath = getDeepValue(clone, item.parent.replace(/\[[0-9]+]$/, ""));

      removePath && removePath.splice(Number(item.parent.match(/\[(\d)]$/)[1]), 1);

      dispatch(change("TagsForm", "childTags", clone.childTags));
      dispatch(change("TagsForm", "refreshFlag", !values.refreshFlag));
    };

    openConfirm({ onConfirm, confirmMessage, confirmButtonText: "DELETE" });
  };

  onDrop = (tagsTree: TreeData) => {
    const { dispatch, values } = this.props;

    dispatch(change("TagsForm", "childTags", treeDataToTags(tagsTree)));
    dispatch(change("TagsForm", "refreshFlag", !values.refreshFlag));
  };

  removeRequirement = index => {
    this.props.dispatch(arrayRemove("TagsForm", "requirements", index));
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

    const { editingId } = this.state;

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

              {values && (
                <TagsTree
                  rootTag={values}
                  classes={classes}
                  onDelete={this.removeChildTag}
                  changeVisibility={this.changeVisibility}
                  setEditingId={this.setEditingId}
                  onDrop={this.onDrop}
                  editingId={editingId}
                  syncErrors={syncErrors}
                />
              )}
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
  onSubmitFail,
  validate
})(connect(mapStateToProps, mapDispatchToProps)(withStyles(styles)(withRouter(TagsFormBase))));

export default TagsForm as ComponentClass<Props>;
