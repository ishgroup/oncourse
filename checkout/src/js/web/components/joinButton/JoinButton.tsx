import * as React from "react";
import {Course} from "../../../model";
import {stopPropagation} from "../../../common/utils/HtmlUtils";


export class JoinButton extends React.Component<Props, State> {

  constructor(props) {
    super(props);

    this.state = {
      isAlreadyAdded: false,
      pending: false,
    };
  }

  add = e => {
    if (this.state.pending) {
      return;
    }

    if (this.props.isAdded) {
      stopPropagation(e);
      this.setState({
        isAlreadyAdded: true,
      });
    } else {
      // ToDo how to handle error?
      this.setState({
        pending: true,
      });
      this.props.addToCart(this.props.course);
      this.setState({
        isAlreadyAdded: false,
        pending: false,
      });
    }
  }


  componentDidMount() {
    const {id, loadById} = this.props;
    loadById(id);
  }

  onAdd(e) {
    e.preventDefault();

    const {addToCart, checkoutPath, course} = this.props;
    addToCart(course);
    const checkoutLink = checkoutPath + "?sourcePath=" + window.location.href;
    document.location.href = `${checkoutLink}`;
  }

  render() {
    const {course} = this.props;

    const content = course && course.id && <a
      href="#"
      className="actionLink"
      onClick={e => this.onAdd(e)}
      dangerouslySetInnerHTML={{__html: this.props.children}}
    />;

    return content || <span />;
  }
}

export interface Props {
  readonly id: string;
  readonly isAdded: boolean;
  readonly course: Course;
  readonly children: any;
  readonly checkoutPath: string;
  readonly loadById: (id: string) => void;
  readonly addToCart?: (item: Course) => void;
}

interface State {
  isAlreadyAdded: boolean;
  pending: boolean;
}

