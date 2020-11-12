import React, { useEffect, useMemo, useRef } from "react";
import { Message } from "@api/model";
import { Dispatch } from "redux";
import { Grid } from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import { format } from "date-fns";
import Uneditable from "../../../../common/components/form/Uneditable";
import { III_DD_MMM_YYYY } from "../../../../common/utils/dates/format";
import { defaultContactName } from "../../contacts/utils";

interface MessageEditViewProps {
  values?: Message;
  isNew?: boolean;
  isNested?: boolean;
  classes?: any;
  dispatch?: Dispatch<any>;
  dirty?: boolean;
  form?: string;
  nestedIndex?: number;
  rootEntity?: string;
  twoColumn?: boolean;
  showConfirm?: any;
  openNestedEditView?: any;
  manualLink?: string;
}

const MessageEditView = React.memo<MessageEditViewProps>(props => {
  const { classes, twoColumn, values } = props;

  const htmlRef = useRef<HTMLDivElement>();

  useEffect(() => {
    if (htmlRef.current && !htmlRef.current.shadowRoot) {
      htmlRef.current.attachShadow({ mode: 'open' });
    }
  }, [htmlRef.current]);

  useEffect(() => {
    if (values && values.htmlMessage && htmlRef.current && htmlRef.current.shadowRoot) {
      htmlRef.current.shadowRoot.innerHTML = values.htmlMessage;
    }
  }, [values && values.htmlMessage]);

  const createdOn = useMemo(() => format(new Date(values.createdOn), III_DD_MMM_YYYY), [values.createdOn]);

  return (
    <div className="p-3">
      <Uneditable value={values.subject} label="Subject" />
      <Grid container>
        <Grid item xs={twoColumn ? 2 : 6}>
          <Uneditable
            value={defaultContactName(values.sentToContactFullname)}
            label="Sent to"
            url={`/contact?search=messages.message.id=${values.id}`}
          />
        </Grid>
        <Grid item xs={twoColumn ? 2 : 6}>
          <Uneditable value={createdOn} label="Created on" />
        </Grid>
      </Grid>
      <Grid container spacing={2}>
        {values.message && (
          <Grid item xs={twoColumn ? 6 : 12}>
            <Typography variant="caption" color="textSecondary">
              Message
            </Typography>
            <Typography variant="body1" component="div">
              <Card>
                <CardContent className="overflow-auto">
                  <code>
                    <pre className="text-pre-wrap">{values.message}</pre>
                  </code>
                </CardContent>
              </Card>
            </Typography>
          </Grid>
        )}

        <Grid item xs={twoColumn ? 6 : 12} className={values.htmlMessage ? undefined : "d-none"}>
          <Typography variant="caption" color="textSecondary">
            HTML message
          </Typography>
          <Card>
            <CardContent>
              <div className="overflow-auto" ref={htmlRef} dangerouslySetInnerHTML={{ __html: values.htmlMessage }} />
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} className={values.sms ? undefined : "d-none"}>
          <Uneditable value={values.sms} label="SMS" />
        </Grid>

        <Grid item xs={12} className={values.postDescription ? undefined : "d-none"}>
          <Uneditable value={values.postDescription} label="Post description" />
        </Grid>

        <Grid item xs={12}>
          <Uneditable value={values.creatorKey} label="Creator key" />
        </Grid>
      </Grid>
    </div>
  );
});

export default props => (props.values ? <MessageEditView {...props} /> : null);
