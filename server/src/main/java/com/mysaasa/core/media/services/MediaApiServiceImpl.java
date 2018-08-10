package com.mysaasa.core.media.services;

import com.mysaasa.MySaasa;
import com.mysaasa.api.model.ApiSuccess;
import com.mysaasa.interfaces.annotations.ApiCall;
import com.mysaasa.interfaces.annotations.SimpleService;
import com.mysaasa.api.model.ApiResult;

/**
 * Created by Adam on 3/15/14.
 */
@SimpleService
public class MediaApiServiceImpl extends MediaApiService {

	@Override
	@ApiCall
	public ApiResult getAllMedia() {
		return new ApiSuccess(MySaasa.getInstance().getInjector().getProvider(MediaService.class).get().getMedia());
	}

}
