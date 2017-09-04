package com.mysaasa.test.mocks;

import com.mysaasa.api.model.ApiError;
import com.mysaasa.api.model.ApiResult;
import com.mysaasa.api.model.ApiSuccess;
import com.mysaasa.interfaces.IApiService;
import com.mysaasa.interfaces.annotations.ApiCall;
import com.mysaasa.interfaces.annotations.SimpleService;

/**
 * Created by Adam on 9/3/2017.
 */
@SimpleService
public class TestService implements IApiService {
    @ApiCall
    public ApiResult<String> test() {
        return new ApiSuccess<>("test");
    }

    ;

    @ApiCall
    public ApiResult<Integer> addTwo(int a, int b) {
        return new ApiSuccess<>(a + b);
    }

    @ApiCall
    public ApiResult<?> throwsUp() {
        throw new RuntimeException("This is Exception");
    }

    @ApiCall
    public ApiResult<?> stringStub(String string) {
        return new ApiSuccess<>(string);
    }

    @ApiCall
    public ApiResult<?> throwsUpWithNullMessage() {
        return new ApiError(new RuntimeException((String) null));
    }

    public ApiResult<?> noAnnotation() {
        return new ApiSuccess<>("STUB");
    }
}
