package ish.oncourse.website.util;

import java.math.BigDecimal;

import ish.oncourse.model.Site;

public class GoogleMapItem {
	private BigDecimal latitude;
	private BigDecimal longitude;
	private Site site;
	private String title;
	private Float focusMatch;

	public static GoogleMapItem getMapItemForSite(Site site, float focusMatch) {
		GoogleMapItem result = null;
		if (site != null) {
			result = new GoogleMapItem();
			result.setLatitude(site.getLatitude());
			result.setLongitude(site.getLongitude());
			result.setSite(site);
			result.setTitle(site.getName());
			result.setFocusMatch(focusMatch);
		}
		return result;
	}

	/**
	 * @return the latitude
	 */
	public BigDecimal getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public BigDecimal getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the site
	 */
	public Site getSite() {
		return site;
	}

	/**
	 * @param site
	 *            the site to set
	 */
	public void setSite(Site site) {
		this.site = site;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the focusMatch
	 */
	public Float getFocusMatch() {
		return focusMatch;
	}

	/**
	 * @param focusMatch
	 *            the focusMatch to set
	 */
	public void setFocusMatch(Float focusMatch) {
		this.focusMatch = focusMatch;
	}

}
