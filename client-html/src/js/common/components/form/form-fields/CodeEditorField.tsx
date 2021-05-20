/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { FunctionComponent } from "react";
import clsx from "clsx";
import AceEditor from "react-ace";
import "ace-builds/src-noconflict/mode-groovy";
import "ace-builds/src-noconflict/theme-textmate";
import { withStyles, createStyles } from "@material-ui/core/styles";

const styles = theme =>
  createStyles({
    editor: {
      filter: theme.palette.type === "dark" ? "invert(80%)" : "",
      zIndex: 0,
      outline: "none"
    },
    errorBackground: {
      "&.ace-tm": {
        backgroundColor: theme.palette.error.light
      }
    },
    errorMessage: {
      position: "absolute",
      top: "50%",
      left: "50%",
      transform: "translate(calc(-50% + 21px), -50%)",
      color: "#fff",
      fontFamily: "sans-serif",
      zIndex: 1
    },
    queryMarker: {
      borderRadius: "4px",
      width: "55px",
      paddingLeft: "4px",
      height: "25px",
      background: "#fff",
      cursor: "pointer",
      position: "absolute",
      zIndex: 2,
      boxShadow: theme.shadows[1],
      "&:after": {
        content: "'Query'",
        marginLeft: "2px"
      },
      "&:hover": {
        background: theme.palette.grey[100]
      }
    }
  });

const CodeEditorField = props => {
  const {
    input,
    meta: { error },
    classes,
    errorMessage,
    disabled,
    onFocus
  } = props;

  return (
    <>
      {error && <div className={classes.errorMessage}>{errorMessage || error}</div>}
      <AceEditor
        mode="groovy"
        theme="textmate"
        value={input.value || " "}
        onFocus={onFocus}
        onChange={input.onChange}
        fontSize={14}
        showPrintMargin={false}
        showGutter
        highlightActiveLine
        width="100%"
        maxLines={Infinity}
        className={clsx(classes.editor, {
          [classes.errorBackground]: error
        })}
        wrapEnabled
        setOptions={{
          enableBasicAutocompletion: true,
          enableLiveAutocompletion: true,
          enableSnippets: true,
          showLineNumbers: true,
          readOnly: disabled,
          tabSize: 2
        }}
        editorProps={{ $blockScrolling: Infinity }}
      />
    </>
  );
};

export default withStyles(styles)(CodeEditorField) as FunctionComponent<any>;
