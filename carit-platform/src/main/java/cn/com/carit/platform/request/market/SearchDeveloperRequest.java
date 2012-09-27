package cn.com.carit.platform.request.market;

import cn.com.carit.platform.request.SearchRequest;

/**
 * <p>
 * <b>功能说明：</b>查询开发者请求模型
 * </p>
 * @author <a href="mailto:xiegengcai@gmail.com">Gengcai Xie</a>
 * @date 2012-9-27
 */
public class SearchDeveloperRequest extends SearchRequest {
	private String name;
	private String website;
	private String email;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
