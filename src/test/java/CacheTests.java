import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CacheTests {
    static CustomCache customCache;

    @BeforeAll
    static void initCache() {
        customCache = new CustomCache(4);
        customCache.createCache("cache_one"); // test 4 delete this cache
        customCache.createCache("cache_two");
        customCache.createCache("cache_three"); // test 8 delete this cache

    }

    @Order(1)
    @Test
    public void checkThrowCachePutWithNullArguments() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> customCache.put(null, "test_key", "test_value"));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> customCache.put("cache_one", null, "test_value"));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> customCache.put("cache_one", "test_key", null));

    }

    @Order(2)
    @Test
    public void checkCacheNotExists() {
        Assertions.assertThrows(NullPointerException.class,
                () -> customCache.put("unknown", "salad", "pasta"));

    }

    @Order(3)
    @Test
    public void checkCachePut() {
        Assertions.assertTrue(customCache.put("cache_two", "test_key", "test_value"));
    }

    @Order(4)
    @Test
    public void checkCacheGetThrowsAfterRemove() {
        customCache.put("cache_one", "test_key", "test_value");

        //delete cache_one
        customCache.clearSomeCache("cache_one");

        //check not exists in main cache
        Assertions.assertThrows(NullPointerException.class,
                () -> customCache.get("cache_one", "test_key"));

        customCache.put("cache_three", "city", "Odessa");

        // check inner cache not exists
        Assertions.assertThrows(NullPointerException.class,
                () -> customCache.get("cache_three", "country"));
    }

    @Order(5)
    @Test
    public void checkCacheGetNotThrow() {
        customCache.put("cache_three", "city", "Kharkiv");

        Assertions.assertDoesNotThrow(() -> customCache.get("cache_three", "city"));

    }

    @Order(6)
    @Test
    public void checkCacheGetThrows() {

        Assertions.assertThrows(NullPointerException.class,
                () -> customCache.get("test_name_test", "test_key"));

        Assertions.assertThrows(NullPointerException.class,
                () -> customCache.get("test_name1", "test_key"));
    }

    @Order(7)
    @Test
    public void checkDeleteNotExistsCacheThrow() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> customCache.clearSomeCache("qwerty"));
    }

    @Order(8)
    @Test
    public void checkDeleteExistingCache() {
        Assertions.assertDoesNotThrow(() -> customCache.clearSomeCache("cache_three"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> customCache.clearSomeCache("cache_three"));
    }

    @AfterAll
    static void clearCache() {
        customCache.clearAllCache();
    }
}
