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

package io.databridges.databridges_sio_java_client.connection;

public class states {
 
    public static final String CONNECTED= "connected";
    public static final String ERROR= "connect_error";
    public static final String DISCONNECTED= "disconnected";
    public static final String RECONNECTING= "reconnecting";
    public static final String CONNECTING= "connecting";
    public static final String STATE_CHANGE= "state_change";
    public static final String RECONNECT_ERROR= "reconnect_error";
    public static final String RECONNECT_FAILED= "reconnect_failed";
    public static final String RECONNECTED= "reconnected";
    public static final String CONNECTION_BREAK= "connection_break";
    public static final String RTTPONG= "rttpong";
    public static final String RTTPING= "rttping";
}
