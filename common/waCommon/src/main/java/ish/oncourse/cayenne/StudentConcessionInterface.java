package ish.oncourse.cayenne;

import java.util.Date;

public interface StudentConcessionInterface {

    String CONCESSION_TYPE_PROPERTY = "concessionType";
    
    String CONCESSION_NUMBER_PROPERTY = "concessionNumber";

    String EXPIRES_ON_PROPERTY = "expiresOn";

    ConcessionTypeInterface getConcessionType();
    
    String getConcessionNumber();

    Date getExpiresOn();
}
