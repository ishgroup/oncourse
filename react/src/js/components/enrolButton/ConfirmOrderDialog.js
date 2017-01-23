import { findDOMNode } from 'react-dom';
import { stopPropagation } from 'js/lib/utils';
import nativeExtend from './ConfirmOrderDialog.extend';
import customExtend from './ConfirmOrderDialog.custom';

let extend = Object.assign({}, nativeExtend, customExtend);

class ConfirmOrderDialog extends React.Component {

    constructor(props) {
        super();
        this.$classItem = $(`.classItem[data-classid=${props.classId}]`);
    }

    componentWillMount() {
        $(document).on('click', this.props.close);
    }

    componentDidMount() {
        $(findDOMNode(this))
            .show()
            .offset(this.$classItem.offset())
            .css({
                left: 0,
                right: '150px'
            })
            .fadeIn('fast');
    }

    componentWillUnmount() {
        $(document).off('click', this.props.close);
    }

    render() {
        return extend.render.apply({
            props: {
                isAlreadyAdded: this.props.isAlreadyAdded,
                classId: this.props.classId,
                className: this.props.className,
                //ToDo ask about time
                classDate: this.$classItem.find('.class-item-info-l > .date a:first').text()
            },
            methods: {
                close: this.props.close
            },
            utils: { stopPropagation }
        });
    }
}

ConfirmOrderDialog.propTypes = {
    isAlreadyAdded: React.PropTypes.bool,
    classId: React.PropTypes.number,
    className: React.PropTypes.string,
    classDate: React.PropTypes.string,
    close: React.PropTypes.func
};

export default ConfirmOrderDialog;
