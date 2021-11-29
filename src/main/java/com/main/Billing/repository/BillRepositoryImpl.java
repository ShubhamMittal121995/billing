package com.main.Billing.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.main.Billing.entity.Bill;
import com.main.Billing.entity.Payment;

@Repository
@Transactional
public class BillRepositoryImpl implements BillRepository{

	@Autowired
	SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	public List<Bill> findBillByQuery(String sql) {
		Session session = this.sessionFactory.getCurrentSession();
		List<Bill> billList = session.createQuery(sql).list();
        return billList;
	}

	@SuppressWarnings("rawtypes")
	public int deleteBillById(int slipNumber) {
		Session session = this.sessionFactory.getCurrentSession();
		Query createQuery = session.createQuery("delete from Bill where slipNumber = :slipNumber");
		createQuery.setParameter("slipNumber", Integer.valueOf(slipNumber));
		return createQuery.executeUpdate();
	}

	public void updateBillByEntity(Bill bill) {
		Session session = this.sessionFactory.getCurrentSession();
		session.update(bill);
	}

	public void createBillByEntity(Bill bill) {
		Session session = this.sessionFactory.getCurrentSession();
		session.save(bill);
	}

	@SuppressWarnings("unchecked")
	public List<Payment> findPaymentByQuery(String query) {
		Session session = this.sessionFactory.getCurrentSession();
		List<Payment> paymentList = session.createQuery(query).list();
        return paymentList;
	}

}
