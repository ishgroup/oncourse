import React, {useCallback, useEffect, useState} from "react";
import {reduxForm, getFormValues, isDirty, initialize, FieldArray} from "redux-form";
import {Dispatch} from "redux";
import {connect} from "react-redux";
import withStyles from "@material-ui/core/styles/withStyles";
import {
  clearWebDavFileContents,
  getWebDavFileContent,
  getWevDavDirectoryContents,
  setWevDavClient,
  updateWebDavFileContents,
} from "../../common/webdav/actions/webDavActions";
import {State} from "../../reducers/state";
import SettingSection from "./components/SettingSection";
import {settingFileName, webDavSrcSitePath} from "../../common/webdav/constants/webDavConfig";
import {formatSectionField} from "../../common/webdav/utils";
import FormSubmitButton from "../../common/components/form/FormSubmitButton";
import AppBar from "../../common/components/layout/AppBar";

const WEBDAV_FORM_NAME = "WebDavForm";

const styles: any = theme => ({
  wrapperOuter: {
    position: "relative",
    margin: -15,
    paddingTop: 66,
  },
  wrapperInner: {
    padding: "20px 15px",
    overflowY: "auto",
    height: "calc(100vh - 86px)",
  },
  root: {
    width: '100%',
  },
  settingsSection: {
    marginBottom: 20,
  },
  detailsRoot: {
    flexGrow: 1,
  },
  heading: {
    fontSize: theme.typography.pxToRem(15),
    fontWeight: theme.typography.fontWeightBold,
    textTransform: "uppercase",
  },
});

const StylesSettingsForm: React.FC<any> = props => {
  const {
    classes, dirty, valid, dispatch, handleSubmit, credentials, setClient, client, getFileContent, fileContent,
    updateFileContents,
  } = props;

  const [stylesCode, setStylesCode] = useState("");

  useEffect(() => {
    if (fileContent) {
      const {
        sections,
        code,
      } = formatSectionField(fileContent);
      setStylesCode(code);
      dispatch(initialize(WEBDAV_FORM_NAME, {webDavSettings: sections}));
    }
  }, [fileContent]);

  useEffect(() => {
    if (credentials) {
      if (client === null) {
        setClient(`${window.location.origin}:443`, {
          username: credentials.email,
          password: credentials.password,
        });
      } else {
        getFileContent(`${webDavSrcSitePath}/${settingFileName}`, {format: "text"});
      }
    }
  }, [client, credentials]);

  const onSubmit = useCallback(values => {
    let updatedCode = stylesCode;

    if (values && values.webDavSettings) {
      values.webDavSettings.forEach(section => {
        section.items.forEach(item => {
          const codeRegex = new RegExp(`\\${item.setting}`, "gm");

          updatedCode = updatedCode.replace(codeRegex, `$${item.name}: ${item.value};`);
        });
      });
      updateFileContents(`${webDavSrcSitePath}/${settingFileName}`, updatedCode, {contentLength: false});
    }
  }, [stylesCode]);

  return (
    <div className={classes.wrapperOuter}>
      <form autoComplete="off" onSubmit={handleSubmit(onSubmit)}>
        <AppBar
          title="Fonts and colors"
        >
          <FormSubmitButton
            disabled={!dirty}
            invalid={!valid}
          />
        </AppBar>
        <div className={classes.wrapperInner}>
          {fileContent && (
            <FieldArray
              name="webDavSettings"
              component={SettingSection}
              classes={classes}
            />
          )}
        </div>
      </form>
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  value: getFormValues(WEBDAV_FORM_NAME)(state),
  isDirty: isDirty(WEBDAV_FORM_NAME)(state),
  client: state.webDav.client,
  fileContent: state.webDav.fileContent,
  directoryContents: state.webDav.directoryContents,
  credentials: state.auth.credentials,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getFileContent: (file, options?: any) => dispatch(getWebDavFileContent(file, options)),
  clearFileContents: () => dispatch(clearWebDavFileContents()),
  setClient: (url, options) => dispatch(setWevDavClient(url, options)),
  getDirectoryContents: (path, options) => dispatch(getWevDavDirectoryContents(path, options)),
  updateFileContents: (filename, data, options) => dispatch(updateWebDavFileContents(filename, data, options)),
});

const StylesSettings = reduxForm({
  form: WEBDAV_FORM_NAME,
})(StylesSettingsForm);

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(StylesSettings));
