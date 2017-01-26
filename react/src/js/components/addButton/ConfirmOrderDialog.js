import { findDOMNode } from 'react-dom';
import { stopPropagation } from 'js/lib/utils';
import nativeExtend from './ConfirmOrderDialog.extend';
import customExtend from './ConfirmOrderDialog.custom';

let extend = Object.assign({}, nativeExtend, customExtend);

class ConfirmOrderDialog extends React.Component {

    constructor(props) {
        super();
        this.$item = $(`.classItem[data-classid=${props.id}]`);
    }

    componentWillMount() {
        $(document).on('click', this.props.close);
    }

    componentDidMount() {
        $(findDOMNode(this))
            .show()
            .offset({ top: this.$item.offset().top })
            .css('right', '150px')
            .fadeIn('fast');
    }

    componentWillUnmount() {
        $(document).off('click', this.props.close);
    }

    render() {
        return extend.render.apply({
            props: {
                isAlreadyAdded: this.props.isAlreadyAdded,
                id: this.props.id,
                name: this.props.name,
                //ToDo ask about time
                date: this.$item.find('.class-item-info-l > .date a:first').text()
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
    id: React.PropTypes.number,
    name: React.PropTypes.string,
    date: React.PropTypes.string,
    close: React.PropTypes.func
};

export default ConfirmOrderDialog;
