/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { makeStyles } from '@material-ui/core/styles';
import { AppTheme } from '../../../styles/themeInterface';

export const useStyles = makeStyles((theme: AppTheme) => ({
  hoverIcon: {
    opacity: 0.5,
    visibility: 'hidden',
    marginLeft: theme.spacing(1)
  },
  editable: {
    display: 'inline-flex',
    color: theme.palette.text.primary,
    minHeight: '32px',
    padding: '4px 0 4px',
    marginTop: theme.spacing(2),
    fontWeight: 400,
    justifyContent: 'space-between',
    alignItems: 'flex-end',
    '&:hover $hoverIcon': {
      visibility: 'visible'
    },
    '&:before': {
      borderBottom: '1px solid transparent',
      left: 0,
      bottom: '4px',
      content: "' '",
      position: 'absolute',
      right: 0,
      transition: theme.transitions.create('border-bottom-color', {
        duration: theme.transitions.duration.standard,
        easing: theme.transitions.easing.easeInOut
      }),
      pointerEvents: 'none'
    },
    '&:hover:before': {
      borderBottom: `1px solid ${theme.palette.primary.main}`
    },
  },
  editorArea: {
    '&#editorRoot': {
      cursor: 'pointer',
      overflow: 'auto',
      position: 'relative',
      flex: 1,
      '& .ck-dropdown__button .ck-button__label': {
        fontSize: '14px'
      },
      '& .ck-list__item .ck-button': {
        height: 'unset',
        padding: '5px 10px'
      },
      '& .ck-list__item .ck-heading_heading3': {
        fontSize: '15px'
      },
      '& .ck-list__item .ck-heading_heading4': {
        fontSize: '14px'
      },
      '& .ck-toolbar': {
        height: '45px',
        background: theme.table.contrastRow.main
      },
      '& .ck-list__item': {
        fontSize: '15px'
      },
      '& .ck-dropdown': {
        fontSize: '15px'
      },
      '& .ck.ck-dropdown .ck-button__label': {
        fontFamily: 'Inter, sans-serif',
        overflow: 'visible',
        width: '120px'
      },
      '& .content-mode-wrapper': {
        position: 'absolute',
        right: '10px',
        top: '6px',
        padding: '5px',
        zIndex: 1000,
        '& .content-mode': {
          maxWidth: '85px',
          border: 0,
          boxShadow: 'none',
          backgroundColor: 'black',
          color: 'white',
          padding: '2px',
          fontSize: '9px'
        }
      },
      '&.block-editor .content-mode-switch': {
        top: '15px'
      },
      '& .ck-editor': {
        height: '100%',
        '& .ck-editor__main': {
          height: 'calc(100% - 45px)'
        },
        '& .ck-source-editing-area': {
          height: '100%',
        },
        '& .ck-source-editing-area textarea': {
          overflow: 'auto'
        },
        '& .ck-content': {
          fontFamily: 'Inter, sans-serif',
          fontWeight: 400,
          fontSize: '14px',
          color: 'black',
          height: '100%'
        },
        '& .ck-content .table': {
          width: 'auto',
          marginLeft: 0
        },
        '& .ck-toolbar': {
          padding: '3px'
        },
        '& .ck-source-editing-button': {
          marginLeft: 'auto',
          marginRight: '68px'
        },
        '& .ck-source-editing-button > *': {
          visibility: 'hidden'
        },
        '& .ck-source-editing-button .ck_source_edit_custom': {
          visibility: 'visible'
        },
        '& .ck-source-editing-button .ck_code_icon_custom': {
          width: '24px'
        }
      },
      '&.ace-wrapper': {
        border: `1px solid ${theme.palette.divider}`,
        paddingTop: '44px',
        background: theme.table.contrastRow.main,
        '& .ace_editor': {
          borderTop: `1px solid ${theme.palette.divider}`
        }
      }
    }
  },
  previewFrame: {
    width: '100%',
    maxHeight: '300px',
    overflow: 'auto',
    fontSize: '14px',
    '& > div': {
      width: '100%'
    },
    '& h1,h2,h3,h4': {
      all: 'revert'
    },
    '& table': {
      marginLeft: 0,
      marginRight: 'auto',
      borderCollapse: 'collapse',
      borderSpacing: 0,
      border: '1px double #b3b3b3',
      '& th': {
        textAlign: 'left',
        fontWeight: 700,
        background: 'hsla(0,0%,0%,5%)',
        minWidth: '2em',
        padding: '0.4em',
        border: '1px solid #bfbfbf'
      },
      '& td': {
        minWidth: '2em',
        padding: '0.4em',
        border: '1px solid #bfbfbf'
      },
    }
  },
  readonly: {
    fontWeight: 300,
    pointerEvents: 'none'
  },
  textField: {
    paddingLeft: '0',
    // @ts-ignore
    paddingBottom: `${theme.spacing(2) - 3}`,
    margin: 0
  },
  '@global': {
    '.ck.ck-balloon-panel': {
      zIndex: 2000
    }
  }
}));
