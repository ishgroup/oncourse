package ish.oncourse.model;

public class QueuedKey {

	private Long id;
	private String entityName;

	public QueuedKey(Long id, String entityName) {
		this.id = id;
		this.entityName = entityName;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof QueuedKey) {
			QueuedKey k = (QueuedKey) obj;
			return this.id.equals(k.id) && this.entityName.equals(k.entityName);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
}
