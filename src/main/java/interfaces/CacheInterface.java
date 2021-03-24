package interfaces;

/**
 * This Interface store methods to create outer/inner Cache and interact with them
 */
public interface CacheInterface {
    /**
     * Method creates inner Cache and put him inside outer Cache
     * @param cache - key of inner Cache which put in outer Cache to get inner Cache
     */
    void createCache(String cache);

    /**
     * This Method put key with value object in inner Cache
     * @param cache - key of inner Cache which store in outer Cache
     * @param key - put to inner cache
     * @param value - value of key in inner cache
     * @return - operation boolean result
     */
    boolean put(String cache, String key, Object value);

    /**
     * Method check if inner cache with this name exists in outer @mainCache
     * @param cache - inner cache name
     * @return - true if this cache exists, false if not
     */
    boolean checkIfInnerCacheExistsInMainCache(String cache);

    /**
     * Method check that key exists in inner cache with name 'cache'
     * @param cache - cache name
     * @param key - key of inner cache
     * @return - true if this key exists in cache with this name, false if not exists
     */
    boolean checkKeyExistsInInnerCache(String cache, String key);

    /**
     * Method return value of @key from inner cache which store in outer cache with @cache key
     * @param cache - key of inner cache
     * @param key - key of value in inner cache
     * @return - value of key in inner cache if this key exists
     */
    Object get(String cache, String key);

    /**
     * re-create outer Cache, so link to old Cache will lost and Garbage Collector will delete old link
     */
    void clearAllCache();

    /**
     * Remove from outer Cache (if exists) - key in which stores inner Cache as value
     * @param cache - key in outer Cache which need to delete
     */
    void clearSomeCache(String cache);
}
