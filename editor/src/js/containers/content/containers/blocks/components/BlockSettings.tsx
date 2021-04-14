import React from 'react';
import {withStyles} from "@material-ui/core/styles";
import IconBack from "../../../../../common/components/IconBack";
import {BlockState} from "../reducers/State";
import {addContentMarker} from "../../../utils";
import CustomButton from "../../../../../common/components/CustomButton";
import {stubFunction} from "../../../../../common/utils/Components";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";

const styles: any = theme => ({
  linkBack: {
    textTransform: "capitalize",
    color: "rgba(0, 0, 0, 0.87)",
    fontSize: "15px",
    display: "block",
    padding: "15px 20px",
    "&:hover": {
      backgroundColor: "rgba(0, 0, 0, 0.1)",
      color: theme.palette.text.primary,
    },
  },
  removeButton: {
    marginRight: theme.spacing(2),
  },
  sideBarSetting: {
    padding: "10px 20px",
  },
});

interface Props {
  block: BlockState;
  classes: any;
  onBack: () => void;
  onEdit?: (settings) => void;
  onDelete?: (id) => void;
  showModal?: (props) => any;
}

class BlockSettings extends React.Component<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      title: props.block.title,
    };
  }

  clickBack(e) {
    const {onBack} = this.props;
    e.preventDefault();
    onBack();
  }

  onChange(event, key) {
    this.setState({
      [key]: event.target.value,
    });
  }

  onSave = () => {
    const {onEdit, block} = this.props;

    onEdit({
      title: this.state.title,
      content: addContentMarker(block.content, block.contentMode),
    });
  }

  onClickDelete =e => {
    e.preventDefault();
    const {onDelete, block, showModal} = this.props;

    showModal({
      text: `You are want to delete block '${block.title}'. Are you sure?`,
      onConfirm: () => onDelete(block.id),
    });
  }

  render () {
    const {block, classes} = this.props;
    const {title} = this.state;

    return (
      <div>
        <ul>
          <li>
            <a href="#" className={classes.linkBack} onClick={e => this.clickBack(e)}>
              <IconBack text={block.title || 'New Block'}/>
            </a>
          </li>
        </ul>

        <div className={classes.sideBarSetting}>
          <div>
            <EditInPlaceField
              label="Title"
              name="blockTitle"
              id="blockTitle"
              meta={{}}
              input={{
                onChange: e => this.onChange(e, 'title'),
                onBlur: stubFunction,
                onFocus: stubFunction,
                value: title,
              }}
              fullWidth
            />
          </div>

          <div className="mt-3">
            <div className="buttons-inline">
              <CustomButton
                styleType="delete"
                onClick={this.onClickDelete}
                styles={classes.removeButton}
              >
                Remove
              </CustomButton>

              <CustomButton
                styleType="submit"
                onClick={this.onSave}
              >
                Save
              </CustomButton>
            </div>
          </div>
        </div>
      </div>
    );
  }
}

export default (withStyles(styles)(BlockSettings));
