import React from "react";
import { Contact } from "@api/model";
import { FieldArray } from "redux-form";
import { Grid } from "@material-ui/core";
import DocumentsRenderer from "../../../../common/components/form/documents/DocumentsRenderer";

interface ContactsDocumentsProps {
  classes?: any;
  twoColumn?: boolean;
  values?: Contact;
  dispatch?: any;
  showConfirm?: any;
  form?: string;
}

const ContactsDocuments: React.FC<ContactsDocumentsProps> = props => {
  const {
    classes, twoColumn, values, dispatch, form, showConfirm
  } = props;

  return values ? (
    <Grid container className="p-3">
      <FieldArray
        name="documents"
        label="Documents"
        entity="Contact"
        classes={classes}
        component={DocumentsRenderer}
        xsGrid={12}
        mdGrid={twoColumn ? 6 : 12}
        lgGrid={twoColumn ? 4 : 12}
        dispatch={dispatch}
        form={form}
        showConfirm={showConfirm}
        rerenderOnEveryChange
      />
    </Grid>
  ) : null;
};

export default ContactsDocuments;
