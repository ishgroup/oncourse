
package au.gov.training.services.trainingcomponent;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.4.0
 * 2011-10-12T16:55:20.973+03:00
 * Generated source version: 2.4.0
 * 
 */

@WebFault(name = "ValidationFault", targetNamespace = "http://training.gov.au/services/")
public class ITrainingComponentServiceGetDetailsValidationFaultFaultFaultMessage extends Exception {
    public static final long serialVersionUID = 20111012165520L;
    
    private au.gov.training.services.trainingcomponent.ValidationFault validationFault;

    public ITrainingComponentServiceGetDetailsValidationFaultFaultFaultMessage() {
        super();
    }
    
    public ITrainingComponentServiceGetDetailsValidationFaultFaultFaultMessage(String message) {
        super(message);
    }
    
    public ITrainingComponentServiceGetDetailsValidationFaultFaultFaultMessage(String message, Throwable cause) {
        super(message, cause);
    }

    public ITrainingComponentServiceGetDetailsValidationFaultFaultFaultMessage(String message, au.gov.training.services.trainingcomponent.ValidationFault validationFault) {
        super(message);
        this.validationFault = validationFault;
    }

    public ITrainingComponentServiceGetDetailsValidationFaultFaultFaultMessage(String message, au.gov.training.services.trainingcomponent.ValidationFault validationFault, Throwable cause) {
        super(message, cause);
        this.validationFault = validationFault;
    }

    public au.gov.training.services.trainingcomponent.ValidationFault getFaultInfo() {
        return this.validationFault;
    }
}
