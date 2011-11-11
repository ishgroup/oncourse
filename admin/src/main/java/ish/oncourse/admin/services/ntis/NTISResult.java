package ish.oncourse.admin.services.ntis;

public class NTISResult {

	private Class<?> type;
	private long numberOfUpdated;
	private long numberOfNew;

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public long getNumberOfNew() {
		return numberOfNew;
	}

	public void setNumberOfNew(long numberOfNew) {
		this.numberOfNew = numberOfNew;
	}

	public long getNumberOfUpdated() {
		return numberOfUpdated;
	}

	public void setNumberOfUpdated(long numberOfUpdated) {
		this.numberOfUpdated = numberOfUpdated;
	}
}
