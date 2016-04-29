package org.headvances.util.cache;

import java.util.LinkedHashMap;
import java.util.Map;
/**
 * Author : Tuan Nguyen
 */
public class InmemoryCache<K, V>  
  extends LinkedHashMap<K, CacheEntry<V>> implements Cache<K, V> {
  private static final long serialVersionUID = 1L;

  private String name_ ;
  private String label_ ;
  
  private int maxSize_ ;
  private long liveTime_ = -1;
  private int cacheHit_ ;
  private int cacheMiss_ ;
  
  public InmemoryCache(String name, int maxSize) {
    maxSize_  = maxSize ;
    name_     = name ;
  }
  
  public String getName() { return name_ ; }
  public void   setName(String s) {  name_ = s ; } 
  
  public String getLabel() { 
    if(label_ ==  null) {
      if(name_.length() > 30 ){
        String shortLabel = name_.substring(name_.lastIndexOf(".") + 1) ; 
        setLabel(shortLabel) ;
        return  shortLabel;
      }
      return name_ ; 
    }
    return label_ ;    
  }
  
  public void setLabel(String name) {  label_ = name ; }
  
  public int  getCacheSize()  { return size() ; }
  
  public int  getMaxSize() { return maxSize_ ; }
  public void setMaxSize(int max) { maxSize_ = max ; }
  
  public long  getLiveTime() { return liveTime_ ; }
  public void  setLiveTime(long period)  { liveTime_ = period * 1000;} 
  
  synchronized public V getCachedObject(K name) {
    CacheEntry<V> info =  super.get(name) ;
    if(info !=  null)  { 
      if(isExpire(info)) {
        super.remove(name) ;
        cacheMiss_++ ;
        return null ;
      }
      cacheHit_++ ;
      return info.get() ;
    }
    cacheMiss_++ ;
    return null ; 
  }
  
  synchronized public V removeCachedObject(K name) {
    CacheEntry<V> ref =  super.remove(name) ;
    if(ref == null)  return null;
    if(isExpire(ref)) {
      return null ;
    }
    return ref.get() ;
  }
    
  synchronized public void putCachedObject(K name, V obj) {
    if(liveTime_ == 0) return ;
    long expireTime = -1 ;
    if(liveTime_ > 0) expireTime = System.currentTimeMillis() + liveTime_ ;
    CacheEntry<V> ref = createCacheEntry(expireTime, obj) ;
    super.put(name, ref) ;
  }
  
  synchronized public void clearCache() {
    super.clear() ;
  }
  
  public int getCacheHit()  { return cacheHit_  ;}
  
  public int getCacheMiss() { return cacheMiss_ ; }
  
  protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
    if(size() > maxSize_ ) {
      try {
        remove(eldest.getKey()) ;
      } catch (Exception ex) {
        throw  new RuntimeException(ex) ;
      }
      return  true ;
    }
    return false ;
  }
  
  private boolean isExpire(CacheEntry info) {
    //-1 mean cache live for ever
    if(liveTime_ < 0) return false ;
    if(System.currentTimeMillis() > info.getExpireTime()) return true ;
    return false ;
  }
  
  protected CacheEntry<V> createCacheEntry(long expTime, V objToCache) {
    return new CacheEntry<V>(expTime, objToCache) ;
  }
}