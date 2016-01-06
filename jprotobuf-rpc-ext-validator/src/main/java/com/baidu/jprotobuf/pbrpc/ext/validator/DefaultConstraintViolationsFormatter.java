/**
 * 
 */
package com.baidu.jprotobuf.pbrpc.ext.validator;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

/**
 * Default formatter implements for {@link ConstraintViolationsFormatter}
 * 
 * @author xiemalin
 * @since 3.4.0
 */
public class DefaultConstraintViolationsFormatter implements ConstraintViolationsFormatter {

	private static final String EMPTY_STRING = "";
	
	private String formatter = "Validate violdation: property '%s' validate failed with message '%s' current value is '%s'\n";

	@Override
	public String format(Set<ConstraintViolation<Object>> result) {
		if (result == null || result.isEmpty()) {
			return EMPTY_STRING;
		}

		StringBuilder builder = new StringBuilder();
		for (ConstraintViolation<Object> constraintViolation : result) {
			String message = constraintViolation.getMessage();
			Object invalidValue = constraintViolation.getInvalidValue();
			Path propertyPath = constraintViolation.getPropertyPath();
			
			builder.append(String.format(formatter, propertyPath, message, String.valueOf(invalidValue)));
		}

		return builder.toString();
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}
}
