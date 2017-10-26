package ish.oncourse.willow.model.web;


public class Course  {
  
    private String id = null;
    private String code = null;
    private String name = null;
    private String description = null;
    private Boolean hasCurrentClasses = null;
    private Boolean hasMoreAvailablePlaces = null;

    /**
     * Internal Unique identifier of course
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
       this.id = id;
    }

    public Course id(String id) {
      this.id = id;
      return this;
    }

    /**
     * Code of course
     * @return code
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
       this.code = code;
    }

    public Course code(String code) {
      this.code = code;
      return this;
    }

    /**
     * Name of course
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
       this.name = name;
    }

    public Course name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Description of course
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
       this.description = description;
    }

    public Course description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Has enrollable classes
     * @return hasCurrentClasses
     */
    public Boolean getHasCurrentClasses() {
        return hasCurrentClasses;
    }

    public void setHasCurrentClasses(Boolean hasCurrentClasses) {
       this.hasCurrentClasses = hasCurrentClasses;
    }

    public Course hasCurrentClasses(Boolean hasCurrentClasses) {
      this.hasCurrentClasses = hasCurrentClasses;
      return this;
    }

    /**
     * Has more available places
     * @return hasMoreAvailablePlaces
     */
    public Boolean getHasMoreAvailablePlaces() {
        return hasMoreAvailablePlaces;
    }

    public void setHasMoreAvailablePlaces(Boolean hasMoreAvailablePlaces) {
       this.hasMoreAvailablePlaces = hasMoreAvailablePlaces;
    }

    public Course hasMoreAvailablePlaces(Boolean hasMoreAvailablePlaces) {
      this.hasMoreAvailablePlaces = hasMoreAvailablePlaces;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Course {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    code: ").append(toIndentedString(code)).append("\n");
      sb.append("    name: ").append(toIndentedString(name)).append("\n");
      sb.append("    description: ").append(toIndentedString(description)).append("\n");
      sb.append("    hasCurrentClasses: ").append(toIndentedString(hasCurrentClasses)).append("\n");
      sb.append("    hasMoreAvailablePlaces: ").append(toIndentedString(hasMoreAvailablePlaces)).append("\n");
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

