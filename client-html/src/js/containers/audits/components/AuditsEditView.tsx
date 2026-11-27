/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Audit } from '@api/model';
import { Table, TableBody, TableCell, TableHead, TableRow, Typography } from '@mui/material';
import Grid from '@mui/material/Grid';
import $t from '@t';
import * as React from 'react';
import FormField from '../../../common/components/form/formFields/FormField';
import { EditViewProps } from '../../../model/common/ListView';

const AuditsEditView = ({ values }: EditViewProps<Audit>) => (
  <Grid container columnSpacing={3} rowSpacing={2} className="p-3 overflow-hidden">
    <Grid size={12}>
      <FormField disabled type="dateTime" name="created" label={$t('date_and_time')} />
    </Grid>
    <Grid size={12}>
      <FormField disabled type="text" name="entityIdentifier" label={$t('entity_name')} />
    </Grid>
    <Grid size={12}>
      <FormField disabled type="text" name="entityId" label={$t('entity_id')} />
    </Grid>
    <Grid size={12}>
      <FormField disabled type="text" name="action" label={$t('action')} />
    </Grid>
    {values?.source && (
      <Grid size={12}>
        <FormField disabled type="text" name="source" label={$t('source')} />
      </Grid>
    )}
    {values?.requestText && (
      <Grid size={12}>
        <FormField disabled type="text" name="requestText" label={$t('request_text')} multiline />
      </Grid>
    )}
    {values?.changeSetRef && (
      <Grid size={12}>
        <FormField disabled type="text" name="changeSetRef" label={$t('change_set')} />
      </Grid>
    )}
    <Grid size={12}>
      <FormField disabled type="text" name="message" label={$t('message')} multiline />
    </Grid>
    {Boolean(values?.fieldChanges?.length) && (
      <Grid size={12}>
        <Typography variant="caption" color="textSecondary">
          {$t('changed_values')}
        </Typography>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>{$t('field')}</TableCell>
              <TableCell>{$t('before')}</TableCell>
              <TableCell>{$t('after')}</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {values.fieldChanges.map(change => (
              <TableRow key={change.name}>
                <TableCell>{change.name}</TableCell>
                <TableCell className="text-pre-wrap">{change.before}</TableCell>
                <TableCell className="text-pre-wrap">{change.after}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Grid>
    )}
  </Grid>
);

export default AuditsEditView;
