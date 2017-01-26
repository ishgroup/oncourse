import nativeExtend from './Discounts.extend';
import customExtend from './Discounts.custom';

let extend = Object.assign({}, nativeExtend, customExtend);

class Discounts extends React.Component {

    constructor() {
        super();

        this.onChange = (e) => {
            this.setState({
                value: e.target.value
            });
        };

        this.add = (e) => {
            e.preventDefault();
            this.setState({ pending: true });
            this.props.add(this.state.value)
                .fail(() => {
                    // ToDo handle error
                    this.setState({ error: 'Error' });
                })
                .always(() => {
                    this.setState({ pending: false });
                });
        };

        this.state = {
            value: '',
            error: null,
            pending: false
        };
    }

    render() {
        return extend.render.apply({
            props: {
                discounts: this.props.discounts,
                pending: this.state.pending,
                error: this.state.error,
                value: this.state.value
            },
            methods: {
                onChange: this.onChange,
                add: this.add
            }
        });
    }
}

Discounts.propTypes = {
    discounts: React.PropTypes.array,
    add: React.PropTypes.func
};

export default Discounts;
