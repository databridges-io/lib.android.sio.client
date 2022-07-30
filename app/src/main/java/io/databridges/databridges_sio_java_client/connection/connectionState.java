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

import android.text.TextUtils;

import java.util.Arrays;
import java.util.Date;

import io.databridges.databridges_sio_java_client.callbacks.connectionHandler;
import io.databridges.databridges_sio_java_client.dBridges;
import io.databridges.databridges_sio_java_client.dispatcher.dispatcher;
import io.databridges.databridges_sio_java_client.exception.dBError;
import io.databridges.databridges_sio_java_client.msgtypes.MessageTypes;
import io.databridges.databridges_sio_java_client.utils.util;

public class connectionState {
    private final  String[] supportedEvents = {"connect_error" , "connected" ,	"disconnected" ,
            "reconnecting", "connecting", "state_change",
            "reconnect_error", "reconnect_failed", "reconnected",
            "connection_break","rttpong"};
    private final  String[] nostatus = {"reconnect_attempt", "rttpong",  "rttping"};
    private dispatcher registry;
    private boolean newLifeCycle;
    public  int reconnect_attempt;
    private dBridges dbcore;



    public void state(String _state) {
        this._state = _state;
    }

    private String _state;

    private boolean _isconnected;
    private long _rttms;

    public String state(){
        return  this._state;
    }




    public boolean isConnected(){
        return this._isconnected;
    }

    public connectionState(dBridges dBCoreObject)
    {
        this._state = "";
        this._isconnected = false;
        this.registry = new dispatcher();
        this.newLifeCycle = true;
        this.reconnect_attempt = 0;
        this.dbcore = dBCoreObject;
        this._rttms = -1;
    }

    public void rttping() throws dBError {
        Date now = new Date();
        long t1 = now.getTime();
        boolean m_status =  util.updatedBNewtworkSC(this.dbcore, MessageTypes.SYSTEM_MSG, null, null ,  null, "rttping" , null, t1,0);
        if(!m_status) throw(new dBError("E011"));
    }

    public long get_rttms(){
        return this._rttms;
    }

    public void set_rttms(long value){
        this._rttms =  value;
    }
    public void rttping(String payload) throws dBError {
        Date now = new Date();
        long t1 = now.getTime();
        boolean m_status =  util.updatedBNewtworkSC(this.dbcore, MessageTypes.SYSTEM_MSG, null, null ,  payload, "rttping" , null, t1,0);
        if(!m_status) throw(new dBError("E011"));
    }

    public  void set_newLifeCycle(boolean value){
        this.newLifeCycle = value;
    }

    public boolean  get_newLifeCycle(){
        return this.newLifeCycle;
    }




    public void bind(String eventName , connectionHandler handler) throws dBError {
        if(TextUtils.isEmpty(eventName)) throw (new dBError("E012"));
        if(! Arrays.asList(this.supportedEvents).contains(eventName)) throw(new dBError("E012"));
        if(handler == null) throw(new dBError("E013"));
        this.registry.bind(eventName , handler);
    }

    public void unbind(String eventName) {
        this.registry.unbind(eventName);
    }

    public void unbind() {
        this.registry.unbind();
    }


    public void _updatestates(String eventName)
    {
        switch(eventName){
            case states.CONNECTED:
            case states.RECONNECTED:
            case states.RTTPONG:
                this._isconnected = true;
                break;
            default:
                this._isconnected = false;
                break;
        }
    }

    public void _handledispatcher(String eventName ,  String eventInfo)
    {
        String previous = this._state;


        if(! Arrays.asList(this.nostatus).contains(eventName)) this._state =  eventName;

        this._updatestates(eventName);

        if(eventName != previous)
        {
            if(! Arrays.asList(this.nostatus).contains(eventName)) {
                if(! Arrays.asList(this.nostatus).contains(previous)){
                      stateChange  lstatechange = new stateChange(previous ,  eventName);
                    this._state =  eventName;
                    if(eventName  == "disconnected")
                    {
                        this._state = "";
                    }
                    this.registry.emit_connectionStatus(states.STATE_CHANGE, lstatechange);

                }
            }
        }

        if(!TextUtils.isEmpty(eventInfo) ){
            this.registry.emit_connectionStatus(eventName, eventInfo);
        }else{
            this.registry.emit_connectionStatus(eventName, null);
        }


    }


    public void _handledispatcher(String eventName ,  Object eventInfo)
    {
        String previous = this._state;


        if(! Arrays.asList(this.nostatus).contains(eventName)) this._state =  eventName;

        this._updatestates(eventName);

        if(eventName != previous)
        {
            if(! Arrays.asList(this.nostatus).contains(eventName)) {
                if(! Arrays.asList(this.nostatus).contains(previous)){
                    stateChange  lstatechange = new stateChange(previous ,  eventName);
                    this._state =  eventName;
                    this.registry.emit_connectionStatus(states.STATE_CHANGE, lstatechange);

                }
            }
        }

        if(!(eventInfo == null)){
            this.registry.emit_connectionStatus(eventName, eventInfo);
        }else{
            this.registry.emit_connectionStatus(eventName, null);
        }


    }
}
