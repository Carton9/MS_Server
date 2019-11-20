/**
 * 
 */
package org.mike.ms.httpcore;

import java.util.UUID;

import org.mike.ms.datacontroller.DataInterface;
import org.mike.ms.datacontroller.GeneralCache;

/**
 * @author c
 *
 */
public interface HTTPResourceCentor {
	public DataInterface<HTTPCase> getIODataInterface();
	public DataInterface<HTTPCase> getParserDataInterface();
	public DataInterface<HTTPCase> getAnalysisterDataInterface();
	public DataInterface<String> getBackEndDataInterface();
}
