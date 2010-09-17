package com.jayway.xml.jaxbscope;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.xml.bind.annotation.XmlElement;

import com.sun.xml.bind.v2.model.annotation.AbstractInlineAnnotationReaderImpl;
import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import com.sun.xml.bind.v2.model.core.ErrorHandler;

public class ScopedAnnotationReader extends AbstractInlineAnnotationReaderImpl<Type,Class,Field,Method>
    implements RuntimeAnnotationReader {

    private final String scope;
    private final RuntimeAnnotationReader delegate = new RuntimeInlineAnnotationReader();

	public ScopedAnnotationReader(String scope) {
		this.scope = scope;
	}

	public <A extends Annotation> A getFieldAnnotation(Class<A> annotation, Field field, Locatable srcPos) {
		A scopedAnnotation = getScopedAnnotation(annotation, field);
		if (scopedAnnotation != null) {
			return scopedAnnotation;
		}
        return delegate.getFieldAnnotation(annotation, field, srcPos);
    }

    public boolean hasFieldAnnotation(Class<? extends Annotation> annotationType, Field field) {
        return delegate.hasFieldAnnotation(annotationType, field) || getScopedAnnotation(annotationType, field) != null;
    }

    private <A extends Annotation> A getScopedAnnotation(Class<A> annotationType, Field field) {
    	XmlScope xmlScope = field.getAnnotation(XmlScope.class);
    	if (xmlScope != null && isInScope(xmlScope.scope())) {
    		if (annotationType == XmlElement.class) {
    			return (A) xmlScope.element();
    		}
    		// TODO: add support for other annotations
    	}
    	return null;
	}

    public Annotation[] getAllFieldAnnotations(Field field, Locatable srcPos) {
        return processScope( delegate.getAllFieldAnnotations(field, srcPos));
    }

	private Annotation[] processScope(Annotation[] r) {
		for( int i=0; i<r.length; i++ ) {
            if (r[i] instanceof XmlScope) {
            	 XmlScope xmlScope = (XmlScope)r[i];
            	 if (isInScope(xmlScope.scope())) {
            		 r[i] = xmlScope.element();
            	 }
            }
        }
		return r;
	}

	private boolean isInScope(String[] scopes) {
		for (String scope : scopes) {
			if (this.scope.equals(scope)) {
				return true;
			}
		}
		return false;
	}

	public Annotation[] getAllMethodAnnotations(Method arg0, Locatable arg1) {
		return delegate.getAllMethodAnnotations(arg0, arg1);
	}

	public <A extends Annotation> A getClassAnnotation(Class<A> arg0, Class arg1, Locatable arg2) {
		return delegate.getClassAnnotation(arg0, arg1, arg2);
	}

	public Type[] getClassArrayValue(Annotation arg0, String arg1) {
		return delegate.getClassArrayValue(arg0, arg1);
	}

	public Type getClassValue(Annotation arg0, String arg1) {
		return delegate.getClassValue(arg0, arg1);
	}

	// TODO: add support for scoped annotations
	public <A extends Annotation> A getMethodAnnotation(Class<A> arg0, Method arg1, Locatable arg2) {
		return delegate.getMethodAnnotation(arg0, arg1, arg2);
	}

	// TODO: add support for scoped annotations
	public <A extends Annotation> A getMethodParameterAnnotation(Class<A> arg0, Method arg1, int arg2, Locatable arg3) {
		return delegate.getMethodParameterAnnotation(arg0, arg1, arg2, arg3);
	}

	public <A extends Annotation> A getPackageAnnotation(Class<A> arg0, Class arg1, Locatable arg2) {
		return delegate.getPackageAnnotation(arg0, arg1, arg2);
	}

	public boolean hasClassAnnotation(Class arg0, Class<? extends Annotation> arg1) {
		return delegate.hasClassAnnotation(arg0, arg1);
	}

	// TODO: add support for scoped annotations
	public boolean hasMethodAnnotation(Class<? extends Annotation> arg0, Method arg1) {
		return delegate.hasMethodAnnotation(arg0, arg1);
	}

	// TODO: add support for scoped annotations
	public boolean hasMethodAnnotation(Class<? extends Annotation> arg0, String arg1, Method arg2, Method arg3,
			Locatable arg4) {
		return delegate.hasMethodAnnotation(arg0, arg1, arg2, arg3, arg4);
	}

	public void setErrorHandler(ErrorHandler arg0) {
		delegate.setErrorHandler(arg0);
	}

    protected String fullName(Method m) {
        return m.getDeclaringClass().getName()+'#'+m.getName();
    }
}
