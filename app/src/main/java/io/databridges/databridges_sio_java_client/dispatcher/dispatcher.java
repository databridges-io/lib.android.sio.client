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

package io.databridges.databridges_sio_java_client.dispatcher;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.databridges.databridges_sio_java_client.callbacks.ServerEventHandler;
import io.databridges.databridges_sio_java_client.callbacks.callINResponse;
import io.databridges.databridges_sio_java_client.callbacks.callRecieved;
import io.databridges.databridges_sio_java_client.callbacks.callResponse;
import io.databridges.databridges_sio_java_client.callbacks.cfeventHandler;
import io.databridges.databridges_sio_java_client.callbacks.connectionHandler;
import io.databridges.databridges_sio_java_client.callbacks.eventHandler;
import io.databridges.databridges_sio_java_client.callbacks.privateHandler;
import io.databridges.databridges_sio_java_client.exception.dBError;
import io.databridges.databridges_sio_java_client.utils.metaData;
import io.databridges.databridges_sio_java_client.utils.serverMetaData;

public class dispatcher {
    private Map<String , List<Object>> local_register;
    private List<Object> global_register;

    public dispatcher()
    {
        this.local_register = new HashMap<>();
        this.global_register = new ArrayList<>();
    }

    public boolean isExists(String eventName){
        return this.local_register.containsKey(eventName);
    }

    public boolean isFunctionDefined(){
        if(this.local_register.isEmpty()) return false;
        return true;
    }
    public int count(){
        return this.local_register.size();
    }

    public void bind(String eventName ,  Object handler) throws dBError
    {
        if(TextUtils.isEmpty(eventName)) throw (new dBError("E012"));

        if(!this.local_register.containsKey(eventName)){
            ArrayList<Object> t = new ArrayList<>();
            t.add(handler);
            this.local_register.put(eventName ,  t );
        }else{
            this.local_register.get(eventName).add(handler);
        }
    }

    public void bind_all(Object handler){
        this.global_register.add(handler);
    }

    public void unbind(String eventName ) {
        if(this.local_register.containsKey(eventName)){
            for(int i = 0; i <  this.local_register.get(eventName).size(); i++ )
            {
                this.local_register.get(eventName).remove(i);
            }

        }
    }


    public void unbind_all(){
        this.global_register.clear();
    }


    public void unbind( ) {
        this.local_register.clear();
    }


    public void emit_connectionStatus(String eventName ,  Object payload)
    {

        if(this.local_register.containsKey(eventName)){
            List<Object> mObject = this.local_register.get(eventName);
            for(int i = 0; i < mObject.size(); i++)
            {
                connectionHandler evt =  (connectionHandler) mObject.get(i);
                evt.onEvent(payload);
            }
        }
    }


    public void emit_channelStatus(String eventName ,  Object payload,  metaData metadata)
    {

        for(int i= 0 ; i < this.global_register.size();  i++) {
            eventHandler evt =  (eventHandler) this.global_register.get(i);
            evt.onEvent(payload ,  metadata);
        }

        if(this.local_register.containsKey(eventName)){
            List<Object> mObject = this.local_register.get(eventName);
            for(int i = 0; i < mObject.size(); i++)
            {
                eventHandler evt =  (eventHandler) mObject.get(i);
                evt.onEvent(payload ,  metadata);
            }
        }
    }


    public void emit_rpcStatus(String eventName ,  Object payload,  serverMetaData metadata)
    {

        for(int i= 0 ; i < this.global_register.size();  i++) {
            ServerEventHandler evt =  (ServerEventHandler) this.global_register.get(i);
            evt.onEvent(payload ,  metadata);
        }

        if(this.local_register.containsKey(eventName)){
            List<Object> mObject = this.local_register.get(eventName);
            for(int i = 0; i < mObject.size(); i++)
            {
                ServerEventHandler evt =  (ServerEventHandler) mObject.get(i);
                evt.onEvent(payload ,  metadata);
            }
        }
    }



    public void emit_clientFunction(String eventName , Object inParameter,  Object response)
    {

        if(this.local_register.containsKey(eventName)){
            List<Object> mObject = this.local_register.get(eventName);
            for(int i = 0; i < mObject.size(); i++)
            {
                callRecieved evt =  (callRecieved) mObject.get(i);
                evt.onCall(inParameter ,  response );
            }
        }
    }


    public void emit_clientFunction2(String eventName , Object inParameter,  Object response)
    {

        if(this.local_register.containsKey(eventName)){
            List<Object> mObject = this.local_register.get(eventName);
            for(int i = 0; i < mObject.size(); i++)
            {
                cfeventHandler evt =  (cfeventHandler) mObject.get(i);
                evt.onEvent(inParameter ,  response);
            }
        }
    }


    public void emit_rpcFunction(String eventName , Object inParameter, Object isEnd)
    {

        if(this.local_register.containsKey(eventName)){
            List<Object> mObject = this.local_register.get(eventName);
            for(int i = 0; i < mObject.size(); i++)
            {
                callResponse evt =  (callResponse) mObject.get(i);
                if(inParameter instanceof String) {
                    //(Boolean) someObject;
                    evt.onResult((String)inParameter,  (Boolean) isEnd);
                }else{
                    if(inParameter instanceof dBError){
                        evt.onError((dBError) inParameter);
                    }
                }
            }
        }
    }


    public void emit_rpcFunction(String eventName , String inParameter, boolean isend , String returnsub)
    {

        if(this.local_register.containsKey(eventName)){
            List<Object> mObject = this.local_register.get(eventName);
            for(int i = 0; i < mObject.size(); i++)
            {
                callINResponse evt =  (callINResponse) mObject.get(i);
                evt.onInternalMessage(inParameter ,  isend, returnsub);
                }
            }
        }

    public void emit_privateFunction(String eventName , String channelName, String sessionid , String action ,  Object response)
    {

        if(this.local_register.containsKey(eventName)){
            List<Object> mObject = this.local_register.get(eventName);
            for(int i = 0; i < mObject.size(); i++)
            {
                privateHandler evt =  (privateHandler) mObject.get(i);
                evt.onPrivate (channelName, sessionid, action, response);
            }
        }
    }
}
