package com.jayway.xml.jaxbscope;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.xml.bind.annotation.XmlElement;

@Retention(RUNTIME) @Target({FIELD, METHOD})
public @interface XmlScope {
	XmlElement element();
	String[] scope();
}
