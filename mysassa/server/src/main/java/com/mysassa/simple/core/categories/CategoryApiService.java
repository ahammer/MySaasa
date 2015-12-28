package com.mysassa.simple.core.categories;

import com.mysassa.simple.api.ApiError;
import com.mysassa.simple.api.ApiResult;
import com.mysassa.simple.api.ApiSuccess;
import com.mysassa.simple.core.blog.services.BlogService;
import com.mysassa.simple.core.website.model.Website;
import com.mysassa.simple.interfaces.IApiService;
import com.mysassa.simple.interfaces.annotations.ApiCall;
import com.mysassa.simple.interfaces.annotations.SimpleService;

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
		return new ApiError("Not Implemented");
		/*
		try {
			return new ApiSuccess(InventoryService.get().getProductCategories(Website.getCurrent().getOrganization()));
		} catch (Exception e) {
			return new ApiError(e);
		}
		*/
	}
}
