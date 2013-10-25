package cz.cuni.mff.xrg.odcs.backend.execution.dpu.impl;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.backend.context.Context;
import cz.cuni.mff.xrg.odcs.backend.data.DataUnitFactory;
import cz.cuni.mff.xrg.odcs.backend.dpu.event.DPUEvent;
import cz.cuni.mff.xrg.odcs.backend.execution.dpu.PreExecutor;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.annotation.AnnotationContainer;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.annotation.AnnotationGetter;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;

/**
 * Examine the given DPU instance for {@link InputDataUnit} annotations. If
 * there is {@link InputDataUnit} annotation on field then create or assign
 * suitable DataUnit. If there is no {@link DataUnit} suitable then publish
 * event and return false.
 * 
 * Executed for every state. If the DPU has been already finished 
 * then we will still need {@link DataUnit}s at the end of the execution.
 * 
 * @author Petyr
 * 
 */
@Component
public class AnnotationsInput implements PreExecutor {

	public static final int ORDER = AnnotationsOutput.ORDER + 1000;
	
	private static final Logger LOG = LoggerFactory
			.getLogger(AnnotationsInput.class);
	
	/**
	 * DataUnit factory used to create new {@link DataUnit}s.
	 */
	@Autowired
	private DataUnitFactory dataUnitFactory;

	/**
	 * Event publisher used to publish error event.
	 */
	@Autowired
	private ApplicationEventPublisher eventPublish;

	@Override
	public int getOrder() {
		return ORDER;
	}

	@Override
	public boolean preAction(Node node,
			Map<Node, Context> contexts,
			Object dpuInstance,
			PipelineExecution execution,
			ProcessingUnitInfo unitInfo) {
		// get current context and DPUInstanceRecord
		Context context = contexts.get(node);

		// InputDataUnit annotation
		List<AnnotationContainer<InputDataUnit>> inputAnnotations = AnnotationGetter
				.getAnnotations(dpuInstance, InputDataUnit.class);
		for (AnnotationContainer<InputDataUnit> item : inputAnnotations) {
			if (annotationInput(item, dpuInstance, context)) {
				// ok
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Set value to given field for given instance. In case of error publish
	 * event and return false.
	 * 
	 * @param field Field to set.
	 * @param instance Instance.
	 * @param value Value to set.
	 * @param context Used to publish exception.
	 * @return True if the field has been set.
	 */
	protected boolean setDataUnit(Field field,
			Object instance,
			Object value,
			Context context) {
		try {
			field.set(instance, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// create message
			final String message = "Failed to set value for '"
					+ field.getName() + "' exception: " + e.getMessage();
			eventPublish.publishEvent(DPUEvent.createPreExecutorFailed(context,
					this, message));
			return false;
		}
		return true;
	}	
	
	/**
	 * Filter given list of {@link ManagableDataUnit}s by using {@link Class}.
	 * 
	 * @param candidates
	 * @param type
	 * @return List with {@link ManagableDataUnit} can be empty, can not be
	 *         null.
	 */
	protected LinkedList<ManagableDataUnit> filter(List<ManagableDataUnit> candidates,
			Class<?> type) {
		LinkedList<ManagableDataUnit> result = new LinkedList<ManagableDataUnit>();
		for (ManagableDataUnit item : candidates) {
			if (type.isInstance(item)) {
				result.add(item);
			}
		}
		return result;
	}

	/**
	 * Filter given list of {@link ManagableDataUnit}s according to their URI.
	 * Is case insensitive.
	 * 
	 * @param candidates
	 * @param type
	 * @return List with {@link ManagableDataUnit} can be empty, can not be
	 *         null.
	 */
	protected LinkedList<ManagableDataUnit> filter(List<ManagableDataUnit> candidates,
			String name) {
		LinkedList<ManagableDataUnit> result = new LinkedList<ManagableDataUnit>();
		for (ManagableDataUnit item : candidates) {
			if (item.getDataUnitName().compareToIgnoreCase(name) == 0) {
				result.add(item);
			}
		}
		return result;
	}
	
	/**
	 * Execute the input annotation ie. get input {@link DataUnit} from
	 * {@link Context} and assign it to annotated field. If annotationContainer
	 * is null then instantly return true. If error appear then publish event
	 * and return false.
	 * 
	 * @param annotationContainer Annotation container.
	 * @param dpuInstance
	 * @param context
	 * @return False in case of error.
	 */
	protected boolean annotationInput(AnnotationContainer<InputDataUnit> annotationContainer,
			Object dpuInstance,
			Context context) {
		if (annotationContainer == null) {
			return true;
		}
		final Field field = annotationContainer.getField();
		final InputDataUnit annotation = annotationContainer.getAnnotation();

		LinkedList<ManagableDataUnit> typeMatch = filter(context.getInputs(),
				field.getType());
		if (typeMatch.isEmpty()) {
			// check if not optional
			if (annotation.optional()) {
				return true;
			}
			final String message = "No input for field: " + field.getName()
					+ " All inputs have different type.";
			eventPublish.publishEvent(DPUEvent.createPreExecutorFailed(context,
					this, message));
			return false;
		}
		// now we filter by name
		LinkedList<ManagableDataUnit> nameMatch = filter(typeMatch,
				annotation.name());
		if (nameMatch.isEmpty()) {
			if (annotation.relaxed()) {
				LOG.debug("Assign DataUnit names: {} for field: {}",
						annotation.name(), field.getName());
				// just use first with good type
				return setDataUnit(field, dpuInstance, typeMatch.getFirst(),
						context);
			} else {
				// check if not optional
				if (annotation.optional()) {
					return true;
				}
				// error
				final String message = "Can't find DataUnit with required name for field:"
						+ field.getName();
				eventPublish.publishEvent(DPUEvent.createPreExecutorFailed(
						context, this, message));
				return false;
			}
		} else {
			LOG.debug("Assign DataUnit URI: {} for field: {}", nameMatch
					.getFirst().getDataUnitName(), field.getName());
			// use first with required name
			return setDataUnit(field, dpuInstance, nameMatch.getFirst(),
					context);
		}
	}
	
}
