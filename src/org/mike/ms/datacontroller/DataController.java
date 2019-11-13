/**
 * 
 */
package org.mike.ms.datacontroller;

import java.util.concurrent.ConcurrentHashMap;

import org.mike.ms.httpcore.HTTPCase;

/**
 * @author c
 *
 */
public class DataController {
	DataPool<HTTPCase> incomeRequest;
	DataPool<HTTPCase> processedRequest;
	ConcurrentHashMap<String, DataPool<HTTPCase>> processedSortedRequest;
	DataPool<HTTPCase> replyRequest;
	ConcurrentHashMap<String, DataPool<HTTPCase>> BackEndResource;
}
