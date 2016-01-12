package com.mysassa.core.media.services;

import com.mysassa.interfaces.annotations.ApiCall;
import com.mysassa.api.ApiResult;
import com.mysassa.interfaces.IApiService;

/**
 * Created by Adam on 3/26/14.
 */
public abstract class MediaApiService implements IApiService {
	@ApiCall
	public abstract ApiResult getAllMedia();
}
