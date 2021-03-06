package org.headvances.nlp.query2.chunker;

import java.util.List;

import org.headvances.nlp.query2.match.RuleMatcher;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.tag.TimeTag;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TimeChunker extends QueryChunker {
  public TimeChunker() throws Exception {
    String timeExp =  "\\d{2}[:h]\\d{2}(:\\d{2})?(am|pm)?" ;
    String gmtExp =  "(gmt|utc)([+-]\\d{1,2})?(:\\d{2})?" ;
    defineMatches(
      // The format end with GMT/UTC in bracket
      "/ regex{" + timeExp + "} .1. regex{\\(} .1. regex{" + gmtExp + "} .1. regex{\\)}",
      // The format end with GMT/UTC
      "/ regex{" + timeExp + "} .1. regex{" + gmtExp + "}",
      // Regular time format
      "/ regex{" + timeExp + "}"
    ) ;
  }

  protected void onMatch(List<IToken> holder, RuleMatcher rmatcher, IToken[] token, int from, int to) {
  	IToken set = createChunking(token, from, to) ;
  	set.add(new TimeTag("time"));
  	holder.add(set) ;
  }
}