package com.mysassa.core.categories;

import com.mysassa.api.ApiResult;
import com.mysassa.api.ApiSuccess;
import com.mysassa.core.website.model.Website;
import com.mysassa.interfaces.annotations.ApiCall;
import com.mysassa.interfaces.annotations.SimpleService;
import com.mysassa.api.ApiError;
import com.mysassa.core.blog.services.BlogService;
import com.mysassa.interfaces.IApiService;

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
