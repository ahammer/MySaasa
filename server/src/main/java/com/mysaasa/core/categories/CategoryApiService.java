package com.mysaasa.core.categories;

import com.mysaasa.api.model.ApiResult;
import com.mysaasa.api.model.ApiSuccess;
import com.mysaasa.core.website.model.Website;
import com.mysaasa.interfaces.annotations.ApiCall;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.api.model.ApiError;
import com.mysaasa.core.blog.services.BlogService;
import com.mysaasa.interfaces.IApiService;

/**
 * Created by adam on 14-12-26.
 */
@SimpleService

public class CategoryApiService implements IApiService {

	@ApiCall
	public ApiResult getBlogCategories() {
		try {
			return new ApiSuccess(BlogService.get().getBlogCategories(Website.getCurrent().getOrganization()));
		} catch (Exception e) {
			return new ApiError(e);
		}
	}

	@ApiCall
	public ApiResult getProductCategories() {
		return new ApiError(new RuntimeException("Not Implemented"));
		/* try { return new ApiSuccess(InventoryService.getInstance().getProductCategories(Website.getCurrent().getOrganization())); } catch (Exception e) { return new ApiError(e); } */
	}
}
