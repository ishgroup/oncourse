package ish.oncourse.services.sms;

import ish.common.types.MessageStatus;
import ish.oncourse.model.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Default implementation of SMS service.
 */
public class DefaultSMSService implements ISMSService {

	private static final Logger logger = Logger.getLogger(DefaultSMSService.class);

	private static final String EncodeType = "UTF-8";

	private static final Pattern NO_WHITESPACE_PATTERN = Pattern.compile("^[^\\s]+$");

	private final String smsGatewayApiId = (System.getProperty("smsGatewayApiId") != null) ? System.getProperty("smsGatewayApiId")
			: "237397";

	private final String smsGatewayUser = (System.getProperty("smsGatewayUser") != null) ? System.getProperty("smsGatewayUser") : "ish";

	private final String smsGatewayPass = (System.getProperty("smsGatewayPass") != null) ? System.getProperty("smsGatewayPass") : "8ujgdw";

	private final String smsGatewayURL = (System.getProperty("smsGatewayURL") != null) ? System.getProperty("smsGatewayURL")
			: "http://api.clickatell.com/http/";

	@Override
	public String authenticate() {

		try {

			if (smsGatewayApiId == null || !NO_WHITESPACE_PATTERN.matcher(smsGatewayApiId).matches() && smsGatewayUser == null
					|| !NO_WHITESPACE_PATTERN.matcher(smsGatewayUser).matches() && smsGatewayPass == null
					|| !NO_WHITESPACE_PATTERN.matcher(smsGatewayPass).matches()) {
				logger.error("sms credentials are invalid", new Exception());
				return null;
			} else {

				URL authenticationURL = new URL(smsGatewayURL + "auth?api_id=" + smsGatewayApiId + "&user=" + smsGatewayUser + "&password="
						+ smsGatewayPass);

				if (logger.isDebugEnabled()) {
					logger.debug("authenticationURL:" + authenticationURL.toString());
				}

				BufferedReader in = new BufferedReader(new InputStreamReader(authenticationURL.openStream()));
				// the response should be one line ERR: errorCode,
				// errorDescription
				// or OK: sessionId
				// anything else is wrong.
				String str = in.readLine();
				in.close();

				if (logger.isDebugEnabled()) {
					logger.debug("auth response:" + str);
				}

				if (str == null || str.equals("")) {
					logger.error("SMS gateway authentication error : no response from server");
					return null;
				} else if (str.startsWith("ERR:")) {
					String errorString = str.substring(str.indexOf(":") + 1, str.length()).trim();
					logger.error("SMS gateway authentication error " + errorString);
					return null;
				} else if (str.startsWith("OK:")) {
					String sessionString = str.substring(str.indexOf(":") + 1, str.length()).trim();
					if (sessionString == null || sessionString.equals("")) {
						logger.error("SMS gateway returned null session");
						return null;
					} else {
						return sessionString;
					}
				} else {
					logger.error("SMS gateway authentication error : response from server malformed");
					return null;
				}
			}
		} catch (Exception e) {
			logger.error("fatal error sending sms messages", e);
			return null;
		}
	}

	@Override
	public Pair<MessageStatus, String> sendSMS(String sessionId, String from, String to, String text) {

		String aResponse;
		MessageStatus returnStatus;

		try {

			String toEncoded = to.replaceAll("\\D", "");

			if (toEncoded.startsWith("0")) {
				toEncoded = toEncoded.replaceFirst("0", "+61");
			}

			String fromEncoded = URLEncoder.encode(from.trim(), EncodeType);
			String textEncoded = URLEncoder.encode(text.trim(), EncodeType);

			URL sendingUrl = new URL(smsGatewayURL + "sendmsg?session_id=" + sessionId + "&to=" + toEncoded + "&from=" + fromEncoded
					+ "&text=" + textEncoded);

			BufferedReader in = new BufferedReader(new InputStreamReader(sendingUrl.openStream()));
			// the response should be one line ERR: errorCode, errorDescription
			// or OK: sessionId
			// anything else is wrong.
			String str = in.readLine();
			in.close();

			logger.debug("response:" + str);

			if (str == null || str.isEmpty()) {
				aResponse = "no response from gateway";
				returnStatus = MessageStatus.FAILED;
				logger.error(aResponse);
			} else if (str.startsWith("ERR:")) {
				String errorString = str.substring(str.indexOf(":") + 1, str.length()).trim();
				aResponse = "sms sending via gateway failed: " + errorString;
				returnStatus = MessageStatus.FAILED;
				logger.error(aResponse);
			} else if (str.startsWith("ID:")) {
				aResponse = str;
				returnStatus = MessageStatus.SENT;
			} else {
				aResponse = "sms gateway response malormed : " + str;
				returnStatus = MessageStatus.FAILED;
				logger.error(aResponse);
			}

		} catch (UnsupportedEncodingException ue) {
			returnStatus = MessageStatus.QUEUED;
			aResponse = "unsupported encoding";
			logger.error("unsupported encoding", ue);
		} catch (MalformedURLException e) {
			returnStatus = MessageStatus.QUEUED;
			aResponse = "url malformed";
			logger.error("Malformed url for sms delivery", e);
		} catch (IOException e) {
			returnStatus = MessageStatus.QUEUED;
			aResponse = "io error";
			logger.debug("IO Error for sms delivery", e);
		} catch (Exception e) {
			returnStatus = MessageStatus.QUEUED;
			aResponse = "programming error";
			logger.error("Unknown exception for sms delivery", e);
		}

		return new Pair<>(returnStatus, aResponse);
	}
}
