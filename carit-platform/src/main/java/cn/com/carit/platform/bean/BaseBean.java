package cn.com.carit.platform.bean;

import java.io.Serializable;
import java.util.Date;

public class BaseBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1039561684887025234L;
	protected int id;
	protected Integer status;
	protected Date createTime;
	protected Date updateTime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
