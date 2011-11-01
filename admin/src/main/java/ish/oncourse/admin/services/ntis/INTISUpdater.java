package ish.oncourse.admin.services.ntis;

import java.util.Date;

public interface INTISUpdater {
	NTISResult doUpdate(Date from, Date to, Class<?> type) throws NTISException;
}
