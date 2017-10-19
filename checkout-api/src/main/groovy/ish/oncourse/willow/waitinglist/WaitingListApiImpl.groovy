package ish.oncourse.willow.waitinglist

import com.google.inject.Inject
import ish.oncourse.willow.cayenne.CayenneService
import ish.oncourse.willow.model.checkout.waitinglist.WaitingListRequest
import ish.oncourse.willow.model.field.FieldHeading
import ish.oncourse.willow.service.WaitingListApi
import ish.oncourse.willow.service.impl.CollegeService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class WaitingListApiImpl implements WaitingListApi {

    final static  Logger logger = LoggerFactory.getLogger(WaitingListApiImpl.class)
    
    private CayenneService cayenneService
    private CollegeService collegeService

    @Inject
    WaitingListApiImpl(CayenneService cayenneService, CollegeService collegeService) {
        this.cayenneService = cayenneService
        this.collegeService = collegeService
    }
    
    @Override
    void submitWaitingList(WaitingListRequest request) {

    }

    @Override
    List<FieldHeading> waitingListFields(String classId) {
        return null
    }
}
