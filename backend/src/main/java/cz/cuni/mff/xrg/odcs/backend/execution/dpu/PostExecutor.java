package cz.cuni.mff.xrg.odcs.backend.execution.dpu;

import java.util.Map;

import org.springframework.core.Ordered;

import cz.cuni.mff.xrg.odcs.backend.context.Context;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;

/**
 * Provide action that should be perform pipeline execution.
 * The {@link PostExecutor}s are used as a singletons, so they
 * must be able to run concurrently on multiple instances.
 * 
 * The PreExecutors are executed in order that is defined by
 * {@link Ordered}
 * 
 * @author Petyr
 *
 */
public interface PostExecutor extends Ordered {
	
	/**
	 * Should perform post-execution actions. If return false then the execution
	 * is cancelled and marked failed.
	 * 
	 * @param dpu Respective DPU.
	 * @param execution Respective execution.
	 * @param context DPU's context.
	 * @param unitInfo DPU's ProcessingUnitInfo.
	 * @return
	 */
	public boolean postAction(Node node,
			Map<Node, Context> contexts,
			Object dpuInstance,
			PipelineExecution execution,
			ProcessingUnitInfo unitInfo);

}
