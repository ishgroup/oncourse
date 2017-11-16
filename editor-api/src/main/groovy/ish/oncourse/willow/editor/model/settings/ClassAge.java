package ish.oncourse.willow.editor.model.settings;

import ish.oncourse.willow.editor.model.settings.ClassCondition;
import ish.oncourse.willow.editor.model.settings.ClassEnrolmentCondition;

public class ClassAge  {
  
    private Double hideClassDays = null;
    private ClassCondition hideClassCondition = null;
    private Double stopWebEnrolmentDays = null;
    private ClassEnrolmentCondition stopWebEnrolmentCondition = null;

    /**
     * Get hideClassDays
     * @return hideClassDays
     */
    public Double getHideClassDays() {
        return hideClassDays;
    }

    public void setHideClassDays(Double hideClassDays) {
       this.hideClassDays = hideClassDays;
    }

    public ClassAge hideClassDays(Double hideClassDays) {
      this.hideClassDays = hideClassDays;
      return this;
    }

    /**
     * Get hideClassCondition
     * @return hideClassCondition
     */
    public ClassCondition getHideClassCondition() {
        return hideClassCondition;
    }

    public void setHideClassCondition(ClassCondition hideClassCondition) {
       this.hideClassCondition = hideClassCondition;
    }

    public ClassAge hideClassCondition(ClassCondition hideClassCondition) {
      this.hideClassCondition = hideClassCondition;
      return this;
    }

    /**
     * Get stopWebEnrolmentDays
     * @return stopWebEnrolmentDays
     */
    public Double getStopWebEnrolmentDays() {
        return stopWebEnrolmentDays;
    }

    public void setStopWebEnrolmentDays(Double stopWebEnrolmentDays) {
       this.stopWebEnrolmentDays = stopWebEnrolmentDays;
    }

    public ClassAge stopWebEnrolmentDays(Double stopWebEnrolmentDays) {
      this.stopWebEnrolmentDays = stopWebEnrolmentDays;
      return this;
    }

    /**
     * Get stopWebEnrolmentCondition
     * @return stopWebEnrolmentCondition
     */
    public ClassEnrolmentCondition getStopWebEnrolmentCondition() {
        return stopWebEnrolmentCondition;
    }

    public void setStopWebEnrolmentCondition(ClassEnrolmentCondition stopWebEnrolmentCondition) {
       this.stopWebEnrolmentCondition = stopWebEnrolmentCondition;
    }

    public ClassAge stopWebEnrolmentCondition(ClassEnrolmentCondition stopWebEnrolmentCondition) {
      this.stopWebEnrolmentCondition = stopWebEnrolmentCondition;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ClassAge {\n");
      
      sb.append("    hideClassDays: ").append(toIndentedString(hideClassDays)).append("\n");
      sb.append("    hideClassCondition: ").append(toIndentedString(hideClassCondition)).append("\n");
      sb.append("    stopWebEnrolmentDays: ").append(toIndentedString(stopWebEnrolmentDays)).append("\n");
      sb.append("    stopWebEnrolmentCondition: ").append(toIndentedString(stopWebEnrolmentCondition)).append("\n");
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

