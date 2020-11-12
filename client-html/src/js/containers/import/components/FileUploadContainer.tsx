import React, {
 useCallback, useEffect, useMemo
} from "react";
import clsx from "clsx";
import { useDropzone } from "react-dropzone";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import Typography from "@material-ui/core/Typography";
import IconButton from "@material-ui/core/IconButton";
import Grid from "@material-ui/core/Grid";
import DeleteIcon from "@material-ui/icons/Delete";
import Button from "../../../common/components/buttons/Button";

const styles = theme => createStyles({
  uploadContainer: {
    border: "2px dashed lightgray",
    borderRadius: "15px",
    color: "gray"
  },
  uploadButton: {
    color: "gray",
    paddingLeft: theme.spacing(10),
    paddingRight: theme.spacing(10),
    borderRadius: "7px"
  },
  fileType: {
    color: theme.heading.color
  },
  deleteButton: {
    marginLeft: "12px",
    padding: "3px"
  },
  deleteIcon: {
    width: "0.75em",
    height: "0.75em",
    color: theme.palette.action.active,
    opacity: 0.6
  }
});

const FileUploadContainer: React.FC<any> = props => {
  const { classes, files, setSelectedFiles } = props;

  const { acceptedFiles, getRootProps, getInputProps } = useDropzone();

  useEffect(() => {
    setSelectedFiles([...files, ...acceptedFiles]);
  }, [acceptedFiles]);

  const fileRenderer = useMemo(() => (files.length > 0 ? (
    <div>
      <Typography color="textSecondary" variant="caption" display="block">
        contactCsv
      </Typography>
      {files.map((file, index) => (
        <div key={index} className="centeredFlex">
          <Typography className="flex-fill text-truncate" variant="body1">
            {file.name}
          </Typography>
          <IconButton className={classes.deleteButton} onClick={() => onRemoveFile(index)}>
            <DeleteIcon className={classes.deleteIcon} />
          </IconButton>
        </div>
        ))}
    </div>
  ) : null), [files]);

  const onRemoveFile = useCallback(
    i => {
      setSelectedFiles(
        files.filter((file, index) => (i !== index ? file : null))
      );
    },
    [files]
  );

  return (
    <Grid container>
      <Typography color="inherit" component="div" className="heading centeredFlex pt-3">
        Files
      </Typography>
      <Grid container className="pt-2">
        {fileRenderer}
      </Grid>
      <div className="container pt-2 pb-2">
        <div
          {...getRootProps({ className: "dropzone" })}
          className={clsx(classes.uploadContainer, "paperBackgroundColor w-100 outline-none p-3")}
        >
          <input {...getInputProps()} />
          <Grid container>
            <Grid item sm={12} className={clsx(classes.fileType, "d-flex justify-content-center pb-3")}>
              <Typography variant="subtitle2" component="div">
                enrolmentCsv
              </Typography>
            </Grid>
            <Grid item sm={12} className="d-flex justify-content-center pb-3 pt-3">
              <Typography variant="subtitle2" component="div">
                Drag & drop here or
              </Typography>
            </Grid>
            <Grid item sm={12} className="d-flex justify-content-center pt-2">
              <Button variant="outlined" className={classes.uploadButton} onClick={() => {}}>
                Select a file
              </Button>
            </Grid>
          </Grid>
        </div>
      </div>
    </Grid>
  );
};

export default withStyles(styles)(FileUploadContainer);
