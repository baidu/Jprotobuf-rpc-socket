/**
 * 
 */
package com.baidu.jprotobuf.pbrpc.ext.validator;

import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * A format tool to print out validate constraint violation result as a {@link String} object
 * 
 * @author xiemalin
 * @since 3.4.0
 */
public interface ConstraintViolationsFormatter {

	
	/**
	 * format validate constraint violation result  as a {@link String} object
	 * @param result validate constraint violation result 
	 * @return formatter string
	 */
	String format(Set<ConstraintViolation<Object>> result);
}
