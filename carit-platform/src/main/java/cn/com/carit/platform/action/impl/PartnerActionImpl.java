package cn.com.carit.platform.action.impl;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import cn.com.carit.common.utils.DataGridModel;
import cn.com.carit.common.utils.JsonPage;
import cn.com.carit.platform.action.PartnerAction;
import cn.com.carit.platform.bean.Equipment;
import cn.com.carit.platform.bean.Partner;
import cn.com.carit.platform.dao.EquipmentDao;
import cn.com.carit.platform.dao.PartnerDao;

@Service
@Transactional(readOnly=true)
public class PartnerActionImpl implements PartnerAction<Partner> {
	
	@Resource
	private PartnerDao<Partner> dao;
	
	@Resource
	private EquipmentDao<Equipment> equipmentDao;

	@Transactional(readOnly=false)
	@Override
	public int add(Partner t) {
		return dao.add(t);
	}

	@Transactional(readOnly=false)
	@Override
	public int update(Partner t) {
		return dao.update(t);
	}

	@Transactional(readOnly=false)
	@Override
	public int delete(int id) {
		return dao.delete(id);
	}

	@Override
	public Partner queryById(int id) {
		return dao.queryById(id);
	}

	@Override
	public JsonPage<Partner> queryByExemple(Partner t, DataGridModel dgm) {
		return dao.queryByExemple(t, dgm);
	}

	@Override
	public List<Partner> queryByExemple(Partner t) {
		return dao.queryByExemple(t);
	}

	@Override
	public List<Partner> queryAll() {
		return dao.queryAll();
	}

	
	@Override
	public void logon(int id, String ip) {
		Partner t = new Partner(id, ip, Calendar.getInstance().getTime());
		dao.update(t);
	}

	@Override
	public boolean checkName(String name) {
		return dao.checkName(name);
	}

	@Override
	public Partner queryByName(String name) {
		return dao.queryByName(name);
	}

	@Override
	public void bindingAccount(int partnerId, String deviceId) {
		equipmentDao.addPartnerReference(partnerId, deviceId);
	}

	@Override
	public Partner queryBoundingPartner(String deviceId) {
		if (StringUtils.hasText(deviceId)) {
			return dao.queryBoundingPartner(deviceId);
		}
		return null;
	}

}
