/**
 * 
 */
package com.baidu.jprotobuf.pbrpc.ext.validator;

import org.junit.Test;

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
		
		try {
			beanValidatorInvokerInterceptor.beforeInvoke(null, null, new Object[] {bean});
		} catch (Exception e) {
			System.out.println(e.getMessage());
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testValidateFailed() {
		PojoBean bean = PojoBean.makeInValidate();
		
		try {
			beanValidatorInvokerInterceptor.beforeInvoke(null, null, new Object[] {bean});
			Assert.fail("should throw exception here.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			Assert.assertNotNull(e.getMessage());
		}
	}
	
}
