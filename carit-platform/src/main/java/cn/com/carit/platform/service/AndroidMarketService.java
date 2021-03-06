package cn.com.carit.platform.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import cn.com.carit.common.Constants;
import cn.com.carit.common.utils.AttachmentUtil;
import cn.com.carit.common.utils.DataGridModel;
import cn.com.carit.common.utils.JsonUtil;
import cn.com.carit.platform.action.AppCatalogAction;
import cn.com.carit.platform.action.AppCommentAction;
import cn.com.carit.platform.action.AppDownloadLogAction;
import cn.com.carit.platform.action.AppVersionAction;
import cn.com.carit.platform.action.ApplicationAction;
import cn.com.carit.platform.bean.account.Account;
import cn.com.carit.platform.bean.market.AppCatalog;
import cn.com.carit.platform.bean.market.AppComment;
import cn.com.carit.platform.bean.market.AppDownloadLog;
import cn.com.carit.platform.bean.market.AppVersion;
import cn.com.carit.platform.bean.market.Application;
import cn.com.carit.platform.cache.CacheManager;
import cn.com.carit.platform.request.market.BatchCheckAppUpdatedRequest;
import cn.com.carit.platform.request.market.CheckAppUpdated;
import cn.com.carit.platform.request.market.CheckAppUpdatedRequest;
import cn.com.carit.platform.request.market.DownloadAppRequest;
import cn.com.carit.platform.request.market.DownloadedReferencedRequest;
import cn.com.carit.platform.request.market.FullTextSearchRequest;
import cn.com.carit.platform.request.market.SearchAppDeveloperRequest;
import cn.com.carit.platform.request.market.SearchApplicationRequest;
import cn.com.carit.platform.request.market.TopRequest;
import cn.com.carit.platform.request.market.ViewApplicationRequest;
import cn.com.carit.platform.response.DownloadResponse;
import cn.com.carit.platform.response.PageResponse;
import cn.com.carit.platform.response.market.AppCatalogResponse;
import cn.com.carit.platform.response.market.AppDeveloperResponse;
import cn.com.carit.platform.response.market.AppVersionResponse;
import cn.com.carit.platform.response.market.ApplicationResponse;

import com.rop.RopRequest;
import com.rop.annotation.HttpAction;
import com.rop.annotation.NeedInSessionType;
import com.rop.annotation.ServiceMethod;
import com.rop.annotation.ServiceMethodBean;
import com.rop.response.BusinessServiceErrorResponse;

/**
 * <p>
 * <b>功能说明：</b>Android Market 服务
 * </p>
 * @author <a href="mailto:xiegengcai@gmail.com">Gengcai Xie</a>
 * 2012-9-21
 */
@ServiceMethodBean(version = "1.0", httpAction=HttpAction.GET, needInSession=NeedInSessionType.NO)
public class AndroidMarketService {
	
	private final static String LANGUAGE_PARAM_NAME="language";
	
	@Resource
	private ApplicationAction<Application> applicationAction;
	@Resource
	private AppCatalogAction<AppCatalog> appCatalogAction;
	@Resource
	private AppVersionAction<AppVersion> appVersionAction;
	@Resource
	private AppDownloadLogAction<AppDownloadLog> appDownloadLogAction;
	@Resource
	private AppCommentAction<AppComment> appCommentAction;
	
	/**
	 * <p>
	 * <b>功能说明：</b>获取所有有效应用分类
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.all.application.catalog</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>language</td><td>语言(cn/en)</td><td>是</td><td>否（默认cn）</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "market.all.application.catalog")
	public Object allApplicationCatalog(RopRequest request){
		String language=request.getRopRequestContext().getParamValue(LANGUAGE_PARAM_NAME);
		if (!StringUtils.hasText(language)) {
			language=Constants.DEAFULD_LANGUAGE;
		}
		return appCatalogAction.queryByLanguage(language);
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>查询应用
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.query.application</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>language</td><td>语言(cn/en)</td><td>是</td><td>否（默认cn）</td></tr>
	 *  <tr><td>page</td><td>起始页（默认1）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>rows</td><td>每页显示记录数（默认10）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sort</td><td>排序字段</td><td>是</td><td>否</td></tr>
	 *  <tr><td>order</td><td>排序规则（desc/asc）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>appName</td><td>应用名称</td><td>是</td><td>否</td></tr>
	 *  <tr><td>version</td><td>版本</td><td>是</td><td>否</td></tr>
	 *  <tr><td>developer</td><td>开发者</td><td>是</td><td>否</td></tr>
	 *  <tr><td>catalogId</td><td>分类Id</td><td>是</td><td>否</td></tr>
	 *  <tr><td>catalogName</td><td>分类名称</td><td>是</td><td>否</td></tr>
	 *  <tr><td>size</td><td>应用大小</td><td>是</td><td>否</td></tr>
	 *  <tr><td>platform</td><td>适用平台</td><td>是</td><td>否</td></tr>
	 *  <tr><td>supportLanguages</td><td>支持语言</td><td>是</td><td>否</td></tr>
	 *  <tr><td>downCount</td><td>下载次数</td><td>是</td><td>否</td></tr>
	 *  <tr><td>appLevel</td><td>评级</td><td>是</td><td>否</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "market.query.application")
	public Object queryApplication(SearchApplicationRequest request){
		PageResponse<ApplicationResponse> page=new PageResponse<ApplicationResponse>();
		BeanUtils.copyProperties(applicationAction.queryByExemple(request), page);
		return page;
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>查询置顶应用
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.query.top.application</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>language</td><td>语言(cn/en)</td><td>是</td><td>否（默认cn）</td></tr>
	 *  <tr><td>limit</td><td>返回记录限制</td><td>是</td><td>否（默认10）</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "market.query.top.application")
	public Object queryTopApps(TopRequest request){
		return applicationAction.queryByStatus(request.getLanguage(), Application.STATUS_TOP, request.getLimit());
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>全文检索应用应用
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.full.text.search.application</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>language</td><td>语言(cn/en)</td><td>是</td><td>否（默认cn）</td></tr>
	 *  <tr><td>page</td><td>起始页（默认1）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>rows</td><td>每页显示记录数（默认10）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>keyword</td><td> @NotNull </td><td>是</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "market.full.text.search.application",version = "1.0",httpAction=HttpAction.GET, needInSession=NeedInSessionType.NO)
	public Object fullTextSearch(FullTextSearchRequest request){
		PageResponse<ApplicationResponse> page=new PageResponse<ApplicationResponse>();
		BeanUtils.copyProperties(applicationAction.fullTextSearch(request), page);
		return page;
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>查看应用
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.view.application</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>language</td><td>语言(cn/en)</td><td>是</td><td>否（默认cn）</td></tr>
	 *  <tr><td>appId</td><td> @NotNull @Min(value=1) @Max(value=Integer.MAX_VALUE)</td><td>是</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "market.view.application")
	public Object viewApplication(ViewApplicationRequest request){
		return applicationAction.queryAppById(request);
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>查询应用版本
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.query.application.versions</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>language</td><td>语言(cn/en)</td><td>是</td><td>否（默认cn）</td></tr>
	 *  <tr><td>page</td><td>起始页（默认1）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>rows</td><td>每页显示记录数（默认10）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sort</td><td>排序字段</td><td>是</td><td>否</td></tr>
	 *  <tr><td>order</td><td>排序规则（desc/asc）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>appId</td><td> @NotNull @Min(value=1) @Max(value=Integer.MAX_VALUE)</td><td>是</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "market.query.application.versions")
	public Object queryAppVersions(DownloadedReferencedRequest request){
		PageResponse<AppVersionResponse> page=new PageResponse<AppVersionResponse>();
		BeanUtils.copyProperties(appVersionAction.queryAppVersions(request), page);
		return page;
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>查询应用评论
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.query.application.comments</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>page</td><td>起始页（默认1）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>rows</td><td>每页显示记录数（默认10）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sort</td><td>排序字段</td><td>是</td><td>否</td></tr>
	 *  <tr><td>order</td><td>排序规则（desc/asc）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>appId</td><td> @NotNull @Min(value=1) @Max(value=Integer.MAX_VALUE)</td><td>是</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "market.query.application.comments")
	public Object queryAppComments(DownloadedReferencedRequest request){
		DataGridModel dgm = new DataGridModel();
		dgm.setSort(request.getSort());
		dgm.setOrder(request.getOrder());
		dgm.setPage(request.getPage());
		dgm.setRows(request.getRows());
		PageResponse<AppCatalogResponse> page=new PageResponse<AppCatalogResponse>();
		BeanUtils.copyProperties(appCommentAction.queryAppComments(request.getAppId(), dgm), page);
		return page;
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>热门免费应用
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.application.hot.free</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>language</td><td>语言(cn/en)</td><td>是</td><td>否（默认cn）</td></tr>
	 *  <tr><td>limit</td><td> @NotNull @Min(value=1) @Max(value=Integer.MAX_VALUE)</td><td>是</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "market.application.hot.free")
	public Object queryHotFreeApplication(TopRequest request){
		return applicationAction.queryHotFree(request);
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>热门免费新应用
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.application.hot.new.free</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>language</td><td>语言(cn/en)</td><td>是</td><td>否（默认cn）</td></tr>
	 *  <tr><td>limit</td><td> @NotNull @Min(value=1) @Max(value=Integer.MAX_VALUE)</td><td>是</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "market.application.hot.new.free")
	public Object queryHotNewFreeApplication(TopRequest request){
		return applicationAction.queryHotNewFree(request);
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>应用评级统计
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.application.stat.grade</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>appId</td><td> @NotNull @Min(value=1) @Max(value=Integer.MAX_VALUE)</td><td>是</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "market.application.stat.grade")
	public Object statApplicationCommentGrade(RopRequest request){
		return appCommentAction.statCommentGrade(Integer.valueOf(request.getRopRequestContext().getParamValue("appId")));
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>查询应用平均评分
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.application.avg.grade</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>appId</td><td> @NotNull @Min(value=1) @Max(value=Integer.MAX_VALUE)</td><td>是</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "market.application.avg.grade")
	public Object queryApplicationAvgGrade(RopRequest request){
		return appCommentAction.statComment(Integer.valueOf(request.getRopRequestContext().getParamValue("appId")));
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>查询应用一个月内的下载趋势
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.application.download.trend.one.month</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>appId</td><td> @NotNull @Min(value=1) @Max(value=Integer.MAX_VALUE)</td><td>是</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "market.application.download.trend.one.month")
	public Object applicationOneMonthDownTrend(RopRequest request){
		return appDownloadLogAction.appOneMonthDownTrend(Integer.valueOf(request.getRopRequestContext().getParamValue("appId")));
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>查询下载过该应用的用户还下载过那些应用
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.application.downloaded.referenced</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>language</td><td>语言(cn/en)</td><td>是</td><td>否（默认cn）</td></tr>
	 *  <tr><td>page</td><td>起始页（默认1）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>rows</td><td>每页显示记录数（默认10）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sort</td><td>排序字段</td><td>是</td><td>否</td></tr>
	 *  <tr><td>order</td><td>排序规则（desc/asc）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>appId</td><td> @NotNull @Min(value=1) @Max(value=Integer.MAX_VALUE)</td><td>是</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "market.application.downloaded.referenced")
	public Object queryDownloadedReferencedApps(DownloadedReferencedRequest request){
		PageResponse<ApplicationResponse> page=new PageResponse<ApplicationResponse>();
		BeanUtils.copyProperties(applicationAction.queryDownloadedReferencedApps(request), page);
		return page;
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>查询应用开发者
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.developer.query</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>email</td><td>开发者邮箱</td><td>是</td><td>否</td></tr>
	 *  <tr><td>name</td><td>开发者名称</td><td>是</td><td>否</td></tr>
	 *  <tr><td>website</td><td>开发者网站</td><td>是</td><td>否</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "market.developer.query",version = "1.0")
	public Object queryDeveloper(SearchAppDeveloperRequest request){
		PageResponse<AppDeveloperResponse> page=new PageResponse<AppDeveloperResponse>();
		BeanUtils.copyProperties(applicationAction.queryAppDeveloper(request), page);
		return page;
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>检测应用是否已经更新 market.application.update.check
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.application.update.check</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>packageName</td><td>应用包名，如：com.tencent.mm</td><td>是</td><td>是</td></tr>
	 *  <tr><td>version</td><td>本地版本号</td><td>是</td><td>是</td></tr>
	 *  <tr><td>language</td><td>语言(cn/en)</td><td>是</td><td>否（默认cn）</td></tr>
	 * </table>
	 * @return 当前版本号
	 */
	@ServiceMethod(method = "market.application.update.check",version = "1.0")
	public Object appUpdatedCheck(CheckAppUpdatedRequest request) {
		return applicationAction.appUpdated(request.getLanguage(),
				request.getPackageName(), request.getVersion());
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>批量检测应用是否已经更新
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.application.batch.update.check</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>apps</td><td>应用包名数组，如：["besttone.restaurant","com.tencent.mm"]</td><td>是</td><td>是</td></tr>
	 *  <tr><td>versions</td><td>应用包名数组，如：["3.5.0.1","3.8"]</td><td>是</td><td>是</td></tr>
	 *  <tr><td>language</td><td>语言(cn/en)</td><td>是</td><td>否（默认cn）</td></tr>
	 * </table>
	 * @return [{"id":216,"name":"号百餐馆","icon":"icons/7679e0cd5e3516f743dd91f99d14cdb2.png","version":"3.5.0.4","size":"886.57K"},{"id":66,"name":"腾讯微信","icon":"icons/WeChat_2754888329849341.png","version":"4.0","size":"10.31M"}]
	 */
	@ServiceMethod(method = "market.application.batch.update.check",version = "1.0")
	public Object appUpdatedBatchCheck(BatchCheckAppUpdatedRequest request){
		List<String> apps = null;
		List<String> versions = null;
		try {
			apps = JsonUtil.jsonToList(request.getApps());
		} catch (Exception e) {
			return new BusinessServiceErrorResponse(request
					.getRopRequestContext().getMethod(),
					Constants.APPS_ARRAY_FORMAT_ERROR, request
							.getRopRequestContext().getLocale());
		}
		try {
			versions = JsonUtil.jsonToList(request.getVersions());
		} catch (Exception e) {
			return new BusinessServiceErrorResponse(request
					.getRopRequestContext().getMethod(),
					Constants.VERSIONS_ARRAY_FORMAT_ERROR, request
							.getRopRequestContext().getLocale());
		}
		if (apps.size() != versions.size()) {
			return new BusinessServiceErrorResponse(request
					.getRopRequestContext().getMethod(),
					Constants.APPS_ARRAY_VERSIONS_ARRAY_LEN_NOT_MATCH, request
							.getRopRequestContext().getLocale(), apps.size(),
					versions.size());
		}

		List<CheckAppUpdated> batchList = new ArrayList<CheckAppUpdated>();
		for (int i = 0; i < apps.size(); i++) {
			batchList.add(new CheckAppUpdated(apps.get(i), versions.get(i)));
		}
		return applicationAction.appUpdatedBatchCheck(request.getLanguage(),
				batchList);
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>下载应用
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.application.down</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>packageName</td><td>应用包名</td><td>是</td><td>是</td></tr>
	 *  <tr><td>email</td><td>下载账号邮箱</td><td>是</td><td>否</td></tr>
	 * </table>
	 * @return 
	 */
	@ServiceMethod(method = "market.application.down",version = "1.0")
	public Object downApp(DownloadAppRequest request){
		Application application = applicationAction.queryByPackageName(request.getPackageName());
		if (application==null) {//应用不存在
			return new BusinessServiceErrorResponse(request
					.getRopRequestContext().getMethod(),
					Constants.APP_NOT_EXIST, request
							.getRopRequestContext().getLocale(), request.getPackageName());
		}
		// 更新下载次数
		application.setDownCount(application.getDownCount()+1);
		applicationAction.update(application);
		
		String email=request.getEmail();
		// 保存下载记录
		AppDownloadLog log=new AppDownloadLog();
		log.setAppId(application.getId());
		log.setVersion(application.getVersion());
		
		if (StringUtils.hasText(email)) {
			// 查询缓存
			Account t = CacheManager.getInstance().getAccount(email);
			if (t == null) {
				return new BusinessServiceErrorResponse(request
						.getRopRequestContext().getMethod(),
						Constants.NO_THIS_ACCOUNT, request
								.getRopRequestContext().getLocale(), email);
			}
			log.setAccountId(t.getId());
		} else { // 匿名下载
			log.setAccountId(0);
		}
		appDownloadLogAction.add(log);
		// 返回响应
		return new DownloadResponse(AttachmentUtil.getInstance().getHost()+"/"+application.getAppFilePath());
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>查询盒子上显示的应用
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>market.query.box.application</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>language</td><td>语言(cn/en)</td><td>是</td><td>否（默认cn）</td></tr>
	 *  <tr><td>limit</td><td>返回记录限制</td><td>是</td><td>否（默认10）</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "market.query.box.application")
	public Object queryBoxApps(TopRequest request){
		return applicationAction
				.queryByStatus(
						"id, app_name appName, package_name packageName, version versionName, icon, big_icon bigIcon",
						request.getLanguage(), Application.STATUS_BOX,
						request.getLimit());
	}
}
