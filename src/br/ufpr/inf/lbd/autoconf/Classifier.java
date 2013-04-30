/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.ufpr.inf.lbd.autoconf;

import org.eclipse.emf.common.util.URI;
import org.eclipse.gmt.am3.core.modelhandler.ModelHandler;
import org.eclipse.gmt.am3.core.modeling.Model;
import org.eclipse.gmt.am3.core.modeling.ModelElement;
import org.eclipse.gmt.am3.core.modeling.ReferenceModel;
import org.eclipse.gmt.am3.core.projectors.Injector;
import org.eclipse.gmt.am3.core.projectors.ProjectorActualParameter;
import org.eclipse.gmt.am3.modelhandler.emf.EMFExtractor;
import org.eclipse.gmt.am3.modelhandler.emf.EMFInjector;
import org.eclipse.gmt.am3.modelhandler.emf.EMFModelHandler;
import org.eclipse.gmt.am3.modelhandler.emf.EMFReferenceModel;
import org.eclipse.gmt.modisco.discoverer.javast.ASTDiscoverer;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Edson Ramiro
 */
public class Classifier {

  public Classifier () {
  }

  public void setFeatures(TuningKnobs tKnobs) {

    String javaFile = "src/org/eclipse/gmt/modisco/discoverer/javast/ASTDiscoverer.java";
    ModelHandler emh = new EMFModelHandler();
    Injector emfinj = EMFInjector.getInstance();
    Map<String, ProjectorActualParameter<?>> params = new HashMap<String, ProjectorActualParameter<?>>();
    params.put("fileURI", new ProjectorActualParameter<URI>(URI.createFileURI("src/org/eclipse/gmt/modisco/discoverer/javast/resources/JavaAST.ecore")));
    ReferenceModel astMM = (ReferenceModel)emh.loadModel(EMFReferenceModel.getMetametamodel(), emfinj, params);
    Model astm = emh.createModel(astMM);
    ModelElement astRoot = astm.createModelElement(astMM.getMetaElementByName("JavaAST::AST"));
    //String modelName = javaFile.substring(javaFile.indexOf('/')+1).replaceAll(".java", "").replace('/', '.');
    System.out.println("Extracting " + javaFile + "...");

    try {
      File file = new File(javaFile);
      BufferedReader in = new BufferedReader(new FileReader(file));
      StringBuffer buffer = new StringBuffer();
      String line = null;
      while (null != (line = in.readLine())) {
        buffer.append("\t" + line);
        buffer.append("\n");
      }

      ASTParser parser = ASTParser.newParser(AST.JLS3);
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      final String text = buffer.toString();
      parser.setSource(text.toCharArray());
      CompilationUnit node = (CompilationUnit) parser.createAST(null);

      ASTVisitor visitor = new ASTDiscoverer(astm, astRoot);
      node.accept(visitor);

      params.clear();
      params.put("fileURI", new ProjectorActualParameter<URI>(URI.createFileURI("samples/" + javaFile.replace(".java", ".xmi").replace('/', '.'))));
      emh.saveModel(astm, EMFExtractor.getInstance(), params);

    } catch (Exception e) {
      System.out.println(e);
    }
    System.out.println("Done !");
  }
}