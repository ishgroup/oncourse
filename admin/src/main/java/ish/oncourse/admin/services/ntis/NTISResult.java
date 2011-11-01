package ish.oncourse.admin.services.ntis;

public class NTISResult {
	private Class<?> type;
	private int numberOfUpdated;
	private int numberOfNew;
	
	public Class<?> getType() {
		return type;
	}
	
	public void setType(Class<?> type) {
		this.type = type;
	}
	
	public int getNumberOfNew() {
		return numberOfNew;
	}
	
	public void setNumberOfNew(int numberOfNew) {
		this.numberOfNew = numberOfNew;
	}
	
	public int getNumberOfUpdated() {
		return numberOfUpdated;
	}
	
	public void setNumberOfUpdated(int numberOfUpdated) {
		this.numberOfUpdated = numberOfUpdated;
	}
}
