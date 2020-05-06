import java.util.LinkedList;

/**
 * A double linked list based implementation of a cache.
 *
 * @author Jeff Allen
 *
 * @param <T> Generic type of the objects in the cache
 */

public class Cache<T> {

	private int count;
	private LinkedList<T> cache;

	/**
	 * Default constructor - Makes a new single level cache
	 * 
	 * @param size - size of the cache
	 */
	public Cache(int size) {
		this.count = size;
		cache = new LinkedList<T>();
	}

	/**
	 * Gets an object from the cache
	 * 
	 * @param object - object to find
	 * @return temp - reference to the object found
	 */
	public T getObject(T object) {
		T temp = cache.get(cache.indexOf(object));
		addObject(temp);
		return temp;
	}
	
	public T getAtIndex(int index){
		T retval = cache.get(index);
		return retval;
	}

	

	/**
	 * Adds an object to the cache
	 * 
	 * @param object - object to be added
	 */
	public void addObject(T object) {
		if (cache.size() < count) { // Check if there is room in the cache to add
			if (cache.contains(object)) { // See if there is a duplicate object in the cache
				cache.remove(object); // Remove the duplicate object before adding
			}
			cache.addFirst(object); // add object to front of cache
		} else {
			if (!cache.contains(object)) { // if object doesn't exist in cache
				cache.removeLast();
				cache.addFirst(object);
			} else {
				cache.remove(object);
				cache.addFirst(object);
			}
		}
	}

	/**
	 * Removes an object from the cache
	 * 
	 * @param object - object to remove
	 * @return temp - reference to the removed object
	 */
	public T removeObject(T object) {
		T temp = null;
		if (cache.remove(object)) {
			temp = object;
		}
		return temp;
	}

	/**
	 * Removes all objects from the cache
	 */
	public void clearCache() {
		cache.clear();
	}

	/**
	 * Gets the current size of the cache
	 * 
	 * @return cash.size - size of the cache
	 */
	public int getSize() {
		return cache.size();
	}

	/**
	 * Finds an object in the cache
	 * 
	 * @param object - object to search for
	 * @return cache.contains(object) - True or False
	 */
	public boolean contains(T object) {
		return cache.contains(object);
	}
	
	public String toString() {
		return cache.toString();
	}
}


