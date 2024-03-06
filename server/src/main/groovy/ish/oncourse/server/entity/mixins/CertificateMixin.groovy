/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.entity.mixins

import ish.oncourse.API
import ish.oncourse.entity.services.CertificateService
import ish.oncourse.server.cayenne.Certificate
import ish.oncourse.server.cayenne.CertificateOutcome
import ish.oncourse.server.cayenne.Outcome
import static ish.oncourse.server.entity.mixins.MixinHelper.getService
import net.glxn.qrgen.core.image.ImageType
import net.glxn.qrgen.javase.QRCode

class CertificateMixin {

	@Deprecated
	static isCompleteQualification(Certificate self) {
		getService(CertificateService).isQualification(self)
	}

	/**
	 * Outcomes attached to the Certificate
	 *
	 * @return list of outcomes
	 */
	@API
	static getOutcomes(Certificate self) {
		getService(CertificateService).getOutcomes(self)
	}

	/**
	 * @return list of Outcomes with an ASSESSABLE OutcomeStatus
	 */
	@API
	static getSuccessfulOutcomes(Certificate self) {
		getService(CertificateService).getSuccessfulOutcomes(self)
	}

	/**
	* @return the CertificateOutcome with the most recent EndDate attached to this certificate
	*/
	@API
	static getLastOutcome(Certificate self) {
		self.certificateOutcomes.collect { co -> co.outcome }.toSorted { o1, o2 ->
			o2.getEndDate() <=> o1.getEndDate()
		}.find { true }
	}

	 /**
     * Attaches Certificate to the given Outcome
     *
     * @param outcome The Outcome to attach the certificate to
     */
	@API
	static addToOutcomes(Certificate self, Outcome outcome) {
		if (outcome != null) {
			CertificateOutcome co = (CertificateOutcome) self.context.newObject(CertificateOutcome)
			co.setCertificate(self)
			co.setOutcome(outcome)
		}
	}

    /**
     * Returns a 128x128 PNG of the unique QR code used to validate this certificate. This is to be used to render the QR code on a printed Certficate report.
     *
     * @return QRCode
     */
	@API
	static getQRCodeImage(Certificate self) {
		return new ByteArrayInputStream(QRCode.from(self.portalUrl).to(ImageType.PNG).withSize(128, 128).stream().toByteArray())
	}

	/**
     * Returns a web URL that can be entered into a web browser to validate this certificate.
     *
     * @return QRCode
     */
	@API
	static getVerificationURL(Certificate self) {
		return self.portalUrl
	}

	/**
	 * return existing uniqueCode or generate it if it's NULL.
	 *
	 * @param self
	 * @return uniqueCode for this certificate
     */
	@API
	static getQrUniqueCode(Certificate self) {
		self.uniqueCode
	}
}
