import React from "react";
import { Contact } from "@api/model";
import { FieldArray } from "redux-form";
import { Grid } from "@mui/material";
import Divider from "@mui/material/Divider";
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
    <Grid container columnSpacing={3} className="pl-3 pr-3">
      <Grid item xs={12}>
        <Divider className="mb-2 mt-2" />
      </Grid>
      
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

      <Grid item xs={12}>
        <Divider className="mb-1 mt-2" />
      </Grid>
    </Grid>
  ) : null;
};

export default ContactsDocuments;
