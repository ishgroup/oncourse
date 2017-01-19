import classnames from 'classnames';
import nativeExtend from './EnrolButton.extend';
import customExtend from './EnrolButton.custom';

let extend = Object.assign({}, nativeExtend, customExtend);

class EnrolButton extends React.Component {

    constructor() {
        super();
        this.add = () => this.props.onEnrol(this.props.id);
    }

    render() {
        return extend.render.apply({
            props: {
                isAdded: this.props.isAdded
            },
            methods: {
                add: this.add
            },
            utils: { classnames }
        });
    }
}

EnrolButton.propTypes = {
    id: React.PropTypes.number,
    isAdded: React.PropTypes.bool,
    onEnrol: React.PropTypes.func
};

export default EnrolButton;