/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import clsx from "clsx";
import AceEditor from "react-ace";
import "ace-builds/src-noconflict/mode-groovy";
import "ace-builds/src-noconflict/theme-textmate";
import { createStyles, withStyles } from "@mui/styles";
import { CodeFieldProps } from "../../../../model/common/Fields";

const styles = theme =>
  createStyles({
    editor: {
      filter: theme.palette.mode === "dark" ? "invert(80%)" : "",
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

const CodeEditorField = ({
   input,
   meta: { error },
   classes,
   errorMessage,
   disabled,
   onFocus,
   className
 }: CodeFieldProps) => {
  return (
    <div id={input.name} className={className}>
      {error && <div className={classes.errorMessage}><div className="shakingError">{errorMessage || error}</div></div>}
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
    </div>
  );
};

export default withStyles(styles)(CodeEditorField);