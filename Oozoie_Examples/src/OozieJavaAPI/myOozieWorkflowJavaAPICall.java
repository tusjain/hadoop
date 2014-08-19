package OozieJavaAPI;

import java.util.Properties;

import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.WorkflowJob;

public class myOozieWorkflowJavaAPICall {

	public static void main(String[] args) {
		OozieClient wc = new OozieClient("http://cdh-dev01:11000/oozie");

		Properties conf = wc.createConfiguration();

		conf.setProperty("nameNode", "hdfs://cdh-nn01.chuntikhadoop.com:8020");
		conf.setProperty("jobTracker", "cdh-jt01:8021");
		conf.setProperty("queueName", "default");
		conf.setProperty("oozie.libpath", "${nameNode}/user/oozie/share/lib");
		conf.setProperty("oozie.use.system.libpath", "true");
		conf.setProperty("oozie.wf.rerun.failnodes", "true");

		conf.setProperty("oozieProjectRoot",
				"${nameNode}/user/akhanolk/oozieProject");
		conf.setProperty("appPath",
				"${oozieProjectRoot}/workflowJavaMainAction");
		conf.setProperty(OozieClient.APP_PATH, "${appPath}");

		conf.setProperty("inputDir", "${oozieProjectRoot}/data/*/*/*/*/*");
		conf.setProperty("outputDir", "${appPath}/output");

		try {
			String jobId = wc.run(conf);
			System.out.println("Workflow job, " + jobId + " submitted");

			while (wc.getJobInfo(jobId).getStatus() == WorkflowJob.Status.RUNNING) {
				System.out.println("Workflow job running ...");
				Thread.sleep(10 * 1000);
			}
			System.out.println("Workflow job completed ...");
			System.out.println(wc.getJobInfo(jobId));
		} catch (Exception r) {
			System.out.println("Errors " + r.getLocalizedMessage());
		}
	}
}