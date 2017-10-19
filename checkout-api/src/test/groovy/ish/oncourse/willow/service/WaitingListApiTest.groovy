package ish.oncourse.willow.service;

import ish.oncourse.willow.model.checkout.waitinglist.WaitingListRequest;
import ish.oncourse.willow.model.common.CommonError;
import ish.oncourse.willow.model.common.ValidationError;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for WaitingListApi
 */
public class WaitingListApiTest {

    private WaitingListApi api;
    
    @Before
    public void setup() {
    }

    
    /**
     * Save Waiting List
     * Get set of college&#39;s preferences
     */
    @Test
    public void submitWaitingListTest() {
        WaitingListRequest request = null;
	//api.submitWaitingList(request);
        
        // TODO: test validations
        
        
    }
    
}
