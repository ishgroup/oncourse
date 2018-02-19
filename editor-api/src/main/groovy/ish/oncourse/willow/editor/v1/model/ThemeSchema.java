package ish.oncourse.willow.editor.v1.model;

import ish.oncourse.willow.editor.v1.model.BlockItem;
import java.util.ArrayList;
import java.util.List;

public class ThemeSchema  {
  
    private List<BlockItem> top = new ArrayList<BlockItem>();
    private List<BlockItem> left = new ArrayList<BlockItem>();
    private List<BlockItem> centre = new ArrayList<BlockItem>();
    private List<BlockItem> right = new ArrayList<BlockItem>();
    private List<BlockItem> footer = new ArrayList<BlockItem>();

    /**
     * Get top
     * @return top
     */
    public List<BlockItem> getTop() {
        return top;
    }

    public void setTop(List<BlockItem> top) {
       this.top = top;
    }

    public ThemeSchema top(List<BlockItem> top) {
      this.top = top;
      return this;
    }

    public ThemeSchema addTopItem(BlockItem topItem) {
      this.top.add(topItem);
      return this;
    }

    /**
     * Get left
     * @return left
     */
    public List<BlockItem> getLeft() {
        return left;
    }

    public void setLeft(List<BlockItem> left) {
       this.left = left;
    }

    public ThemeSchema left(List<BlockItem> left) {
      this.left = left;
      return this;
    }

    public ThemeSchema addLeftItem(BlockItem leftItem) {
      this.left.add(leftItem);
      return this;
    }

    /**
     * Get centre
     * @return centre
     */
    public List<BlockItem> getCentre() {
        return centre;
    }

    public void setCentre(List<BlockItem> centre) {
       this.centre = centre;
    }

    public ThemeSchema centre(List<BlockItem> centre) {
      this.centre = centre;
      return this;
    }

    public ThemeSchema addCentreItem(BlockItem centreItem) {
      this.centre.add(centreItem);
      return this;
    }

    /**
     * Get right
     * @return right
     */
    public List<BlockItem> getRight() {
        return right;
    }

    public void setRight(List<BlockItem> right) {
       this.right = right;
    }

    public ThemeSchema right(List<BlockItem> right) {
      this.right = right;
      return this;
    }

    public ThemeSchema addRightItem(BlockItem rightItem) {
      this.right.add(rightItem);
      return this;
    }

    /**
     * Get footer
     * @return footer
     */
    public List<BlockItem> getFooter() {
        return footer;
    }

    public void setFooter(List<BlockItem> footer) {
       this.footer = footer;
    }

    public ThemeSchema footer(List<BlockItem> footer) {
      this.footer = footer;
      return this;
    }

    public ThemeSchema addFooterItem(BlockItem footerItem) {
      this.footer.add(footerItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ThemeSchema {\n");
      
      sb.append("    top: ").append(toIndentedString(top)).append("\n");
      sb.append("    left: ").append(toIndentedString(left)).append("\n");
      sb.append("    centre: ").append(toIndentedString(centre)).append("\n");
      sb.append("    right: ").append(toIndentedString(right)).append("\n");
      sb.append("    footer: ").append(toIndentedString(footer)).append("\n");
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

