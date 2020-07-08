package ish.oncourse.usi.rest;

import com.google.inject.Inject;
import com.sun.xml.wss.XWSSecurityException;
import ish.common.types.LocateUSIResult;
import ish.common.types.USIVerificationResult;
import ish.oncourse.usi.USIService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.stream.XMLStreamException;
import java.text.ParseException;

@Path("/")
public class UsiRestService {

    @Inject
    private USIService usiService;

    @GET
    @Path("/verify")
    @Produces({ "application/json" })
    public USIVerificationResult verify(@QueryParam("studentFirstName") String studentFirstName,
                                        @QueryParam("studentLastName") String studentLastName,
                                        @QueryParam("studentBirthDate") String studentBirthDate,
                                        @QueryParam("usiCode") String usiCode,
                                        @QueryParam("orgCode") String orgCode,
                                        @QueryParam("collegeABN") String collegeABN,
                                        @QueryParam("softwareId") String softwareId,
                                        @QueryParam("collegeKey") String collegeKey) throws ParseException {
        return usiService.verifyUsi(studentFirstName, studentLastName, studentBirthDate, usiCode, orgCode, collegeABN, softwareId, collegeKey);
    }

    @GET
    @Path("/locate")
    @Produces({ "application/json" })
    public LocateUSIResult locate(@QueryParam("orgCode") String orgCode,
                                  @QueryParam("firstName") String firstName,
                                  @QueryParam("middleName") String middleName,
                                  @QueryParam("familyName") String familyName,
                                  @QueryParam("gender") String gender,
                                  @QueryParam("dateOfBirth") String dateOfBirth,
                                  @QueryParam("townCityOfBirth") String townCityOfBirth,
                                  @QueryParam("emailAddress") String emailAddress,
                                  @QueryParam("userReference") String userReference,
                                  @QueryParam("collegeABN") String collegeABN,
                                  @QueryParam("softwareId") String softwareId) throws ParseException, XWSSecurityException, XMLStreamException {
        return usiService.locateUSI(orgCode,
                firstName,
                middleName,
                familyName,
                gender,
                dateOfBirth,
                townCityOfBirth,
                emailAddress,
                userReference,
                collegeABN,
                softwareId);
    }
}
