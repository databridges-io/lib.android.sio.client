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

package io.databridges.databridges_sio_java_client.rpc;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.databridges.databridges_sio_java_client.callbacks.ServerEventHandler;
import io.databridges.databridges_sio_java_client.channel.caccess;
import io.databridges.databridges_sio_java_client.dBridges;
import io.databridges.databridges_sio_java_client.dispatcher.dispatcher;
import io.databridges.databridges_sio_java_client.exception.dBError;
import io.databridges.databridges_sio_java_client.msgtypes.MessageTypes;
import io.databridges.databridges_sio_java_client.utils.accessTokenActions;
import io.databridges.databridges_sio_java_client.utils.serverMetaData;
import io.databridges.databridges_sio_java_client.utils.systemEvents;
import io.databridges.databridges_sio_java_client.utils.util;

public class cRpc {

     private dBridges dbcore;
    private cfClient cf ;
    private Map<String , Object>  serverSid_registry;

    private Map<String , HashMap<String,String>> serverName_sid;
    private dispatcher dispatch;
    private Map<String , Object>  callersid_object;
    private static String[] server_type = {"pvt", "prs", "sys"};

    private static String regex = "^[a-zA-Z0-9@$&-.+:]*$";
    private static Pattern pattern;


   public  cRpc(dBridges dbcorelib){
        this.dbcore =  dbcorelib;
        this.cf =  new cfClient(this.dbcore);

       this.serverName_sid = new HashMap<String,  HashMap<String,String>>();
        this.serverSid_registry =new HashMap<String , Object>();
        this.dispatch =  new dispatcher();
        this.callersid_object=new HashMap<String , Object>();
       pattern = Pattern.compile(regex);
    }



    private boolean isEmptyOrSpaces(String str){
       return TextUtils.isEmpty(str.trim());
    }

    public static boolean isAlphaNumeric(String s) {
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }


    public boolean _validateServerName(String serverName) throws dBError {
        return this._validateServerName(serverName , 0);
    }
    public boolean _validateServerName(String serverName,int error_type) throws dBError {
        if(this.isEmptyOrSpaces(serverName)) {
            if(error_type==1){
                throw(new dBError("E048"));
            }
        }

        if(serverName.length() > 64) {
            if(error_type==1){
                throw(new dBError("E051"));
            }
        }
        if(!isAlphaNumeric(serverName)){
            if(error_type==1){
                throw(new dBError("E052"));
            }
        }

        if (serverName.contains(":")) {
            String[] sdata = serverName.toLowerCase().split(":");
            if(!Arrays.asList(this.server_type).contains(sdata[0])) {
                if(error_type==1){
                    throw(new dBError("E052"));
                }
            }
        }
        return true;
    }

    public String _get_rpcStatus(String sid)
    {
        rpcstore m_object = (rpcstore) this.serverSid_registry.get(sid);
        return m_object.status;
    }

    public void bind(String eventName, ServerEventHandler handler) throws dBError {
        this.dispatch.bind(eventName, handler);
    }

    public void unbind(String eventName) throws dBError{
        this.dispatch.unbind(eventName);
    }

    public void bind_all(ServerEventHandler handler) {
        this.dispatch.bind_all(handler);
    }

    public void unbind_all() {
        this.dispatch.unbind_all();
    }



    private void _communicateR(String serverName, String sid , String access_token) throws dBError{
        boolean cStatus =  false;
        cStatus = util.updatedBNewtworkSC(this.dbcore, MessageTypes.CONNECT_TO_RPC_SERVER, serverName, sid ,  access_token,  null, null,0 , 0);
        if(!cStatus) {
            throw(new dBError("E053"));
        }
    }


    private void _ReSubscribe(String sid , String serverName) {

        rpcstore m_object = (rpcstore) this.serverSid_registry.get(sid);
        if( m_object == null) return;

        String access_token = "";
        serverName = m_object.name;
        boolean mprivate =  this.isPrivateChannel(m_object.name);

            switch(m_object.status)
            {
                case rpcStatus.RPC_CONNECTION_ACCEPTED:
                    try {
                        if(!mprivate){
                            this._communicateR(m_object.name , sid, access_token);
                        }else{
                            rpcAccessResponse response =  new rpcAccessResponse(serverName ,  sid , this);
                            this.dbcore._accesstoken_dispatcher(serverName , accessTokenActions.RPCCONNECT ,  response );
                        }
                    } catch (dBError error) {
                        String [] eventsexp ={systemEvents.RPC_CONNECT_FAIL};
                        this._handleRegisterEvents(eventsexp , error,  m_object);
                        return;
                    }
                    break;
            }
   }




    public void _ReSubscribeAll() {

        List<String> publiclist = new ArrayList<>();
        List<String> privateList =  new ArrayList<>();

        for (Map.Entry mapElement : this.serverName_sid.entrySet()) {
            String key = (String) mapElement.getKey();
            HashMap<String, String> hm = (HashMap<String, String>) mapElement.getValue();
            for (Map.Entry mapElement2 : hm.entrySet()) {
                if(isPrivateChannel(key)){
                   privateList.add((String) mapElement2.getKey()) ;
                }else{
                    publiclist.add((String) mapElement2.getKey());
                }
            }

            for(String sid : publiclist){
                this._ReSubscribe(sid , "");
            }

            for(String sid:  privateList){
                this._ReSubscribe(sid , "");
            }

        }
    }


    public void dispatchEvents(String eventName ,  Object eventData , rpcstore m_object) {
        serverMetaData metadata = new serverMetaData();
        metadata.eventname = eventName;
        metadata.servername = m_object.name;
        switch (eventName) {
            case systemEvents.REGISTRATION_SUCCESS:
            case systemEvents.SERVER_ONLINE:
            case systemEvents.RPC_CONNECT_SUCCESS:
            case systemEvents.UNREGISTRATION_SUCCESS:
            case systemEvents.REMOVE:
                this.dispatch.emit_rpcStatus(eventName, eventData, metadata);
                ((rpCaller) m_object.ino).emit_rpcStatus(eventName, eventData, metadata);
                break;

            case systemEvents.REGISTRATION_FAIL:
            case systemEvents.RPC_CONNECT_FAIL:
            case systemEvents.SERVER_OFFLINE:
                this.dispatch.emit_rpcStatus(eventName, eventData, metadata);
                ((rpCaller) m_object.ino).emit_rpcStatus(eventName, eventData, metadata);
                break;

            default:
                break;

        }
    }


        public void _handleRegisterEvents(String []eventName , Object eventData , rpcstore m_object)
        {
            for(int i =0; i < eventName.length; i++)
            {
                this.dispatchEvents(eventName[i] ,  eventData ,  m_object);
            }
        }




    public void _updateRegistrationStatus(String sid , String status , Object reason)
    {
        if(!this.serverSid_registry.containsKey(sid)) return;
        rpcstore m_object = (rpcstore) this.serverSid_registry.get(sid);
        String [] events;
        switch(m_object.type)
        {
            case "r":
                switch(status){
                    case rpcStatus.REGISTRATION_ACCEPTED:
                        ((rpcstore)this.serverSid_registry.get(sid)).status = status;
                        ((rpCaller)m_object.ino).set_isOnline(true);
                        events = new String[] {systemEvents.REGISTRATION_SUCCESS, systemEvents.SERVER_ONLINE};
                        this._handleRegisterEvents( events, "" ,  m_object);
                        break;
                    default:
                        ((rpcstore)this.serverSid_registry.get(sid)).status = status;
                        ((rpCaller)m_object.ino).set_isOnline(false);
                        events = new String[] {systemEvents.REGISTRATION_FAIL};
                        this._handleRegisterEvents( events,  reason,  m_object);
                        this.serverName_sid.remove(this.serverName_sid.get(m_object.name)) ; //delete(m_object.name);
                        this.serverSid_registry.remove(m_object);

                        break;

                }
                break;
            case "c":
                switch(status){
                    case rpcStatus.RPC_CONNECTION_ACCEPTED:
                        ((rpcstore)this.serverSid_registry.get(sid)).status = status;
                        ((rpCaller)m_object.ino).set_isOnline(true);
                        events = new String[] {systemEvents.RPC_CONNECT_SUCCESS, systemEvents.SERVER_ONLINE};
                        this._handleRegisterEvents(events , "" ,  m_object);
                        break;
                    default:
                        ((rpcstore)this.serverSid_registry.get(sid)).status = status;
                        ((rpCaller)m_object.ino).set_isOnline(false);
                        events = new String[] {systemEvents.RPC_CONNECT_FAIL};
                        this._handleRegisterEvents( events, reason ,  m_object);
                        this.serverName_sid.remove(this.serverName_sid.get(m_object.name)) ; //delete(m_object.name);
                        this.serverSid_registry.remove(m_object);

                        break;

                }
                break;
            default:
                break;
        }

    }




    public void _updateRegistrationStatusRepeat(String sid , String status , Object reason)
    {
        if(!this.serverSid_registry.containsKey(sid)) return;
        rpcstore m_object = (rpcstore) this.serverSid_registry.get(sid);
        String [] events;

        switch(m_object.type)
        {
            case "r":
                switch(status){
                    case rpcStatus.REGISTRATION_ACCEPTED:
                        ((rpcstore)this.serverSid_registry.get(sid)).status = status;
                        ((rpCaller)m_object.ino).set_isOnline(true);
                        events = new String[] {systemEvents.SERVER_ONLINE};

                        this._handleRegisterEvents( events, "" ,  m_object);
                        break;
                    default:
                        ((rpcstore)this.serverSid_registry.get(sid)).status = status;
                        ((rpCaller)m_object.ino).set_isOnline(false);
                        events = new String[] {systemEvents.SERVER_OFFLINE};
                        this._handleRegisterEvents(events, reason ,  m_object);
                        break;

                }
                break;
            case "c":
                switch(status){
                    case rpcStatus.RPC_CONNECTION_ACCEPTED:
                        ((rpcstore)this.serverSid_registry.get(sid)).status = status;
                        ((rpCaller)m_object.ino).set_isOnline(true);
                        events = new String[] {systemEvents.SERVER_ONLINE};
                        this._handleRegisterEvents(events, "" ,  m_object);
                        break;
                    default:
                        ((rpcstore)this.serverSid_registry.get(sid)).status = status;
                        ((rpCaller)m_object.ino).set_isOnline(false);
                        events = new String[] {systemEvents.SERVER_OFFLINE};
                        this._handleRegisterEvents( events, reason ,  m_object);
                        break;

                }
                break;
            default:
                break;
        }

    }


    public void _updateRegistrationStatusAddChange(int life_cycle, String sid ,  String status ,  Object reason)
    {
        if(life_cycle == 0)
        {
            this._updateRegistrationStatus(sid , status, reason);
        }else{
            this._updateRegistrationStatusRepeat(sid , status, reason);
        }
    }


    private boolean isPrivateChannel(String serverName)
    {
        boolean flag = false;
        if(serverName.contains(":")){
            String[] sdata = serverName.toLowerCase().split(":");
            if(Arrays.asList(this.server_type).contains(sdata[0])) {
                flag = true;
            }else{
                flag=false;
            }
        }
        return flag;
    }



    private rpCaller _communicate(String serverName, boolean mprivate , String action) throws dBError {
        boolean cStatus =  false;
        Object m_channel = null;
        rpcstore  m_value;
        String access_token =  null;
        String sid =  util.GenerateUniqueId();

        if(!mprivate){
            cStatus = util.updatedBNewtworkSC(this.dbcore, MessageTypes.CONNECT_TO_RPC_SERVER, serverName, sid ,  access_token , null ,null,0 , 0);
            if(!cStatus) 	throw(new dBError("E053"));

        }else{
            rpcAccessResponse response =  new rpcAccessResponse( serverName ,  sid , this);
            this.dbcore._accesstoken_dispatcher(serverName , action ,  response );


        }

        rpCaller rpccaller = new rpCaller(serverName,this.dbcore, this);

        m_value = new rpcstore(serverName ,  "c" , rpcStatus.RPC_CONNECTION_INITIATED ,  rpccaller);

        if( !this.serverName_sid.containsKey(serverName))
        {
            this.serverName_sid.put(serverName ,  new HashMap<String,String>());
            this.serverName_sid.get(serverName).put(sid , "");
        }else{
            this.serverName_sid.get(serverName).put(sid, "");
        }

        this.serverSid_registry.put(sid ,  m_value);

        return rpccaller;
    }



    public void _failure_dispatcher( String sid , String reason)
    {

        rpcstore m_object = (rpcstore) this.serverSid_registry.get(sid);

        ((rpCaller)m_object.ino).set_isOnline(false);

       dBError dberror = new dBError("E104");
        dberror.updatecode("" ,  reason);
        String []events = new String[] {systemEvents.RPC_CONNECT_FAIL};
        this._handleRegisterEvents  (events , dberror ,  m_object);

        this.serverName_sid.remove(this.serverName_sid.get(m_object.name));
        this.serverSid_registry.remove(m_object);
;
    }



    public void _send_to_dbr( String serverName , String sid , caccess access_data)
    {

        boolean cStatus = false;

        if(access_data.statuscode != 0 ){
            this._failure_dispatcher(sid ,  access_data.error_message);
            return;
        }

        String access_token = access_data.accesskey;
        cStatus = util.updatedBNewtworkSC(this.dbcore, MessageTypes.CONNECT_TO_RPC_SERVER, serverName, sid ,  access_token, null, null, 0, 0);

        if(!cStatus){
            this._failure_dispatcher( sid ,  "library is not connected with the dbridges network");
        }
    }

   public rpCaller connect(String serverName) throws dBError{
        try {
            this._validateServerName(serverName, 1);
        } catch (dBError error) {
            throw(error);
        }

		boolean mprivate =  this.isPrivateChannel(serverName);

        rpCaller m_caller = null;

        try {
            m_caller = this._communicate(serverName, mprivate, accessTokenActions.RPCCONNECT);
        } catch (dBError error) {
            throw(error);
        }
        return m_caller;
    }

    public rpCaller ChannelCall(String channelName){

        if(this.serverName_sid.containsKey(channelName)) {

            HashMap<String,String> sids =  this.serverName_sid.get(channelName);
            String sid = sids.keySet()
                             .toArray(new String[sids.size()])[0];
           rpcstore mobject =  (rpcstore) this.serverSid_registry.get(sid);
            ((rpcstore)this.serverSid_registry.get(sid)).count = mobject.count + 1;

            return (rpCaller) mobject.ino;
        }

        String sid =  util.GenerateUniqueId();
        rpCaller rpccaller = new rpCaller(channelName,this.dbcore, this, "ch");
        if( !this.serverName_sid.containsKey(channelName))
        {
            this.serverName_sid.put(channelName ,  new HashMap<String,String>());
            this.serverName_sid.get(channelName).put(sid , "");
        }else{
            this.serverName_sid.get(channelName).put(sid, "");
        }
        rpcstore m_value =  new rpcstore(channelName , "x",  rpcStatus.RPC_CONNECTION_INITIATED, rpccaller);

        this.serverSid_registry.put(sid ,  m_value);
        return rpccaller;
    }

    public void store_object(String sid , rpCaller rpccaller)
    {
        this.callersid_object.put(sid ,  rpccaller);
    }

    public void delete_object(String sid)
    {
        this.callersid_object.remove(this.callersid_object.get(sid));
    }

    public rpCaller get_object(String sid)
    {
        if(this.callersid_object.containsKey(sid)) {
            return (rpCaller) this.callersid_object.get(sid);
        }
        return null;
    }

    public void _send_OfflineEvents(){
        for (Map.Entry<String,Object> entry : this.serverSid_registry.entrySet()) {
            String sid =  entry.getKey();
            rpcstore value =  (rpcstore) entry.getValue();

            String[] events = new String[] {systemEvents.SERVER_OFFLINE};
            this._handleRegisterEvents( events, "" , value);

        }
    }


    public void  clean_channel(String sid)
    {
        rpcstore mobject = (rpcstore) this.serverSid_registry.get(sid);
        if(mobject.type == "c"){
            ((rpCaller)mobject.ino).unbind();
        }
    }

    public void cleanUp_All()
    {
        for (Map.Entry mapElement : this.serverName_sid.entrySet()) {
            String key = (String) mapElement.getKey();
            HashMap<String, String> hm = (HashMap<String, String>) mapElement.getValue();
                for (Map.Entry mapElement2 : hm.entrySet()) {
                    String sid = (String) mapElement2.getKey();
                    clean_channel(sid);
                    this.serverSid_registry.remove(sid);
                }
        }
        this.serverName_sid.clear();
    }


}
