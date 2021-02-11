/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { State } from "../../../../../reducers/state";
import { onSubmitFail } from "../../../../utils/highlightFormClassErrors";
import FullScreenEditView from "./FullScreenEditView";
import { clearListNestedEditRecord, closeListNestedEditRecord, updateListNestedEditRecord } from "../../actions";
import { ApiMethods } from "../../../../../model/common/apiHandlers";

class NestedEditView extends React.Component<any, any> {
  onClose = index => {
    const {
      closeListNestedEditRecord,
      clearListNestedEditRecord,
      nestedEditRecords,
      updateRoot,
      rootId,
      creatingNew,
      updateSelection
    } = this.props;

    const callback = () => {
      if (rootId && nestedEditRecords.length === 1) {
        updateRoot(rootId);
      }

      closeListNestedEditRecord(index);

      setTimeout(() => {
        clearListNestedEditRecord(index);
      }, 500);

      if (creatingNew) {
        updateSelection([]);
      }
    };

    callback();
  };

  onSave = (record, index, entity, formProps) => {
    const {
      updateListNestedEditRecord, rootEntity, onBeforeSave, creatingNew, onSave
    } = this.props;

    if (creatingNew) {
      onSave(record);
      return;
    }

    if (typeof onBeforeSave === "function") {
      const argsObj = {
        formProps,
        onSave: updateListNestedEditRecord,
        onSaveArgs: [record.id, record, entity, index, rootEntity]
      };

      onBeforeSave(argsObj);
      return;
    }

    updateListNestedEditRecord(record.id, record, entity, index, rootEntity);
  };

  onSubmit = (record, dispatch, formProps, r, index) => {
    const { fetch: { pending } } = this.props;

    if (pending) {
      return;
    }

    return (r.customOnSave
      ? r.customOnSave(record, dispatch, formProps)
      : this.onSave(record, index, r.entity, formProps));
  }

  render() {
    const {
      nestedEditRecords,
      EditViewContent,
      nestedEditFields,
      nameCondition,
      showConfirm,
      openNestedEditView,
      manualLink,
      validate,
      creatingNew
    } = this.props;

    return nestedEditRecords.map((r, index) => (
      <FullScreenEditView
        validate={validate}
        hideFullScreenAppBar={r.hideFullScreenAppBar}
        manualLink={manualLink}
        nameCondition={nameCondition}
        key={index + r.record.id}
        rootEntity={r.entity}
        form={`NestedEditViewForm[${index}]`}
        fullScreenEditView={r.opened}
        toogleFullScreenEditView={() => this.onClose(index)}
        EditViewContent={
          nestedEditFields && nestedEditFields[r.entity] ? nestedEditFields[r.entity] : EditViewContent
        }
        onSubmit={(record, dispatch, formProps) => this.onSubmit(record, dispatch, formProps, r, index)}
        creatingNew={r.opened ? creatingNew : false}
        nestedIndex={index}
        showConfirm={showConfirm}
        openNestedEditView={openNestedEditView}
        onSubmitFail={onSubmitFail}
        hasSelected
        isNested
      />
    ));
  }
}

const mapFormStateToProps = (state: State) => ({
  fetch: state.fetch,
  nestedEditRecords: state.list.nestedEditRecords,
  rootId: state.list.editRecord && state.list.editRecord.id
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  clearListNestedEditRecord: (index: number) => dispatch(clearListNestedEditRecord(index)),
  closeListNestedEditRecord: (index: number) => dispatch(closeListNestedEditRecord(index)),
  updateListNestedEditRecord: (
    id: number,
    record: any,
    entity: string,
    index: number,
    listRootEntity: string,
    method?: ApiMethods
  ) => dispatch(updateListNestedEditRecord(id, record, entity, index, listRootEntity, method))
});

export default connect<any, any, any>(
  mapFormStateToProps,
  mapDispatchToProps
)(NestedEditView);
