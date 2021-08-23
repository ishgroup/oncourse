package ish.oncourse.server.api.traits

trait OutcomeProgressionDTOTrait {
    abstract BigDecimal getAbsent();

    abstract BigDecimal getAttended();

    abstract BigDecimal getNotMarked();

    abstract BigDecimal getFutureTimetable();

    abstract Integer getNotReleased();

    abstract Integer getReleased();

    abstract Integer getSubmitted();

    abstract Integer getMarked();

    /**
     *
     * @return max value of attended hours, depends on method, which is in use in OutcomeProgressionChar.tsx file
     */
    BigDecimal getMaxAttendedHoursProgressValue() {
        return getAbsent().add(getAttended()).add(getNotMarked()).add(getFutureTimetable())
    }

    /**
     *
     * @return max value of marked assessments, depends on method, which is in use in OutcomeProgressionChar.tsx file
     */
    BigDecimal getMaxMarkedAssessmentsProgressValue() {
        return getNotReleased() + getReleased() + getSubmitted() + getMarked()
    }
}