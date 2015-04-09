package ish.oncourse.webservices.jobs;

import ish.common.types.MessageStatus;
import ish.oncourse.model.College;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.model.Pair;
import ish.oncourse.services.message.IMessagePersonService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.services.sms.ISMSService;
import ish.oncourse.services.template.WillowMessagingTemplateEngine;
import ish.oncourse.services.template.WillowMessagingTemplateResponder;
import ish.persistence.CommonPreferenceController;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Job for sending SMS
 */
public class SMSJob implements Job {
	
	private static final int MESSAGE_FETCH_LIMIT = 500;

	private static final Logger logger = Logger.getLogger(SMSJob.class);

	private final IMessagePersonService messagePersonService;
	private final ISMSService smsService;
	private final ICayenneService cayenneService;
	private final PreferenceControllerFactory prefFactory;

	public SMSJob(IMessagePersonService messagePersonService, ISMSService smsService, PreferenceControllerFactory prefFactory,
			ICayenneService cayenneService) {
		this.messagePersonService = messagePersonService;
		this.smsService = smsService;
		this.prefFactory = prefFactory;
		this.cayenneService = cayenneService;
	}

	/**
	 * Sends sms messages, which were replicated from Angel.
	 */
	public void execute() {

		logger.info("SMS Job started. Fetching sms messages...");

		ObjectContext objectContext = null;

		try {
			List<MessagePerson> smsMessages = messagePersonService.smsToSend(MESSAGE_FETCH_LIMIT);

			filterStaleMessages(smsMessages);

			if (smsMessages.isEmpty()) {
				return;
			}

			String sessionId = getSessionId();

			objectContext = cayenneService.newContext();

			for (MessagePerson p : smsMessages) {

				MessagePerson smsMessage = objectContext.localObject(p);

				College aCollege = smsMessage.getCollege();

				CommonPreferenceController pref = prefFactory.getPreferenceController(aCollege);

				if (aCollege == null || !pref.getLicenseSms()) {
					smsMessage.setStatus(MessageStatus.FAILED);
					smsMessage.setResponse("sms license is not enabled");
				} else {
					String fromAddress = pref.getSMSFromAddress();

					if (fromAddress == null || fromAddress.isEmpty()) {
						smsMessage.setStatus(MessageStatus.QUEUED);
						smsMessage.setResponse("From address not configured");
					} else {
						String toAddress = smsMessage.getDestinationAddress();

						if (toAddress == null || toAddress.isEmpty()) {
							smsMessage.setStatus(MessageStatus.FAILED);
							smsMessage.setResponse("no mobileNumber found for recipient");
						} else {
							String smsTemplateText = smsMessage.getMessage().getSmsText();

							if (smsTemplateText == null || smsTemplateText.isEmpty()) {
								smsMessage.setStatus(MessageStatus.FAILED);
								smsMessage.setResponse("no sms text provided");
							} else {
								sendSMS(sessionId, fromAddress, smsMessage);
							}
						}
					}
				}

				objectContext.commitChanges();
			}
		} catch (Exception e) {
			logger.error("Error in SMSJob.", e);

			if (objectContext != null) {
				objectContext.rollbackChanges();
			}
		}

		logger.info("SMS Job finished.");
	}

	/**
	 * Calls gateway to send sms.
	 * 
	 * @param sessionId
	 *            gateway session id.
	 * @param fromAddress
	 *            from mobile number
	 * @param mp
	 *            message person object
	 */
	private void sendSMS(String sessionId, String fromAddress, MessagePerson mp) {

		String smsTranslatedText = smsTextFromTemplate(mp.getMessage().getSmsText(), mp);

		try {
			Pair<MessageStatus, String> resp = smsService.sendSMS(sessionId, fromAddress, mp.getDestinationAddress(), smsTranslatedText);

			MessageStatus status = resp.getFirst();

			if (status == MessageStatus.SENT) {
				mp.setTimeOfDelivery(new Date());
			}

			mp.setStatus(status);
			mp.setResponse(resp.getSecond());

		} catch (Exception e) {
			logger.error("Fatal exception during contacting sms gateway.", e);
			mp.setStatus(MessageStatus.FAILED);
			mp.setResponse("Fatal exception during contacting sms gateway.");
		}
	}

	/**
	 * Contacts sms gateway and initiates session.
	 * 
	 * @return session id
	 * @throws Exception
	 *             if can not contact gateway or empty session id.
	 */
	private String getSessionId() throws Exception {
		try {
			String sessionId = smsService.authenticate();
			if (sessionId == null) {
				logger.error("Empty session id from sms service.");
				throw new Exception("Empty session id from sms service.");
			}
			return sessionId;
		} catch (Exception e) {
			logger.error("Fatal error during authentication to sms gateway.", e);
			throw new Exception("Fatal error during authentication to sms gateway.", e);
		}
	}

	/**
	 * Filters and fails expired messages from the list
	 * 
	 * @param smsMessages
	 *            list of sms messages
	 */
	private void filterStaleMessages(List<MessagePerson> smsMessages) {

		Calendar calendar = Calendar.getInstance();
		calendar.setLenient(true);
		calendar.add(Calendar.HOUR, -24);

		Expression recentMessageQual = ExpressionFactory.lessOrEqualExp(MessagePerson.CREATED_PROPERTY, calendar.getTime());

		List<MessagePerson> staleMessages = recentMessageQual.filterObjects(smsMessages);

		if (!staleMessages.isEmpty()) {
			// Fail old sms messages
			for (MessagePerson m : staleMessages) {
				try {
					smsMessages.remove(m);
					m.setStatus(MessageStatus.FAILED);
					m.setResponse("onCourse-web fails sms messages older than 24 hours. Won't deliver.");
					m.getObjectContext().commitChanges();
				} catch (CayenneRuntimeException ce) {
					logger.error("Unable to fail old sms message.", ce);
					m.getObjectContext().rollbackChanges();
				}
			}
		}
	}

	/**
	 * Creates sms message context
	 * 
	 * @param smsTemplateText
	 *            sms template
	 * @param mp
	 *            MessagePerson object
	 * @return sms content
	 */
	private String smsTextFromTemplate(final String smsTemplateText, final MessagePerson mp) {

		Map<String, String> filteredContents = WillowMessagingTemplateEngine
				.generateResponseForTemplateResponder(new WillowMessagingTemplateResponder() {

					public String templateMessage() {
						return smsTemplateText;
					}

					public String templateSubject() {
						return "";
					}

					public Map<String, Object> templateValuesForKeys(Set<String> keys) {
						Map<String, Object> results = new HashMap<>();

						for (String key : keys) {
							// TODO (lachlan) move these definitions and
							// translations into MessageTranslator
							switch (key) {
								case "contact.firstName":
									results.put("contact.firstName", mp.getContact().getGivenName());
									break;
								case "contact.lastName":
									results.put("contact.lastName", mp.getContact().getFamilyName());
									break;
								default:
									results.put(key, Void.TYPE);
									break;
							}
						}
						return results;
					}
				});

		return filteredContents.get("message");
	}
}
