
package au.gov.training.services.trainingcomponent;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.4.1
 * 2011-11-17T14:05:04.244+03:00
 * Generated source version: 2.4.1
 */

@WebFault(name = "ValidationFault", targetNamespace = "http://training.gov.au/services/")
public class ITrainingComponentServiceGetClassificationSchemesValidationFaultFaultFaultMessage extends Exception {
    
    private au.gov.training.services.trainingcomponent.ValidationFault validationFault;

    public ITrainingComponentServiceGetClassificationSchemesValidationFaultFaultFaultMessage() {
        super();
    }
    
    public ITrainingComponentServiceGetClassificationSchemesValidationFaultFaultFaultMessage(String message) {
        super(message);
    }
    
    public ITrainingComponentServiceGetClassificationSchemesValidationFaultFaultFaultMessage(String message, Throwable cause) {
        super(message, cause);
    }

    public ITrainingComponentServiceGetClassificationSchemesValidationFaultFaultFaultMessage(String message, au.gov.training.services.trainingcomponent.ValidationFault validationFault) {
        super(message);
        this.validationFault = validationFault;
    }

    public ITrainingComponentServiceGetClassificationSchemesValidationFaultFaultFaultMessage(String message, au.gov.training.services.trainingcomponent.ValidationFault validationFault, Throwable cause) {
        super(message, cause);
        this.validationFault = validationFault;
    }

    public au.gov.training.services.trainingcomponent.ValidationFault getFaultInfo() {
        return this.validationFault;
    }
}
