import React from "react";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import { Grid } from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import { AppTheme } from "../../../../../../model/common/Theme";
import DocumentHeader from "./DocumentHeader";
import DocumentTags from "./DocumentTags";

const styles = (theme: AppTheme) => createStyles({
  container: {
    paddingLeft: `${theme.spacing(10.5)}px !important`
  },
  documentNoTags: {
    fontSize: 13
  }
});

class DocumentItem extends React.PureComponent<any, any> {
  openFullDocumentView = () => {
    const { editItem, item } = this.props;
    editItem(item);
  };

  render() {
    const {
      classes, item, unlink, index, entity
    } = this.props;
    return (
      <Paper onClick={this.openFullDocumentView} classes={{ root: "cursor-pointer" }}>
        <Grid container className={`p-1 relative ${classes.container}`}>
          <DocumentHeader item={item} unlink={unlink} index={index} entity={entity} />
          <DocumentTags tags={item.tags} classes={classes} />
        </Grid>
      </Paper>
    );
  }
}

export default withStyles(styles)(DocumentItem);
