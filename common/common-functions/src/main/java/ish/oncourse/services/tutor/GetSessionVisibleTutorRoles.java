/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.tutor;

import ish.oncourse.model.Session;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import java.util.List;
import java.util.stream.Collectors;

public class GetSessionVisibleTutorRoles {

	private ObjectContext context;
	private Session session;
	private QueryCacheStrategy cacheStrategy = QueryCacheStrategy.LOCAL_CACHE;

	private GetSessionVisibleTutorRoles() {}

	public static GetSessionVisibleTutorRoles valueOf(ObjectContext context, Session session) {
		GetSessionVisibleTutorRoles obj = new GetSessionVisibleTutorRoles();
		obj.context = context;
		obj.session = session;
		return obj;
	}

	public GetSessionVisibleTutorRoles cacheStrategy(QueryCacheStrategy cacheStrategy) {
		this.cacheStrategy = cacheStrategy;
		return this;
	}

	public GetSessionVisibleTutorRoles courseClass(Session session) {
		this.session = session;
		return this;
	}

	public List<TutorRole> get() {
		return ObjectSelect.query(TutorRole.class)
				.where(TutorRole.TUTOR.dot(Tutor.SESSION_TUTORS).dot(SessionTutor.SESSION).eq(session)
					.andExp(TutorRole.IN_PUBLICITY.isTrue()))
				.prefetch(TutorRole.TUTOR.joint())
				.prefetch(TutorRole.TUTOR.dot(Tutor.CONTACT).joint())
				.cacheStrategy(cacheStrategy, TutorRole.class.getSimpleName())
				.select(context)
				.stream()
				.filter(tr -> GetIsActiveTutor.valueOf(tr.getTutor()).get())
				.collect(Collectors.toList());
	}
}
