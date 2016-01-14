/**
 * 
 */
package com.baidu.jprotobuf.pbrpc.ext.validator;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

/**
 * A POJO bean using Bean Validation features
 * 
 * @author xiemalin
 * @since 3.4.0
 */
public class PojoBean {

	// 必须不为 null, 大小是 10
	@Protobuf
	@NotNull
	@Size(min = 10, max = 10, message="order length must be 10")
	private String orderId;
	// 必须不为空
	@Protobuf
	@NotEmpty
	private String customer;
	// 必须是一个电子信箱地址
	@Protobuf
	@Email
	private String email;
	// 必须不为空
	@Protobuf
	@NotEmpty
	private String address;
	// 必须不为 null, 必须是下面四个字符串'created', 'paid', 'shipped', 'closed'其中之一
	// @Status 是一个定制化的 contraint
	@Protobuf
	@NotNull
	private String status;
	// 必须不为 null
	@NotNull
	private Date createDate;
	
	public static PojoBean makeValidate() {
		PojoBean ret = new PojoBean();
		ret.orderId = "1234567890";
		ret.customer = "not null";
		ret.email = "thisismail@mail.com";
		ret.address = "not null";
		ret.status = "not null";
		ret.createDate = new Date();
		return ret;
	}
	
	public static PojoBean makeInValidate() {
		PojoBean ret = new PojoBean();
		ret.orderId = "12345678";
		ret.customer = null;
		ret.email = "thisismaiail.com";
		ret.address = null;
		ret.status = null;
		ret.createDate = null;
		return ret;
	}
	
	
	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}
	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	/**
	 * @return the customer
	 */
	public String getCustomer() {
		return customer;
	}
	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}
	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	
}
