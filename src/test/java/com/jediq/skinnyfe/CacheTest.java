package com.jediq.skinnyfe;

import java.util.ArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeThat;
import org.junit.Test;

/**
 *
 */
public class CacheTest {
    
    @Test
    public void testSimple() {
        Cache<String, String> cache = new Cache<>();
        cache.item("item1", () -> "i1");
        cache.item("item2", () -> "i2");
        cache.item("item3", () -> "i3");
        cache.item("item4", () -> "i4");
        cache.item("item5", () -> "i5");

        assertThat(cache.itemSet().size(), is(5));
        assertThat(cache.itemSet(), containsInAnyOrder("i1", "i2", "i3", "i4", "i5"));

        cache.clear("item2");
        assertThat(cache.itemSet().size(), is(4));
        assertThat(cache.itemSet(), containsInAnyOrder("i1", "i3", "i4", "i5"));
    }

    @Test
    public void testClear() {
        Cache<String, Object> cache = new Cache<>(100000);
        cache.item("item1", Object::new);
        assertThat(cache.itemSet().size(), is(1));
        cache.clear();
        assertThat(cache.itemSet().size(), is(0));
    }

    @Test
    public void testItemSetWithEmptyElements() {
        Cache<String, Object> cache = new Cache<>(100000);
        cache.item("item1", Object::new);
        cache.item("item2", ()->null);
        assertThat(cache.itemSet().size(), is(1));
    }

    @Test
    public void testForceGarbageCollectionOfCache() {

        // Don't try to force out of memory for Travis-CI
        assumeThat(System.getProperty("os.name"), is("Mac OS X"));

        
        Cache<String, Object> cache = new Cache<>(100000);
        Object createdObjectString = cache.item("item1", Object::new).toString();
        
        Object cachedObjectString = cache.item("item1", Object::new).toString();

        forceOutOfMemory();

        Object newObjectString = cache.item("item1", Object::new).toString();

        assertThat(createdObjectString, is(cachedObjectString));
        assertThat(createdObjectString, is(not(newObjectString)));
    }
    
    private void forceOutOfMemory() {
        // Force an OoM
        try {
            final ArrayList<Object[]> allocations = new ArrayList<>();
            int size;
            while( (size = Math.min(Math.abs((int) Runtime.getRuntime().freeMemory()), Integer.MAX_VALUE))>0 )
                allocations.add( new Object[size] );
        } catch( OutOfMemoryError e ) {
            // great!
        }
        
    }
    
    
}
