package ish.oncourse.willow.editor.v1.model;


public class BlockItem  {
  
    private Integer id = null;
    private Integer position = null;

    /**
     * unique id of the Block
     * @return id
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
       this.id = id;
    }

    public BlockItem id(Integer id) {
      this.id = id;
      return this;
    }

    /**
     * order position of the block
     * @return position
     */
    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
       this.position = position;
    }

    public BlockItem position(Integer position) {
      this.position = position;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class BlockItem {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    position: ").append(toIndentedString(position)).append("\n");
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

