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

import android.text.TextUtils;

import java.util.Arrays;

import io.databridges.databridges_sio_java_client.callbacks.callResponse;
import io.databridges.databridges_sio_java_client.dBridges;
import io.databridges.databridges_sio_java_client.dispatcher.dispatcher;
import io.databridges.databridges_sio_java_client.exception.dBError;
import io.databridges.databridges_sio_java_client.msgtypes.MessageTypes;
import io.databridges.databridges_sio_java_client.utils.util;
import io.databridges.databridges_sio_java_client.rpc.rpCaller;

public class channel extends dispatcher {
    private  String _channelName;
    private  String _sid;
    private dBridges _dbcore;
    private  boolean _isOnline;
    private static final String[] _funcion_type = {"channelMemberList", "channelMemberInfo", "timeout" ,  "err"};

    public channel(String channelName, String sid , dBridges dBCoreObject)
    {
        this._channelName = channelName;
        this._sid = sid;
        this._dbcore = dBCoreObject;
        this._isOnline = false;
    }

    public String getChannelName(){
        return this._channelName;
    }

    public boolean isOnline()
    {
        return this._isOnline;
    }

    public void set_isOnline(boolean value)
    {
        this._isOnline = value;
    }


    public void publish(String eventName  , String eventData, long seqnum) throws dBError {
        if (!this._isOnline) throw (new dBError("E014" ));
        if (this._channelName.toLowerCase() == "sys:*") throw (new dBError("E015"));
        if(TextUtils.isEmpty(eventName)) throw (new dBError("E058"));
        boolean m_status = util.updatedBNewtworkSC(this._dbcore, MessageTypes.PUBLISH_TO_CHANNEL, this._channelName, null, eventData, eventName, null, 0, seqnum);
        if (!m_status) throw (new dBError("E014"));
        return;
    }

    public void publish(String eventName  , String eventData) throws dBError {
        if (!this._isOnline) throw (new dBError("E014" ));
        if (this._channelName.toLowerCase() == "sys:*") throw (new dBError("E015"));
        if(TextUtils.isEmpty(eventName)) throw (new dBError("E058"));
        boolean m_status = util.updatedBNewtworkSC(this._dbcore, MessageTypes.PUBLISH_TO_CHANNEL, this._channelName, null, eventData, eventName, null, 0, 0);
        if (!m_status) throw (new dBError("E014"));
        return;
    }


    public void call(String functionName, String inparam ,long ttlms ,  callResponse handler ) throws dBError{

        if(Arrays.asList(channel._funcion_type).contains(functionName)) {
            if (this._channelName.toLowerCase().startsWith("prs:") ||
                    this._channelName.toLowerCase().startsWith("sys:")) {
                rpCaller caller = this._dbcore.rpc.ChannelCall(this._channelName);

                caller.call(functionName, inparam, ttlms, new callResponse() {
                    @Override
                    public void onResult(String response, boolean isEnd) {

                        handler.onResult(response, isEnd);
                    }

                    @Override
                    public void onError(dBError dberror) {
                        handler.onError(dberror);
                    }
                });

            } else {

                throw (new dBError("E039"));
            }
        }else{
            throw (new dBError("E038"));
        }
    }
}
