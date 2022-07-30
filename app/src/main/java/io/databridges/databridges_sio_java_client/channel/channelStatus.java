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

package io.databridges.databridges_sio_java_client.channel;

public class channelStatus {
    
    public static final String SUBSCRIPTION_INITIATED= "subscription_initiated";
    public static final String SUBSCRIPTION_PENDING= "subscription_pending";
    public static final String SUBSCRIPTION_ACCEPTED= "subscription_accepted";
    public static final String SUBSCRIPTION_ERROR= "subscription_error";
    public static final String CONNECTION_INITIATED= "connection_initiated";
    public static final String CONNECTION_PENDING= "connection_pending";
    public static final String CONNECTION_ACCEPTED= "connection_accepted";
    public static final String CONNECTION_ERROR= "connection_error";
    public static final String UNSUBSCRIBE_INITIATED= "unsubscribe_initiated";
    public static final String UNSUBSCRIBE_ACCEPTED= "unsubscribe_accepted";
    public static final String UNSUBSCRIBE_ERROR= "unsubscribe_error";
    public static final String DISCONNECT_INITIATED= "disconnect_initiated";
    public static final String DISCONNECT_ACCEPTED= "disconnect_accepted";
    public static final String DISCONNECT_ERROR= "disconnect_error";
}
