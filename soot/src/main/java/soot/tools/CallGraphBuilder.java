package soot.tools;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashSet;

import soot.*;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;
import soot.util.dot.DotGraph;

/**
 * A primary class for generating dot graph file for a call graph
 *
 * @author Mingxi Ye
 */
public class CallGraphBuilder extends SceneTransformer {

	static LinkedList<String> excludeList;
	private static String projectDir = System.getProperty("user.dir");

	public static void main(String[] args)	{

		String mainclass = args[0];
		String classesDir = args[1];

  		//set classpath
        String javapath = System.getProperty("java.class.path");
        String jredir = System.getProperty("java.home")+"/lib/rt.jar";
        String path = javapath+File.pathSeparator+jredir+File.pathSeparator+classesDir;
        Scene.v().setSootClassPath(path);

        //add an intra-procedural analysis phase to Soot
	    CallGraphBuilder analysis = new CallGraphBuilder();
	    PackManager.v().getPack("wjtp").add(new Transform("wjtp.CallGraphBuilder", analysis));
	    excludeJDKLibrary();

	    //whole program analysis
	    Options.v().set_whole_program(true);

        //load and set main class
	    Options.v().set_app(true);
	    SootClass appclass = Scene.v().loadClassAndSupport(mainclass);
		Scene.v().setMainClassFromOptions();
	    Scene.v().loadNecessaryClasses();

	    //enable call graph
	    enableCHACallGraph();
	    enableSparkCallGraph();

        //start working
	    PackManager.v().runPacks();
	}

	private static void excludeJDKLibrary()
	{
	    Options.v().set_exclude(excludeList());
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_allow_phantom_refs(true);
	}

	//Enable Spark
	private static void enableSparkCallGraph() {
	      HashMap<String,String> opt = new HashMap<String,String>();
	      opt.put("propagator","worklist");
	      opt.put("simple-edges-bidirectional","false");
	      opt.put("on-fly-cg","true");
	      opt.put("set-impl","double");
	      opt.put("double-set-old","hybrid");
	      opt.put("double-set-new","hybrid");
	      opt.put("pre_jimplify", "true");
	      SparkTransformer.v().transform("",opt);
	      PhaseOptions.v().setPhaseOption("cg.spark", "enabled:true");
	}

	//Enable CHA
	private static void enableCHACallGraph() {
		CHATransformer.v().transform();
	}

	private static LinkedList<String> excludeList()
	{
		if(excludeList==null)
		{
			excludeList = new LinkedList<String> ();

			excludeList.add("java.");
		    excludeList.add("javax.");
		    excludeList.add("sun.");
		    excludeList.add("sunw.");
		    excludeList.add("com.sun.");
		    excludeList.add("com.ibm.");
		    excludeList.add("com.apple.");
		    excludeList.add("apple.awt.");
		}
		return excludeList;
	}

	@Override
	protected void internalTransform(String phaseName, Map options) {
		String filename = projectDir + "/sootOutput/CallGraph.dot";
		DotGraph canvas = new DotGraph("CallGraph");
		canvas.setGraphLabel("CallGraph");

		CallGraph callGraph = Scene.v().getCallGraph();

		HashSet<String> nodes = new HashSet<String>();
		for(SootClass sc : Scene.v().getApplicationClasses()){
			if(Scene.v().isExcluded(sc)){
				continue;
			}
			for(SootMethod sm : sc.getMethods()){
				nodes.add(sm.getSignature());
				Iterator<MethodOrMethodContext> targets = new Targets(
					callGraph.edgesOutOf(sm));
				while (targets.hasNext()) {
					SootMethod tgn = (SootMethod) targets.next();
					if(Scene.v().isExcluded(tgn.getDeclaringClass())){
						continue;
					}
					nodes.add(tgn.getSignature());

				}
			}
		}
		for(String node : nodes){
			canvas.drawNode(node);
		}

		for(SootClass sc : Scene.v().getApplicationClasses()){
			for(SootMethod sm : sc.getMethods()){
				Iterator<MethodOrMethodContext> targets = new Targets(
					callGraph.edgesOutOf(sm));
				while (targets.hasNext()) {
					SootMethod tgn = (SootMethod) targets.next();
					if(Scene.v().isExcluded(tgn.getDeclaringClass())){
						continue;
					}
					canvas.drawEdge(sm.getSignature(), tgn.getSignature());
				}
			}
		}
		
		canvas.plot(filename);
	}
}