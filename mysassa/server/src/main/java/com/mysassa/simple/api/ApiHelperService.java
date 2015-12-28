package com.mysassa.simple.api;

import com.mysassa.simple.Simple;
import com.mysassa.simple.interfaces.annotations.ApiCall;
import com.mysassa.simple.interfaces.IApiService;
import com.mysassa.simple.interfaces.annotations.SimpleService;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.util.string.StringValue;

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
	public static ApiHelperService get() {
		return Simple.get().getInjector().getProvider(ApiHelperService.class).get();
	}

	public ApiHelperService() {}

	private Map<String, ApiMapping> pathMapping = new HashMap<String, ApiMapping>();

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
	private void registerMethod(Method method) {
		if (!method.isAnnotationPresent(ApiCall.class))
			throw new IllegalArgumentException("Not a ApiCall function");
		String className = method.getDeclaringClass().getSimpleName();
		String methodName = method.getName();
		getPathMapping().put(className + "/" + methodName, new ApiMapping(method));
	}

	public ApiRequest getApiRequest(String path, Request request) {
		if (!getPathMapping().containsKey(path))
			throw new IndexOutOfBoundsException("Attempt to access void space in the hash");
		ApiMapping mapping = getPathMapping().get(path);

		if (request.getPostParameters().getParameterNames().size() != mapping.getParameters().size()) {

			return new ApiRequestPreconditionFail("Incorrect number of arguments\ncurrent mapping: " + mapping + " : " + mapping.getParameters().size() + " != " + request.getPostParameters().getParameterNames().size());
		}
		if (request.getPostParameters().getParameterNames().size() == mapping.getParameters().size() && request.getPostParameters().getParameterNames().size() == 0) {
			return new ApiRequest(mapping);
		} else {
			Iterator<String> itr = request.getPostParameters().getParameterNames().iterator();
			Object[] args = new Object[mapping.getParameters().size()];
			//("Args: "+args.length);
			int pos = 0;
			try {
				Set<String> parameters = new HashSet<String>();

				do {
					String input_name = itr.next();
					parameters.add(input_name);
				} while (itr.hasNext());

				if (parameters.size() != args.length) {
					throw new IllegalArgumentException("Incorrect number of arguments " + parameters);
				}

				for (ApiParameter apiParameter : mapping.getParameters()) {
					args[pos] = parseQueryStringObject(request.getPostParameters(), apiParameter, apiParameter.getName());
					pos++;
				}
			} catch (NumberFormatException e) {
				return new ApiRequestPreconditionFail("Expected a number but instead had a parse error " + e.getLocalizedMessage());
			}
			//("Created a set of args: "+args.length);
			return new ApiRequest(mapping, args);

		}
	}

	private Object parseQueryStringObject(IRequestParameters queryParameters, ApiParameter p, String qName) {
		StringValue queryStringValue = queryParameters.getParameterValue(qName);
		if (p.get_class().equals(String.class))
			return queryStringValue.toString();
		if (p.get_class().isPrimitive())
			return Integer.parseInt(queryStringValue.toString());
		return 0;
	}

	public Map<String, ApiMapping> getPathMapping() {
		return pathMapping;
	}

	public void setPathMapping(Map<String, ApiMapping> pathMapping) {
		this.pathMapping = pathMapping;
	}
}
