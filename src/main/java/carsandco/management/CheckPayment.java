package carsandco.management;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import carsandco.tools.JsonHandler;
import carsandco.tools.MongoClass;
import de.uniko.digicom.carsandco.messages.PaymentNotification;

public class CheckPayment implements JavaDelegate {
	private static final Logger LOGGER = Logger.getLogger(CheckPayment.class);

	public void execute(DelegateExecution execution) throws Exception {
		String paymentNoteJson = (String) execution.getVariable("paymentNote");
		PaymentNotification paymentNote = JsonHandler.toObject(paymentNoteJson, PaymentNotification.class);
		String contractID = (String) execution.getVariable("contractID");
		String invoiceID = (String) execution.getVariable("invoiceID");

		// Check if payment notification purpose equals current invoice purpose
		// and update invoice with payed = true or false
		try {
			LOGGER.info("Checking received payment notification...");
			if (paymentNote.getPurpose().equals(contractID)) {
				MongoClass.addFieldWithValueToDoc("invoices", invoiceID, "payed", "true");
				LOGGER.info("Invoice with ID: " + invoiceID + " and purpose: " + paymentNote.getPurpose()
						+ " got payed correctly.");
				LOGGER.info("PROCESS WITH ID " + execution.getProcessInstanceId() + " AND BUSINESSKEY "
						+ execution.getBusinessKey() + " FINISHED SUCCESSFULLY.");
			} else {
				MongoClass.addFieldWithValueToDoc("invoices", invoiceID, "payed", "false");
				LOGGER.error("The purpose: " + paymentNote.getPurpose()
						+ " of the payment notification could not be resolved to existing invoices.");
			}
		} catch (Exception e) {
			LOGGER.error("Error checking payment notification.");
		}
		MongoClass.closeDatabaseConnection();
	}

}
