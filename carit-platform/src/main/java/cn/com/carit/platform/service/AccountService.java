package cn.com.carit.platform.service;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import cn.com.carit.common.Constants;
import cn.com.carit.common.utils.AttachmentUtil;
import cn.com.carit.common.utils.CaritUtils;
import cn.com.carit.common.utils.DataGridModel;
import cn.com.carit.common.utils.ImageUtils;
import cn.com.carit.common.utils.MD5Util;
import cn.com.carit.common.utils.RandomUtils;
import cn.com.carit.platform.action.AccountAction;
import cn.com.carit.platform.action.AppCommentAction;
import cn.com.carit.platform.action.AppDownloadLogAction;
import cn.com.carit.platform.action.ApplicationAction;
import cn.com.carit.platform.action.EquipmentAction;
import cn.com.carit.platform.action.SystemMessageAction;
import cn.com.carit.platform.bean.Equipment;
import cn.com.carit.platform.bean.account.Account;
import cn.com.carit.platform.bean.account.SystemMessage;
import cn.com.carit.platform.bean.market.AppComment;
import cn.com.carit.platform.bean.market.AppDownloadLog;
import cn.com.carit.platform.bean.market.Application;
import cn.com.carit.platform.cache.CacheManager;
import cn.com.carit.platform.request.account.AccountRequest;
import cn.com.carit.platform.request.account.AddEquipmentRequest;
import cn.com.carit.platform.request.account.ApplicationRequest;
import cn.com.carit.platform.request.account.CheckEmailRequest;
import cn.com.carit.platform.request.account.CheckNicknameRequest;
import cn.com.carit.platform.request.account.CommentRequest;
import cn.com.carit.platform.request.account.LogonRequest;
import cn.com.carit.platform.request.account.ReadSystemMessageRequest;
import cn.com.carit.platform.request.account.RegisterAccountRequest;
import cn.com.carit.platform.request.account.SearchByAccountRequest;
import cn.com.carit.platform.request.account.SearchSystemMessageRequest;
import cn.com.carit.platform.request.account.UpdateAccountRequest;
import cn.com.carit.platform.request.account.UpdatePasswordRequest;
import cn.com.carit.platform.request.account.UploadUserPhotoRequest;
import cn.com.carit.platform.response.AccountResponse;
import cn.com.carit.platform.response.DownloadResponse;
import cn.com.carit.platform.response.PageResponse;
import cn.com.carit.platform.response.SystemMessageResponse;
import cn.com.carit.platform.response.UploadUserPhotoResponse;
import cn.com.carit.platform.response.market.ApplicationResponse;

import com.rop.RopRequest;
import com.rop.annotation.HttpAction;
import com.rop.annotation.NeedInSessionType;
import com.rop.annotation.ServiceMethod;
import com.rop.annotation.ServiceMethodBean;
import com.rop.response.BusinessServiceErrorResponse;
import com.rop.response.CommonRopResponse;
import com.rop.response.NotExistErrorResponse;
import com.rop.security.SubErrors;

/**
 * <p>
 * <b>功能说明：</b>账号相关服务接口
 * </p>
 * @author <a href="mailto:xiegengcai@gmail.com">Gengcai Xie</a>
 * 2012-9-21
 */
@ServiceMethodBean(version = "1.0")
public class AccountService {
	
	@Resource
	private AccountAction<Account> action;
	@Resource
	private ApplicationAction<Application> applicationAction;
	
	@Resource
	private AppDownloadLogAction<AppDownloadLog> appDownloadLogAction;
	@Resource
	private AppCommentAction<AppComment> appCommentAction;
	@Resource
	private SystemMessageAction<SystemMessage> systemMessageAction;
	@Resource
	private EquipmentAction<Equipment> equipmentAction;
	
	/**
	 * <p>
	 * <b>功能说明：</b>账号登录
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>account.logon</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>email</td><td>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}</td><td>是</td><td>是</td></tr>
	 *  <tr><td>password</td><td>\\w{6,30}</td><td>否</td><td>是</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 * </table>
	 * @return
	 * @throws Exception
	 */
	@ServiceMethod(method = "account.logon",version = "1.0", httpAction=HttpAction.POST)
    public Object logon(LogonRequest request) throws Exception {
		// 查询缓存
		Account t = CacheManager.getInstance().getAccount(request.getEmail());
		// 记录本次登录信息
		action.logon(t.getId(), request.getRopRequestContext().getIp());
		
        AccountResponse accountResponse=new AccountResponse();
        BeanUtils.copyProperties(t, accountResponse);
        
        //返回响应
        return  accountResponse;
    }
	
	/**
	 * <p>
	 * <b>功能说明：</b>账号登出
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>account.logout</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml（可选，默认xml）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "account.logout",version = "1.0", httpAction=HttpAction.GET)
	public Object logout(RopRequest request){
		return CommonRopResponse.SUCCESSFUL_RESPONSE;
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>检测账号。检测email是否已注册
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数名</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>platform.heartbeat</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml（可选，默认xml）</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>email</td>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}<td></td><td>是</td><td>是</td></tr>
	 * </table>
	 * @param request
	 * @return
	 */
	@ServiceMethod(method = "account.check.email",version = "1.0", httpAction=HttpAction.GET)
	public Object checkEmail(CheckEmailRequest request){
		return CommonRopResponse.SUCCESSFUL_RESPONSE;
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>检测账号。检测nickName是否已注册
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数名</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>platform.heartbeat</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml（可选，默认xml）</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>nickName</td>@Length(min=3 ,max=50)<td></td><td>是</td><td>是</td></tr>
	 * </table>
	 * @param request
	 * @return
	 */
	@ServiceMethod(method = "account.check.nickname",version = "1.0", httpAction=HttpAction.GET)
	public Object checkNickname(CheckNicknameRequest request){
		return CommonRopResponse.SUCCESSFUL_RESPONSE;
	}
	
	
	/**
	 * <p>
	 * <b>功能说明：</b>注册账号
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>account.register</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml（可选，默认xml）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>email</td><td>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}</td><td>是</td><td>是</td></tr>
	 *  <tr><td>password</td><td>\\w{6,30}</td><td>否</td><td>是</td></tr>
	 *  <tr><td>nickName</td><td>字符长度3~50</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 * </table>
	 * @return
	 * @throws Exception
	 */
	@ServiceMethod(method = "account.register",version = "1.0", httpAction=HttpAction.POST)
	public Object register(RegisterAccountRequest request) throws Exception{
		String email=request.getEmail();
		String password=request.getPassword();
		// 密码加密
		password=MD5Util.md5Hex(password);
		// 二次加密
		password=MD5Util.md5Hex(email+password+MD5Util.DISTURBSTR);
		action.register(email, password, request.getNickName());
		return CommonRopResponse.SUCCESSFUL_RESPONSE;
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>修改账号资料
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>account.update</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml（可选，默认xml）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>email</td><td>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}</td><td>是</td><td>是</td></tr>
	 *  <tr><td>password</td><td>\\w{6,30}</td><td>否</td><td>是</td></tr>
	 *  <tr><td>nickName</td><td>字符长度3~50</td><td>是</td><td>否</td></tr>
	 *  <tr><td>gender</td><td>数字0~2</td><td>是</td><td>否</td></tr>
	 *  <tr><td>birthday</td><td>yyyy-MM-dd 格式字符串</td><td>是</td><td>否</td></tr>
	 *  <tr><td>realName</td><td>字符长度3~20</td><td>是</td><td>否</td></tr>
	 *  <tr><td>idCard</td><td>15或18位字符串</td><td>是</td><td>否</td></tr>
	 *  <tr><td>officePhone</td><td>限长18字符</td><td>是</td><td>否</td></tr>
	 *  <tr><td>mobile</td><td>11位手机号码</td><td>是</td><td>否</td></tr>
	 *  <tr><td>address</td><td>限长200字符</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 * </table>
	 * @return
	 * @throws Exception
	 */
	@ServiceMethod(method = "account.update",version = "1.0",needInSession = NeedInSessionType.YES, httpAction=HttpAction.POST)
	public Object update(UpdateAccountRequest request) throws Exception{
		// 查询缓存
		Account t = CacheManager.getInstance().getAccount(request.getEmail());
		boolean needToUpdate=false;
		Account update=new Account();
		update.setId(t.getId());
		if (request.getAddress()!=null && !request.getAddress().equals(t.getAddress())) {
			update.setAddress(request.getAddress());
			needToUpdate=true;
		}
		if (request.getGender()!=null && request.getGender().byteValue()!=t.getGender().byteValue()) {
			update.setGender(request.getGender());
			needToUpdate=true;
		}
		if (StringUtils.hasText(request.getBirthday())) {
			update.setBirthday(CaritUtils.strToDate(request.getBirthday(), Constants.DATE_FORMATTER));
			needToUpdate=true;
		}
		if (request.getMobile()!=null && !request.getMobile().equals(t.getMobile())) {
			update.setMobile(request.getMobile());
			needToUpdate=true;
		}
		if (request.getIdCard()!=null && !request.getIdCard().equals(t.getIdCard())) {
			update.setIdCard(request.getIdCard());
			needToUpdate=true;
		}
		if (request.getNickName()!=null && !request.getNickName().equals(t.getNickName())) {
			update.setNickName(request.getNickName());
			needToUpdate=true;
		}
		if (request.getOfficePhone()!=null && !request.getOfficePhone().equals(t.getOfficePhone())) {
			update.setOfficePhone(request.getOfficePhone());
			needToUpdate=true;
		}
		if (request.getRealName()!=null && !request.getRealName().equals(t.getRealName())) {
			update.setRealName(request.getRealName());
			needToUpdate=true;
		}
		
		if (needToUpdate) {
			// 更新
			action.update(update);
			// 更新缓存
			CacheManager.refreshAccount(update, t);
		}
		
		AccountResponse accountResponse=new AccountResponse();
        BeanUtils.copyProperties(t, accountResponse);
        
        //返回响应
        return  accountResponse;
	}
	/**
	 * <p>
	 * <b>功能说明：</b>修改账号密码
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>account.update.password</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml（可选，默认xml）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>email</td><td>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}</td><td>是</td><td>是</td></tr>
	 *  <tr><td>password</td><td>\\w{6,30}</td><td>否</td><td>是</td></tr>
	 *  <tr><td>newPassword</td><td>\\w{6,30}</td><td>否</td><td>是</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 * </table>
	 * @return
	 * @throws Exception
	 */
	@ServiceMethod(method = "account.update.password",version = "1.0",needInSession = NeedInSessionType.YES, httpAction=HttpAction.POST)
	public Object updatePwd(UpdatePasswordRequest request) throws Exception{
		String email=request.getEmail();
		// 查询缓存
		Account t = CacheManager.getInstance().getAccount(email);
		String newPassword=request.getNewPassword();
		// 密码加密
		newPassword=MD5Util.md5Hex(newPassword);
		// 二次加密
		newPassword=MD5Util.md5Hex(email+newPassword+MD5Util.DISTURBSTR);
		
		// 更新密码
		action.updatePwd(email, newPassword);
		
		// 更新缓存
		t.setPassword(newPassword);
		
		return CommonRopResponse.SUCCESSFUL_RESPONSE;
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>修改账号资料
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>account.upload.photo</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml（可选，默认xml）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>email</td><td>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}</td><td>是</td><td>是</td></tr>
	 *  <tr><td>password</td><td>\\w{6,30}</td><td>否</td><td>是</td></tr>
	 *  <tr><td>photo</td><td>文件类型后缀（如png|jpg|gif）@文件二进制字节流串</td><td>否</td><td>是</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 * </table>
	 * @return
	 * @throws Throwable
	 */
	@ServiceMethod(method = "account.upload.photo",version = "1.0",needInSession = NeedInSessionType.YES, httpAction=HttpAction.POST)
	public Object updatePhoto(UploadUserPhotoRequest request) throws Throwable {
		// 查询缓存
		Account t = CacheManager.getInstance().getAccount(request.getEmail());
		String fileType = request.getPhoto().getFileType();
        long nanoTime=System.nanoTime();
        // 随机文件名
    	String prefix =  "user_"+t.getId()+"_"+nanoTime;// 构建文件名称
        // 头像文件名
        String photo=prefix+"."+fileType;
        
        FileCopyUtils.copy(request.getPhoto().getContent()
        		, AttachmentUtil.getInstance().getPhotoFile(photo));
        
        // 缩略图文件名
        String thumbPhoto=prefix+"_thumb."+fileType;
        // 生成缩略图
        ImageUtils.scale(AttachmentUtil.getInstance().getPhotoPath(photo)
        		, AttachmentUtil.getInstance().getPhotoPath(thumbPhoto)
        		, 24, 24, false);
        String hostPhotoPath = AttachmentUtil.getInstance().getHost() 
				+ Constants.BASE_PATH_PHOTOS +photo;
        String hostThumbPhoto = AttachmentUtil.getInstance().getHost() 
				+ Constants.BASE_PATH_PHOTOS +thumbPhoto;
        // 保存图片
		action.uploadPhoto(t, hostPhotoPath, hostThumbPhoto);
		return new UploadUserPhotoResponse(hostPhotoPath, hostThumbPhoto);
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>下载应用
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>account.upload.photo</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml（可选，默认xml）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>email</td><td>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}</td><td>是</td><td>是</td></tr>
	 *  <tr><td>password</td><td>\\w{6,30}</td><td>否</td><td>是</td></tr>
	 *  <tr><td>appId</td><td>应用Id</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 * </table>
	 * @return
	 * @throws Throwable
	 */
	@ServiceMethod(method = "account.download.application",version = "1.0",needInSession = NeedInSessionType.YES, httpAction=HttpAction.POST)
	public Object downloadApplicaton(ApplicationRequest request){
		// 查询缓存
		Account t = CacheManager.getInstance().getAccount(request.getEmail());
		Application application = CacheManager.getInstance().getApplication(request.getAppId());
		
		// 更新下载次数
		application.setDownCount(application.getDownCount()+1);
		applicationAction.update(application);
		
		// 保存下载记录
		AppDownloadLog log=new AppDownloadLog();
		log.setAccountId(t.getId());
		log.setAppId(application.getId());
		log.setVersion(application.getVersion());
		appDownloadLogAction.add(log);
		// 返回响应
		return new DownloadResponse(AttachmentUtil.getInstance().getHost()+"/"+application.getAppFilePath());
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>发表评论
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数名</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>account.application.addComment</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml（可选，默认xml）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>email</td><td>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}</td><td>是</td><td>是</td></tr>
	 *  <tr><td>password</td><td>\\w{6,30}</td><td>否</td><td>是</td></tr>
	 *  <tr><td>appId</td><td>应用Id</td><td>是</td><td>是</td></tr>
	 *  <tr><td>grade</td><td>应用评价(0~10)</td><td>是</td><td>否</td></tr>
	 *  <tr><td>comment</td><td>200字符内</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 * </table>
	 * @return
	 * @throws Throwable
	 */
	@ServiceMethod(method = "account.application.addComment",version = "1.0",needInSession = NeedInSessionType.YES, httpAction=HttpAction.POST)
	public Object addComment(CommentRequest request){
		// 查询缓存
		Account t = CacheManager.getInstance().getAccount(request.getEmail());
		
		int appId = Integer.valueOf(request.getAppId());
		if (appDownloadLogAction.hasDownloaded(t.getId(), appId)) {
			// 下载过应用，可以评论
			AppComment comment=new AppComment();
			comment.setAppId(appId);
			comment.setUserId(t.getId());
			comment.setGrade(request.getGrade());
			comment.setComment(request.getComment());
			appCommentAction.add(comment);
			
			return CommonRopResponse.SUCCESSFUL_RESPONSE;
		}
		
		// 没下载过应用
		return new BusinessServiceErrorResponse(
				request.getRopRequestContext().getMethod(), Constants.HAVENT_DOWNLOAD_APPLICTION,
				request.getRopRequestContext().getLocale(), t.getEmail(), request.getAppId());
		
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>查询用户下载过的应用
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>account.downloaded.application</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>email</td><td>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}</td><td>是</td><td>是</td></tr>
	 *  <tr><td>password</td><td>\\w{6,30}</td><td>否</td><td>是</td></tr>
	 *  <tr><td>language</td><td>语言(cn/en)</td><td>是</td><td>否（默认cn）</td></tr>
	 *  <tr><td>page</td><td>起始页（默认1）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>rows</td><td>每页显示记录数（默认10）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sort</td><td>排序字段</td><td>是</td><td>否</td></tr>
	 *  <tr><td>order</td><td>排序规则（desc/asc）</td><td>是</td><td>否</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "account.downloaded.application",version = "1.0",httpAction=HttpAction.GET)
	public Object queryAccountDownloadedApp(SearchByAccountRequest request) {
		DataGridModel dgm = new DataGridModel();
		dgm.setSort(request.getSort());
		dgm.setOrder(request.getOrder());
		dgm.setPage(request.getPage());
		dgm.setRows(request.getRows());
		//查询账号
		Account account=CacheManager.getInstance().getAccount(request.getEmail());
		PageResponse<ApplicationResponse> page=new PageResponse<ApplicationResponse>();
		BeanUtils.copyProperties(applicationAction.queryAccountDownloadedApp(account.getId(), request.getLanguage(), dgm), page);
		return page;
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>查询用户的系统消息
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>account.query.sys.msg</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>email</td><td>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}</td><td>是</td><td>是</td></tr>
	 *	<tr><td>password</td><td>\\w{6,30}</td><td>否</td><td>是</td></tr>
	 *  <tr><td>page</td><td>起始页（默认1）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>rows</td><td>每页显示记录数（默认10）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sort</td><td>排序字段</td><td>是</td><td>否</td></tr>
	 *  <tr><td>order</td><td>排序规则（desc/asc）</td><td>是</td><td>否</td></tr>
	 *  <tr><td>msgType</td><td>0:系统推送消息；1：应用更新消息</td><td>是</td><td>否</td></tr>
	 *  <tr><td>status</td><td>0：未读；1：已读</td><td>是</td><td>否</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "account.query.sys.msg",version = "1.0",httpAction=HttpAction.GET)
	public Object queryAccountSystemMessage(SearchSystemMessageRequest request){
		DataGridModel dgm = new DataGridModel();
		dgm.setSort(request.getSort());
		dgm.setOrder(request.getOrder());
		dgm.setPage(request.getPage());
		dgm.setRows(request.getRows());
		//查询账号
		Account account=CacheManager.getInstance().getAccount(request.getEmail());
		SystemMessage t=new SystemMessage();
		t.setAccountId(account.getId());
		t.setCatalog(request.getMsgType());
		t.setStatus(request.getStatus());
		PageResponse<SystemMessage> page=new PageResponse<SystemMessage>();
		BeanUtils.copyProperties(systemMessageAction.queryByExemple(t, dgm), page);
		return page;
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>阅读消息
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>account.read.sys.msg</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>email</td><td>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}</td><td>是</td><td>是</td></tr>
	 *	<tr><td>password</td><td>\\w{6,30}</td><td>否</td><td>是</td></tr>
	 *  <tr><td>msgId</td><td>@NotNull @Min(value=1) @Max(value=Integer.MAX_VALUE)</td><td>是</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "account.read.sys.msg",version = "1.0",httpAction=HttpAction.GET)
	public Object readAccountSystemMessage(ReadSystemMessageRequest request){
		//查询账号
		Account account=CacheManager.getInstance().getAccount(request.getEmail());
		SystemMessage msg=systemMessageAction.queryById(request.getMsgId());
		if (msg==null) { // 没有这条消息
			return new NotExistErrorResponse("SystemMessage", "id"
					, request.getMsgId(), request.getRopRequestContext().getLocale());
		}
		if (msg.getAccountId()!=account.getId()) { // 不是你的消息读个求啊
			return new BusinessServiceErrorResponse(request.getRopRequestContext().getMethod()
					, Constants.MESSAGE_ACCOUNT_ID_NOT_MATCH
					, request.getRopRequestContext().getLocale()
					, msg.getAccountId(), account.getId());
		}
		// 置为已读
		systemMessageAction.readSystemMessage(msg.getId());
		// 构建响应
		SystemMessageResponse response=new SystemMessageResponse();
		response.setCatalog(msg.getCatalog());
		response.setTitle(msg.getTitle());
		response.setContent(msg.getContent());
		return response;
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>删除消息
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>account.delete.sys.msg</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>email</td><td>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}</td><td>是</td><td>是</td></tr>
	 *	<tr><td>password</td><td>\\w{6,30}</td><td>否</td><td>是</td></tr>
	 *  <tr><td>msgId</td><td>@NotNull @Min(value=1) @Max(value=Integer.MAX_VALUE)</td><td>是</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "account.delete.sys.msg",version = "1.0",httpAction=HttpAction.GET)
	public Object deleteAccountSystemMessage(ReadSystemMessageRequest request){
		//查询账号
		Account account=CacheManager.getInstance().getAccount(request.getEmail());
		SystemMessage msg=systemMessageAction.queryById(request.getMsgId());
		if (msg==null) { // 没有这条消息
			return new NotExistErrorResponse("SystemMessage", "id"
					, request.getMsgId(), request.getRopRequestContext().getLocale());
		}
		if (msg.getAccountId()!=account.getId()) { // 不是你的消息，别乱删
			return new BusinessServiceErrorResponse(request.getRopRequestContext().getMethod()
					, Constants.MESSAGE_ACCOUNT_ID_NOT_MATCH
					, request.getRopRequestContext().getLocale()
					, msg.getAccountId(), account.getId());
		}
		// 删除
		systemMessageAction.delete(msg.getId());
		return CommonRopResponse.SUCCESSFUL_RESPONSE;
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>查询账号绑定的设备列表
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>account.equipment.query</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>email</td><td>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}</td><td>是</td><td>是</td></tr>
	 *	<tr><td>password</td><td>\\w{6,30}</td><td>否</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "account.equipment.query",version = "1.0",httpAction=HttpAction.GET)
	public Object queryEquipments(LogonRequest request){
		//查询账号
		Account account=CacheManager.getInstance().getAccount(request.getEmail());
		return equipmentAction.queryByAccount(account.getId());
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>绑定设备
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>account.equipment.add</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sessionId</td><td>{@link PlatformService#getSession(RopRequest)}获取到的sessionId</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 *  <tr><td>email</td><td>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}</td><td>是</td><td>是</td></tr>
	 *	<tr><td>password</td><td>\\w{6,30}</td><td>否</td><td>是</td></tr>
	 *	<tr><td>deviceId</td><td>@NotEmpty</td><td>是</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "account.equipment.add",version = "1.0",httpAction=HttpAction.GET)
	public Object addEquipment(AddEquipmentRequest request){
		//查询账号
		Account account=CacheManager.getInstance().getAccount(request.getEmail());
		// 如果设备不存在
		if (equipmentAction.queryByDeviceId(request.getDeviceId()) == null) {
			Equipment t=new Equipment();
			t.setAccountId(account.getId());
			t.setDeviceId(request.getDeviceId());
			equipmentAction.add(t);
			return CommonRopResponse.SUCCESSFUL_RESPONSE;
		}
		// 设备已经存在，判断其它限制条件
		int count=equipmentAction.queryAccountCountByDeviceId(request.getDeviceId());
		// 已经绑定过
		if (equipmentAction.checkBounding(account.getId(), request.getDeviceId())>0) {
			return CommonRopResponse.SUCCESSFUL_RESPONSE;
		}
		// 没绑定过，但设备绑定的账号数达到上限
		if (count>=Equipment.MAX_BOUND_ACCOUNT_COUNT) {
			return new BusinessServiceErrorResponse(request.getRopRequestContext().getMethod()
					, Constants.EQUIPMENT_BINDING_ACCOUNT_TO_UPPER_LIMIT
					, request.getRopRequestContext().getLocale()
					, Equipment.MAX_BOUND_ACCOUNT_COUNT);
		}
		// 没绑定过，且设备绑定的账号数未达到上限
		return CommonRopResponse.SUCCESSFUL_RESPONSE;
	}
	
	/**
	 * <p>
	 * <b>功能说明：</b>找回密码
	 * </p>
	 * @param request
	 * <table border='1'>
	 * 	<tr><th>参数</th><th>规则/值</th><th>是否需要签名</th><th>是否必须</th></tr>
	 *  <tr><td>appKey</td><td>申请时的appKey</td><td>是</td><td>是</td></tr>
	 *  <tr><td>method</td><td>account.getback.password</td><td>是</td><td>是</td></tr>
	 *  <tr><td>v</td><td>1.0</td><td>是</td><td>是</td></tr>
	 *  <tr><td>locale</td><td>zh_CN/en</td><td>是</td><td>是</td></tr>
	 *  <tr><td>messageFormat</td><td>json/xml</td><td>是</td><td>否</td></tr>
	 *  <tr><td>email</td><td>[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}</td><td>是</td><td>是</td></tr>
	 *  <tr><td>sign</td><td>所有需要签名的参数按签名规则生成sign</td><td>否</td><td>是</td></tr>
	 * </table>
	 * @return
	 */
	@ServiceMethod(method = "account.getback.password",version = "1.0",httpAction=HttpAction.GET, needInSession=NeedInSessionType.NO)
	public Object getBackPassword(AccountRequest request){
		//查询账号
		Account account=CacheManager.getInstance().getAccount(request.getEmail());
		// 随机密码
		String password = RandomUtils.randomPassword();
		// 获取邮件内容
		String content = SubErrors.getSubError(Constants.FORGET_PASSWORD_MAIL_CONTENT,
				Constants.FORGET_PASSWORD_MAIL_CONTENT,
				request.getRopRequestContext().getLocale(), account.getNickName(), password)
				.getMessage();
		
		// 密码加密
		password=MD5Util.md5Hex(password);
		// 二次加密
		password=MD5Util.md5Hex(request.getEmail()+password+MD5Util.DISTURBSTR);
		
		// 更新密码并发送邮件
		action.getBackPassword(
				request.getEmail(),
				password,
				SubErrors.getSubError(Constants.FORGET_PASSWORD_MAIL_SUBJECT,
						Constants.FORGET_PASSWORD_MAIL_SUBJECT,
						request.getRopRequestContext().getLocale(),
						account.getNickName(), password).getMessage()
				, content);
		
		return CommonRopResponse.SUCCESSFUL_RESPONSE;
	}
}
