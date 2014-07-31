package ish.oncourse.services.tutor;

import ish.oncourse.model.Tutor;

import java.util.Date;
import java.util.List;

public interface ITutorService {

	Tutor findById(Long tutorId);

	Date getLatestModifiedDate();

	List<Tutor> getTutors();

	/**
	 * Retrieves the tutoe with the given angelId.
	 * 
	 * @param angelId
	 * @return
	 */
	Tutor findByAngelId(Long angelId);

    public boolean isActiveTutor(Tutor tutor);

}
