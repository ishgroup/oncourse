package ish.oncourse.admin.services.ntis;

import au.gov.training.services.trainingcomponent.*;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.datacontract.schemas._2004._07.system.DateTimeOffset;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.*;

@Ignore("for testing requests/responses of NTIS service")
public class ManualNTISServiceTest {

    ITrainingComponentService service;
    @Before
    public void setup() {
        service = createService();
    }


    @Test
    public void ntisServiceTest() throws Exception {
        Date to = new Date();
        Date from = DateUtils.addMonths(to, -12);

        List<TrainingComponentSummary> modifiedList = fetchModifiedRecords(from, to);

        List<DeletedTrainingComponent> deletedList = fetchDeletedRecords(from, to);

        System.out.println("\nMODIFIED >");
        modifiedList.stream().forEach(s ->
                System.out.println(
                        String.format("\t%s : type %s, created %s, updated %s; isCurrent %s, usage %s",
                                s.getCode().getValue(),
                                StringUtils.join(" ", s.getComponentType()),
                                s.getCreatedDate().getDateTime(),
                                s.getUpdatedDate().getDateTime(),
                                s.getIsCurrent().getValue().toString(),
                                s.getUsageReccomendation() == null ? "NULL ur" :s.getUsageReccomendation().value())
                ));

        System.out.println("\nDELETED >");
        deletedList.forEach(s ->
                System.out.println(
                        String.format("\t%s : operation %s, updated %s;",
                                s.getNationalCode().getValue(),
                                s.getOperation().value(),
                                s.getUpdatedDate().getDateTime())));
    }

    private List<TrainingComponentSummary> fetchModifiedRecords(Date from, Date to) throws Exception {
        TrainingComponentModifiedSearchRequest searchRequest = buildSearchRequest(from, to, getTypeFilterForModule(), 1);
        TrainingComponentSearchResult searchResult = service.searchByModifiedDate(searchRequest);

        int pageCount = (int) Math.ceil((double) searchResult.getCount() / 1000);

        List<TrainingComponentSummary> modifiedList = new ArrayList<>();
        modifiedList.addAll(searchResult.getResults().getValue().getTrainingComponentSummary());

        for (int i = 2; i <= pageCount; i++) {
            searchRequest = buildSearchRequest(DateUtils.addMonths(to, -6), to, getTypeFilterForModule(), i);
            searchResult = service.searchByModifiedDate(searchRequest);
            modifiedList.addAll(searchResult.getResults().getValue().getTrainingComponentSummary());
        }
        return modifiedList;
    }

    private List<DeletedTrainingComponent> fetchDeletedRecords(Date from, Date to) throws Exception {
        DeletedSearchRequest deletedRequest = buildDeletedSearchRequest(from, to);
        List<DeletedTrainingComponent> deletedList = service.searchDeletedByDeletedDate(deletedRequest).getDeletedTrainingComponent();
        return deletedList;
    }

    private TrainingComponentTypeFilter getTypeFilterForModule() {
        TrainingComponentTypeFilter typeFilter = new TrainingComponentTypeFilter();

        typeFilter.setIncludeUnit(true);
        typeFilter.setIncludeAccreditedCourseModule(true);
        return typeFilter;
    }

    private TrainingComponentModifiedSearchRequest buildSearchRequest(Date from, Date to, TrainingComponentTypeFilter typeFilter, int pageNumber) throws DatatypeConfigurationException {
        GregorianCalendar cal = new GregorianCalendar();

        DateTimeOffset fromDate = new DateTimeOffset();
        DateTimeOffset toDate = new DateTimeOffset();

        cal.setTime(from);
        fromDate.setDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
        cal.setTime(to);
        cal.add(Calendar.HOUR_OF_DAY, 23);
        cal.add(Calendar.MINUTE, 59);
        toDate.setDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));

        ObjectFactory objectFactory = new ObjectFactory();

        TrainingComponentModifiedSearchRequest request = new TrainingComponentModifiedSearchRequest();

        request.setStartDate(objectFactory.createTrainingComponentModifiedSearchRequestStartDate(fromDate));
        request.setEndDate(objectFactory.createTrainingComponentModifiedSearchRequestEndDate(toDate));
        request.setTrainingComponentTypes(objectFactory.createTrainingComponentTypeFilter(typeFilter));
        request.setPageSize(100);
        request.setPageNumber(pageNumber);
        return request;
    }

    private DeletedSearchRequest buildDeletedSearchRequest(Date from, Date to) throws DatatypeConfigurationException {

        GregorianCalendar cal = new GregorianCalendar();

        DateTimeOffset fromDate = new DateTimeOffset();
        DateTimeOffset toDate = new DateTimeOffset();

        cal.setTime(from);
        fromDate.setDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
        cal.setTime(to);
        cal.add(Calendar.HOUR_OF_DAY, 23);
        cal.add(Calendar.MINUTE, 59);
        toDate.setDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
        DeletedSearchRequest deletedRequest = new DeletedSearchRequest();

        ObjectFactory objectFactory = new ObjectFactory();

        deletedRequest.setStartDate(objectFactory.createTrainingComponentModifiedSearchRequestStartDate(fromDate));
        deletedRequest.setEndDate(objectFactory.createTrainingComponentModifiedSearchRequestEndDate(toDate));
        return deletedRequest;
    }

    private ITrainingComponentService createService() {
        return new TrainingComponentServiceBuilder().buildService(null);
    }
}
