package com.mysaasa.development;

import com.mysaasa.api.ApiHelperService;
import com.mysaasa.api.ApiMapping;
import com.mysaasa.api.ApiParameter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Code generation utilities
 * Created by adam on 2014-10-16.
 */
public class CodeGen {
	/**
	 * There is a lot of boilerplate I use, and auto-generated code seems best suited for this,
	 *
	 * it's super rare, I'm just going to build it in engine for now to save me time.
	 * @return
	 */
	public static void generateRetrofitCode() {
		ApiHelperService service = ApiHelperService.get();
		for (String path : service.getPathMapping().keySet()) {
			ApiMapping mapping = service.getMapping(path);
			String function_name =
					"\n\n@FormUrlEncoded\n" +
					"@POST(\""+path+"\")\n" +
							"Call<"+
							upperCaseFirstChar(mapping.getMethod().getName())+"Response> "+mapping.getMethod().getName();
			String params = "(";
			int count = 0;
			for (ApiParameter param : mapping.getParameters()) {
				if (count > 0) {
					params += ", ";
				}
				params += "@Field(\""+param.getName()+"\")";
				String type = param.get_class().toString();

				if (param.get_class() == String.class) {
					type = "String";
				}
				params += type + " " + param.getName();
				count++;
			}
			params += ");";

			System.out.println(function_name + params);

		}
	}

	private static String upperCaseFirstChar(String name) {
		checkNotNull(name);
		String c1 = ""+name.charAt(0);
		return c1.toUpperCase()+name.substring(1);
	}

}
