/**
 * 
 */
package com.baidu.jprotobuf.pbrpc.ext.validator;

import org.junit.Test;

import com.baidu.jprotobuf.pbrpc.intercept.MethodInvocationInfo;

import junit.framework.Assert;

/**
 * Test class for {@link BeanValidatorInvokerInterceptor}
 * 
 * @author xiemalin
 * @since 3.4.0
 */
public class BeanValidatorInvokerInterceptorTest {

	private BeanValidatorInvokerInterceptor beanValidatorInvokerInterceptor = new BeanValidatorInvokerInterceptor();
	
	
	@Test
	public void testValidateSuccess() {
		PojoBean bean = PojoBean.makeValidate();
		
		MethodInvocationInfo methodInvocationInfo = new MethodInvocationInfo(null, new Object[] {bean}, null, null);
		
		try {
			beanValidatorInvokerInterceptor.beforeInvoke(methodInvocationInfo);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testValidateFailed() {
		PojoBean bean = PojoBean.makeInValidate();
		MethodInvocationInfo methodInvocationInfo = new MethodInvocationInfo(null, new Object[] {bean}, null, null);
		
		try {
			beanValidatorInvokerInterceptor.beforeInvoke(methodInvocationInfo);
			Assert.fail("should throw exception here.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			Assert.assertNotNull(e.getMessage());
		}
	}
	
}
