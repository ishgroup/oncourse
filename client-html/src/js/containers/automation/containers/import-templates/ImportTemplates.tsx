import React, { useEffect, useMemo } from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router";
import { getFormValues, initialize, reduxForm } from "redux-form";
import { Dispatch } from "redux";
import { ExportTemplate } from "@api/model";
import { onSubmitFail } from "../../../../common/utils/highlightFormClassErrors";
import { State } from "../../../../reducers/state";
import { showConfirm } from "../../../../common/actions";
import ImportTemplatesForm from "./containers/ImportTemplatesForm";
import {
  createImportTemplate,
  getImportTemplate,
  updateImportTemplate,
  updateInternalImportTemplate,
  removeImportTemplate
} from "./actions";
import { usePrevious } from "../../../../common/utils/hooks";

export const IMPORT_TEMPLATES_FORM_NAME = "ImportTemplatesForm";

const initialDefault: ExportTemplate = {
  name: null,
  keyCode: null,
  entity: null,
  body: null,
  enabled: false,
  variables: [],
  options: []
};

const ImportTemplates = React.memo<any>(props => {
  const {
    dispatch,
    getImportTemplate,
    match: {
      params: { id }
    },
    ...rest
  } = props;

  const prevId = usePrevious(id);

  const isNew = useMemo(() => id === "new", [id]);

  useEffect(() => {
    if (id && prevId !== id) {
      if (isNew) {
        dispatch(initialize(IMPORT_TEMPLATES_FORM_NAME, initialDefault));
      } else {
        getImportTemplate(id);
      }
    }
  }, [id, prevId]);

  return <ImportTemplatesForm dispatch={dispatch} isNew={isNew} {...rest} />;
});

const mapStateToProps = (state: State) => ({
  values: getFormValues(IMPORT_TEMPLATES_FORM_NAME)(state)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onCreate: (template: ExportTemplate) => dispatch(createImportTemplate(template)),
  onUpdate: (template: ExportTemplate) => dispatch(updateImportTemplate(template)),
  onUpdateInternal: (template: ExportTemplate) => dispatch(updateInternalImportTemplate(template)),
  onDelete: (id: number) => dispatch(removeImportTemplate(id)),
  openConfirm: (onConfirm: any, confirmMessage?: string) => dispatch(showConfirm(onConfirm, confirmMessage)),
  getImportTemplate: (id: number) => dispatch(getImportTemplate(id))
});

export default reduxForm({
  form: IMPORT_TEMPLATES_FORM_NAME,
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withRouter(ImportTemplates)));
