package com.main.Billing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.main.Billing.entity.PostingJournal;
import com.main.Billing.repository.PostingJournalRepositoryImpl;
import com.main.Billing.utility.Utility;

@Service
public class PostingJournalService {

	@Autowired
	PostingJournalRepositoryImpl pjRepository;
	
	public void deletingPostingJournalForBill(int slipNumber) {
		int row = pjRepository.deletePostingJournalBySlipNumber(slipNumber);
		if(row > 0) {
			Utility.beep();
			//Utility.getAlert("Information", "Deleted !!", "Bill Deleted successfully!!!", null,AlertType.INFORMATION);
		}
	}

	public void createOrUpdatePostingJournalForBill(PostingJournal pj, boolean isUpdate) {
		if(isUpdate) {
			PostingJournal oldPostingJournal = pjRepository.findPostingJournalBySlipNumber(pj.getSlipNumber());
			if(oldPostingJournal != null) {
				pj.setPostingId(oldPostingJournal.getPostingId());
				pjRepository.updatePostingJournal(pj);
			} else {
				pjRepository.createPostingJournal(pj);
			}
		} else {
			pjRepository.createPostingJournal(pj);
		}
		Utility.beep();
		//Utility.getAlert("Information", isUpdate ? "Updated !!":"Inserted !!", isUpdate ? "Posting Journal Updated Sucessfully.": "Posting Journal inserted successfully!!!", null, AlertType.INFORMATION);
	}

	public void deletingPostingJournalForPayment(Integer paymentId) {
		int row = pjRepository.deletePostingJournalByPaymentId(paymentId);
		if(row > 0) {
			Utility.beep();
			//Utility.getAlert("Information", "Deleted !!", "Bill Deleted successfully!!!", null,AlertType.INFORMATION);
		}
	}

	public void createOrUpdatePostingJournalForPayment(PostingJournal pj, boolean isUpdate) {
		if(isUpdate) {
			PostingJournal oldPostingJournal = pjRepository.findPostingJournalByPaymentId(pj.getPaymentId());
			if(oldPostingJournal != null) {
				pj.setPostingId(oldPostingJournal.getPostingId());
				pjRepository.updatePostingJournal(pj);
			} else {
				pjRepository.createPostingJournal(pj);
			}
		} else {
			pjRepository.createPostingJournal(pj);
		}
		Utility.beep();
		
	}

}
