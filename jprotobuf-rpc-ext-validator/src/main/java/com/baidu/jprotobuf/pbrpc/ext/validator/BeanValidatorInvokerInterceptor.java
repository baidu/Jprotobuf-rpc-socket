/**
 * 
 */
package com.baidu.jprotobuf.pbrpc.ext.validator;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.baidu.jprotobuf.pbrpc.intercept.InvokerInterceptor;

/**
 * This class is to implements {@link InvokerInterceptor} interface for validating target bean argument before invoke method process
 * 
 * @author xiemalin
 * @since 3.4.0
 */
public class BeanValidatorInvokerInterceptor implements InvokerInterceptor {
	
	private ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
	private Validator validator = validatorFactory.getValidator(); 
	
	private ConstraintViolationsFormatter formatter = new DefaultConstraintViolationsFormatter();
	
	public void setFormatter(ConstraintViolationsFormatter formatter) {
		this.formatter = formatter;
	}

	/**
	 * To validate bean by Bean-Validation tool. if validate failed will throws RuntimeException
	 * 
	 * @param target target invoke object
	 * @param method method object
	 * @param args method arguments 
	 */
	@Override
	public void beforeInvoke(Object object, Method method, Object[] args) {
		if (formatter == null) {
			throw new IllegalArgumentException("'property' formatter is null");
		}
		
		if (args == null || args.length == 0) {
			return;
		}
		
		boolean voilationFound = false;
		Set<ConstraintViolation<Object>> results = new HashSet<ConstraintViolation<Object>>();
		for (Object o : args) {
			Set<ConstraintViolation<Object>> result = validator.validate(o);
			if (result == null || result.isEmpty()) {
				continue;
			}
			
			voilationFound = true;
			results.addAll(result);
			
		}
		
		if (voilationFound) {
			String message = formatter.format(results);
			throw new RuntimeException(message);
		}
		
		
	}

	@Override
	public Object process(Object object, Method method, Object[] args) {
		return null;
	}

}
