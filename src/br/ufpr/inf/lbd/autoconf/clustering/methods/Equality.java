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

package br.ufpr.inf.lbd.autoconf.clustering.methods;

import br.ufpr.inf.lbd.autoconf.FeatureVector;
import br.ufpr.inf.lbd.autoconf.Wrapper;
import br.ufpr.inf.lbd.autoconf.clustering.Classifiers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

/**
 * User: Edson
 * Date: 5/27/13
 * Time: 4:39 PM
 */
public class Equality implements Classifiers {
	/* List of operators from all jobs
	 * <GroupID, <OperatorName, OperatorCounter> > */
	HashMap<Integer, Map<String, Integer>> operators = new HashMap<Integer, Map<String, Integer>>();
	HashMap<Integer, String> tuningFiles = new HashMap<Integer, String>();
	HashMap<Integer, Integer> counter = new HashMap<Integer, Integer>();

	/**
	 * @param home
	 */
	public Equality(String home) {
		System.out.println("\t- Using Equality to compare groups.");
		loadTuningGroups(home + "/conf/operators/");
	}

	/**
	 * @param wrapper
	 * @return wrapper Tuned Wrapper
	 * @TODO: ver se funciona  
	 */
	@Override
	public Wrapper classify(Wrapper wrapper) {
		int minorId;
		Map<String, Integer> features = wrapper.getFeatureVector().getFeatures();
		if (operators.isEmpty()) {
			System.out.println("AutoConf: Warn: There is no tuningFiles group. Impossible to auto-configure job...");
		} else {
			FeatureVector fv = new FeatureVector();
			for (Integer group : operators.keySet()) {
				fv.setFeatureVector(operators.get(group));
				if(wrapper.getFeatureVector().compareTo(fv)==1){
					//  if (isEqual(operators.get(group), features)) {
					minorId = group;
					System.out.println(" * Job " + wrapper.getJobName() + " has been classified as group " + minorId);
					updateGroupCounter(minorId);
					copyTo(minorId, wrapper);
				}
			}
		}
		System.out.println("AutoConf: Warn: There is no equivalent group. Job has not been optimized...");
		return wrapper;
	}


	/**
	 * Compare two feature vectors
	 * true if they are equal
	 * false if they differ.
	 *
	 * @param src  Feature Vector
	 * @param dest Feature Vector
	 * @return boolean
	 * @TODO: se livra do equals e mude pro compareTo
	 */
	/*
  private boolean isEqual(Map<String, Integer> src, Map<String, Integer> dest) {
    for (String key : src.keySet()) {
      int srcValue = 0, destValue = 0;
      if (src.get(key) != null)
        srcValue = src.get(key);
      if (dest.get(key) != null)
        destValue = dest.get(key);
      if (srcValue != destValue) {
        return false;
      }
    }
    return true;
  } 
	 */

	/**
	 * @param groupId
	 * @param w
	 * @throws IOException
	 */
	private void copyTo(int groupId, Wrapper w) {
		w.getTuningKnobs().loadKnobsFromFile(tuningFiles.get(groupId));
	}

	/**
	 * Load tuningFiles groups from all .xml file in tuningDir
	 * A file must contain a single tuningFiles group.
	 *
	 * @param tuningDir
	 */
	public void loadTuningGroups(String tuningDir) {
		int groupId = 1;
		String fileName;
		String tuningFile = "";
		File folder = new File(tuningDir);
		File[] files = folder.listFiles();
		Properties properties = new Properties();
		try {
			/* For all .xml files in tuningDir */
			System.out.print("\t- Loading groups ");
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					fileName = files[i].getName();
					if (fileName.endsWith(".xml") || fileName.endsWith(".XML")) {
						Map<String, Integer> m = new HashMap<String, Integer>();

						FileInputStream inputStream = new FileInputStream(files[i].getAbsoluteFile());
						properties.loadFromXML(inputStream);

						/* Save the tuningFiles operators in this.operators */
						for (String key : properties.stringPropertyNames()) {
							if (key.equalsIgnoreCase("groupid")) {
								groupId = Integer.parseInt(properties.getProperty(key));
							} else if (key.equalsIgnoreCase("tuning")) {
								/* Load tuningFiles configuration for groupId */
								tuningFile = properties.getProperty(key);
							} else {
								// System.out.print(key + ":" + properties.getProperty(key)+ " ");
								String value = properties.getProperty(key);
								m.put(key, Integer.valueOf(value));
							}
						}

						System.out.format("\n\t GrpId  %2d ", groupId);
						for (String s : m.keySet()) {
							System.out.print(s + " " + m.get(s) + " ");
						}
						tuningFiles.put(groupId, tuningFile);
						operators.put(groupId, m);
						properties.clear();
						inputStream.close();
						updateGroupCounter(groupId);
					}
				}
			}
			System.out.println();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param groupId
	 */
	private void updateGroupCounter(int groupId) {
		if (counter.containsKey(groupId))
			counter.put(groupId, counter.get(groupId) + 1);
		else
			counter.put(groupId, 1);
	}
}