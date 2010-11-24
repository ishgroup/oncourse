/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.soap.stubs;

import java.util.Date;


/**
 *
 * @author Marek Wawrzyczny
 */
public class TrainingPackage_Stub extends WillowEntity_Stub {

	private String copyrightCategory;
	private String copyrightContract;
	private Date created;
	private String developer;
	private Date endorsementFrom;
	private Date endorsementTo;
	private Long ishVersion;
	private Date modified;
	private String nationalISC;
	private String purchaseFrom;
	private String title;
	private String type;

	
	public String getCopyrightCategory() {
		return copyrightCategory;
	}

	public void setCopyrightCategory(String copyrightCategory) {
		this.copyrightCategory = copyrightCategory;
	}

	public String getCopyrightContract() {
		return copyrightContract;
	}

	public void setCopyrightContract(String copyrightContract) {
		this.copyrightContract = copyrightContract;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getDeveloper() {
		return developer;
	}

	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	public Date getEndorsementFrom() {
		return endorsementFrom;
	}

	public void setEndorsementFrom(Date endorsementFrom) {
		this.endorsementFrom = endorsementFrom;
	}

	public Date getEndorsementTo() {
		return endorsementTo;
	}

	public void setEndorsementTo(Date endorsementTo) {
		this.endorsementTo = endorsementTo;
	}

	public Long getIshVersion() {
		return ishVersion;
	}

	public void setIshVersion(Long ishVersion) {
		this.ishVersion = ishVersion;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public String getNationalISC() {
		return nationalISC;
	}

	public void setNationalISC(String nationalISC) {
		this.nationalISC = nationalISC;
	}

	public String getPurchaseFrom() {
		return purchaseFrom;
	}

	public void setPurchaseFrom(String purchaseFrom) {
		this.purchaseFrom = purchaseFrom;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
