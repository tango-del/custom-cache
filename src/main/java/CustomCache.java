import interfaces.CacheInterface;
import loggers.CustomLogger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;

import java.util.concurrent.TimeUnit;

/**
 * Class make implementation of interface CacheInterface
 * to interact with Cache in this program
 */
public class CustomCache implements CacheInterface {
    private static CacheManager cacheManager;
    private static Cache<String, Cache> mainCache;
    private static Integer cacheLifeCycle;
    private static final String NAME_MAIN_CACHE = "main-cache";

    /**
     * Default Constructor which set cache lifecycle.
     * Build and init CacheManager for create all Caches
     * Create main outer Cache in which will store all Caches
     *
     * @param cacheLifeCycle - number of cache lifecycle
     */
    public CustomCache(final Integer cacheLifeCycle) {
        this.cacheLifeCycle = cacheLifeCycle;
        buildCacheManager();
        buildAndConfigureMainCache();
    }

    /**
     * Method check that key exists in inner cache with name 'cache'
     * @param cache - cache name
     * @param key - key of inner cache
     * @return - true if this key exists in cache with this name, false if not exists
     */
    @Override
    public boolean checkKeyExistsInInnerCache(final String cache, final String key) {
        return mainCache.get(cache).containsKey(key);
    }

    /**
     * Method check outer @mainCache contains inside inner cache with this name
     * @param cache - inner cache name
     * @return - result if this cache exists in outer @mainCache
     */
    @Override
    public boolean checkIfInnerCacheExistsInMainCache(final String cache) {
        return mainCache.containsKey(cache);
    }

    /**
     * Method create inner Cache.
     * At first check that in outer @mainCache already exists key - @cache
     *
     * Create cache with parameters: key type - String and value type - Object
     * Take value @cacheLifeCycle for set lifecycle
     *
     * At the end put created cache in outer @mainCache as value, his key - @cache
     *
     * @param cache - key of inner Cache which put in outer Cache to get inner Cache
     * @throws IllegalArgumentException - cause if key with name @cache already exists in outer @mainCache
     */
    @Override
    public void createCache(final String cache) {

        if (checkIfInnerCacheExistsInMainCache(cache)) {
            CustomLogger.logError(String.format("Cache with name '%s' already exists", cache));
            throw new IllegalArgumentException(String.format("Cache with name '%s' already exists%n", cache));
        }

        Cache<String, Object> innerCache = cacheManager
                .createCache(cache,
                        CacheConfigurationBuilder
                                .newCacheConfigurationBuilder(String.class, Object.class,
                                        ResourcePoolsBuilder.heap(10))
                                .withExpiry(Expirations.timeToLiveExpiration(Duration.of(cacheLifeCycle, TimeUnit.MINUTES))));
        mainCache.put(cache, innerCache);

        CustomLogger.logDebug(String.format("Create Cache with name '%s' and put in '%s'", cache, NAME_MAIN_CACHE));
    }

    /**
     * Method put in inner cache key with his value.
     *
     * First check that all arguments not null.
     *
     * Then get link inner cache from outer @mainCache with key @cache
     * If there is no such key then it return null (and throw NullPointerException)
     *
     * If this inner cache exists then put him @key with his @value
     *
     * At last check that inner cache contain this key inside and return result
     *
     * @param cache - key of inner Cache which store in outer @mainCache
     * @param key - put to inner cache
     * @param value - value of key in inner cache
     * @return - if this method not throw exceptions all time return true
     * @throws IllegalArgumentException - if one of arguments is null
     * @throws NullPointerException - if there is no key with name @cache in outer @mainCache
     */
    @Override
    public boolean put(final String cache, final String key, final Object value) {
        if (cache == null || key == null || value == null) {
            CustomLogger.logError("One of arguments in method 'put' is null");
            throw new IllegalArgumentException("One of arguments in method 'put' is null");
        }

        Cache tempCache = getInnerCache(cache);

        if (tempCache == null) {
            CustomLogger.logError(String.format("Cache with name '%s' not found", cache));
            throw new NullPointerException(String.format("Cache with name '%s' not found%n", cache));
        }

        CustomLogger.logDebug(String.format("Put key: '%s' with value: '%s' in inner Cache with name: '%s'", key, value, cache));
        tempCache.put(key, value);

        return tempCache.containsKey(key);
    }

    /**
     * Method return value of inner cache.
     *
     * First checks that key @cache exists in outer @mainCache
     * Then check that @key exists in inner cache
     *
     * If no exception than return value
     *
     * @param cache - key of inner cache
     * @param key - key of value in inner cache
     * @return - value with type Object which store in inner cache with key @key
     * @throws NullPointerException - if outer @mainCache not contain key - @cache or if inner cache not contain @key
     */
    @Override
    public Object get(final String cache, final String key) {
        if (!checkIfInnerCacheExistsInMainCache(cache)) {
            CustomLogger.logError(String.format("Cache not found with name : '%s'", cache));
            throw new NullPointerException(String.format("Cache not found with name : '%s'%n", cache));
        }
        if (!checkKeyExistsInInnerCache(cache, key)) {
            CustomLogger.logError(String.format("Cache with name '%s' don't have key : '%s'", cache, key));
            throw new NullPointerException(String.format("Cache with name '%s' don't have key : '%s'%n", cache, key));
        }

        CustomLogger.logDebug(String.format("Get value from inner Cache with name : '%s'", key));
        return mainCache.get(cache).get(key);
    }

    /**
     * CacheManager remove all inner caches which stored inside outer @mainCache
     * So user can create later caches with same names.
     *
     * Then CacheManager remove @mainCache which keep inside all user created inner caches and create new outer @mainCache
     */
    @Override
    public void clearAllCache() {
        mainCache.forEach(f -> cacheManager.removeCache(f.getKey()));
        CustomLogger.logDebug(String.format("CacheManager remove all inner Caches in '%s'", NAME_MAIN_CACHE));
        cacheManager.removeCache(NAME_MAIN_CACHE);
        CustomLogger.logDebug(String.format("Remove '%s'", NAME_MAIN_CACHE));
        buildAndConfigureMainCache();
    }

    /**
     * Method delete specific inner cache
     *
     * First check that in outer @mainCache exists key - @cache
     *
     * Then remove this key with his value as inner cache from outer @mainCache
     * Then remove specific inner cache from CacheManager
     *
     * @param cache - key in outer Cache which need to delete
     * @throws IllegalArgumentException - if outer @mainCache not contain key - @cache
     */
    @Override
    public void clearSomeCache(final String cache) {
        if (!checkIfInnerCacheExistsInMainCache(cache)) {
            CustomLogger.logError(String.format("Cache with name '%s' not exists", cache));
            throw new IllegalArgumentException(String.format("Cache with name '%s' not exists%n", cache));
        }
        mainCache.remove(cache);
        cacheManager.removeCache(cache);
        CustomLogger.logDebug(String.format("Remove Cache with name : %s", cache));
    }

    /**
     * Method return link to inner cache that stores in cacheManager.
     *
     * @param cache - name of cache which need to get
     * @return - Link to inner cache. If no such name @cache in cacheManager then return null
     */
    private static Cache<String, Object> getInnerCache(final String cache) {
        return cacheManager.getCache(cache, String.class, Object.class);
    }

    /**
     * Build and Init CacheManager
     */
    private static void buildCacheManager() {
        // create cache manager
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
        cacheManager.init();
        CustomLogger.logDebug("CacheManager initialized");
    }

    /**
     * Create outer @mainCache with configuration
     * name: @NAME_MAIN_CACHE
     * lifecycle: @cacheLifeCycle
     * key: String.class
     * value: Cache.class
     */
    private static void buildAndConfigureMainCache() {
        // create main cache <String, Cache>
        mainCache = cacheManager.createCache(NAME_MAIN_CACHE, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, Cache.class, ResourcePoolsBuilder.heap(10))
                .withExpiry(Expirations.timeToLiveExpiration(Duration.of(cacheLifeCycle, TimeUnit.MINUTES))));
        CustomLogger.logDebug(String.format("Cache '%s' created in EhcacheManager", NAME_MAIN_CACHE));
    }
}
