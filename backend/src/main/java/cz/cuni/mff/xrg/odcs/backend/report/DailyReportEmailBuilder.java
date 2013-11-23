package cz.cuni.mff.xrg.odcs.backend.report;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Create email with daily report.
 * 
 * @author Petyr
 */
@Component
public class DailyReportEmailBuilder {
	
	public String build(List<PipelineExecution> executions) {
		StringBuilder body = new StringBuilder();
		
		body.append("<table border=2 cellpadding=2 >");
		body.append("<tr bgcolor=\"#C0C0C0\">");
		body.append("<th>pipeline</th><th>start</th><th>end</th><th>result</th>");
		body.append("</tr>");
		
		for (PipelineExecution exec : executions) {
			body.append("<tr>");
			// pipeline
			body.append("<td>");
			body.append(exec.getPipeline().getName());
			body.append("</td>");
			// start
			body.append("<td>");
			body.append(exec.getStart().toString());
			body.append("</td>");
			// end
			body.append("<td>");
			body.append(exec.getEnd().toString());
			body.append("</td>");
			// result
			body.append("<td>");
			switch(exec.getStatus()) {
				case CANCELLED:
					body.append("cancelled");
					break;
				case CANCELLING:
					body.append("canceling");
					break;
				case FAILED:
					body.append("failed");
					break;
				case FINISHED_SUCCESS:
					body.append("finished");
					break;
				case FINISHED_WARNING:
					body.append("finished with warning");
					break;
				case QUEUED:
					body.append("queued");
					break;
				case RUNNING:
					body.append("running");
					break;
			}
			body.append("</td>");
			
			// end line
			body.append("</tr>");
		}
		
		body.append("</table>");
		
		return body.toString();
	}
	
}
