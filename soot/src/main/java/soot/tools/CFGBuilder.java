package soot.tools;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.*;
import soot.options.Options;
import soot.util.cfgcmd.CFGGraphType;
import soot.util.cfgcmd.CFGIntermediateRep;
import soot.util.cfgcmd.CFGToDotGraph;
import soot.util.dot.DotGraph;
import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.JimpleBody;
import soot.toolkits.graph.DirectedGraph;

/**
 * A primary class for generating dot graph file for a control flow graph
 *
 * @author Mingxi Ye
 */
public class CFGBuilder extends BodyTransformer{
  private static final Logger logger = LoggerFactory.getLogger(CFGBuilder.class);
  private static String projectDir = System.getProperty("user.dir");

  private CFGGraphType graphtype;
  private CFGIntermediateRep ir;
  private CFGToDotGraph drawer;
  private Map<String, String> methodsToPrint; // If the user specifies

  public static void main(String[] args)	{

    String mainclass = args[0];
    String classesDir = args[1];

    //set classpath
    String javapath = System.getProperty("java.class.path");
    String jredir = System.getProperty("java.home")+"/lib/rt.jar";
    String path = javapath+File.pathSeparator+jredir+File.pathSeparator+classesDir;
    Scene.v().setSootClassPath(path);

    CFGBuilder analysis = new CFGBuilder();
    PackManager.v().getPack("jtp").add(new Transform("jtp.CFGBuilder", analysis));
    Options.v().set_app(true);

    SootClass appclass = Scene.v().loadClassAndSupport(mainclass);
    Scene.v().setMainClass(appclass);
    Scene.v().loadNecessaryClasses();
    PackManager.v().runPacks();
  }

	@Override
	protected void internalTransform(Body b, String phaseName,
		  Map<String, String> options) {
    initialize(options);
    SootMethod meth = b.getMethod();

    if ((methodsToPrint == null) 
        || (meth.getDeclaringClass().getName() 
        == methodsToPrint.get(meth.getName()))) {
      Body body = ir.getBody((JimpleBody) b);
      print_cfg(body);
    }
	}

  private void initialize(Map<String, String> options) {
    if (drawer == null) {
      drawer = new CFGToDotGraph();
      drawer.setBriefLabels(true);
      drawer.setOnePage(false);
      drawer.setUnexceptionalControlFlowAttr("color", "black");
      drawer.setExceptionalControlFlowAttr("color", "red");
      drawer.setExceptionEdgeAttr("color", "lightgray");
      drawer.setShowExceptions(Options.v().show_exception_dests());
      ir = CFGIntermediateRep.getIR("jimple");
      graphtype = CFGGraphType.getGraphType("BriefUnitGraph");
    }
  }

  protected void print_cfg(Body body) {
    String filename = projectDir + "/sootOutput";
    if (!filename.isEmpty()) {
      filename = filename + File.separator;
    }
    String methodname = body.getMethod().getSubSignature();
    String classname = body.getMethod().getDeclaringClass().getName().replaceAll("\\$", "\\.");
    filename = filename + classname + " " + methodname.replace(File.separatorChar, '.') + DotGraph.DOT_EXTENSION;
    logger.debug("Generate dot file in " + filename);

    DirectedGraph<?> graph = graphtype.buildGraph(body);
    DotGraph canvas = graphtype.drawGraph(drawer, graph, body);
    canvas.plot(filename);
  }
}