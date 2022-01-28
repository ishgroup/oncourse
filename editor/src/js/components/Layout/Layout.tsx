/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from 'react';
import { withStyles } from '@material-ui/core/styles';
import clsx from 'clsx';
import { Grid } from '@material-ui/core';

const styles = () => ({
  contentWrapper: {
    padding: '15px 25px',
    '& .block-wrapper': {
      '& .editor-wrapper.ace-wrapper .ace_editor, .mde-textarea-wrapper .mde-text, .mde-preview .mde-text': {
        height: 'calc(100vh - 160px) !important',
        minHeight: 250,
      },
      '& #editorRoot .ck-editor .ck-source-editing-button': {
        marginRight: '35px'
      },
      '& #editorRoot .content-mode': {
        top: '15px'
      },
    },
    '& .fullscreen-page-block': {
      height: 'calc(100vh - 30px)',
      '& .react-resizable': {
        width: '100% !important',
        height: '100% !important',
        '& .react-resizable-handle': {
          display: 'none',
        },
      },
      '& .editor-wrapper.ace-wrapper .ace_editor, .mde-textarea-wrapper .mde-text, .mde-preview .mde-text, & .editor-wrapper .mde-preview .ck.ck-editor__main': {
        height: 'calc(100vh - 145px) !important',
        minHeight: 250,
      }
    },
    '& .react-resizable': {
      '& .react-resizable-handle': {
        display: 'none',
      },
      '&:hover .react-resizable-handle': {
        display: 'block',
      }
    }
  },
  fullHeight: {
    minHeight: '100vh',
    height: '100vh',
  },
});

interface Props {
  classes: any;
  sidebar?: any;
  content?: any;
  fullHeight?: boolean;
}

const Layout = (props: Props) => {
  const {
    classes, sidebar, fullHeight, content
  } = props;

  return (
    <>
      <Grid container className={clsx(fullHeight && classes.fullHeight)}>
        {sidebar
          && sidebar}

        <Grid item xs={sidebar ? 10 : 12} className={classes.contentWrapper}>
          {content}
        </Grid>
      </Grid>
    </>
  );
};

export default (withStyles(styles)(Layout));
