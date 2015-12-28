package com.mysassa.simple.core.media.services;

import com.mysassa.simple.api.ApiResult;
import com.mysassa.simple.interfaces.IApiService;
import com.mysassa.simple.interfaces.annotations.ApiCall;

/**
 * Created by Adam on 3/26/14.
 */
public abstract class MediaApiService implements IApiService {
	@ApiCall
	public abstract ApiResult getAllMedia();
}
