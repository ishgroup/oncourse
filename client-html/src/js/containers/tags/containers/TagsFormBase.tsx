/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Tag } from '@api/model';
import { TreeData } from '@atlaskit/tree';
import { ShowConfirmCaller } from 'ish-ui';
import React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import {
  arrayPush,
  arrayRemove,
  change,
  getFormSyncErrors,
  getFormValues,
  InjectedFormProps,
  reduxForm
} from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { showConfirm } from '../../../common/actions';
import { IAction } from '../../../common/actions/IshAction';
import { onSubmitFail } from '../../../common/utils/highlightFormErrors';
import { getPluralSuffix } from '../../../common/utils/strings';
import { CatalogItemType } from '../../../model/common/Catalog';
import { Fetch } from '../../../model/common/Fetch';
import { FormTag } from '../../../model/tags';
import { State } from '../../../reducers/state';
import { createTag, deleteTag, updateTag } from '../actions';
import { treeDataToTags } from '../components/Trees';
import { EmptyTag, TAGS_FORM_NAME } from '../constants';
import { styles } from '../styles/TagItemsStyles';
import { COLORS, getAllTags, rootTagToServerModel } from '../utils';
import { validateTagsForm } from '../utils/validation';

interface Props {
  tags: CatalogItemType[];
  redirectOnDelete?: () => void;
  openConfirm?: ShowConfirmCaller;
  values: FormTag;
  classes: any;
  dispatch: any;
  className: string;
  fetch: Fetch;
  onUpdate: (tag: Tag) => void;
  onCreate: (tag: Tag) => void;
  onDelete: (tag: Tag) => void;
  openTagEditView: (item: Tag) => void;
  closeTagEditView: () => void;
  history: any;
  syncErrors?: any;
}

interface FormState {
  editingIds: number[];
}

export class TagsFormBase extends React.PureComponent<Props & InjectedFormProps<FormTag>, FormState> {
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

  componentDidUpdate() {
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

  setEditingIds = editingIds => {
    this.setState({
      editingIds
    });
  };

  setEditingId = editingId => {
    this.setEditingIds(this.state.editingIds.includes(editingId)
      ? this.state.editingIds.filter(id => id !== editingId)
      : this.state.editingIds.concat(editingId));
  };

  onSave = values => {
    const { onUpdate, onCreate } = this.props;

    const isNew = !values.id;
    
    const tags = rootTagToServerModel(values);

    this.isPending = true;

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;

      if (isNew) {
        onCreate(tags);
      } else {
        onUpdate(tags);
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
    const { dispatch, values, openConfirm, array, form } = this.props;

    const confirmMessage = item.childrenCount
      ? `Deleting this tag will automatically delete ${item.childrenCount} 
    children tag${getPluralSuffix(item.childrenCount)}. ${
          item.taggedRecordsCount ? item.taggedRecordsCount + " records will be untagged. " : ""
        }After saving the records it cannot be undone`
      : `You are about to delete ${item.type === "Checklist" ? "checklist item" : "tag"}. After saving the records it cannot be undone`;

    const onConfirm = () => {
      array.remove(item.parent.replace(/\[[0-9]+]$/, ""), parseInt(item.parent.match(/\[([0-9]+)]$/)[1]));
      dispatch(change(form, "refreshFlag", !values.refreshFlag));
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

const mapDispatchToProps = (dispatch: Dispatch<IAction>) => ({
  onUpdate: (tag: Tag) => dispatch(updateTag(TAGS_FORM_NAME, tag)),
  onCreate: (tag: Tag) => dispatch(createTag(TAGS_FORM_NAME, tag)),
  onDelete: (tag: Tag) => dispatch(deleteTag(tag)),
  openConfirm: props => dispatch(showConfirm(props)),
});

export const TagsFormWrapper = reduxForm<any, any, any>({
  form: TAGS_FORM_NAME,
  onSubmitFail,
  validate: validateTagsForm,
  shouldError: () => true
})(connect(mapStateToProps, mapDispatchToProps)(withStyles((props: any) => <props.Root {...props} />, styles)));