export default {
    render() {
        const {
            isAdded,
            isCanceled,
            isFinished,
            hasAvailableEnrolmentPlaces,
            paymentGatewayEnabled,
            allowByApplication,
            freePlaces,
            isAlreadyAdded,
            showedPopup
        } = this.props;

        const { ConfirmOrderDialog } = this.components;

        const isActive = !isFinished && !isCanceled && hasAvailableEnrolmentPlaces && paymentGatewayEnabled;

        const showedPlaces = hasAvailableEnrolmentPlaces;

        const reverseElements = isFinished;

        let text = '';

        if(isCanceled) {
            text = 'Cancelled'
        } else if(isActive) {
            if(isAdded) {
                text = 'Added';
            } else if(allowByApplication) {
                text = 'Apply Now';
            } else {
                text = 'Enrol Now';
            }
        } else if(!hasAvailableEnrolmentPlaces) {
            text = 'Class Full';
        } else {
            text = 'Finished';
        }

        let { plural, classnames } = this.utils;

        let elements = [
            paymentGatewayEnabled && <button key="enrol_button" className={classnames('enrolAction', {
                'enrol-added-class': isAdded,
                'disabled': !isActive
            })} title={text} onClick={isActive ? this.methods.add : null}>
                {text}
            </button>,
            showedPlaces && <div key="free_places" className="classStatus">
                {freePlaces > 5 ? 'There are places available' : `There ${plural(freePlaces, ['is one place', `${freePlaces} are places`])} available`}
            </div>
        ];

        return (
            <div className="classAction">
                {reverseElements ? elements.reverse() : elements}
                {showedPopup && <ConfirmOrderDialog classId={this.props.classId} className={this.props.className}
                                                    isAlreadyAdded={isAlreadyAdded} close={this.methods.closePopup}/>}
            </div>
        );
    }
};
