package com.mysaasa.api;

import com.mysaasa.Simple;
import com.mysaasa.interfaces.annotations.ApiCall;
import com.mysaasa.interfaces.IApiService;
import com.mysaasa.interfaces.annotations.SimpleService;
import org.apache.wicket.request.Request;
import org.apache.wicket.util.string.StringValue;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This service registers Api Methods and such
 *
 * It records Mappings, so Class:Function + Parameters
 *
 * When you contact the API it expects /Class/Function?Arg1=&Arg2=
 *
 * Things need to be exact and in order, and in the correct format
 *
 * If they are incorrect, the Api will respond with errors.
 *
 * It expects the same arguments, in the same format, in the same order.
 *
 * If you pass ASD to a int, it will fail with a error
 * if you pass second before first, it will fail
 * If the expected arguments != query arguments, it will fail
 *
 * there is no exceptions, the API will be accessed by rules, they are simple rules that
 * let it be easily expandable
 *
 * Created by Adam on 3/12/14.
 */
@SimpleService
public class ApiHelperService {
	private Map<String, ApiMapping> pathMapping = new HashMap<String, ApiMapping>();

	public ApiHelperService() {}

	public static ApiHelperService get() {
		return Simple.get().getInjector().getProvider(ApiHelperService.class).get();
	}

	public ApiMapping getMapping(String s) {
		return getPathMapping().get(s);
	}

	/**
	 * Gets the mounted points
	 *
	 * They are stored in a hash map, so this is just a array of the String keys.
	 *
	 * @return Array of Paths,
	 * ServiceName/Function....
	 * e.g. (Incomplete list
	 *
	 * UserApiService/BlogTest
	 * UserApiService/logout
	 * UserApiService/WebsiteTest
	 * UserApiService/postToBlog
	 * UserApiService/getSession
	 * UserApiService/loginUser
	 * UserApiService/Media
	 * MediaApiServiceImpl/getAllMedia
	 * UserApiService/createUser
	 */
	public String[] getPaths() {
		String[] result = new String[getPathMapping().keySet().size()];
		getPathMapping().keySet().toArray(result);
		return result;
	}

	/**
	 * If a service is a IApiService it needs extra processing to bind it to the API so we can find it later
	 *
	 * @param service
	 */
	public void bindApiService(IApiService service) {
		for (Method method : service.getClass().getMethods()) {
			if (method.isAnnotationPresent(ApiCall.class)) {
				registerMethod(method);
			}
		}
	}

	public boolean isApiPathBound(String path) {
		return getPathMapping().containsKey(path);
	}

	/**
	 * Registers a method to the pathMapping hash.
	 *
	 * @param method a java method we are mounting, it should have the @ApiCall annotation
	 */
	void registerMethod(Method method) {
		if (!method.isAnnotationPresent(ApiCall.class))
			throw new IllegalArgumentException("Not a ApiCall function");
		String className = method.getDeclaringClass().getSimpleName();
		String methodName = method.getName();
		getPathMapping().put(className + "/" + methodName, new ApiMapping(method));
	}

	public ApiRequest getApiRequest(Request request) {
		String path = request.getClientUrl().getPath().substring(1);
		if (!getPathMapping().containsKey(path))
			return new ApiRequestPreconditionFail("This API Path does not exist: " + path);
		ApiMapping mapping = getPathMapping().get(path);

		//Check arguments
		if (!hasCorrectParameterCount(request, mapping)) {
			return processApiError(mapping, request, new IllegalArgumentException("Wrong number of parameters"));
		}

		if (isNoArgsRequest(request, mapping))
			return new ApiRequest(mapping);
		else
			return processHasArgsRequest(request, mapping);
	}

	private ApiRequest processHasArgsRequest(Request request, ApiMapping mapping) {
		Object[] args = new Object[mapping.getParameters().size()];
		//("Args: "+args.length);
		int pos = 0;
		try {
			//I think this is dead
			//Set<String> parameters = buildParamSet(request);
			// /if (parameters.size() != args.length) {
			//	throw new IllegalArgumentException("Incorrect number of arguments " + parameters);
			//}

			for (ApiParameter apiParameter : mapping.getParameters()) {
				args[pos] = castArgumentStringToObject(apiParameter, request.getPostParameters().getParameterValue(apiParameter.getName()));
				pos++;
			}
		} catch (Exception e) {
			return processApiError(mapping, request, e);
		}
		//("Created a set of args: "+args.length);
		return new ApiRequest(mapping, args);
	}

	private boolean isNoArgsRequest(Request request, ApiMapping mapping) {
		return request.getPostParameters().getParameterNames().size() == mapping.getParameters().size() && request.getPostParameters().getParameterNames().size() == 0;
	}

	private Set<String> buildParamSet(Request request) {
		Iterator<String> itr = request.getPostParameters().getParameterNames().iterator();
		Set<String> parameters = new HashSet<String>();

		do {
			String input_name = itr.next();
			parameters.add(input_name);
		} while (itr.hasNext());
		return parameters;
	}

	private ApiRequest processApiError(ApiMapping apiMapping, Request request, Exception e) {
		Set<String> params = buildParamSet(request);
		Set<String> paramsAndValues = new HashSet<String>();
		for (String param : params) {
			paramsAndValues.add(param + " = " + request.getPostParameters().getParameterValue(param).toString());
		}

		String message = e.getMessage();
		if (message == null)
			message = "N/A";

		String result = "Found ApiMapping = " + apiMapping.toString() + "\n Input: " + paramsAndValues.toString() + "\n Exception Message: " + message;

		return new ApiRequestPreconditionFail(result);
	}

	/**
	 * Checks to see if a particular Request object has enough parameters
	 * @param request
	 * @param mapping
	 * @return
	 */
	private boolean hasCorrectParameterCount(Request request, ApiMapping mapping) {
		return request.getPostParameters().getParameterNames().size() == mapping.getParameters().size();
	}

	private Object castArgumentStringToObject(ApiParameter apiParameter, StringValue parameterValue) {
		if (apiParameter.get_class().equals(String.class))
			return parameterValue.toString();
		if (apiParameter.get_class().isPrimitive())
			return Integer.parseInt(parameterValue.toString());
		return 0;
	}

	public Map<String, ApiMapping> getPathMapping() {
		return pathMapping;
	}
}
