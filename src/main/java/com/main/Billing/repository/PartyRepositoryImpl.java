package com.main.Billing.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.main.Billing.entity.Party;

@Repository
@Transactional
public class PartyRepositoryImpl implements PartyRepository {

	@Autowired
	SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	public List<Party> findAllParty() {
		Session session = this.sessionFactory.getCurrentSession();
		List<Party> partyList = session.createQuery("from Party").list();
		return partyList;
	}

	public void createParty(Party party) {
		Session session = this.sessionFactory.getCurrentSession();
		session.save(party);
	}

	public void updateParty(Party party) {
		Session session = this.sessionFactory.getCurrentSession();
		session.update(party);	
	}

}
