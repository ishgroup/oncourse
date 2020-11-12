import React from "react";
import { Grid } from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import DocumentHeader from "./DocumentHeader";
import DocumentTags from "./DocumentTags";

class DocumentItem extends React.PureComponent<any, any> {
  openFullDocumentView = () => {
    const { editItem, item } = this.props;
    editItem(item);
  };

  render() {
    const {
 item, unlink, index, entity 
} = this.props;
    return (
      <Paper onClick={this.openFullDocumentView} classes={{ root: "cursor-pointer" }}>
        <Grid container className="p-1 relative">
          <DocumentHeader item={item} unlink={unlink} index={index} entity={entity} />
          <DocumentTags tags={item.tags} />
        </Grid>
      </Paper>
    );
  }
}

export default DocumentItem;
