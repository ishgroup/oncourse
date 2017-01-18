import { connect } from 'react-redux';

class PopupContainer extends React.Component {

    render() {
        return (
            <div>
                {this.props.content}
            </div>
        );
    }

}

export default connect((state) => {
    return {
        content: state.popup.content
    };
})(PopupContainer);