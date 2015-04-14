package ish.oncourse.admin.pages;

import ish.oncourse.model.PostcodeDb;
import ish.oncourse.services.location.IPostCodeDbService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.threading.ThreadSource;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.Session;
import org.apache.xml.dtm.ref.DTMNodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UpdatePostcode {
	static final String NEVER_UPDATED_VALUE = "NEVER";
	static final String POSTCODE_UPDATE_PROCESSED_FLAG = "postcodeUpdateProcessed";
	static final String IS_POSTCODE_UPDATE_STARTED_FLAG = "isPostcodeUpdateStarted";

	@SuppressWarnings("all")
	@Property
	private String lastUpdateDate;
	
	@Inject
	private ThreadSource threadSource;
	
	@Inject
	private Request request;
	
	@Inject
	private Response response;
	
	@SuppressWarnings("all")
	@Inject
	private IPostCodeDbService postCodeDbService;
	
	@Inject
	private PreferenceController preferenceController;
	
	@SuppressWarnings("all")
	@Property
	private String processResultUrl;
		
	@SetupRender
	void setupRender() {
		processResultUrl = response.encodeURL(request.getContextPath() + "/UpdatePostcodeCallback");
		final String lastUpdate = preferenceController.getPostcodesLastUpdate();
		if (lastUpdate != null) {
			this.lastUpdateDate = lastUpdate;
		} else {
			this.lastUpdateDate = NEVER_UPDATED_VALUE;
		}
	}
	
	/**
	 * Checks if already running update.
	 * @return true/false
	 */
	public boolean isUpdateInProgress() {
		Session session = request.getSession(false);
		Boolean started = (Boolean) session.getAttribute(IS_POSTCODE_UPDATE_STARTED_FLAG);
		return Boolean.TRUE.equals(started);
	}
	
	@OnEvent(component = "updateForm", value = "success")
	void submitted() throws Exception {
		final Session session = request.getSession(false);
		final PreferenceController preferenceController = this.preferenceController;
		synchronized (session) {
			Boolean started = (Boolean) session.getAttribute(IS_POSTCODE_UPDATE_STARTED_FLAG);
			if (started == null) {
				session.setAttribute(IS_POSTCODE_UPDATE_STARTED_FLAG, true);
				threadSource.runInThread(new UpdatePostcodesTask(session, preferenceController, postCodeDbService));
			}
		}
	}
	
	static class UpdatePostcodesTask implements Runnable {
		private static final String POST_CODE_UPDATED_DATE_FORMAT = "dd/MM/yyyy";
		private static final String AUSTRALIA_SEARCH_COUNTRY_POSTFIX = ",Australia&sensor=false";
		private static final String GOOGLE_MAPS_API_GEOCODE_ADDRESS = "http://maps.googleapis.com/maps/api/geocode/xml?address=";
		private static final String UTF_8_ENCODING = "UTF-8";
		private static final String ZERO_CHARACTER = "0";
		private static final String POSTAL_CODE_TYPE_VALUE = "postal_code";
		private static final String LONG_NAME_TAG_NAME = "long_name";
		private static final String TYPE_TAG_NAME = "type";
		private static final String LATITUDE_MINIMAL_VALUE = "-50.0";
		private static final String LONGITUDE_MAXIMAL_VALUE = "160.0";
		private static final String LONGITUDE_MINIMAL_VALUE = "100.0";
		private static final String LONGITUDE_XPATH = "//geometry/location/lng/text()";
		private static final String LATITUDE_XPATH = "//geometry/location/lat/text()";
		private static final String ADDRESS_COMPONENT_TAG_NAME = "address_component";
		private static final String RESULT_TAG_NAME = "result";
		private Session session;
		private PreferenceController preferenceController;
		private IPostCodeDbService postCodeDbService;
		

		public UpdatePostcodesTask(Session session, PreferenceController preferenceController, IPostCodeDbService postCodeDbService) {
			super();
			this.session = session;
			this.preferenceController = preferenceController;
			this.postCodeDbService = postCodeDbService;
		}



		@Override
		public void run() {
			try {
				long processed = 0;
				synchronized (session) {
					session.setAttribute(POSTCODE_UPDATE_PROCESSED_FLAG, processed);
				}
				for (final PostcodeDb postcode : postCodeDbService.getAllPostcodes()) {
					String encodedSubUrb;
					try {
						encodedSubUrb = URLEncoder.encode(postcode.getSuburb(), UTF_8_ENCODING);
					} catch (UnsupportedEncodingException e2) {
						encodedSubUrb = StringUtils.EMPTY;
					}
					final StringBuilder url = new StringBuilder(GOOGLE_MAPS_API_GEOCODE_ADDRESS).append(encodedSubUrb).append(",")
						.append(postcode.getPostcode()).append(AUSTRALIA_SEARCH_COUNTRY_POSTFIX);
					final String result = requestPostcode(url.toString());
					try {
						final List<BigDecimal> geometry = parseResponse(result, postcode.getPostcode().toString());
						if (!geometry.isEmpty() && geometry.size() == 2) {
							postcode.setLat(geometry.get(0).doubleValue());
							postcode.setLon(geometry.get(1).doubleValue());
							postcode.getObjectContext().commitChanges();
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
					processed++;
					synchronized (session) {
						session.setAttribute(POSTCODE_UPDATE_PROCESSED_FLAG, processed);
					}
				}
				preferenceController.setPostcodesLastUpdate(new SimpleDateFormat(POST_CODE_UPDATED_DATE_FORMAT).format(new Date()));
			} finally {
				synchronized (session) {
					session.setAttribute(IS_POSTCODE_UPDATE_STARTED_FLAG, null);
				}
			}
			
		}
		
		private List<BigDecimal> parseResponse(final String response, final String originalCode) throws Throwable {
			final List<BigDecimal> geometry = new ArrayList<>(2);
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			InputSource source = new InputSource();
			source.setCharacterStream(new StringReader(response));
			Document doc = builder.parse(source);
			NodeList resultList = doc.getElementsByTagName(RESULT_TAG_NAME);
			if (resultList != null) {
				for (int i = 0; i < resultList.getLength(); i++) {
					Element resultElement = (Element) resultList.item(i);
					if (findCorrectPostalCode(resultElement.getElementsByTagName(ADDRESS_COMPONENT_TAG_NAME), originalCode)) {
						XPath xpath = XPathFactory.newInstance().newXPath();
						XPathExpression latExpr = xpath.compile(LATITUDE_XPATH);
						XPathExpression longExpr = xpath.compile(LONGITUDE_XPATH);
						
						String latitude =((DTMNodeList) latExpr.evaluate(resultElement, XPathConstants.NODESET)).item(0).getNodeValue();
						String longitude = ((DTMNodeList) longExpr.evaluate(resultElement, XPathConstants.NODESET)).item(0).getNodeValue();
						BigDecimal latitudeValue = new BigDecimal(latitude);
						BigDecimal longitudeValue = new BigDecimal(longitude);
						if (longitudeValue.compareTo(new BigDecimal(LONGITUDE_MINIMAL_VALUE)) > 0 && longitudeValue.compareTo(new BigDecimal(LONGITUDE_MAXIMAL_VALUE)) < 0 && 
							latitudeValue.compareTo(BigDecimal.ZERO) < 0 && latitudeValue.compareTo(new BigDecimal(LATITUDE_MINIMAL_VALUE)) > 0) {
							geometry.add(latitudeValue);
							geometry.add(longitudeValue);
							return geometry;
						}
					}
				}
			}
			return geometry;
		}
		
		private boolean findCorrectPostalCode(final NodeList addressComponentsList, final String originalCode) {
			if (addressComponentsList != null) {
				for (int j = 0; j < addressComponentsList.getLength(); j++) {
					Element addressComponentElement = (Element) addressComponentsList.item(j);
					NodeList typeList = addressComponentElement.getElementsByTagName(TYPE_TAG_NAME);
					if (typeList != null && typeList.getLength() == 1) {
						Element typeElement = (Element) typeList.item(0);
						final String typeValue = typeElement.getFirstChild().getNodeValue();
						if (POSTAL_CODE_TYPE_VALUE.equalsIgnoreCase(typeValue)) {
							NodeList names = addressComponentElement.getElementsByTagName(LONG_NAME_TAG_NAME);
							if (names != null && names.getLength() == 1) {
								Element code = (Element) names.item(0);
								final String codeValue = code.getFirstChild().getNodeValue();
								if (StringUtils.trimToNull(codeValue) != null && (originalCode.equals(codeValue.trim()) || 
									codeValue.substring(0, codeValue.length() - originalCode.length()).replaceAll(ZERO_CHARACTER, StringUtils.EMPTY).trim().isEmpty())) {
									return true;
								}
							}
						}
					}
				}
			}
			return false;
		}
		
		private URLConnection openHttpConnection(final URL url) throws IOException {
			if (url == null) {
				return null;
			}
			final URLConnection connection = url.openConnection();
			connection.connect();
			return connection;
		}
		
		private String requestPostcode(final String requestUrl) {
			OutputStream output = null;
			InputStream input = null;
			final StringBuffer result = new StringBuffer();
			try {
				URL url = new URL(requestUrl);
				byte[] buf = new byte[1024];
				int byteRead = 0;
				output = new BufferedOutputStream(new OutputStream() {
					@Override
					public void write(int b) throws IOException {
						result.append((char) b);
					}
				});
				input = new DataInputStream(openHttpConnection(url).getInputStream());
				while ((byteRead = input.read(buf)) != -1) {
					output.write(buf, 0, byteRead);
				}
			} catch (Exception e) {
				return StringUtils.EMPTY;
			} finally {
				try {
					if (input != null) {
						input.close();
					}
					if (output != null) {
						output.flush();
						output.close();
					}
				} catch (IOException e) {
					return StringUtils.EMPTY;
				}
			}
			return result.toString();
		}
		
	}

}
