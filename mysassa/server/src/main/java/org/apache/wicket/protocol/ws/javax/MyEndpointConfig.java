package org.apache.wicket.protocol.ws.javax;

/**
 * This is actually important. Something else uses reflection, and without access to the namespace it fails.
 *
 * I think it's WebSocket's in wicket 6 or something. So leave this here.
 *
 * Created by adam on 14-12-10.
 */
public class MyEndpointConfig extends WicketServerEndpointConfig {}
