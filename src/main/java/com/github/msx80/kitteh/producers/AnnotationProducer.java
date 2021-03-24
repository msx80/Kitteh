package com.github.msx80.kitteh.producers;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.github.msx80.kitteh.DocumentProducer;
import com.github.msx80.kitteh.Redirection;
import com.github.msx80.kitteh.Request;
import com.github.msx80.kitteh.Response;
import com.github.msx80.kitteh.producers.annotations.AnnotationProducerException;
import com.github.msx80.kitteh.producers.annotations.Get;
import com.github.msx80.kitteh.producers.annotations.NamedArg;
import com.github.msx80.kitteh.producers.annotations.ParamMapper;
import com.github.msx80.kitteh.producers.annotations.Post;
import com.github.msx80.kitteh.producers.annotations.ResultHandler;

public class AnnotationProducer implements DocumentProducer {

	Object instance;
	private DocumentProducer fallback;
	
	private Map<Class<?>, ParamMapper<?>> classMapper = new HashMap<>();
	private Map<Class<?>, ResultHandler<?>> resultHandler = new HashMap<>();
	private ResultHandler<Exception> exceptionHandler = null;
	
	public AnnotationProducer(Object instance, DocumentProducer fallback) {
		this.instance = instance;
		this.fallback = fallback;
		
		addParamMapper(String.class, new ParamMapper<String>() {
			@Override
			public String parseString(String val) {
				return val;
			}
		});
		
		addParamMapper(Integer.class, new ParamMapper<Integer>() {
			@Override
			public Integer parseString(String val) {
				return Integer.parseInt(val);
			}
		});
		
		addParamMapper(Long.class, new ParamMapper<Long>() {
			@Override
			public Long parseString(String val) {
				return Long.parseLong(val);
			}
		});
		
		addParamMapper(Double.class, new ParamMapper<Double>() {
			@Override
			public Double parseString(String val) {
				return Double.parseDouble(val);
			}
		});
		
		addResultHandler(String.class, new ResultHandler<String>() {
			@Override
			public void handleResult(String result, Response response) {
				response.setContent(result);
			}
		});
		
		addResultHandler(byte[].class, new ResultHandler<byte[]>() {
			@Override
			public void handleResult(byte[] result, Response response) {
				response.setContent(result);
			}
		});
		
		addResultHandler(InputStream.class, new ResultHandler<InputStream>() {
			@Override
			public void handleResult(InputStream result, Response response) throws Exception {
				response.setContent(result);
			}
		});
		
	}
	
	public AnnotationProducer(Object instance) {
		this(instance, DocumentProducer.ERR_404_PRODUCER);
		
	}
	
	public <T> AnnotationProducer addParamMapper(Class<T> cls, ParamMapper<T> mapper)
	{
		classMapper.put(cls, mapper);
		return this;
	}
	
	public <T> AnnotationProducer addResultHandler(Class<T> cls, ResultHandler<T> handler)
	{
		resultHandler.put(cls, handler);
		return this;
	}
	
	public AnnotationProducer setExceptionHandler(ResultHandler<Exception> handler)
	{
		exceptionHandler = handler;
		return this;
	}
	
	
	
	@Override
	public void produceDocument(Request request, Response response) throws Exception, Redirection {

		String docName = request.getPageName();
		
		
		// check if instance has a method equals to request.getDocumentName
		// map params, call method
		
		for(Method m : instance.getClass().getMethods())
		{
			if(m.getName().equals(docName))
			{
				if( m.isAnnotationPresent(request.getMethod().getRelatedAnnotation()))
				{
					//this is it
					doCallMethod(request, response, m);
					return;
				}
			}
		}
		fallback.produceDocument(request, response);
	}

	private void doCallMethod(Request request, Response response, Method m) throws Exception {
		try
		{
			callMethod(m, request, response);
		}
		catch(Exception e)
		{
			if(this.exceptionHandler != null)
			{
				exceptionHandler.handleResult(e, response);
			}
			else
			{
				throw e;
			}
		}
	}



	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void callMethod(Method m, Request request, Response response) throws Exception {
		
		Class<?>[] paramClass = m.getParameterTypes();
		Annotation[][] annotations = m.getParameterAnnotations();
		Object[] values = new Object[paramClass.length];
		for (int i = 0; i < values.length; i++) {
			try
			{
				values[i] = handleParam(paramClass[i],annotations[i], request, response);
			}
			catch(Exception e)
			{
				throw new AnnotationProducerException("Error while handling parameter #"+i+" of "+m.getName()+": "+e.getMessage(), e);
			}
		}
		
		Object ret;
		try {
			ret = m.invoke(instance, values);
		} catch (InvocationTargetException e) {
			if(e.getTargetException() instanceof Exception) throw (Exception)e.getTargetException();
			throw e;
			
			
		}
		if(m.getReturnType() != void.class)
		{
			// returned a value, we must pass it along to response
			Class<?> oc = ret.getClass();

			ResultHandler rh = resultHandler.get(oc);
			if(rh != null)
			{
				rh.handleResult(ret, response);
			}
			else
			{
				throw new AnnotationProducerException("No result handler found for class "+oc.getName()+". Add one with addResultHandler().");
			}
		}
	}

	private Object handleParam(Class<?> c, Annotation[] annotations,  Request request, Response response) {
		
		if(c == Request.class)
		{
			return request;
		}
		else if(c == Response.class)
		{
			return response;
		}
		
		NamedArg g= null;
		for (Annotation a : annotations) {
			if(a instanceof NamedArg)
			{
				g = (NamedArg) a;
				break;
			}
		}
		if(g == null) throw new AnnotationProducerException("Parameter is not named. Annotate it with @NamedArg");
		
		String name = g.value();
		String value = request.getParameter(name);
		if(value == null) return null;
		
	
		ParamMapper<?> mapper = classMapper.get(c);
		if(mapper != null)
		{
			return mapper.parseString(value);
		}
	
		
		throw new AnnotationProducerException("No parameter mapper found for class "+c.getName()+". Add one with addParameterMapper().");
	}

	
}
