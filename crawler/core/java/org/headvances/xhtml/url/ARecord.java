package org.headvances.xhtml.url;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.headvances.xhtml.url.Record;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 13, 2010  
 */
public class ARecord implements Record {
  private String url ;
  private int    value ;
  private int    segment ;
  
  public String getURL() { return url; }
  public void setURL(String url) { this.url = url; }

  public int getValue() { return value ;}
  public void setValue(int value) { this.value = value; }

  public int getSegment() { return segment ;}
  public void setSegment(int segment) { this.segment = segment; }

  public void readFields(DataInput in) throws IOException {
    this.url = Text.readString(in) ;
    this.value = in.readInt();
    this.segment = in.readInt();
  }

  public void write(DataOutput out) throws IOException {
    Text.writeString(out, url) ;
    out.writeInt(value) ;
    out.writeInt(segment) ;
  }
}