package com.main.Billing.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.main.Billing.entity.PostingJournal;

@Repository
@Transactional
public class PostingJournalRepositoryImpl implements PostingJournalRepository {

	@Autowired
	SessionFactory sessionFactory;
	
	@SuppressWarnings("rawtypes")
	public int deletePostingJournalBySlipNumber(int slipNumber) {
		Session session = this.sessionFactory.getCurrentSession();
		Query createQuery = session.createQuery("delete from PostingJournal where slipNumber = :slipNumber");
		createQuery.setParameter("slipNumber", Integer.valueOf(slipNumber));
		return createQuery.executeUpdate();		
	}

	public void createPostingJournal(PostingJournal pj) {
		Session session = this.sessionFactory.getCurrentSession();
		session.save(pj);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PostingJournal findPostingJournalBySlipNumber(Integer slipNumber) {
		Session session = this.sessionFactory.getCurrentSession();
		Query createQuery = session.createQuery("select pj from PostingJournal pj where pj.slipNumber = :slipNumber");
		createQuery.setParameter("slipNumber", Integer.valueOf(slipNumber));
		List<PostingJournal> pjList = createQuery.list();
		if(pjList != null && !pjList.isEmpty())
			return pjList.get(0);
		return null;
	}

	public void updatePostingJournal(PostingJournal pj) {
		Session session = this.sessionFactory.getCurrentSession();
		session.update(pj);
	}

	@SuppressWarnings("rawtypes")
	public int deletePostingJournalByPaymentId(Integer paymentId) {
		Session session = this.sessionFactory.getCurrentSession();
		Query createQuery = session.createQuery("delete from PostingJournal where paymentId = :paymentId");
		createQuery.setParameter("paymentId", Integer.valueOf(paymentId));
		return createQuery.executeUpdate();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PostingJournal findPostingJournalByPaymentId(Integer paymentId) {
		Session session = this.sessionFactory.getCurrentSession();
		Query createQuery = session.createQuery("select pj from PostingJournal pj where pj.paymentId = :paymentId");
		createQuery.setParameter("paymentId", Integer.valueOf(paymentId));
		List<PostingJournal> pjList = createQuery.list();
		if(pjList != null && !pjList.isEmpty())
			return pjList.get(0);
		return null;
	}

}
