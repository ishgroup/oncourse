export default {
    render() {
        let product = this.props.product;

        return (
            <li>
                <a href={`/product/${product.uniqueIdentifier}`}>{product.name}</a>
                <span className={this.utils.classnames('deleteItem', {
                    'loading': this.props.pending
                })} title="Remove item">
                    <a onClick={this.methods.remove}>X</a>
                </span>
                {/*
                    ToDo Discussed with Artyom this part.
                    Why we display product create and modified dates as date range?
                    Weird stuff
                 */}
                {product.created && product.modified &&
                    <div className="shortListOrderClasses">
                        <abbr class="dtstart" title="">{product.created}</abbr>
                        <abbr class="dtend" title="">{product.modified}</abbr>
                    </div>}
            </li>
        );
    }
};
