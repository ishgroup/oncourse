package ish.oncourse.willow.editor.v1.model;


public class SkillsOnCourseSettings  {
  
    private Boolean hideStudentDetails = null;
    private Boolean enableOutcomeMarking = null;
    private String tutorFeedbackEmail = null;

    /**
     * Get hideStudentDetails
     * @return hideStudentDetails
     */
    public Boolean HideStudentDetails() {
        return hideStudentDetails;
    }

    public void setHideStudentDetails(Boolean hideStudentDetails) {
       this.hideStudentDetails = hideStudentDetails;
    }

    public SkillsOnCourseSettings hideStudentDetails(Boolean hideStudentDetails) {
      this.hideStudentDetails = hideStudentDetails;
      return this;
    }

    /**
     * Get enableOutcomeMarking
     * @return enableOutcomeMarking
     */
    public Boolean EnableOutcomeMarking() {
        return enableOutcomeMarking;
    }

    public void setEnableOutcomeMarking(Boolean enableOutcomeMarking) {
       this.enableOutcomeMarking = enableOutcomeMarking;
    }

    public SkillsOnCourseSettings enableOutcomeMarking(Boolean enableOutcomeMarking) {
      this.enableOutcomeMarking = enableOutcomeMarking;
      return this;
    }

    /**
     * Get tutorFeedbackEmail
     * @return tutorFeedbackEmail
     */
    public String getTutorFeedbackEmail() {
        return tutorFeedbackEmail;
    }

    public void setTutorFeedbackEmail(String tutorFeedbackEmail) {
       this.tutorFeedbackEmail = tutorFeedbackEmail;
    }

    public SkillsOnCourseSettings tutorFeedbackEmail(String tutorFeedbackEmail) {
      this.tutorFeedbackEmail = tutorFeedbackEmail;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class SkillsOnCourseSettings {\n");
      
      sb.append("    hideStudentDetails: ").append(toIndentedString(hideStudentDetails)).append("\n");
      sb.append("    enableOutcomeMarking: ").append(toIndentedString(enableOutcomeMarking)).append("\n");
      sb.append("    tutorFeedbackEmail: ").append(toIndentedString(tutorFeedbackEmail)).append("\n");
      sb.append("}");
      return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private static String toIndentedString(java.lang.Object o) {
      if (o == null) {
        return "null";
      }
      return o.toString().replace("\n", "\n    ");
    }
}

