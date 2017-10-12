package ish.oncourse.portal.services;

import java.io.Serializable;

public class Notification implements Serializable{

    private int newResourcesCount = 0;
    private int newResultsCount = 0;
    private int newHistoryCount = 0;

    public Notification(int newHistoryCount, int newResultsCount, int newResourcesCount) {
        this.newResourcesCount = newResourcesCount;
        this.newResultsCount = newResultsCount;
        this.newHistoryCount = newHistoryCount;
    }
    
    public boolean hasNewResources() {
        return newResourcesCount > 0;
    }

    public boolean hasNewResults() {
        return newResultsCount > 0;
    }

    public boolean hasNewHistory() {
        return newHistoryCount > 0;
    }

    public int getNewResourcesCount() {
        return newResourcesCount;
    }

    public void setNewResourcesCount(int newResourcesCount) {
        this.newResourcesCount = newResourcesCount;
    }

    public int getNewResultsCount() {
        return newResultsCount;
    }

    public void setNewResultsCount(int newResultsCount) {
        this.newResultsCount = newResultsCount;
    }

    public int getNewHistoryCount() {
        return newHistoryCount;
    }

    public void setNewHistoryCount(int newHistoryCount) {
        this.newHistoryCount = newHistoryCount;
    }
}
