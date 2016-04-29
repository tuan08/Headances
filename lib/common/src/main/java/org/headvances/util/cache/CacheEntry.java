package org.headvances.util.cache;

import java.lang.ref.SoftReference;

/**
 * Author : Tuan Nguyen
 */
public class CacheEntry<V> extends SoftReference<V>{
  private long expireTime_ ;
  
  public CacheEntry(long expireTime, V o) {
    super(o) ;
    expireTime_ = expireTime ;
  }
  
  public long getExpireTime() { return expireTime_ ;  }
}
