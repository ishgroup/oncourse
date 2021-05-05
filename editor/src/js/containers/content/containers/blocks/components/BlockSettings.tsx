import React from 'react';
import {withStyles} from "@material-ui/core/styles";
import IconBack from "../../../../../common/components/IconBack";
import {BlockState} from "../reducers/State";
import {addContentMarker} from "../../../utils";
import CustomButton from "../../../../../common/components/CustomButton";
import {stubFunction} from "../../../../../common/utils/Components";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";
import clsx from "clsx";
import MenuIcon from "@material-ui/icons/Menu";
import {IconButton} from "@material-ui/core";
import {showNavigation} from "../../../../../common/containers/Navigation/actions";

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
  actionsGroup: {
    display: "flex",
    justifyContent: "space-between",
  },
});

interface Props {
  block: BlockState;
  classes: any;
  onBack: () => void;
  onEdit?: (settings) => void;
  onDelete?: (id) => void;
  showModal?: (props) => any;
  showNavigation?: () => any;
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
      text: `You are want to delete block '${block.title}'.`,
      onConfirm: () => onDelete(block.id),
    });
  }

  render () {
    const {block, classes, showNavigation} = this.props;
    const {title} = this.state;

    return (
      <div>
        <ul>
          <li className={"pl-1"}>
            <IconButton onClick={showNavigation}>
              <MenuIcon/>
            </IconButton>

            {/*<a href="#" className={classes.linkBack} onClick={e => this.clickBack(e)}>*/}
            {/*  <IconBack text="Blocks"/>*/}
            {/*</a>*/}
          </li>
        </ul>

        <div className={classes.sideBarSetting}>
          <div className="heading mb-2">Blocks</div>

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

          <div className={clsx(classes.actionsGroup, "mt-3")}>
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
    );
  }
}

export default (withStyles(styles)(BlockSettings));
