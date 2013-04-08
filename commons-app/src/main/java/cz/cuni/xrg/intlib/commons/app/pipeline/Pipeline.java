package cz.cuni.xrg.intlib.commons.app.pipeline;

import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import javax.persistence.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import cz.cuni.xrg.intlib.commons.app.pipeline.event.ETLPipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.event.PipelineAbortedEvent;
import cz.cuni.xrg.intlib.commons.app.pipeline.event.PipelineCompletedEvent;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Edge;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.app.user.Resource;
import cz.cuni.xrg.intlib.commons.event.ProcessingContext;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.extractor.ExtractCompletedEvent;
import cz.cuni.xrg.intlib.commons.extractor.ExtractFailedEvent;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadCompletedEvent;
import cz.cuni.xrg.intlib.commons.loader.LoadFailedEvent;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.commons.transformer.TransformCompletedEvent;
import cz.cuni.xrg.intlib.commons.transformer.TransformFailedEvent;

/**
 * Represents a fixed workflow composed of one or several {@link Extract}s,
 * {@link Transform}s and {@link Load}s organized in acyclic graph. <br/>
 * Processing will always take place in the following order: <ol> <li>Execute
 * all {@link Extract}s</li> <ul> <li>If an Extractor throws an error publish
 * an {@link ExtractFailedEvent} - otherwise publish
 * an {@link ExtractCompletedEvent}</li> <li>If an Extractor requests
 * cancellation of the pipeline through {@link ProcessingContext#cancelPipeline}
 * publish a {@link PipelineAbortedEvent} and exit</li> </ul> <li>Execute all
 * {@link Transform}s in the order of their dependences given by graph</li>
 * <ul> <li>If a Transformer throws an error publish
 * an {@link TransformFailedEvent} - otherwise publish
 * an {@link TransformCompletedEvent}</li>
 * <li>If a Transformer requests cancellation of the pipeline through
 * {@link ProcessingContext#cancelPipeline} publish a
 * {@link PipelineAbortedEvent} and exit</li> </ul> <li>Execute all
 * {@link Load}s</li> <ul> <li>If a Loader throws an error publish
 * an {@link LoadFailedEvent} - otherwise publish an {@link LoadCompletedEvent}
 * </li>
 * <li>If a Loader requests cancellation of the pipeline through
 * {@link ProcessingContext#cancelPipeline} publish a
 * {@link PipelineAbortedEvent} and exit</li> </ul> <li>Publish a
 * {@link PipelineCompletedEvent}
 * </ol> <br/> A Spring {@link ApplicationEventPublisher} is required for
 * propagation of important events occurring throughout the pipeline.
 *
 * @see Extract
 * @see Transform
 * @see Load
 * @author Alex Kreiser (akreiser@gmail.com)
 * @author Jiri Tomes
 * @author Jan Vojt <jan@vojt.net>
 * @author Bogo
 */
@Entity
@Table(name="pipeline_model")
public class Pipeline implements ETLPipeline, Resource, ApplicationEventPublisherAware {

    /**
     * Unique ID for each pipeline
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    //private State state;

    /**
     * Human-readable pipeline name
     */
    private String name;

    /**
     * Human-readable pipeline description
     */
    private String description;

    @Transient
    private PipelineGraph graph;

    /**
     * Publisher instance responsible for publishing pipeline execution events.
     */
    @Transient
    protected ApplicationEventPublisher eventPublisher;

    private int dpuCounter = 0;

	private int connectionCounter = 0;

	private int CONNECTION_SEED = 100000;

    /**
     * Default constructor for JPA
     */
    public Pipeline() {

    }

    public Pipeline(String name, String description) {
        this.name = name;
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

 /*
    public State getState() {
        return state;
    }

    public void setState(State newState) {
        state = newState;
    }
*/

    @Override
    public PipelineGraph getGraph() {
        return graph;
    }

    @Override
    public void setGraph(PipelineGraph graph) {
        this.graph = graph;
    }

    public int getId() {
        return id;
    }

    /**
     * Returns the event publisher instance.
     *
     * @return
     */
    public ApplicationEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    /**
     * Setter for event publisher instance.
     * @param eventPublisher
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Runs the pipeline.
     */
    @Override
    public void run() {
    	//TODO implement
    }

    public int GetUniqueDpuInstanceId() {
		return ++dpuCounter;
	}

	public int GetUniquePipelineConnectionId() {
		return ++connectionCounter + CONNECTION_SEED;
	}

    public int AddDpu(DPU dpu) {
		DPUInstance dpuInstance = new DPUInstance(dpu);
		Node node = new Node(this, dpuInstance);
        this.getGraph().getNodes().add(node);
		return node.getId();
	}

	public boolean RemoveDpu(int dpuId) {
		Node node = getNodeById(dpuId);
		if(node != null) {
			return this.getGraph().getNodes().remove(node);
		}
		return false;
	}

	public int AddEdge(int fromId, int toId) {
		Node dpuFrom = getNodeById(fromId);
		Node dpuTo = getNodeById(toId);

		//TODO: Check if same connection doesn't exist already!
		//If it does - add to Set fails and returns false - TODO: 2.Find Id of equal existing connection

		Edge edge = new Edge(this, dpuFrom, dpuTo);
		boolean newElement = this.getGraph().getEdges().add(edge);
		if(!newElement) {
			return 0;
		}
		return edge.getId();
	}

	public boolean RemoveEdge(int pcId) {
		Edge pc = getEdgeById(pcId);
		if(pc != null) {
			return this.getGraph().getEdges().remove(pc);
		}
		return false;
	}

	private Edge getEdgeById(int id) {
		for(Edge el : this.getGraph().getEdges()) {
			if(el.getId() == id) {
				return el;
			}
		}
		return null;
	}

	private Node getNodeById(int id) {
		for(Node el : this.getGraph().getNodes()) {
			if(el.getId() == id) {
				return el;
			}
		}
		return null;
	}

	@Override
	public String getResourceId() {
		return Pipeline.class.toString();
	}

}
