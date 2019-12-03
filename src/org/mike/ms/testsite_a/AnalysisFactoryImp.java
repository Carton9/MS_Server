/**
 * 
 */
package org.mike.ms.testsite_a;

import org.mike.ms.httpcore.HTTPAnalysister;
import org.mike.ms.httpcore.HTTPAnalysisterFactory;

/**
 * @author c
 *
 */
public class AnalysisFactoryImp implements HTTPAnalysisterFactory {

	/**
	 * 
	 */
	public AnalysisFactoryImp() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public HTTPAnalysister getHttpAnalysister() {
		// TODO Auto-generated method stub
		return new TestSiteAnalysis();
	}

}
