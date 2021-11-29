package com.main.Billing.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.main.Billing.entity.ReportInfo;

@Repository
@Transactional
public class ReportInfoImpl implements ReportInfoRepository{

	@Autowired
	SessionFactory sessionFactory;

	public void createReportInfo(ReportInfo reportInfo) {
		Session session = this.sessionFactory.getCurrentSession();
		session.save(reportInfo);
	}

	@SuppressWarnings("unchecked")
	public List<ReportInfo> findReportByQuery(String sql) {
		Session session = this.sessionFactory.getCurrentSession();
		List<ReportInfo> reportList = session.createQuery(sql).list();
        return reportList;
	}
}
