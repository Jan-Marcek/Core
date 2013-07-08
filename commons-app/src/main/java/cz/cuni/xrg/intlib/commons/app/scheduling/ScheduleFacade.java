package cz.cuni.xrg.intlib.commons.app.scheduling;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;

/**
 * Facade providing actions with plan.
 *
 */
public class ScheduleFacade {

	private static final Logger logger = LoggerFactory.getLogger(ScheduleFacade.class);
	
	/**
	 * Entity manager for accessing database with persisted objects
	 */
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Returns list of all Plans currently persisted in database.
	 * @return Plans list
	 */
	public List<Schedule> getAllSchedules() {
		
		@SuppressWarnings("unchecked")
		List<Schedule> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM Schedule e").getResultList(),
				Schedule.class
		);

		return resultList;
	}
	
	/**
	 * Fetches all {@link Schedule}s that should be activated after given
	 * pipeline execution.
	 *
	 * @param pipeline
	 * @return
	 */
	public List<Schedule> getFollowers(Pipeline pipeline) {
		@SuppressWarnings("unchecked")
		List<Schedule> resultList = Collections.checkedList(
			em.createQuery(
				"SELECT s FROM Schedule s"
					+ "	JOIN s.afterPipelines p"
					+ " WHERE p.id = :pipeline AND s.type = :type"
				).setParameter("pipeline", pipeline.getId())
				.setParameter("type", ScheduleType.AFTER_PIPELINE)
				.getResultList(),
				Schedule.class
		);
		return resultList;
	}
	
	/**
	 * Fetches all {@link Schedule}s which are activated in
	 * certain time.
	 *
	 * @param pipeline
	 * @return
	 */	
	public List<Schedule> getAllTimeBased() {
		@SuppressWarnings("unchecked")
		List<Schedule> resultList = Collections.checkedList(
			em.createQuery(
				"SELECT s FROM Schedule s"
					+ "	JOIN s.afterPipelines p"
					+ " s.type = :type"
				).setParameter("type", ScheduleType.PERIODICALLY)
				.getResultList(),
				Schedule.class
		);
		return resultList;		
	}
	
	/**
	 * Find Schedule in database by ID and return it.
	 * @param id
	 * @return
	 */
	public Schedule getSchedule(long id) {
		return em.find(Schedule.class, id);
	}	
	
	/**
	 * Saves any modifications made to the Schedule into the database.
	 * @param schedule
	 */
	@Transactional
	public void save(Schedule schedule) {
		if (schedule.getId() == null) {
			em.persist(schedule);
		} else {
			em.merge(schedule);
		}
	}

	/**
	 * Deletes Schedule from the database.
	 * @param schedule
	 */
	@Transactional
	public void delete(Schedule schedule) {
		// we might be trying to remove detached entity
		// lets fetch it again and then try to remove
		// TODO Honza: this is just a workaround -> resolve in future release!
		Schedule s = schedule.getId() == null ? schedule : getSchedule(schedule.getId());
		if (s != null) {
			em.remove(s);
		} else {
			logger.warn("Schedule with ID " + schedule.getId() + " was not found and so cannot be deleted!");
		}
	}	
	
}
