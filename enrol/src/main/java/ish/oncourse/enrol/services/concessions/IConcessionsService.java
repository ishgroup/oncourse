package ish.oncourse.enrol.services.concessions;

import ish.oncourse.model.ConcessionType;

import java.util.List;

public interface IConcessionsService {

	List<ConcessionType> getActiveConcessionTypes();
}
