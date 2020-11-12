import * as React from "react";
import TagsForm from "./containers/TagsForm";
import { Tag } from "@api/model";
import history from "../../constants/History";
import { State } from "../../reducers/state";
import { connect } from "react-redux";
import withTheme from "@material-ui/core/styles/withTheme";
import Content from "../../common/components/layout/Content";

const emptyTag: Tag = {
  id: null,
  name: "",
  status: "Private",
  system: false,
  urlPath: null,
  content: "",
  weight: 1,
  taggedRecordsCount: 0,
  created: "",
  modified: "",
  requirements: [],
  childTags: []
};

class Tags extends React.Component<any, any> {
  shouldComponentUpdate({
    match: {
      params: { id }
    },
    tags,
    submitSucceeded,
    form
  }) {
    const currentName = this.props.match.params.id;

    if (currentName === "new" && submitSucceeded) {
      const newId = tags.find(item => item.name === form).id;

      if (newId) {
        setTimeout(() => {
          history.push("/tags/" + newId);
        });
      }
    }

    return !this.props.tags.length || currentName !== id || Boolean(submitSucceeded);
  }

  redirectOnDelete = () => {
    const { tags } = this.props;

    if (tags.length) {
      history.push("/tags/" + tags[0].id);
      return;
    }
    history.push("/tags");
  };

  render() {
    const {
      tags,
      match: {
        params: { id }
      },
      theme
    } = this.props;

    emptyTag.color = theme.palette.primary.main.replace("#", "");

    const isNew = id === "new";

    const currentTag = isNew ? emptyTag : tags && tags.find(i => i.id.toString() === id);

    return currentTag ? (
      <Content>
        <TagsForm rootTag={currentTag} isNew={isNew} redirectOnDelete={this.redirectOnDelete} tags={tags} />
      </Content>
    ) : null;
  }
}

const mapStateToProps = (state: State) => ({
  form: state.form.TagsForm && state.form.TagsForm.values && state.form.TagsForm.values.name,
  submitSucceeded: state.form.TagsForm && state.form.TagsForm.submitSucceeded,
  tags: state.tags.allTags
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(withTheme(Tags));
