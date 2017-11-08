import * as React from "react";
import {Course} from "../../../model";
import {stopPropagation} from "../../../common/utils/HtmlUtils";


export class JoinButton extends React.Component<Props, State> {

  constructor() {
    super();

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
    document.location.href = `${checkoutPath}`;
  }

  render() {
    const {course, isAdded} = this.props;

    return (
      <a
        href="#"
        className="actionLink"
        onClick={e => this.onAdd(e)}
        dangerouslySetInnerHTML={{__html: this.props.children}}
      />
    );
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

