package ish.oncourse.cayenne;

public interface StudentConcessionInterface {

    String CONCESSION_NUMBER_PROPERTY = "concessionNumber";

    ConcessionTypeInterface getConcessionType();
    String getConcessionNumber();
}
