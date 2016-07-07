package com.mysaasa.api.observables;

import com.mysaasa.api.AuthenticationManager;
import com.mysaasa.api.messages.LoginStateChanged;
import com.mysaasa.api.responses.RegisterGcmKeyResponse;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Adam on 3/26/2016.
 */
public class CreateAccountObservableBase extends StandardMySaasaObservable<com.mysaasa.api.responses.CreateUserResponse> {
    private AuthenticationManager authenticationManager;
    private final String username;
    private final String password;

    public CreateAccountObservableBase(AuthenticationManager authenticationManager, String username, String password) {
        super(authenticationManager.mySaasa, false);
        this.authenticationManager = authenticationManager;
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    protected Call<com.mysaasa.api.responses.CreateUserResponse> getNetworkCall() {
        return this.getMySaasa().getGateway().createUser(this.username, this.password);
    }

    @Override
    public boolean postResponse(com.mysaasa.api.responses.CreateUserResponse response) {
        getMySaasa().bus.post(new LoginStateChanged());
        PushIdGenerator generator = authenticationManager.pushIdGenerator;
        if (generator != null) {
            String pushId = generator.getPushId();
            System.out.println(pushId);
            if (pushId != null) {
                Call<RegisterGcmKeyResponse> call = authenticationManager.mySaasa.getGateway().registerGcmKey(pushId);
                try {
                    Response<RegisterGcmKeyResponse> registerResponse = call.execute();
                    return registerResponse.isSuccess();
                } catch (Exception e) {/*Do Nothing*/}
            }
        }
        return true;
    }
}
