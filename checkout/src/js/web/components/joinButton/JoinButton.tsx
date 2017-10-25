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
    const {course, isAdded, checkoutPath, enrollableClassesEmpty, hasMoreAvailablePlaces} = this.props;
    console.log(this.props);

    return (
      <p className="waiting-list-title">
        <a href="#" className="actionLink">
          {!enrollableClassesEmpty && hasMoreAvailablePlaces &&
            <span>If there isn't a class to suit you, please </span>
          }
          {!enrollableClassesEmpty && !hasMoreAvailablePlaces &&
            <span>Classes are full. Please </span>
          }
          {enrollableClassesEmpty &&
            <span>This course has no current classes. Please</span>
          }
          If there isn't a class to suit you, please
          <button onClick={e => this.onAdd(e)} type="button" className="join-btn"> Join </button> the waiting list.
        </a>
      </p>
    );
  }
}

export interface Props {
  readonly id: string;
  readonly isAdded: boolean;
  readonly enrollableClassesEmpty?: boolean;
  readonly hasMoreAvailablePlaces?: boolean;
  readonly course: Course;
  readonly checkoutPath: string;
  readonly loadById: (id: string) => void;
  readonly addToCart?: (item: Course) => void;
}

interface State {
  isAlreadyAdded: boolean;
  pending: boolean;
}

