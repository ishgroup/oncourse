
package au.gov.training.services.trainingcomponent;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.2
 * 2013-05-06T14:27:17.597+03:00
 * Generated source version: 2.6.2
 */

@WebFault(name = "ValidationFault", targetNamespace = "http://training.gov.au/services/")
public class ITrainingComponentServiceGetDataManagersValidationFaultFaultFaultMessage extends Exception {
    
    private au.gov.training.services.trainingcomponent.ValidationFault validationFault;

    public ITrainingComponentServiceGetDataManagersValidationFaultFaultFaultMessage() {
        super();
    }
    
    public ITrainingComponentServiceGetDataManagersValidationFaultFaultFaultMessage(String message) {
        super(message);
    }
    
    public ITrainingComponentServiceGetDataManagersValidationFaultFaultFaultMessage(String message, Throwable cause) {
        super(message, cause);
    }

    public ITrainingComponentServiceGetDataManagersValidationFaultFaultFaultMessage(String message, au.gov.training.services.trainingcomponent.ValidationFault validationFault) {
        super(message);
        this.validationFault = validationFault;
    }

    public ITrainingComponentServiceGetDataManagersValidationFaultFaultFaultMessage(String message, au.gov.training.services.trainingcomponent.ValidationFault validationFault, Throwable cause) {
        super(message, cause);
        this.validationFault = validationFault;
    }

    public au.gov.training.services.trainingcomponent.ValidationFault getFaultInfo() {
        return this.validationFault;
    }
}
