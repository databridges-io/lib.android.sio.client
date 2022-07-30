/*
	DataBridges client library for Java targeting Android
	https://www.databridges.io/



	Copyright 2022 Optomate Technologies Private Limited.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	    http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package io.databridges.databridges_sio_java_client.utils;

public class systemEvents {
    public static final String SUBSCRIBE_SUCCESS="dbridges:subscribe.success";
    public static final String SUBSCRIBE_FAIL="dbridges:subscribe.fail";
    public static final String ONLINE="dbridges:channel.online";
    public static final String OFFLINE="dbridges:channel.offline";
    public static final String REMOVE="dbridges:channel.removed";
    public static final String UNSUBSCRIBE_SUCCESS="dbridges:unsubscribe.success";
    public static final String UNSUBSCRIBE_FAIL="dbridges:unsubscribe.fail";
    public static final String CONNECT_SUCCESS="dbridges:connect.success";
    public static final String CONNECT_FAIL="dbridges:connect.fail";
    public static final String  DISCONNECT_SUCCESS="dbridges:disconnect.success";
    public static final String DISCONNECT_FAIL="dbridges:disconnect.fail";
    public static final String RESUBSCRIBE_SUCCESS="dbridges:resubscribe.success";
    public static final String RESUBSCRIBE_FAIL="dbridges:resubscribe.fail";
    public static final String RECONNECT_SUCCESS="dbridges:reconnect.success";
    public static final String  RECONNECT_FAIL="dbridges:reconnect.fail";
    public static final String PARTICIPANT_JOINED="dbridges:participant.joined";
    public static final String PARTICIPANT_LFET="dbridges:participant.left";
    public static final String REGISTRATION_SUCCESS="dbridges:rpc.server.registration.success";
    public static final String REGISTRATION_FAIL="dbridges:rpc.server.registration.fail";
    public static final String SERVER_ONLINE="dbridges:rpc.server.online";
    public static final String SERVER_OFFLINE="dbridges:rpc.server.offline";
    public static final String UNREGISTRATION_SUCCESS="dbridges:rpc.server.unregistration.success";
    public static final String UNREGISTRATION_FAIL="dbridges:rpc.server.unregistration.fail";
    public static final String RPC_CONNECT_SUCCESS="dbridges:rpc.server.connect.success";
    public static final String  RPC_CONNECT_FAIL="dbridges:rpc.server.connect.fail";


    public static String[] nbd_connectSupportedEvents = {CONNECT_SUCCESS ,
            CONNECT_FAIL,
            RECONNECT_SUCCESS,
            RECONNECT_FAIL,
            DISCONNECT_SUCCESS,
            DISCONNECT_FAIL,
            ONLINE,
            OFFLINE,
            REMOVE,
            PARTICIPANT_JOINED,
            PARTICIPANT_LFET};

}
