package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Facade for working with DPUs.
 *
 * @author Jan Vojt
 */
public class DPUFacade {

	private static final Logger LOG = LoggerFactory.getLogger(DPUFacade.class);

	/**
	 * Entity manager for accessing database with persisted objects.
	 */
	@PersistenceContext
	private EntityManager em;

	/* ******************* Methods for DPURecord management *********************** */

	/**
	 * Creates DPURecord and its {@link TemplateConfiguration} without persisting it.
	 *
	 * @return
	 */
	public DPURecord createDpu() {
		DPURecord dpu = new DPURecord();
		dpu.setTemplateConfiguration(new TemplateConfiguration());
		return dpu;
	}

	/**
	 * Creates a new DPURecord with the same properties and configuration as in given
	 * {@link DPUInstance}. Note that newly created DPURecord is only returned, but
	 * not managed by database. To persist it, {@link #save(DPURecord)} must be called
	 * explicitly.
	 *
	 * @param instance
	 * @return new DPURecord
	 */
	public DPURecord createDpuFromInstance(DPUInstance instance) {

		DPURecord oDpu = instance.getDpu();
		DPURecord nDpu = new DPURecord(instance.getName(), oDpu.getType());

		// copy properties
		nDpu.setDescription(instance.getDescription());
		nDpu.setJarPath(oDpu.getJarPath());
		nDpu.setVisibility(VisibilityType.PRIVATE);

		// copy configuration
		TemplateConfiguration conf = new TemplateConfiguration();
		conf.setValues(instance.getInstanceConfig().getValues());
		nDpu.setTemplateConfiguration(conf);

		return nDpu;
	}

	/**
	 * Returns list of all DPUs currently persisted in database.
	 * @return DPURecord list
	 */
	public List<DPURecord> getAllDpus() {

		@SuppressWarnings("unchecked")
		List<DPURecord> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM DPURecord e").getResultList(),
				DPURecord.class
		);

		return resultList;
	}

	/**
	 * Find DPURecord in database by ID and return it.
	 * @param id
	 * @return
	 */
	public DPURecord getDpu(long id) {
		return em.find(DPURecord.class, id);
	}

	/**
	 * Saves any modifications made to the DPURecord into the database.
	 * @param dpu
	 */
	@Transactional
	public void save(DPURecord dpu) {
		if (dpu.getId() == null) {
			em.persist(dpu);
		} else {
			em.merge(dpu);
		}
	}

	/**
	 * Deletes DPURecord from the database.
	 * @param dpu
	 */
	@Transactional
	public void delete(DPURecord dpu) {
		// we might be trying to remove detached entity
		// lets fetch it again and then try to remove
		// TODO this is just a workaround -> resolve in future release!
		DPURecord d = dpu.getId() == null
				? dpu : getDpu(dpu.getId());
		if (d != null) {
			em.remove(d);
		} else {
			LOG.warn("DPURecord with ID " + dpu.getId() + " was not found and so cannot be deleted!");
		}
	}

	/* **************** Methods for DPURecord Instance management ***************** */

	/**
	 * Creates DPUInstance with configuration copied from template without
	 * persisting it.
	 *
	 * @return
	 */
	public DPUInstance createDPUInstance(DPURecord dpu) {
		DPUInstance dpuInstance = new DPUInstance(dpu);

		// convert template configuration to instance configuration
		InstanceConfiguration conf = new InstanceConfiguration();
		conf.setValues(dpu.getTemplateConfiguration().getValues());
		dpuInstance.setInstanceConfig(conf);

		return dpuInstance;
	}

	/**
	 * Returns list of all DPUInstances currently persisted in database.
	 *
	 * @return DPUInstance list
	 */
	public List<DPUInstance> getAllDPUInstances() {

		@SuppressWarnings("unchecked")
		List<DPUInstance> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM DPUInstance e").getResultList(),
				DPUInstance.class
		);

		return resultList;
	}

	/**
	 * Find DPUInstance in database by ID and return it.
	 *
	 * @param id
	 * @return
	 */
	public DPUInstance getDPUInstance(long id) {
		return em.find(DPUInstance.class, id);
	}

	/**
	 * Saves any modifications made to the DPUInstance into the database.
	 * @param dpu
	 */
	@Transactional
	public void save(DPUInstance dpu) {
		if (dpu.getId() == null) {
			em.persist(dpu);
		} else {
			em.merge(dpu);
		}
	}

	/**
	 * Deletes DPUInstance from the database.
	 * @param dpu
	 */
	@Transactional
	public void delete(DPUInstance dpu) {
		// we might be trying to remove detached entity
		// lets fetch it again and then try to remove
		// TODO this is just a workaround -> resolve in future release!
		DPUInstance d = dpu.getId() == null
				? dpu : getDPUInstance(dpu.getId());
		if (d != null) {
			em.remove(d);
		} else {
			LOG.warn("DPURecord instance with ID " + dpu.getId() + " was not found and so cannot be deleted!");
		}
	}

	/* **************** Methods for DPURecord Record management ***************** */

	/**
	 * Returns list of all DPURecords currently persisted in database.
	 *
	 * @return Record list
	 */
	public List<Record> getAllDPURecords() {

		@SuppressWarnings("unchecked")
		List<Record> resultList = Collections.checkedList(
			em.createQuery("SELECT e FROM Record e").getResultList(),
			Record.class
		);

		return resultList;
	}

	/**
	 * Fetches all DPURecords emitted by given DPUInstance.
	 *
	 * @param dpuInstance
	 * @return
	 */
	public List<Record> getAllDPURecords(DPUInstance dpuInstance) {

		@SuppressWarnings("unchecked")
		List<Record> resultList = Collections.checkedList(
			em.createQuery("SELECT r FROM Record r WHERE r.dpuInstance = :ins")
				.setParameter("ins", dpuInstance)
				.getResultList(),
			Record.class
		);

		return resultList;
	}

	/**
	 * Fetches all DPURecords emitted by given PipelineExecution.
	 *
	 * @param pipelineExec
	 * @return
	 */
	public List<Record> getAllDPURecords(PipelineExecution pipelineExec) {

		@SuppressWarnings("unchecked")
		List<Record> resultList = Collections.checkedList(
			em.createQuery("SELECT r FROM Record r WHERE r.execution = :ins")
				.setParameter("ins", pipelineExec)
				.getResultList(),
			Record.class
		);

		return resultList;
	}

	/**
	 * Find Record in database by ID and return it.
	 *
	 * @param id
	 * @return
	 */
	public Record getDPURecord(long id) {
		return em.find(Record.class, id);
	}

	/**
	 * Saves any modifications made to the Record into the database.
	 *
	 * @param record
	 */
	@Transactional
	public void save(Record record) {
		if (record.getId() == null) {
			em.persist(record);
		} else {
			em.merge(record);
		}
	}

	/**
	 * Deletes Record from the database.
	 *
	 * @param record
	 */
	@Transactional
	public void delete(Record record) {
		em.remove(record);
	}
}
