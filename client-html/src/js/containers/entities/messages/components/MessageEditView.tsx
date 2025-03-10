/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Message } from '@api/model';
import { Card, CardContent, Grid } from '@mui/material';
import Typography from '@mui/material/Typography';
import $t from '@t';
import { format } from 'date-fns';
import { III_DD_MMM_YYYY } from 'ish-ui';
import React, { useEffect, useMemo, useRef } from 'react';
import { Dispatch } from 'redux';
import { ContactLinkAdornment } from '../../../../common/components/form/formFields/FieldAdornments';
import Uneditable from '../../../../common/components/form/formFields/Uneditable';

interface MessageEditViewProps {
  values?: Message;
  isNew?: boolean;
  isNested?: boolean;
  classes?: any;
  dispatch?: Dispatch<any>;
  dirty?: boolean;
  form?: string;
  rootEntity?: string;
  twoColumn?: boolean;
  showConfirm?: any;
  manualLink?: string;
}

const MessageEditView = React.memo<MessageEditViewProps>(props => {
  const { twoColumn, values } = props;

  const htmlRef = useRef<HTMLDivElement>(undefined);

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
    <div className="p-3 saveButtonTableOffset">
      <Grid container columnSpacing={3} rowSpacing={2} className="mb-2">
        {!twoColumn && (<Grid item xs={12}><Uneditable value={values.subject} label={$t('subject')} /></Grid>)}
        <Grid item xs={twoColumn ? 2 : 6}>
          <Uneditable
            value={values.sentToContactFullname}
            label={$t('sent_to')}
            labelAdornment={(
              <ContactLinkAdornment id={values?.contactId} />
            )}
          />
        </Grid>
        <Grid item xs={twoColumn ? 2 : 6}>
          <Uneditable value={createdOn} label={$t('created_on')} />
        </Grid>
      </Grid>
      <Grid container columnSpacing={3} rowSpacing={2}>
        {values.message && (
          <Grid item xs={twoColumn ? 6 : 12}>
            <Typography variant="caption" color="textSecondary">
              {$t('message')}
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
            {$t('html_message')}
          </Typography>
          <Card>
            <CardContent>
              <div className="overflow-auto" ref={htmlRef} />
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} className={values.sms ? undefined : "d-none"}>
          <Uneditable value={values.sms} label={$t('sms')} />
        </Grid>

        <Grid item xs={12} className={values.postDescription ? undefined : "d-none"}>
          <Uneditable value={values.postDescription} label={$t('post_description')} />
        </Grid>

        <Grid item xs={12}>
          <Uneditable value={values.creatorKey} label={$t('creator_key')} />
        </Grid>
      </Grid>
    </div>
  );
});

export default props => (props.values ? <MessageEditView {...props} /> : null);
