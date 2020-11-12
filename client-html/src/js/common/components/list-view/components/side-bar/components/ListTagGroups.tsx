import React, { useCallback } from "react";
import { State } from "../../../../../../reducers/state";
import { connect } from "react-redux";
import { createStyles, withStyles } from "@material-ui/core";
import { MenuTag } from "../../../../../../model/tags";
import ListTagGroup from "./ListTagGroup";

const styles = theme =>
  createStyles({
    container: {
      marginLeft: theme.spacing(-0.5)
    }
  });

interface Props {
  tags: MenuTag[];
  classes: any;
  onChangeTagGroups: (tags: MenuTag[], type: string) => void;
}

const ListTagGroups: React.FC<Props> = ({ tags, classes, onChangeTagGroups }) => {
  const updateActive = useCallback(
    (updated: MenuTag) => {
      const updatedTags = tags.map(t => {
        if (t.tagBody.id === updated.tagBody.id && t.prefix === updated.prefix) {
          return updated;
        }

        return t;
      });
      onChangeTagGroups(updatedTags, "tags");
    },
    [tags]
  );

  return (
    <div>
      {tags.map(t => {
        if (!t.children.length) {
          return null;
        }
        return (
          <ListTagGroup
            key={t.prefix + t.tagBody.id.toString()}
            rootTag={t}
            classes={classes}
            updateActive={updateActive}
          />
        );
      })}
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  tags: state.list.menuTags
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(withStyles(styles)(ListTagGroups));
