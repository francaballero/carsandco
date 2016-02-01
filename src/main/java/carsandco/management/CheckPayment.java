package carsandco.management;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import carsandco.tools.JsonHandler;
import carsandco.tools.MongoClass;
import de.uniko.digicom.carsandco.messages.PaymentNotification;

public class CheckPayment implements JavaDelegate {
	//TODO: Maybe external payment process due to triggering and currency issues of waiting message event?
	
	public void execute(DelegateExecution execution) throws Exception {
		String paymentNoteJson = (String) execution.getVariable("paymentNote");
		PaymentNotification paymentNote = JsonHandler.toObject(paymentNoteJson, PaymentNotification.class);
		String contractID = (String) execution.getVariable("contractID");
		String invoiceID = (String) execution.getVariable("invoiceID");

//Check if payment notification purpose equals current invoice purpose and update invoice with payed = true or false
		if(paymentNote.getPurpose().equals(contractID)){
			MongoClass.addFieldWithValueToDoc("invoices", invoiceID , "payed", "true");
			System.out.println("Invoice with ID: " + invoiceID + " and purpose: " + paymentNote.getPurpose() + " got payed correctly.");
		} else {
			MongoClass.addFieldWithValueToDoc("invoices", invoiceID , "payed", "false");
			System.err.println("The purpose: " + paymentNote.getPurpose() + " of the payment notification could not be resolved to existing invoices.");
		}
				
	}
	

}
