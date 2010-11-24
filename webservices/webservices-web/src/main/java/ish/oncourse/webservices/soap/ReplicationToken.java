/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.soap;


/**
 *
 * @author Marek Wawrzyczny
 */
public class ReplicationToken {

	private String identifier;
	private String remoteVersion;
	private String latestVersion;


	public ReplicationToken(String identifier, String latestVersion) {
		setIdentifier(identifier);
		setLatestVersion(latestVersion);
	}

	/**
	 * Identifier for the token, currently populated with HTTP Session ID.
	 *
	 * @return
	 */
	public String getIdentifier() {
		return identifier;
	}
	private void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Version of Angel running at the remote site.
	 *
	 * @return
	 */
	public String getRemoteVersion() {
		return remoteVersion;
	}
	public void setRemoteVersion(String remoteVersion) {
		this.remoteVersion = remoteVersion;
	}

	/**
	 * The most recent available Angel version.
	 *
	 * @return
	 */
	public String getLatestVersion() {
		return latestVersion;
	}
	private void setLatestVersion(String latestVersion) {
		this.latestVersion = latestVersion;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || !(obj instanceof ReplicationToken)) {
			return false;
		}
		ReplicationToken other = (ReplicationToken) obj;

		return ((this.getIdentifier() != null) && (other.getIdentifier() != null)
				&& this.getIdentifier().equals(other.getIdentifier()));
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 59 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
		return hash;
	}

}
