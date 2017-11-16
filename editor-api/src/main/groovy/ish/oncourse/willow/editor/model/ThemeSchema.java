package ish.oncourse.willow.editor.model;

import ish.oncourse.willow.editor.model.BlockItem;
import java.util.ArrayList;
import java.util.List;

public class ThemeSchema  {
  
    private List<BlockItem> top = new ArrayList<BlockItem>();
    private List<BlockItem> middle1 = new ArrayList<BlockItem>();
    private List<BlockItem> middle2 = new ArrayList<BlockItem>();
    private List<BlockItem> middle3 = new ArrayList<BlockItem>();
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
     * Get middle1
     * @return middle1
     */
    public List<BlockItem> getMiddle1() {
        return middle1;
    }

    public void setMiddle1(List<BlockItem> middle1) {
       this.middle1 = middle1;
    }

    public ThemeSchema middle1(List<BlockItem> middle1) {
      this.middle1 = middle1;
      return this;
    }

    public ThemeSchema addMiddle1Item(BlockItem middle1Item) {
      this.middle1.add(middle1Item);
      return this;
    }

    /**
     * Get middle2
     * @return middle2
     */
    public List<BlockItem> getMiddle2() {
        return middle2;
    }

    public void setMiddle2(List<BlockItem> middle2) {
       this.middle2 = middle2;
    }

    public ThemeSchema middle2(List<BlockItem> middle2) {
      this.middle2 = middle2;
      return this;
    }

    public ThemeSchema addMiddle2Item(BlockItem middle2Item) {
      this.middle2.add(middle2Item);
      return this;
    }

    /**
     * Get middle3
     * @return middle3
     */
    public List<BlockItem> getMiddle3() {
        return middle3;
    }

    public void setMiddle3(List<BlockItem> middle3) {
       this.middle3 = middle3;
    }

    public ThemeSchema middle3(List<BlockItem> middle3) {
      this.middle3 = middle3;
      return this;
    }

    public ThemeSchema addMiddle3Item(BlockItem middle3Item) {
      this.middle3.add(middle3Item);
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
      sb.append("    middle1: ").append(toIndentedString(middle1)).append("\n");
      sb.append("    middle2: ").append(toIndentedString(middle2)).append("\n");
      sb.append("    middle3: ").append(toIndentedString(middle3)).append("\n");
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

