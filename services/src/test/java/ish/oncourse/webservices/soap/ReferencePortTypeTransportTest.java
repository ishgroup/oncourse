package ish.oncourse.webservices.soap;

import ish.oncourse.webservices.soap.v4.ReferencePortType;
import ish.oncourse.webservices.soap.v4.ReferenceService;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceResult;
import ish.oncourse.webservices.v4.stubs.reference.ReferenceStub;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReferencePortTypeTransportTest  extends AbstractTransportTest  {

	@Test
	public void  test_getMaximumVersion() throws JAXBException {
		assertEquals(Long.MAX_VALUE, getReferencePortType().getMaximumVersion());
	}


	@Test
	public void  test_getRecords() throws JAXBException {
		ReferenceResult referenceResult = getReferencePortType().getRecords(Long.MAX_VALUE);
		List<ReferenceStub> resultStub = referenceResult.getCountryOrLanguageOrModule();
		assertListStubs(resultStub,PACKAGE_NAME_REFERENCE_STUBS,ReferenceStub.class);
	}

	private ReferencePortType getReferencePortType() throws JAXBException {
		ReferenceService referenceService = new ReferenceService(ReferencePortType.class.getClassLoader().getResource("wsdl/v4_reference.wsdl"));
		ReferencePortType referencePortType = referenceService.getReferencePort();

		initPortType((BindingProvider) referencePortType, "/services/v4/reference");
		return referencePortType;
	}

}
