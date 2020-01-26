package com.test.client.hapi.fhirclient;


import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Patient.ContactComponent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;

@SpringBootApplication
public class FhirClientApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FhirClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		FhirContext ctx = FhirContext.forR4();
		
		String serverBase = "http://localhost:8080/hapi-fhir-jpaserver/fhir";

		IGenericClient client = ctx.newRestfulGenericClient(serverBase);

		// Perform a search
		Bundle results = client
		      .search()
		      .forResource(Patient.class)
		      .where(Patient.FAMILY.matches().value("duck"))
		      .returnBundle(Bundle.class)
		      .execute();

		System.out.println("Found " + results.getEntry().size() + " patients named 'duck'");
		
		generatePatientJson(ctx);
	}
	
	private void generatePatientJson(FhirContext ctx ) {
		Patient patient = new Patient();

		// FIRST AND LAST NAME
		patient.addName().setFamily("Duck").addGiven("Donald");
		// SOCIAL SECURITY NUMBER
		// https://www.hl7.org/FHIR/datatypes.html#Identifier
		// https://www.hl7.org/FHIR/identifier-registry.html

		patient.addIdentifier()
		.setType(new CodeableConcept().addCoding(
		 new Coding().setCode("SB").setSystem("http://hl7.org/fhir/v2/0203")
		))
		.setSystem("http://hl7.org/fhir/sid/us-ssn")
		.setValue("123456789");

		// GENDER
		patient.setGender(AdministrativeGender.FEMALE);

		// ADDRESS INFORMATION
		patient.addAddress()
		.setUse(AddressUse.HOME)
		.addLine("Street name, number, direction & P.O. Box etc.")
		.setCity("Name of city, town etc.")
		.setState("Sub-unit of country (abbreviations ok)")
		.setPostalCode("Postal/ZIP code for area");

		// CONTACT https://www.hl7.org/fhir/datatypes-examples.html#ContactPoint
		patient.addTelecom()
		.setSystem(ContactPointSystem.PHONE)
		.setValue("(555) 675 5745");

		patient.addTelecom()
		.setSystem(ContactPointSystem.PHONE)
		.setValue("(415) 675 5745");

		patient.addTelecom()
		.setSystem(ContactPointSystem.EMAIL)
		.setValue("test@test.com");

		// EMERGENCY CONTACT https://www.hl7.org/FHIR/patient-definitions.html#Patient.contact
		ContactComponent emergencyContact = new ContactComponent();

		emergencyContact.addTelecom().setSystem(ContactPointSystem.PHONE)
		.setValue("(111) 675 5745");

		// Relationship to patient
		emergencyContact
		.addRelationship()
		.addCoding()
		 .setSystem("http://hl7.org/fhir/ValueSet/v2-0131")
		 .setCode("C");

		emergencyContact.setName(
				new HumanName().setFamily("Duck Emergency contact").addGiven("Duke")
		);

		patient.addContact(emergencyContact);

		// Encode to JSON
		IParser jsonParser = ctx.newJsonParser();
		jsonParser.setPrettyPrint(true);
		String encoded = jsonParser.encodeResourceToString(patient);
		System.out.println(encoded);
	}

}
