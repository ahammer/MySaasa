package com.mysaasa.core.media.services;

import com.mysaasa.api.ApiSuccess;
import com.mysaasa.interfaces.annotations.ApiCall;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.SimpleImpl;
import com.mysaasa.api.ApiResult;

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
