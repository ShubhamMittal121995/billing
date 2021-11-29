package com.main.Billing.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.main.Billing.entity.Payment;

@Repository
@Transactional
public class PaymentRepositoryImpl implements PaymentRepository {

	@Autowired
	SessionFactory sessionFactory;
	
	@SuppressWarnings("rawtypes")
	public int deletePaymentById(Integer paymentId) {
		Session session = sessionFactory.getCurrentSession();
		Query createQuery = session.createQuery("delete from Payment where paymentId = :paymentId");
		createQuery.setParameter("paymentId", Integer.valueOf(paymentId));
		return createQuery.executeUpdate();
	}

	public Integer createPaymentByEntity(Payment payment) {
		Session session = sessionFactory.getCurrentSession();
		session.save(payment);
		return payment.getPaymentId();
	}

	public void updatePayment(Payment payment) {
		Session session = sessionFactory.getCurrentSession();
		session.update(payment);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Payment findPaymentByPaymentId(Integer paymentId) {
		Session session = sessionFactory.getCurrentSession();
		Query createQuery = session.createQuery("select p from Payment p where p.paymentId = :paymentId");
		createQuery.setParameter("paymentId", Integer.valueOf(paymentId));
		List<Payment> paymentList = createQuery.list();
		if(paymentList != null && !paymentList.isEmpty()) 
			return paymentList.get(0);
		return null;
	}

}
