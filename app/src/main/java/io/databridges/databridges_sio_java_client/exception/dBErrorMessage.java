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

package io.databridges.databridges_sio_java_client.exception;

import java.util.HashMap;
import java.util.Map;

//version: 20220311

public class dBErrorMessage {
    public static final Map<String, int[]> errMsg = new HashMap<String, int[]>();
    static {
        errMsg.put( "E001", new int[] {1,1});
        errMsg.put( "E002", new int[] {1,2});
        errMsg.put( "E004", new int[] {1,4});
        errMsg.put( "E006", new int[] {1,5});
        errMsg.put( "E008", new int[] {1,5});
        errMsg.put( "E009", new int[] {1,7});
        errMsg.put( "E010", new int[] {1,7});
        errMsg.put( "E011", new int[] {4,8});
        errMsg.put( "E012", new int[] {5,9});
        errMsg.put( "E013", new int[] {5,10});
        errMsg.put( "E014", new int[] {6,8});
        errMsg.put( "E015", new int[] {6,11});
        errMsg.put( "E024", new int[] {11,8});
        errMsg.put( "E025", new int[] {11,11});
        errMsg.put( "E026", new int[] {11,11});
        errMsg.put( "E027", new int[] {11,14});
        errMsg.put( "E028", new int[] {11,11});
        errMsg.put( "E030", new int[] {12,16});
        errMsg.put( "E033", new int[] {20,8});
        errMsg.put( "E038", new int[] {20,24});
        errMsg.put( "E039", new int[] {20,11});
        errMsg.put( "E040", new int[] {3,3});
        errMsg.put( "E041", new int[] {10,13});
        errMsg.put( "E042", new int[] {20,19});
        errMsg.put( "E048", new int[] {21,18});
        errMsg.put( "E051", new int[] {21,18});
        errMsg.put( "E052", new int[] {21,18});
        errMsg.put( "E053", new int[] {21,8});
        errMsg.put( "E054", new int[] {15,3});
        errMsg.put( "E055", new int[] {22,3});
        errMsg.put( "E058", new int[] {6,20});
        errMsg.put( "E059", new int[] {6,20});
        errMsg.put( "E060", new int[] {1,21});
        errMsg.put( "E061", new int[] {24,22});
        errMsg.put( "E062", new int[] {24,23});
        errMsg.put( "E063", new int[] {1,8});
        errMsg.put( "E064", new int[] {7,3});
        errMsg.put( "E065", new int[] {14,3});
        errMsg.put( "E066", new int[] {16,9});
        errMsg.put( "E067", new int[] {16,10});
        errMsg.put( "E068", new int[] {27,8});
        errMsg.put( "E070", new int[] {13,3});
        errMsg.put( "E076", new int[] {18,9});
        errMsg.put( "E077", new int[] {18,10});
        errMsg.put( "E079", new int[] {15,8});
        errMsg.put( "E080", new int[] {15,25});
        errMsg.put( "E082", new int[] {9,3});
        errMsg.put( "E084", new int[] {28,3});
        errMsg.put( "E088", new int[] {29,3});
        errMsg.put( "E090", new int[] {30,8});
        errMsg.put( "E091", new int[] {11,28});
        errMsg.put( "E092", new int[] {30,28});
        errMsg.put( "E093", new int[] {11,29});
        errMsg.put( "E094", new int[] {30,30});
        errMsg.put( "E095", new int[] {30,31});
        errMsg.put( "E096", new int[] {12,40});
        errMsg.put( "E097", new int[] {12,32});
        errMsg.put( "E098", new int[] {12,8});
        errMsg.put( "E099", new int[] {31,31});
        errMsg.put( "E100", new int[] {31,40});
        errMsg.put( "E101", new int[] {31,34});
        errMsg.put( "E102", new int[] {31,8});
        errMsg.put( "E103", new int[] {30,41});
        errMsg.put( "E104", new int[] {21,36});
        errMsg.put( "E105", new int[] {27,37});
        errMsg.put( "E108", new int[] {23,38});
        errMsg.put( "E109", new int[] {20,38});
        errMsg.put( "E110", new int[] {32,39});
        errMsg.put( "E111", new int[] {32,10});
    };

    public static final Map<Integer, String> source_lookup = new HashMap<Integer,String>();
    static {
        source_lookup.put(1,"DBLIB_CONNECT");
        source_lookup.put(2,"DBCFCALLEE_CF_CALL");
        source_lookup.put(3,"DBNET_CHANNEL_CALL");
        source_lookup.put(4,"DBLIB_RTTPING");
        source_lookup.put(5,"DBLIB_CONNECT_BIND");
        source_lookup.put(6,"DBLIB_CHANNEL_PUBLISH");
        source_lookup.put(7,"DBNET_CHANNEL_SUBSCRIBE");
        source_lookup.put(8,"DBNET_RPC_REGISTER");
        source_lookup.put(9,"DBNET_RPC_CONNECT");
        source_lookup.put(10,"DBRPCCALLEE_CHANNEL_CALL");
        source_lookup.put(11,"DBLIB_CHANNEL_SUBSCRIBE");
        source_lookup.put(12,"DBLIB_CHANNEL_UNSUBSCRIBE");
        source_lookup.put(13,"DBNET_CF_CALL");
        source_lookup.put(14,"DBNET_CHANNEL_UNSUBSCRIBE");
        source_lookup.put(15,"DBNET_RPC_CALL");
        source_lookup.put(16,"DBLIB_CF_BIND");
        source_lookup.put(17,"DBLIB_RPC_BIND");
        source_lookup.put(18,"DBLIB_RPC_CALLER");
        source_lookup.put(19,"DBLIB_CHANNEL_SENDMSG");
        source_lookup.put(20,"DBLIB_CHANNEL_CALL");
        source_lookup.put(21,"DBLIB_RPC_CONNECT");
        source_lookup.put(22,"DBRPCCALLEE_RPC_CALL");
        source_lookup.put(23,"DBLIB_RPC_CALL");
        source_lookup.put(24,"DBNET_CONNECT");
        source_lookup.put(25,"DBLIB_RPC_INIT");
        source_lookup.put(26,"DBLIB_RPC_REGISTER");
        source_lookup.put(27,"DBLIB_CF_CALL");
        source_lookup.put(28,"DBNET_CHANNEL_CONNECT");
        source_lookup.put(29,"DBNET_CHANNEL_DISCONNECT");
        source_lookup.put(30,"DBLIB_CHANNEL_CONNECT");
        source_lookup.put(31,"DBLIB_CHANNEL_DISCONNECT");
        source_lookup.put(32,"DBLIB_CF_REGFN");
        source_lookup.put(33,"DBLIB_RPC_REGFN");
    };


    public static final Map<Integer, String> code_lookup = new HashMap<Integer,String>();
    static {
        code_lookup.put(1,"INVALID_URL");
        code_lookup.put(2,"INVALID_AUTH_PARAM");
        code_lookup.put(3,"ERR_");
        code_lookup.put(4,"INVALID_ACCESSTOKEN_FUNCTION");
        code_lookup.put(5,"HTTP_");
        code_lookup.put(6,"AUTH_FAILED");
        code_lookup.put(7,"INVALID_CLIENTFUNCTION");
        code_lookup.put(8,"NETWORK_DISCONNECTED");
        code_lookup.put(9,"INVALID_EVENTNAME");
        code_lookup.put(10,"INVALID_CALLBACK");
        code_lookup.put(11,"INVALID_CHANNELNAME");
        code_lookup.put(12,"ACCESSTOKEN_FAILED");
        code_lookup.put(13,"CALLEE_EXCEPTION");
        code_lookup.put(14,"INVALID_CHANNELNAME_LENGTH");
        code_lookup.put(15,"CHANNEL_ALREADY_SUBSCRIBED");
        code_lookup.put(16,"CHANNEL_NOT_SUBSCRIBED");
        code_lookup.put(17,"CHANNEL_ALREADY_UNSUBSCRIBED");
        code_lookup.put(18,"INVALID_SERVERNAME");
        code_lookup.put(19,"RPC_TIMEOUT");
        code_lookup.put(20,"INVALID_SUBJECT");
        code_lookup.put(21,"RECONNECT_ATTEMPT_EXCEEDED");
        code_lookup.put(22,"DISCONNECT_REQUEST");
        code_lookup.put(23,"RECONNECT_REQUEST");
        code_lookup.put(24,"INVALID_FUNCTION");
        code_lookup.put(25,"RESPONSE_TIMEOUT");
        code_lookup.put(26,"RPC_INVALID_FUNCTIONS");
        code_lookup.put(27,"ACCESS_TOKEN_FAIL");
        code_lookup.put(28,"ACCESS_TOKEN");
        code_lookup.put(29,"CHANNEL_EXISTS");
        code_lookup.put(30,"CHANNEL_ALREADY_CONNECTED");
        code_lookup.put(31,"INVALID_CHANNEL");
        code_lookup.put(32,"UNSUBSCRIBE_ALREADY_INITIATED");
        code_lookup.put(33,"INVALID_TYPE");
        code_lookup.put(34,"DISCONNECT_ALREADY_INITIATED");
        code_lookup.put(35,"INVALID_BINDING");
        code_lookup.put(36,"ACCESS_DENIED");
        code_lookup.put(37,"RESPONSE_OBJECT_CLOSED");
        code_lookup.put(38,"ID_GENERATION_FAILED");
        code_lookup.put(39,"INVALID_FUNCTION_NAME");
        code_lookup.put(40,"INVALID_CHANNEL_TYPE");
        code_lookup.put(41,"INVALID_CHANNEL_TYPE_BINDING");
    };
}
