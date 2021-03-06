package cn.com.carit.platform.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import cn.com.carit.DaoImpl;
import cn.com.carit.common.Constants;
import cn.com.carit.common.utils.CaritUtils;
import cn.com.carit.common.utils.DataGridModel;
import cn.com.carit.common.utils.JsonPage;
import cn.com.carit.platform.bean.market.Application;
import cn.com.carit.platform.dao.ApplicationDao;
import cn.com.carit.platform.request.market.CheckAppUpdated;
import cn.com.carit.platform.request.market.DownloadedReferencedRequest;
import cn.com.carit.platform.request.market.SearchAppDeveloperRequest;
import cn.com.carit.platform.request.market.SearchApplicationRequest;
import cn.com.carit.platform.request.market.TopRequest;
import cn.com.carit.platform.request.market.ViewApplicationRequest;
import cn.com.carit.platform.response.market.AppDeveloperResponse;
import cn.com.carit.platform.response.market.ApplicationResponse;

@Repository
public class ApplicationDaoImpl extends DaoImpl implements ApplicationDao<Application> {

	private final String DEFAULT_VIEW="v_application_cn";
	private final RowMapper<Application> rowMapper = new RowMapper<Application>() {

		@Override
		public Application mapRow(ResultSet rs, int rowNum) throws SQLException {
			Application t = new Application();
			t.setId(rs.getInt("id"));
			t.setAppName(rs.getString("app_name"));
			t.setEnName(rs.getString("en_name"));
			t.setVersion(rs.getString("version"));
			t.setIcon(rs.getString("icon"));
			t.setCatalogId(rs.getInt("catalog_id"));
			t.setSize(rs.getString("size"));
			t.setAppFilePath(rs.getString("app_file_path"));
			t.setPlatform(rs.getString("platform"));
			t.setSupportLanguages(rs.getInt("support_languages"));
			t.setPrice(rs.getDouble("price"));
			t.setDownCount(rs.getInt("down_count"));
			t.setAppLevel(rs.getInt("app_level"));
			t.setDescription(rs.getString("description"));
			t.setEnDescription(rs.getString("en_description"));
			t.setPermissionDesc(rs.getString("permission_desc"));
			t.setEnPermissionDesc(rs.getString("en_permission_desc"));
			t.setFeatures(rs.getString("features"));
			t.setEnFeatures(rs.getString("en_features"));
			t.setImages(rs.getString("images"));
			t.setStatus(rs.getInt("status"));
			t.setCreateTime(rs.getTimestamp("create_time"));
			t.setUpdateTime(rs.getTimestamp("update_time"));
			t.setBigIcon(rs.getString("big_icon"));
			t.setDeveloper(rs.getInt("developer"));
			t.setMainPic(rs.getString("main_pic"));
			return t;
		}
	};

	@Override
	public int add(final Application t) {
		final String sql = "insert into t_application (app_name"
				+ ", en_name, version, icon, big_icon, developer, catalog_id"
				+ ", size, app_file_path, platform"
				+ ", support_languages, price"
				+ ", description , permission_desc, en_description , en_permission_desc"
				+ ", images, status, create_time, update_time, features, en_features,main_pic"
				+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),now(),?,?,?)";
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		KeyHolder gkHolder = new GeneratedKeyHolder(); 
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps=con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				int i=1;
				ps.setString(i++, t.getAppName());
				ps.setString(i++, t.getEnName());
				ps.setString(i++, t.getVersion());
				ps.setString(i++, t.getIcon());
				ps.setString(i++, t.getBigIcon());
				ps.setInt(i++, t.getDeveloper());
				ps.setInt(i++, t.getCatalogId());
				ps.setString(i++, t.getSize()); 
				ps.setString(i++, t.getAppFilePath());
				ps.setString(i++, t.getPlatform());
				ps.setInt(i++, t.getSupportLanguages());
				ps.setDouble(i++, t.getPrice()==null?0:t.getPrice()); 
				ps.setString(i++, t.getDescription());
				ps.setString(i++, t.getPermissionDesc());
				ps.setString(i++, t.getEnDescription());
				ps.setString(i++, t.getEnPermissionDesc()); 
				ps.setString(i++, t.getImages());
				// 重置状态值
				if(t.getStatus().intValue()>Constants.STATUS_INVALID){
					t.setStatus(Constants.STATUS_VALID|t.getStatus());
				}
				ps.setInt(i++, t.getStatus());
				ps.setString(i++, t.getFeatures());
				ps.setString(i++, t.getEnFeatures());
				ps.setString(i++, t.getMainPic());
				return ps;
			}
		}, gkHolder);
		return gkHolder.getKey().intValue();
	}

	@Override
	public int update(Application t) {
		StringBuilder sql = new StringBuilder(
				"update t_application set update_time=now()");
		List<Object> args = new ArrayList<Object>();
		if (StringUtils.hasText(t.getAppName())) {
			sql.append(", app_name=?");
			args.add(t.getAppName());
		}
		if (StringUtils.hasText(t.getEnName())) {
			sql.append(", en_name=?");
			args.add(t.getEnName());
		}
		if (StringUtils.hasText(t.getVersion())) {
			sql.append(", version=?");
			args.add(t.getVersion());
		}
		if (StringUtils.hasText(t.getIcon())) {
			sql.append(", icon=?");
			args.add(t.getIcon());
		}
		if (StringUtils.hasText(t.getBigIcon())) {
			sql.append(", big_icon=?");
			args.add(t.getBigIcon());
		}
		if (t.getDeveloper()!=null) {
			sql.append(", developer=?");
			args.add(t.getDeveloper());
		}
		if (t.getCatalogId() != null) {
			sql.append(", catalog_id=?");
			args.add(t.getCatalogId());
		}
		if (StringUtils.hasText(t.getSize())) {
			sql.append(", size=?");
			args.add(t.getSize());
		}
		if (StringUtils.hasText(t.getAppFilePath())) {
			sql.append(", app_file_path=?");
			args.add(t.getAppFilePath());
		}
		if (StringUtils.hasText(t.getPlatform())) {
			sql.append(", platform=?");
			args.add(t.getPlatform());
		}
		if (t.getSupportLanguages() != null) {
			sql.append(", support_languages=?");
			args.add(t.getSupportLanguages());
		}
		if (t.getPrice() != null) {
			sql.append(", price=?");
			args.add(t.getPrice());
		}
		if (t.getDownCount() != null) {
			sql.append(", down_count=?");
			args.add(t.getDownCount());
		}
		if (t.getAppLevel() != null) {
			sql.append(", app_level=?");
			args.add(t.getAppLevel());
		}
		if (StringUtils.hasText(t.getDescription())) {
			sql.append(", description=?");
			args.add(t.getDescription());
		}
		if (StringUtils.hasText(t.getEnDescription())) {
			sql.append(", en_description=?");
			args.add(t.getEnDescription());
		}
		if (StringUtils.hasText(t.getPermissionDesc())) {
			sql.append(", permission_desc=?");
			args.add(t.getPermissionDesc());
		}
		if (StringUtils.hasText(t.getEnPermissionDesc())) {
			sql.append(", en_permission_desc=?");
			args.add(t.getEnPermissionDesc());
		}
		if (t.getStatus() != null) {
			if(t.getStatus().intValue()==Constants.STATUS_INVALID){
				sql.append(", status=?");
			} else {
				sql.append(", status=(status|?)");
			}
			args.add(t.getStatus());
		}
		if (StringUtils.hasText(t.getImages())) {
			sql.append(", images=?");
			args.add(t.getImages());
		}
		if (StringUtils.hasText(t.getFeatures())) {
			sql.append(", features=?");
			args.add(t.getFeatures());
		}
		if (StringUtils.hasText(t.getFeatures())) {
			sql.append(", en_features=?");
			args.add(t.getEnFeatures());
		}
		if (StringUtils.hasText(t.getMainPic())) {
			sql.append(", main_pic=?");
			args.add(t.getMainPic());
		}
		sql.append(" where id=?");
		args.add(t.getId());
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		return jdbcTemplate.update(sql.toString(), args.toArray());
	}

	@Override
	public int delete(int id) {
		String sql = "delete from t_application where id=?";
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		return jdbcTemplate.update(sql, id);
	}

	@Override
	public Application queryById(int id) {
		String sql = "select * from t_application where id=?";
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		return query(sql, id, rowMapper);
	}
	
	@Override
	public JsonPage<Application> queryByExemple(Application t, DataGridModel dgm) {
		JsonPage<Application> jsonPage = new JsonPage<Application>(dgm.getPage(), dgm.getRows());
		StringBuilder sql = new StringBuilder(
				"select * from t_application where 1=1");
		List<Object> args = new ArrayList<Object>();
		List<Integer> argTypes = new ArrayList<Integer>();
		String whereSql=buildWhere(args, argTypes, t);
		sql.append(whereSql);
		String countSql="select count(1) from t_application where 1=1"+whereSql;
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", countSql));
		}
		int totalRow = queryForInt(countSql, args, argTypes);
		// 更新
		jsonPage.setTotal(totalRow);
		// 排序
		if (StringUtils.hasText(dgm.getOrder())
				&& StringUtils.hasText(dgm.getSort())) {
			sql.append(" order by ")
					.append(CaritUtils.splitFieldWords(dgm.getSort()))
					.append(" ").append(dgm.getOrder()).append(", update_time desc");
		} else {
			sql.append(" order by update_time desc");
		}
		sql.append(" limit ?, ?");
		args.add(jsonPage.getStartRow());
		args.add(jsonPage.getPageSize());
		argTypes.add(Types.INTEGER);
		argTypes.add(Types.INTEGER);
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		jsonPage.setRows(query(sql.toString(), args, argTypes, rowMapper));
		return jsonPage;
	}

	@Override
	public List<Application> queryByExemple(Application t) {
		StringBuilder sql = new StringBuilder(
				"select * from t_application where 1=1");
		List<Object> args = new ArrayList<Object>();
		List<Integer> argTypes = new ArrayList<Integer>();
		sql.append(buildWhere(args, argTypes, t));
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		return query(sql.toString(), args, argTypes, rowMapper);
	}

	@Override
	public List<Application> queryAll() {
		String sql="select * from t_application where status>0";
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		return jdbcTemplate.query(sql, rowMapper);
	}

	@Override
	public String buildWhere(List<Object> args, List<Integer> argTypes,
			Application t) {
		StringBuilder sql=new StringBuilder();
		if (StringUtils.hasText(t.getAppName())) {
			sql.append(" and app_name like CONCAT('%',?,'%')");
			args.add(t.getAppName());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(t.getEnName())) {
			sql.append(" and en_name like CONCAT('%',?,'%')");
			args.add(t.getEnName());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(t.getVersion())) {
//			sql.append(" and version like CONCAT('%',?,'%')");
			sql.append(" and version=?");
			args.add(t.getVersion());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(t.getIcon())) {
			sql.append(" and icon like CONCAT('%',?,'%')");
			args.add(t.getIcon());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(t.getBigIcon())) {
			sql.append(", big_icon like CONCAT('%',?,'%')");
			args.add(t.getBigIcon());
			argTypes.add(12);// java.sql.Types type
		}
		if (t.getDeveloper()!=null) {
			sql.append(", developer =?");
			args.add(t.getDeveloper());
			argTypes.add(4);// java.sql.Types type
		}
		if (t.getCatalogId() != null) {
			sql.append(" and catalog_id=?");
			args.add(t.getCatalogId());
			argTypes.add(4);// java.sql.Types type
		}
		if (StringUtils.hasText(t.getSize())) {
//			sql.append(" and size like CONCAT('%',?,'%')");
			sql.append(" and size=?");
			args.add(t.getSize());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(t.getAppFilePath())) {
			sql.append(" and app_file_path like CONCAT('%',?,'%')");
			args.add(t.getAppFilePath());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(t.getPlatform())) {
			sql.append(" and platform like CONCAT('%',?,'%')");
			args.add(t.getPlatform());
			argTypes.add(12);// java.sql.Types type
		}
		if (t.getSupportLanguages() != null) {
			sql.append(" and support_languages=?");
			args.add(t.getSupportLanguages());
			argTypes.add(4);// java.sql.Types type
		}
		if (t.getPrice() != null) {
			sql.append(" and price=?");
			args.add(t.getPrice());
			argTypes.add(8);// java.sql.Types type
		}
		if (t.getDownCount() != null) {
			sql.append(" and down_count=?");
			args.add(t.getDownCount());
			argTypes.add(4);// java.sql.Types type
		}
		if (t.getAppLevel() != null) {
			sql.append(" and app_level=?");
			args.add(t.getAppLevel());
			argTypes.add(4);// java.sql.Types type
		}
		if (StringUtils.hasText(t.getDescription())) {
			sql.append(" and description like CONCAT('%',?,'%')");
			args.add(t.getDescription());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(t.getPermissionDesc())) {
			sql.append(" and permission_desc like CONCAT('%',?,'%')");
			args.add(t.getPermissionDesc());
			argTypes.add(12);// java.sql.Types type
		}
		if (t.getStatus() != null) {
			sql.append(" and (status&?)!=0");
			args.add(t.getStatus());
			argTypes.add(4);// java.sql.Types type
		}
		if (t.getCreateTime() != null) {
			sql.append(" and create_time=?");
			args.add(t.getCreateTime());
			argTypes.add(93);// java.sql.Types type
		}
		if (t.getUpdateTime() != null) {
			sql.append(" and update_time=?");
			args.add(t.getUpdateTime());
			argTypes.add(93);// java.sql.Types type
		}
		if (StringUtils.hasText(t.getFeatures())) {
			sql.append(" and features like CONCAT('%',?,'%')");
			args.add(t.getFeatures());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(t.getFeatures())) {
			sql.append(" and en_features like CONCAT('%',?,'%')");
			args.add(t.getFeatures());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(t.getMainPic())) {
			sql.append(" and main_pic like CONCAT('%',?,'%')");
			args.add(t.getMainPic());
			argTypes.add(12);// java.sql.Types type
		}
		return sql.toString();
	}

	private final RowMapper<ApplicationResponse> responseRowMapper=new RowMapper<ApplicationResponse>() {
		
		@Override
		public ApplicationResponse mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			ApplicationResponse t=new ApplicationResponse();
			t.setId(rs.getInt("id"));
			t.setAppName(rs.getString("app_name"));
			t.setIcon(rs.getString("icon"));
			t.setBigIcon(rs.getString("big_icon"));
			t.setDeveloper(rs.getString("developer"));
			t.setDeveloperWebsite(rs.getString("website"));
			t.setDeveloperEmail(rs.getString("email"));
			t.setVersion(rs.getString("version"));
			t.setCatalogName(rs.getString("catalog_name"));
			t.setSize(rs.getString("size"));
			t.setAppFilePath(rs.getString("app_file_path"));
			t.setAppLevel(rs.getInt("app_level"));
			t.setAppFilePath(rs.getString("app_file_path"));
			t.setPlatform(rs.getString("platform"));
			t.setSupportLanguages(rs.getString("support_languages"));
			t.setPrice(rs.getDouble("price"));
			t.setDownCount(rs.getInt("down_count"));
			t.setAppLevel(rs.getInt("app_level"));
			t.setDescription(rs.getString("description"));
			t.setFeatures(rs.getString("features"));
			t.setPermissionDesc(rs.getString("permission_desc"));
			String images=rs.getString("images");
			if (StringUtils.hasText(images)) {
				t.setImageList(images.split(";"));
			}
			t.setUpdateTime(rs.getTimestamp("update_time"));
			t.setMainPic(rs.getString("main_pic"));
//			t.setStatus(rs.getInt("status"));
			return t;
		}
	};
	
	@Override
	public JsonPage<ApplicationResponse> queryByExemple(
			SearchApplicationRequest request) {
		String viewName=DEFAULT_VIEW;
		if (!Constants.DEAFULD_LANGUAGE.equalsIgnoreCase(request.getLanguage())) {
			viewName="v_application_en";
		}
		DataGridModel dgm = new DataGridModel();
		dgm.setSort(request.getSort());
		dgm.setOrder(request.getOrder());
		dgm.setPage(request.getPage());
		dgm.setRows(request.getRows());
		JsonPage<ApplicationResponse> jsonPage = new JsonPage<ApplicationResponse>(dgm.getPage(), dgm.getRows());
		StringBuilder sql = new StringBuilder("select * from ").append(
				viewName).append(" where 1=1");
		StringBuilder countSql=new StringBuilder("select count(1) from ").append(
				viewName).append(" where 1=1");
		List<Object> args = new ArrayList<Object>();
		List<Integer> argTypes = new ArrayList<Integer>();
		if (StringUtils.hasText(request.getAppName())) {
			sql.append(" and app_name like CONCAT('%',?,'%')");
			countSql.append(" and app_name like CONCAT('%',?,'%')");
			args.add(request.getAppName());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(request.getVersion())) {
			sql.append(" and version like CONCAT('%',?,'%')");
			countSql.append(" and version like CONCAT('%',?,'%')");
			args.add(request.getVersion());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(request.getDeveloper())) {
			sql.append(" and developer=?");
			countSql.append(" and developer=?");
			args.add(request.getDeveloper());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(request.getDeveloperWebsite())) {
			sql.append(" and website like CONCAT('%',?,'%')");
			countSql.append(" and website like CONCAT('%',?,'%')");
			args.add(request.getDeveloperWebsite());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(request.getDeveloperEmail())) {
			sql.append(" and email like CONCAT('%',?,'%')");
			countSql.append(" and email like CONCAT('%',?,'%')");
			args.add(request.getDeveloperEmail());
			argTypes.add(12);// java.sql.Types type
		}
		if (request.getCatalogId() != null) {
			sql.append(" and catalog_id=?");
			countSql.append(" and catalog_id=?");
			args.add(request.getCatalogId());
			argTypes.add(4);// java.sql.Types type
		}
		if (StringUtils.hasText(request.getCatalogName())) {
			sql.append(" and catalog_name like CONCAT('%',?,'%')");
			countSql.append(" and catalog_name like CONCAT('%',?,'%')");
			args.add(request.getCatalogId());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(request.getSize())) {
			sql.append(" and size like CONCAT('%',?,'%')");
			countSql.append(" and size like CONCAT('%',?,'%')");
			args.add(request.getSize());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(request.getPlatform())) {
			sql.append(" and platform like CONCAT('%',?,'%')");
			countSql.append(" and platform like CONCAT('%',?,'%')");
			args.add(request.getPlatform());
			argTypes.add(12);// java.sql.Types type
		}
		if (StringUtils.hasText(request.getSupportLanguages())) {
			sql.append(" and support_languages like CONCAT('%',?,'%')");
			countSql.append(" and support_languages like CONCAT('%',?,'%')");
			args.add(request.getSupportLanguages());
			argTypes.add(4);// java.sql.Types type
		}
		if (request.getPrice() != null) {
			sql.append(" and price=?");
			countSql.append(" and price=?");
			args.add(request.getPrice());
			argTypes.add(8);// java.sql.Types type
		}
		if (request.getDownCount() != null) {
			sql.append(" and down_count=?");
			countSql.append(" and down_count=?");
			args.add(request.getDownCount());
			argTypes.add(4);// java.sql.Types type
		}
		if (request.getAppLevel() != null) {
			sql.append(" and app_level=?");
			countSql.append(" and app_level=?");
			args.add(request.getAppLevel());
			argTypes.add(4);// java.sql.Types type
		}
		if (request.getStatus()!=null) {
			sql.append(" and (status&?)!=0");
			countSql.append(" and (status&?)!=0");
			args.add(request.getStatus());
			argTypes.add(Types.INTEGER);
		}
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", countSql));
		}
		int totalRow = queryForInt(countSql.toString(), args, argTypes);
		// 排序
		if (StringUtils.hasText(dgm.getOrder())
				&& StringUtils.hasText(dgm.getSort())) {
			sql.append(" order by ")
					.append(CaritUtils.splitFieldWords(dgm.getSort()))
					.append(" ").append(dgm.getOrder());
		} else {
			sql.append(" order by update_time desc");
		}
		sql.append(" limit ?, ?");
		args.add(jsonPage.getStartRow());
		args.add(jsonPage.getPageSize());
		argTypes.add(Types.INTEGER);
		argTypes.add(Types.INTEGER);
		// 更新
		jsonPage.setTotal(totalRow);
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		jsonPage.setRows(query(sql.toString(), args, argTypes, responseRowMapper));
		return jsonPage;
	}

	@Override
	public ApplicationResponse queryAppById(ViewApplicationRequest request) {
		String viewName=DEFAULT_VIEW;
		if (!Constants.DEAFULD_LANGUAGE.equalsIgnoreCase(request.getLanguage())) {
			viewName="v_application_en";
		}
		String sql = "select * from "+viewName+" where id=?";
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql, request.getAppId()));
		}
		return query(sql, request.getAppId(), responseRowMapper);
	}

	@Override
	public List<ApplicationResponse> queryHotFree(TopRequest request) {
		String viewName=DEFAULT_VIEW;
		if (!Constants.DEAFULD_LANGUAGE.equalsIgnoreCase(request.getLanguage())) {
			viewName="v_application_en";
		}
		String sql="select * from "+viewName+" where price=0 order by down_count desc limit ?";
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql, request.getLimit()));
		}
		return jdbcTemplate.query(sql, new Object[]{request.getLimit()}, responseRowMapper);
	}

	@Override
	public List<ApplicationResponse> queryHotNewFree(TopRequest request) {
		String viewName=DEFAULT_VIEW;
		if (Constants.DEAFULD_LANGUAGE.equalsIgnoreCase(request.getLanguage())) {
			viewName="v_application_en";
		}
		String sql="select * from "+viewName+" where price=0 order by down_count,update_time desc limit ?";

		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql, request.getLimit()));
		}
		return jdbcTemplate.query(sql, new Object[]{request.getLimit()}, responseRowMapper);
	}

	@Override
	public JsonPage<ApplicationResponse> fullTextSearch(String language,
			String ids, DataGridModel dgm) {
		String viewName=DEFAULT_VIEW;
		if (!Constants.DEAFULD_LANGUAGE.equalsIgnoreCase(language)) {
			viewName="v_application_en";
		}
		JsonPage<ApplicationResponse> jsonPage = new JsonPage<ApplicationResponse>(dgm.getPage(), dgm.getRows());
		StringBuilder sql = new StringBuilder("select * from ").append(
				viewName).append(" where id in (").append(ids).append(")");
		StringBuilder countSql=new StringBuilder("select count(1) from ").append(
				viewName).append(" where id in (").append(ids).append(")");
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", countSql));
		}
		int totalRow = jdbcTemplate.queryForInt(countSql.toString());
		// 更新
		jsonPage.setTotal(totalRow);
		// 排序
		if (StringUtils.hasText(dgm.getOrder())
				&& StringUtils.hasText(dgm.getSort())) {
			sql.append(" order by ")
					.append(CaritUtils.splitFieldWords(dgm.getSort()))
					.append(" ").append(dgm.getOrder());
		} else {
			sql.append(" order by update_time desc");
		}
		sql.append(" limit ?, ?");
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		List<ApplicationResponse> rows=jdbcTemplate.query(sql.toString()
				, new Object[]{jsonPage.getStartRow()
			, jsonPage.getPageSize()}, responseRowMapper);
		jsonPage.setRows(rows);
		return jsonPage;
	}

	@Override
	public JsonPage<ApplicationResponse> queryAccountDownloadedApp(int accountId, String language, DataGridModel dgm){
		String viewName=DEFAULT_VIEW;
		if (!Constants.DEAFULD_LANGUAGE.equalsIgnoreCase(language)) {
			viewName="v_application_en";
		}
		JsonPage<ApplicationResponse> jsonPage = new JsonPage<ApplicationResponse>(dgm.getPage(), dgm.getRows());
		StringBuilder sql = new StringBuilder("select a.* from ")
				.append(viewName)
				.append(" a where exists (select 1 from t_app_download_log where app_id=a.id and account_id=?)");
		StringBuilder countSql = new StringBuilder("select count(1) from ")
				.append(viewName)
				.append(" a where exists (select 1 from t_app_download_log where app_id=a.id and account_id=?)");
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", countSql));
		}
		int totalRow = jdbcTemplate.queryForInt(countSql.toString(), accountId);
		// 更新
		jsonPage.setTotal(totalRow);
		// 排序
		if (StringUtils.hasText(dgm.getOrder())
				&& StringUtils.hasText(dgm.getSort())) {
			sql.append(" order by ")
					.append(CaritUtils.splitFieldWords(dgm.getSort()))
					.append(" ").append(dgm.getOrder());
		} else {
			sql.append(" order by update_time desc");
		}
		sql.append(" limit ?, ?");
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		List<ApplicationResponse> rows = jdbcTemplate.query(
				sql.toString(),
				new Object[] {accountId , jsonPage.getStartRow(),
						jsonPage.getPageSize() }, responseRowMapper);
		jsonPage.setRows(rows);
		return jsonPage;
	}

	@Override
	public JsonPage<ApplicationResponse> queryDownloadedReferencedApps(DownloadedReferencedRequest request) {
		String viewName=DEFAULT_VIEW;
		if (!Constants.DEAFULD_LANGUAGE.equalsIgnoreCase(request.getLanguage())) {
			viewName="v_application_en";
		}
		DataGridModel dgm=new DataGridModel();
		dgm.setSort(request.getSort());
		dgm.setOrder(request.getOrder());
		dgm.setPage(request.getPage());
		dgm.setRows(request.getRows());
		JsonPage<ApplicationResponse> jsonPage = new JsonPage<ApplicationResponse>(dgm.getPage(), dgm.getRows());
		StringBuilder sql = new StringBuilder("select a.* from ")
		.append(viewName)
		.append(" a left join t_app_download_log b on a.id=b.app_id left join t_app_download_log c on b.account_id=c.account_id where c.app_id=? and b.app_id!=? group by b.app_id");
		StringBuilder countSql = new StringBuilder("select count(distinct a.id) from ")
				.append(viewName)
				.append(" a left join t_app_download_log b on a.id=b.app_id left join t_app_download_log c on b.account_id=c.account_id where c.app_id=? and b.app_id!=?");
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", countSql));
		}
		int totalRow = jdbcTemplate.queryForInt(countSql.toString(), request.getAppId(), request.getAppId());
		// 更新
		jsonPage.setTotal(totalRow);
		// 排序
		if (StringUtils.hasText(dgm.getOrder())
				&& StringUtils.hasText(dgm.getSort())) {
			sql.append(" order by ")
					.append(CaritUtils.splitFieldWords(dgm.getSort()))
					.append(" ").append(dgm.getOrder());
		} else {
			sql.append(" order by update_time desc");
		}
		sql.append(" limit ?, ?");
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		List<ApplicationResponse> rows = jdbcTemplate.query(
				sql.toString(),
				new Object[] { request.getAppId(), request.getAppId(), jsonPage.getStartRow(),
						jsonPage.getPageSize() }, responseRowMapper);
		jsonPage.setRows(rows);
		return jsonPage;
	}

	@Override
	public JsonPage<AppDeveloperResponse> queryAppDeveloper(
			SearchAppDeveloperRequest request) {
		StringBuilder sql=new StringBuilder("select * from t_app_developer where 1=1");
		StringBuilder countSql = new StringBuilder("select count(1) from t_app_developer where 1=1");
		DataGridModel dgm=new DataGridModel();
		dgm.setSort(request.getSort());
		dgm.setOrder(request.getOrder());
		dgm.setPage(request.getPage());
		dgm.setRows(request.getRows());
		JsonPage<AppDeveloperResponse> jsonPage = new JsonPage<AppDeveloperResponse>(dgm.getPage(), dgm.getRows());
		List<Object> args = new ArrayList<Object>();
		List<Integer> argTypes = new ArrayList<Integer>();
		if (StringUtils.hasText(request.getEmail())) {
			sql.append(" and email like CONCAT('%',?,'%')");
			countSql.append(" and email like CONCAT('%',?,'%')");
			args.add(request.getEmail());
			argTypes.add(Types.VARCHAR);
		}
		if (StringUtils.hasText(request.getWebsite())) {
			sql.append(" and website like CONCAT('%',?,'%')");
			countSql.append(" and website like CONCAT('%',?,'%')");
			args.add(request.getWebsite());
			argTypes.add(Types.VARCHAR);
		}
		if (StringUtils.hasText(request.getName())) {
			sql.append(" and name like CONCAT('%',?,'%')");
			countSql.append(" and name like CONCAT('%',?,'%')");
			args.add(request.getName());
			argTypes.add(Types.VARCHAR);
		}
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", countSql));
		}
		int totalRow = queryForInt(countSql.toString(), args, argTypes);
		// 更新
		jsonPage.setTotal(totalRow);
		// 排序
		if (StringUtils.hasText(dgm.getOrder())
				&& StringUtils.hasText(dgm.getSort())) {
			sql.append(" order by ")
					.append(CaritUtils.splitFieldWords(dgm.getSort()))
					.append(" ").append(dgm.getOrder());
		} else {
			sql.append(" order by update_time desc");
		}
		sql.append(" limit ?, ?");
		args.add(jsonPage.getStartRow());
		args.add(jsonPage.getPageSize());
		argTypes.add(Types.INTEGER);
		argTypes.add(Types.INTEGER);
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		jsonPage.setRows(query(sql.toString(), args, argTypes, new RowMapper<AppDeveloperResponse>() {

			@Override
			public AppDeveloperResponse mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				AppDeveloperResponse t=new AppDeveloperResponse();
				t.setEmail(rs.getString("email"));
				t.setName(rs.getString("name"));
				t.setWebsite(rs.getString("website"));
				return t;
			}
		}));
		return jsonPage;
	}

	@Override
	public Map<String, Object> appUpdated(String language, String packageName, String version) {
		String viewName=DEFAULT_VIEW;
		if (!Constants.DEAFULD_LANGUAGE.equalsIgnoreCase(language)) {
			viewName="v_application_en";
		}
		String sql = "select id, app_name name, icon, version, size from "+viewName+" where package_name=? and version<>? limit 1";
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		try {
			return jdbcTemplate.queryForMap(sql, packageName ,version);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			return Collections.emptyMap();
		}
	}

	@Override
	public List<Map<String, Object>> appUpdatedBatchCheck(String language,
			List<CheckAppUpdated> batchList) {
		String viewName=DEFAULT_VIEW;
		if (!Constants.DEAFULD_LANGUAGE.equalsIgnoreCase(language)) {
			viewName="v_application_en";
		}
		String baseSql = "select id, app_name name, icon, version, size from "+viewName+" where package_name=? and version<>?";
		StringBuilder sql = new StringBuilder();
		List<Object> args = new ArrayList<Object>();
		List<Integer> argTypes = new ArrayList<Integer>();
		for (CheckAppUpdated app : batchList) {
			sql.append(baseSql).append(" union ");
			args.add(app.getPackageName());
			argTypes.add(Types.VARCHAR);
			args.add(app.getVersion());
			argTypes.add(Types.VARCHAR);
		}
		sql.delete(sql.lastIndexOf(" union"), sql.length());
		return queryForList(sql.toString(), args, argTypes);
	}

	@Override
	public Application queryByPackageName(String packageName) {
		String sql="select * from t_application where package_name=? limit 1";
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		return query(sql, packageName, rowMapper);
	}

	@Override
	public List<Map<String, Object>> queryByStatus(String language, int status,
			int limit) {
		String viewName=DEFAULT_VIEW;
		if (!Constants.DEAFULD_LANGUAGE.equalsIgnoreCase(language)) {
			viewName="v_application_en";
		}
		StringBuilder sql = new StringBuilder("select id, main_pic mainPic from ")
		.append(viewName)
		.append(" where (status&?)<>0");
		// 排序
		sql.append(" order by down_count, update_time desc");
		sql.append(" limit ?");
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		return jdbcTemplate.queryForList(sql.toString(), status, limit);
	}

	@Override
	public List<Map<String, Object>> queryByStatus(String fetchFields, String language,
			int status, int limit) {
		String viewName=DEFAULT_VIEW;
		if (!Constants.DEAFULD_LANGUAGE.equalsIgnoreCase(language)) {
			viewName="v_application_en";
		}
		StringBuilder sql = new StringBuilder("select " + fetchFields + " from ")
		.append(viewName)
		.append(" where (status&?)<>0");
		// 排序
		sql.append(" order by down_count, update_time desc");
		sql.append(" limit ?");
		if (log.isDebugEnabled()) {
			log.debug(String.format("\n%1$s\n", sql));
		}
		return jdbcTemplate.queryForList(sql.toString(), status, limit);
	}
	
	
}
