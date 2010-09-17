package com.jayway.xml.jaxbscope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;

public class ScopeTest {
	@Test
	public void alternativeScopeMarshalling() throws Exception {
		Person person = new Person("Jan", "Kronquist");
		String xml = toXml(person);
		
		defaultScopeShouldNotContainLastName(person, xml);
		alternativeScopeShouldContainLastName(person, xml);
	}

	private String toXml(Person person) throws JAXBException {
		JAXBContext jaxb = makeAlternativeScopeJaxb();
		StringWriter writer = new StringWriter();
		jaxb.createMarshaller().marshal(person, writer);
		String xml = writer.toString();
		return xml;
	}

	private JAXBContext makeAlternativeScopeJaxb() throws JAXBException {
		Map<String, Object> jaxbConfig = new HashMap<String, Object>();
        jaxbConfig.put(JAXBRIContext.ANNOTATION_READER, new ScopedAnnotationReader("alternative"));

		return JAXBContextImpl.newInstance(new Class[] {Person.class}, jaxbConfig);
	}

	private JAXBContext makeDefaultScopeJaxb() throws JAXBException {
		Map<String, Object> jaxbConfig2 = new HashMap<String, Object>();
		return JAXBContext.newInstance(new Class[] {Person.class}, jaxbConfig2);
	}

	private void defaultScopeShouldNotContainLastName(Person person, String xml) throws JAXBException {
		JAXBContext jaxb = makeDefaultScopeJaxb();
		Person result = (Person) jaxb.createUnmarshaller().unmarshal(new StringReader(xml));
		assertEquals(person.getFirstName(), result.getFirstName());
		assertNull(result.getLastName());
	}

	private void alternativeScopeShouldContainLastName(Person person, String xml) throws JAXBException {
		JAXBContext jaxb = makeAlternativeScopeJaxb();
		Person result = (Person) jaxb.createUnmarshaller().unmarshal(new StringReader(xml));
		assertEquals(person.getFirstName(), result.getFirstName());
		assertEquals(person.getLastName(), result.getLastName());
	}
}
