package com.mysassa.development;

import com.mysassa.api.ApiHelperService;
import com.mysassa.api.ApiMapping;
import com.mysassa.api.ApiParameter;

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
	public static void generateJavaApiFunctions() {
		generateJavaApiEnum();
		ApiHelperService service = ApiHelperService.get();
		for (String path : service.getPathMapping().keySet()) {
			ApiMapping mapping = service.getMapping(path);
			String function_name = path.replace("/", "_");//Let's use ! because auto-gen, prevents people from touching
			String params = "(";
			int count = 0;
			for (ApiParameter param : mapping.getParameters()) {
				if (count > 0) {
					params += ", ";
				}
				String type = param.get_class().toString();

				if (param.get_class() == String.class) {
					type = "String";
				}
				params += type + " " + param.getName();
				count++;
			}
			params += ")";

			String code = "        String path = \"/" + path + "\";\n" + "        HttpPost post = new HttpPost(domain  + path);\n";

			if (mapping.getParameters().size() > 0) {
				code += "        List<NameValuePair> formparams = new ArrayList<NameValuePair>();\n";

				for (ApiParameter param : mapping.getParameters()) {
					if (param.get_class() == String.class) {
						code += "formparams.add(new BasicNameValuePair(\"" + param.getName() + "\", " + param.getName() + ".trim()));\n";
					} else {
						code += "formparams.add(new BasicNameValuePair(\"" + param.getName() + "\", String.valueOf(" + param.getName() + ")));\n";
					}
				}

				code += "        UrlEncodedFormEntity entity = null;\n" + "        try {\n" + "            entity = new UrlEncodedFormEntity(formparams);\n" + "        } catch (UnsupportedEncodingException e) {\n" + "            throw new RuntimeException(\"Unsupported Encoding!!\");\n" + "        }\n" + "        post.setEntity(entity);\n";
			}
			code += "        HttpResponse response = httpClient.execute(post);\n" + "        String responseBody = EntityUtils.toString(response.getEntity());\n" + "        System.out.println(responseBody);\n" + "        SimpleResponse simpleResponse = SimpleResponse.create(parser.parse(responseBody).getAsJsonObject(), API_FUNCTIONS." + path.replace("/", "_") + ");\n" + "\n" + "        return simpleResponse;\n";

			System.out.println("public SimpleResponse " + function_name + params + " throws IOException, NotAuthorizedException ");
			System.out.println("{");
			System.out.println(code);
			System.out.println("}");

		}
	}

	public static void generateJavaApiEnum() {
		ApiHelperService service = ApiHelperService.get();
		String output = "public static  enum API_FUNCTIONS {";
		int count = 0;
		for (String path : service.getPathMapping().keySet()) {
			if (count > 0)
				output += ", ";
			output += path.replace("/", "_");
			count++;
		}
		output += "};";
		System.out.println(output);

	}
}
