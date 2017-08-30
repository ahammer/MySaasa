package com.mysaasa.core.media.services;

import com.mysaasa.interfaces.annotations.ApiCall;
import com.mysaasa.api.model.ApiResult;
import com.mysaasa.interfaces.IApiService;

/**
 * Created by Adam on 3/26/14.
 */
public abstract class MediaApiService implements IApiService {
	@ApiCall
	public abstract ApiResult getAllMedia();
}
