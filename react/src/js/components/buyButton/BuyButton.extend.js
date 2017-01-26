export default {
    render() {
        if(!this.props.canBuy || !this.props.paymentGatewayEnabled) {
            return null;
        }

        const { ConfirmOrderDialog } = this.components;

        const isAdded = this.props.isAdded;

        return (
            <div className="classAction">
                <a className={this.utils.classnames('enrolAction', {
                    'enrol-added-class': isAdded
                })} onClick={this.methods.add}>
                    {isAdded ? 'Added' : 'BUY NOW'}
                </a>
                {this.props.showedPopup && <ConfirmOrderDialog id={this.props.id} name={this.props.name}
                                                               isAlreadyAdded={this.props.isAlreadyAdded} close={this.methods.closePopup}/>}
            </div>
        );
    }
};