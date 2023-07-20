/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import {
  arrayPush,
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
import { showConfirm } from "../../../common/actions";
import { ShowConfirmCaller } from "../../../../ish-ui/model/Confirm";
import { onSubmitFail } from "../../../common/utils/highlightFormErrors";
import { getPluralSuffix } from "../../../common/utils/strings";
import { AppTheme } from "../../../../ish-ui/model/Theme";
import { FormTag } from "../../../model/tags";
import { validate } from "../utils/validation";
import { CatalogItemType } from "../../../model/common/Catalog";
import { EmptyTag, TAGS_FORM_NAME } from "../constants";
import { COLORS, getAllTags } from "../utils";

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
  onDelete: (tag: Tag) => void;
  openTagEditView: (item: Tag) => void;
  closeTagEditView: () => void;
  history: any;
  syncErrors?: any;
}

const setWeight = items =>
  items.map((i, index) => {
    let item = { ...i, weight: index + 1 };

    delete item.dragIndex;
    delete item.parent;
    delete item.refreshFlag;

    if (item.id.toString().includes("new")) {
      item.id = null;
    }

    if (item.childTags.length) {
      item = { ...item, childTags: setWeight([...item.childTags]) };
    }

    return item;
});

const treeItemDataToTag = (id: number | string, tree: TreeData, allTags: Tag[]): Tag => {
  const tag = { ...allTags.find(t => t.id === id) };
  tag.childTags = tree.items[id].children.map(id => treeItemDataToTag(id, tree, allTags));
  return tag;
};

const treeDataToTags = (tree: TreeData, allTags: Tag[]): Tag[] => tree.items[tree.rootId].children.map(id => treeItemDataToTag(id, tree, allTags));

interface FormState {
  editingIds: number[];
}

export class TagsFormBase extends React.PureComponent<FormProps, FormState> {
  resolvePromise;

  rejectPromise;

  isPending;

  counter;

  state = {
    editingIds: []
  };

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
      editingIds: this.state.editingIds.includes(editingId)
        ? this.state.editingIds.filter(id => id !== editingId)
        : this.state.editingIds.concat(editingId)
    });
  };

  onSave = values => {
    const { onUpdate, onCreate } = this.props;

    const isNew = !values.id;
    
    const tags = { ...values, childTags: setWeight([...values.childTags]) };

    delete tags.dragIndex;
    delete tags.parent;
    delete tags.refreshFlag;

    if (!tags.weight) tags.weight = 1;

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

  onDelete = (tag: Tag) => {
    const { onDelete, redirectOnDelete } = this.props;

    this.isPending = true;

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      onDelete(tag);
    }).then(() => {
      if (redirectOnDelete) redirectOnDelete();
    });
  };

  addTag = () => {
    const { dispatch } = this.props;

    const newTag: FormTag = {
      ...EmptyTag,
      color: COLORS[Math.floor(Math.random() * COLORS.length)],
      id: ("new" + this.counter) as any,
    };

    dispatch(arrayPush(TAGS_FORM_NAME, "childTags", newTag));
    this.setEditingId(newTag.id);

    this.counter++;
  };

  changeVisibility = (item: FormTag) => {
    const { dispatch, values } = this.props;
    dispatch(change(TAGS_FORM_NAME, item.parent ? item.parent + ".status" : "status", item.status === "Private" ? "Show on website" : "Private"));
    dispatch(change(TAGS_FORM_NAME, "refreshFlag", !values.refreshFlag));
  };

  removeChildTag = (item: FormTag) => {
    const { dispatch, values, openConfirm } = this.props;

    const confirmMessage = item.childrenCount
      ? `Deleting this tag will automatically delete ${item.childrenCount} 
    children tag${getPluralSuffix(item.childrenCount)}. ${
          item.taggedRecordsCount ? item.taggedRecordsCount + " records will be untagged. " : ""
        }After saving the records it cannot be undone`
      : `You are about to delete ${item.type === "Checklist" ? "checklist item" : "tag"}. After saving the records it cannot be undone`;

    const onConfirm = () => {
      const clone = JSON.parse(JSON.stringify(values));

      if (item.parent) {
        const removePath = getDeepValue(clone, item.parent.replace(/\[[0-9]+]$/, ""));

        if (removePath) {
          const deleteItem = item.parent.match(/\[(\d+)]$/);
          if (deleteItem && deleteItem.length > 0) removePath.splice(Number(deleteItem[1]), 1);
        }
      }

      dispatch(change(TAGS_FORM_NAME, "childTags", clone.childTags));
      dispatch(change(TAGS_FORM_NAME, "refreshFlag", !values.refreshFlag));
    };

    openConfirm({ onConfirm, confirmMessage, confirmButtonText: "DELETE" });
  };

  onDrop = (tagsTree: TreeData) => {
    const { dispatch, values } = this.props;
    dispatch(change(TAGS_FORM_NAME, "childTags", treeDataToTags(tagsTree, getAllTags([values]))));
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
  fetch: state.fetch
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  onUpdate: (id: number, tag: Tag) => dispatch(updateTag(id, tag)),
  onCreate: (tag: Tag) => dispatch(createTag(tag)),
  onDelete: (tag: Tag) => dispatch(deleteTag(tag)),
  openConfirm: props => dispatch(showConfirm(props)),
});

export const TagsFormWrapper = reduxForm<any, any, any>({
  form: TAGS_FORM_NAME,
  onSubmitFail,
  validate,
  shouldError: () => true
})(connect(mapStateToProps, mapDispatchToProps)(withStyles(styles)((props: any) => <props.Root {...props} />)));