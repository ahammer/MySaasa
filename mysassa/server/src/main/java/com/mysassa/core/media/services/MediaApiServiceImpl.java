package com.mysassa.core.media.services;

import com.mysassa.api.ApiSuccess;
import com.mysassa.interfaces.annotations.ApiCall;
import com.mysassa.interfaces.annotations.SimpleService;
import com.mysassa.SimpleImpl;
import com.mysassa.api.ApiResult;

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
