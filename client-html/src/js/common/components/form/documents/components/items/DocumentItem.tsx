import React from "react";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import { Grid } from "@mui/material";
import Paper from "@mui/material/Paper";
import clsx from "clsx";
import { AppTheme } from "../../../../../../model/common/Theme";
import DocumentHeader from "./DocumentHeader";
import DocumentTags from "./DocumentTags";

const styles = (theme: AppTheme) => createStyles({
  container: {
    paddingLeft: `${theme.spacing(10.5)} !important`
  },
  documentNoTags: {
    fontSize: 13
  },
  documentPaper: {
    "&:hover": {
      boxShadow: theme.shadows["3"]
    }
  }
});

class DocumentItem extends React.PureComponent<any, any> {
  openFullDocumentView = () => {
    const { editItem, item } = this.props;
    editItem(item);
  };

  openDocumentView = () => {
    const { viewItem, item } = this.props;
    viewItem(item);
  };

  render() {
    const {
      classes, item, unlink, index, entity
    } = this.props;
    return (
      <Paper onClick={this.openDocumentView} classes={{ root: clsx("cursor-pointer h-100", classes.documentPaper) }}>
        <Grid container columnSpacing={3} className={clsx("p-1 relative h-100 align-content-between", classes.container)}>
          <DocumentHeader
            item={item}
            unlink={unlink}
            index={index}
            entity={entity}
            editItem={this.openFullDocumentView}
            viewItem={this.openDocumentView}
          />
          <DocumentTags tags={item.tags} classes={classes} />
        </Grid>
      </Paper>
    );
  }
}

export default withStyles(styles)(DocumentItem);
