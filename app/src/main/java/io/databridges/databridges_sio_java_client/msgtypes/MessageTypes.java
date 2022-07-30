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
package io.databridges.databridges_sio_java_client.msgtypes;

public class MessageTypes {
    public static final int SUBSCRIBE_TO_CHANNEL = 11;
    public static final int CONNECT_TO_CHANNEL= 12;
    public static final int UNSUBSCRIBE_DISCONNECT_FROM_CHANNEL= 13;
    public static final int PUBLISH_TO_CHANNEL= 16;
    public static final int SERVER_SUBSCRIBE_TO_CHANNEL= 111;
    public static final int SERVER_UNSUBSCRIBE_DISCONNECT_FROM_CHANNEL= 113;
    public static final int SERVER_PUBLISH_TO_CHANNEL= 116;
    public static final int SERVER_CHANNEL_SENDMSG= 117;
    public static final int LATENCY= 99;
    public static final int SYSTEM_MSG= 0;
    public static final int PARTICIPANT_JOIN= 17;
    public static final int PARTICIPANT_LEFT= 18;
    public static final int CF_CALL_RECEIVED= 44;
    public static final int CF_CALL= 44;
    public static final int CF_CALL_RESPONSE= 46;
    public static final int CF_CALL_TIMEOUT= 49;
    public static final int CF_RESPONSE_TRACKER= 48;
    public static final int CF_CALLEE_QUEUE_EXCEEDED= 50;
    public static final int REGISTER_RPC_SERVER= 51;
    public static final int UNREGISTER_RPC_SERVER= 52;
    public static final int CONNECT_TO_RPC_SERVER= 53;
    public static final int CALL_RPC_FUNCTION= 54;
    public static final int CALL_CHANNEL_RPC_FUNCTION= 55;
    public static final int RPC_CALL_RECEIVED= 54;
    public static final int RPC_CALL_RESPONSE= 56;
    public static final int RPC_CALL_TIMEOUT= 59;
    public static final int RPC_RESPONSE_TRACKER= 58;
    public static final int RPC_CALLEE_QUEUE_EXCEEDED= 60;
}
