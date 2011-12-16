package solr2155.lucene;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;

import java.io.IOException;

/**
 * Wraps Lucene 3 TermEnum to make it look like a Lucene 4 TermsEnum
 * @author dsmiley
 */
public class TermsEnumCompatibility {
  private final IndexReader reader;
  private final String fieldName;
  private TermEnum termEnum;

  public TermsEnumCompatibility(IndexReader reader, String fieldName) throws IOException {
    this.reader = reader;
    this.fieldName = fieldName.intern();
    this.termEnum = reader.terms(new Term(this.fieldName));
  }

  public Term term() {
    Term t = termEnum.term();
    return t != null && t.field() == fieldName ? t : null;
  }

  public Term next() throws IOException {
    return termEnum.next() ? term() : null;
  }

  public static enum SeekStatus {END, FOUND, NOT_FOUND}

  public SeekStatus seek(String value) throws IOException {
    termEnum = reader.terms(new Term(this.fieldName,value));
    Term t = term();
    if (t == null)
      return SeekStatus.END;
    return (t.text().equals(value)) ? SeekStatus.FOUND : SeekStatus.NOT_FOUND;
  }
}
