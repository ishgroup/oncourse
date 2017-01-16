import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { plural } from 'js/lib/utils';
import { removeCourse as onRemoveCourse } from '../actions/cart';

class CartOrderItem extends React.Component {

    static propTypes = {
        course: React.PropTypes.object,
        onRemove: React.PropTypes.func
    };

    remove = (e) => {
        e.stopPropagation();
        this.props.onRemove(this.props.course.id);
    };

    render() {
        let course = this.props.course;

        return (
            <li>
                <a href="/class/bushwalking.sydney-1">{course.name}</a>
                <span title="Remove item" className="deleteItem">
                    <a onClick={this.remove}>X</a>
                </span>
                <div className="shortListOrderClasses">
                    <abbr title="" className="dtstart">{course.date_start}</abbr>
                    - <abbr title="" className="dtend">{course.date_end}</abbr>
                </div>
            </li>
        );
    }

}

class Cart extends React.Component {

    static propTypes = {
        courses: React.PropTypes.array,
        onRemoveCourse: React.PropTypes.func
    };

    constructor() {
        super();

        this.state = {
            showedShortList: false
        };
    }

    componentWillReceiveProps(nextProps) {
        if(!nextProps.courses.length) {
            this.setState({
                showedShortList: false
            });
        }
    }

    toggleShortList = () => {
        this.setState({
            showedShortList: !this.state.showedShortList
        });
    };

    render() {
        let count = this.props.courses.length;

        return (
            <div className="top-cart" onClick={this.toggleShortList}>
                <aside id="headerToolbar" className="header-toolbar">
                    <div className="short-list" id="shortlist">
                        <div id="info" className="shortlistInfo clearfix">
                            <span>{count}</span>
                        </div>
                        {this.state.showedShortList && !!count && <div className="shortlistChoices dialogContainer">
                            <ul className="shortListOrder shortlistChoices" style={{display: 'block'}}>
                                {this.props.courses.map((course) => {
                                    return <CartOrderItem key={course.id} course={course} onRemove={this.props.onRemoveCourse}/>;
                                })}
                                <li className="shortListOrderEnrol">
                                    <a className="shortlistLinkEnrol abtn gamma" href="/enrol/">Enrol</a>
                                </li>
                            </ul>
                            <div className="closeButton">X</div>
                        </div>}
                    </div>
                </aside>
            </div>
        );
    }
}

function getCourses(courses) {
    return courses.reduce((arr, course) => {
        if(course.pending || course.error) {
            return arr;
        }

        arr.push(course.data);
        return arr;
    }, []);
}

export default connect((state) => {
    return {
        courses: getCourses(state.cart.courses)
    };
}, (dispatch) => {
    return bindActionCreators({ onRemoveCourse }, dispatch);
})(Cart);