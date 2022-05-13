/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import {
  arrayInsert,
  arrayRemove,
  change,
  getFormSyncErrors,
  getFormValues,
  reduxForm
} from "redux-form";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { Tag } from "@api/model";
import { createStyles, withStyles } from "@mui/styles";
import { alpha } from "@mui/material/styles";
import { TreeData } from "@atlaskit/tree";
import { State } from "../../../reducers/state";
import { getDeepValue } from "../../../common/utils/common";
import { createTag, deleteTag, updateTag } from "../actions";
import { setNextLocation, showConfirm } from "../../../common/actions";
import { ShowConfirmCaller } from "../../../model/common/Confirm";
import { onSubmitFail } from "../../../common/utils/highlightFormClassErrors";
import { getPluralSuffix } from "../../../common/utils/strings";
import { AppTheme } from "../../../model/common/Theme";
import { FormTag } from "../../../model/tags";
import { validate } from "../utils/validation";
import { CatalogItemType } from "../../../model/common/Catalog";
import { EmptyTag, TAGS_FORM_NAME } from "../constants";

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
  checklistCardGrid: {
    gridTemplateColumns: "auto 1fr auto",
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


interface Props {
  isNew?: boolean;
  tags: CatalogItemType[];
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

export class TagsFormBase extends React.PureComponent<FormProps, FormState> {
  resolvePromise;

  rejectPromise;

  isPending;

  disableConfirm;

  counter;

  state = {
    editingId: null
  }

  constructor(props) {
    super(props);

    // New Tags counter
    this.counter = 1;
  }

  componentDidUpdate(prevProps) {
    const {
      fetch
    } = this.props;

    if (this.isPending && fetch && fetch.success === false) {
      this.isPending = false;
      this.rejectPromise();
    }
    if (this.isPending && fetch && fetch.success) {
      this.isPending = false;
      this.resolvePromise();
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
    const { dispatch } = this.props;

    const newTag: FormTag = {
      ...EmptyTag,
      id: ("new" + this.counter) as any,
    };

    dispatch(arrayInsert(TAGS_FORM_NAME, "childTags", 0, newTag));

    this.counter++;
  };

  changeVisibility = (item: FormTag) => {
    const { dispatch, values } = this.props;
    dispatch(change(TAGS_FORM_NAME, item.parent ? item.parent + ".status" : "status", item.status === "Private" ? "Show on website" : "Private"));
    dispatch(change(TAGS_FORM_NAME, "refreshFlag", !values.refreshFlag));
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

      dispatch(change(TAGS_FORM_NAME, "childTags", clone.childTags));
      dispatch(change(TAGS_FORM_NAME, "refreshFlag", !values.refreshFlag));
    };

    openConfirm({ onConfirm, confirmMessage, confirmButtonText: "DELETE" });
  };

  onDrop = (tagsTree: TreeData) => {
    const { dispatch, values } = this.props;

    dispatch(change(TAGS_FORM_NAME, "childTags", treeDataToTags(tagsTree)));
    dispatch(change(TAGS_FORM_NAME, "refreshFlag", !values.refreshFlag));
  };

  removeRequirement = index => {
    this.props.dispatch(arrayRemove(TAGS_FORM_NAME, "requirements", index));
  };

  validateRequirements = value => (value.length ? undefined : "At least one table should be selected before record can be saved");
}

const mapStateToProps = (state: State) => ({
  values: getFormValues(TAGS_FORM_NAME)(state),
  syncErrors: getFormSyncErrors(TAGS_FORM_NAME)(state),
  tags: state.tags.allTags,
  fetch: state.fetch,
  nextLocation: state.nextLocation
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  onUpdate: (id: number, tag: Tag) => dispatch(updateTag(id, tag)),
  onCreate: (tag: Tag) => dispatch(createTag(tag)),
  onDelete: (id: number) => dispatch(deleteTag(id)),
  openConfirm: props => dispatch(showConfirm(props)),
  setNextLocation: (nextLocation: string) => dispatch(setNextLocation(nextLocation)),
});

export const TagsFormWrapper = reduxForm({
  form: TAGS_FORM_NAME,
  onSubmitFail,
  validate,
  shouldError: () => true
})(connect(mapStateToProps, mapDispatchToProps)(withStyles(styles)((props: any) => <props.Root {...props} />))) as any;
