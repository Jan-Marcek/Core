package cz.cuni.mff.xrg.odcs.frontend.doa.container;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataAccessRead;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataQueryBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link ContainerSource}. Has data caching abilities.
 *
 * @author Petyr
 * @param <T>
 */
public class CachedSource<T extends DataObject>
		implements ContainerSource<T>,
		ContainerSource.Filterable,
		ContainerSource.Sortable {

	private static final Logger LOG = LoggerFactory.getLogger(CachedSource.class);

	/**
	 * The maximum size of cache, it this size is exceeded then the cache
	 * is cleared.
	 */
	private static final int CACHE_MAX_SIZE = 200;
	
	/**
	 * Store size of data set in database.
	 */
	protected Integer size;

	/**
	 * Store cached data.
	 */
	protected final Map<Long, T> data = new HashMap<>();

	/**
	 * Store indexes for current data.
	 */
	protected final Map<Integer, Long> dataIndexes = new HashMap<>();

	/**
	 * Data source.
	 */
	protected final DataAccessRead<T> source;

	/**
	 * The query builder.
	 */
	protected final DataQueryBuilder<T> queryBuilder;

	/**
	 * Filters that can be set by {@link Filterable} interface.
	 */
	protected final List<Filter> filters = new LinkedList<>();

	/**
	 * Special set of core filters.
	 */
	protected final List<Filter> coreFilters;

	protected final ClassAccessor<T> classAccessor;

	/**
	 * Initialize the source with given data access. No core filters are used.
	 *
	 * @param access
	 * @param classAccessor
	 */
	public CachedSource(DataAccessRead<T> access, ClassAccessor<T> classAccessor) {
		this.source = access;
		this.queryBuilder = source.createQueryBuilder();
		this.coreFilters = null;
		this.classAccessor = classAccessor;
	}

	/**
	 * Initialize the source with given data access. The core filters are apply
	 * before every query, ant the list is used as reference. That means that
	 * changes in list changed the used filters in source.
	 *
	 * @param access
	 * @param classAccessor
	 * @param coreFilters
	 */
	public CachedSource(DataAccessRead<T> access, ClassAccessor<T> classAccessor,
			List<Filter> coreFilters) {
		this.source = access;
		this.queryBuilder = source.createQueryBuilder();
		this.coreFilters = coreFilters;
		this.classAccessor = classAccessor;
	}

	/**
	 * Invalidate data cache.
	 */
	public void invalidate() {
		size = null;
		data.clear();
		dataIndexes.clear();
	}

	/**
	 * Load data size from {@link #source} and return it. {@link #size}.
	 */
	int loadSize() {
		LOG.trace("loadSize()");
		applyFilters();
		int size = (int) source.executeSize(queryBuilder.getCountQuery());
		LOG.trace("loadSize() -> {}", size);
		return size;
	}

	/**
	 * Read data from {@link #source} and return it.
	 *
	 * @param index
	 */
	T loadByIndex(int index) {
		LOG.trace("loadByIndex({})", index);
		applyFilters();
		T item = source.execute(queryBuilder.getQuery().limit(index, 1));
		if (item == null) {
			return null;
		}
		LOG.trace("loadByIndex({}) -- done", index);
		return item;
	}

	/**
	 * Load data on given indexes and return them.
	 *
	 * @param startIndex
	 * @param numberOfItems
	 * @return
	 */
	List<T> loadByIndex(int startIndex, int numberOfItems) {
		LOG.trace("loadByIndex({}, {})", startIndex, numberOfItems);
		applyFilters();
		final List<T> items = source.executeList(
				queryBuilder.getQuery().limit(startIndex, numberOfItems));
		LOG.trace("loadByIndex({}, {}) --> done", startIndex, numberOfItems);
		return items;
	}

	/**
	 * Read data from {@link #source} with given ID.
	 *
	 * @param id
	 */
	protected void loadById(Long id) {
		LOG.trace("loadById({})", id);
		applyFilters();

		if (queryBuilder instanceof DataQueryBuilder.Filterable) {
			// ok continue
		} else {
			LOG.warn("Can not set filters on nonfilterable query builder."
					+ " We can not ask for given id.");
			return;
		}

		DataQueryBuilder.Filterable<T> filtrableBuilder
				= (DataQueryBuilder.Filterable<T>) queryBuilder;

		filtrableBuilder.addFilter(new Compare.Equal("id", id));
		T item = source.execute(queryBuilder.getQuery().limit(0, 1));
		if (item == null) {
			LOG.warn("Failed to load data with id {}", id);
			return;
		}
		// add to the data cache
		data.put(item.getId(), item);
	}

	/**
	 * Re-apply all filters to the {@link #queryBuilder}. If it's filterable.
	 */
	protected void applyFilters() {
		// TODO we can optimize and do not set those filter twice .. 

		if (queryBuilder instanceof DataQueryBuilder.Filterable) {
			// ok continue
		} else {
			LOG.warn("Can not set filters on nonfilterable query builder. The filters are ignored.");
			return;
		}

		DataQueryBuilder.Filterable<T> filtrableBuilder
				= (DataQueryBuilder.Filterable<T>) queryBuilder;

		// clear filters and build news
		filtrableBuilder.claerFilters();
		// add filters
		for (Filter filter : filters) {
			filtrableBuilder.addFilter(filter);
		}
		// add core filters if eqists
		if (coreFilters != null) {
			for (Filter filter : coreFilters) {
				filtrableBuilder.addFilter(filter);
			}
		}
	}

	/**
	 * Add items to the cache, if there are collisions between new and old data
	 * in ID then the old data are replaced.
	 * @param items 
	 * @param startIndex
	 */
	void add(List<T> items, int startIndex) {
		int index = startIndex;
		for(T item : items) {
			data.put(item.getId(), item);
			dataIndexes.put(index, item.getId());
			++index;
		}
	}
		
	@Override
	public int size() {
		if (size == null) {
			// reload size
			size = loadSize();
		}
		return size;
	}

	@Override
	public T getObject(Long id) {		
		if (data.containsKey(id)) {
			// the data are already cached
		} else {
			LOG.trace("getObject({}) - non, cached", id);
			loadById(id);
		}
		return data.get(id);
	}

	@Override
	public T getObjectByIndex(int index) {
		if (dataIndexes.containsKey(index)) {
			LOG.trace("getObjectByIndex({}) -> getObject", index);
			// we have the mapping index -> id
			return getObject(dataIndexes.get(index));
		} else {
			T item = loadByIndex(index);
			if (item != null) {
				// add to caches
				data.put(item.getId(), item);
				dataIndexes.put(index, item.getId());
			}
			// return new item .. can be null
			return item;
		}
	}

	@Override
	public boolean containsId(Long id) {
		LOG.trace("containsId({})", id);
		if (data.containsKey(id)) {
			return true;
		}
		LOG.debug("containsId called on non-cached data .. this generates the query into database");		
		// try to load that object
		loadById(id);
		// ask again		
		return data.containsKey(id);
	}

	@Override
	public List<?> getItemIds(int startIndex, int numberOfItems) {
		
boolean onlyCached = true;
		List<Long> result = new ArrayList<>(numberOfItems);
		// first try to load data from cache
		int endIndex = startIndex + numberOfItems;
		for (int index = startIndex; index < endIndex; ++index) {
			if (dataIndexes.containsKey(index)) {
				// we have mapping, so use it to return the index
				result.add(dataIndexes.get(index));
			} else {
onlyCached = false;				
				// some data are mising, we have to load them
				final int toLoad = numberOfItems - (index - startIndex);
				List<T> newData = loadByIndex(index, toLoad);
				// gather IDs and add data to caches
				List<Long> newIDs = new ArrayList<>(numberOfItems);
				for (T item : newData) {
					data.put(item.getId(), item);
					dataIndexes.put(index++, item.getId());
					newIDs.add(item.getId());
				}
				// add new IDs to the result list
				result.addAll(newIDs);
				break;
			}
		}
		
		if (result.contains(null)) {
			LOG.error("getItemIds return null!! only cached = {}", onlyCached);
		}
		
		if (data.size() > CACHE_MAX_SIZE) {
			LOG.debug("Cache cleared");
			// we preserve indexes as we may need to use them 
			// for some direct access
			
			// what we remove are the data .. if they are not in result one
			List<Long> ids = new ArrayList<>(data.keySet());
			ids.removeAll(result);
			// now in ids, are ids to remove ..
			for (Long item : ids) {
				data.remove(item);
			}
			
			// this may result in additional query for the data we drop
			// maybe we can try to be smarter here ...
		}
				
		return result;
	}

	@Override
	public int indexOfId(Long itemId) {
		for (Integer index : dataIndexes.keySet()) {
			if (dataIndexes.get(index) == itemId) {
				return index;
			}
		}

		throw new RuntimeException("Can not determine the index of non cached data.");
	}

	@Override
	public ClassAccessor<T> getClassAccessor() {
		return classAccessor;
	}

	@Override
	public void addFilter(Container.Filter filter) {
		filters.add(filter);
		// and invalidate data
		invalidate();
	}

	@Override
	public void removeFilter(Container.Filter filter) {
		filters.remove(filter);
		// and invalidate data
		invalidate();

	}

	@Override
	public void removeAllFilters() {
		filters.clear();
		// and invalidate data
		invalidate();
	}

	@Override
	public Collection<Container.Filter> getFilters() {
		return filters;
	}

	@Override
	public void sort(Object[] propertyId, boolean[] ascending) {
		if (queryBuilder instanceof DataQueryBuilder.Sortable) {
			// ok continue
		} else {
			LOG.warn("Call of sort(Objet[], boolean[]) on non sortable-source ignored.");
			return;
		}

		final DataQueryBuilder.Sortable<T> sortableBuilder
				= (DataQueryBuilder.Sortable<T>) queryBuilder;

		switch (propertyId.length) {
			case 0: // remove sort
				sortableBuilder.sort(null, false);
				invalidate();
				break;
			default:
				LOG.warn("sort(Objet[], boolean[]) called with multiple targets."
						+ " Only first used others are ignored.");
			case 1: // sort, but we need expresion for sorting first
				sortableBuilder.sort((String) propertyId[0], ascending[0]);
				invalidate();
				break;
		}

	}

}
