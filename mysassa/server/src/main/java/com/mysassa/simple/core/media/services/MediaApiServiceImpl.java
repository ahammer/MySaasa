package com.mysassa.simple.core.media.services;

import com.mysassa.simple.SimpleImpl;
import com.mysassa.simple.interfaces.annotations.ApiCall;
import com.mysassa.simple.interfaces.annotations.SimpleService;
import com.mysassa.simple.api.ApiResult;
import com.mysassa.simple.api.ApiSuccess;

/**
 * Created by Adam on 3/15/14.
 */
@SimpleService
public class MediaApiServiceImpl extends MediaApiService {

	@Override
	@ApiCall
	public ApiResult getAllMedia() {
		return new ApiSuccess(SimpleImpl.get().getInjector().getProvider(MediaService.class).get().getMedia());
	}

}
