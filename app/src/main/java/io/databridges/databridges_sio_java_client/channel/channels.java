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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import io.databridges.databridges_sio_java_client.callbacks.eventHandler;
import io.databridges.databridges_sio_java_client.dBridges;
import io.databridges.databridges_sio_java_client.dispatcher.dispatcher;
import io.databridges.databridges_sio_java_client.exception.dBError;
import io.databridges.databridges_sio_java_client.msgtypes.MessageTypes;
import io.databridges.databridges_sio_java_client.utils.accessTokenActions;
import io.databridges.databridges_sio_java_client.utils.metaData;
import io.databridges.databridges_sio_java_client.utils.systemEvents;
import io.databridges.databridges_sio_java_client.utils.util;

public class channels {
    private static final String[] _channel_type = {"pvt", "prs", "sys"};
    private Map<String , Object> _channelsid_registry;
    private Map<String , String> _channelname_sid;
    private dBridges _dbcore;
    private dispatcher _dispatch;
    private static String regex = "^[a-zA-Z0-9.:_-]*$";
    private static Pattern pattern;
    public channels(dBridges dBCoreObject){
        this._channelsid_registry=new HashMap<>();
        this._channelname_sid = new HashMap<>();
        this._dbcore = dBCoreObject;
        this._dispatch =  new dispatcher();
        pattern = Pattern.compile(regex);
    }


    public void bind(String eventName, eventHandler handler) throws dBError {
        this._dispatch.bind(eventName, handler);
    }

    public void unbind(String eventName) {
        this._dispatch.unbind(eventName);
    }

    public void bind_all(eventHandler handler) {
        this._dispatch.bind_all(handler);
    }

    public void unbind_all() {
        this._dispatch.unbind_all();
    }

    public void _handledispatcher(String eventName ,  String eventInfo, metaData metadata) {
        this._dispatch.emit_channelStatus(eventName, eventInfo, metadata);
    }

    public void _handledispatcher(String eventName ,  Object eventInfo, metaData metadata) {
        this._dispatch.emit_channelStatus(eventName, eventInfo, metadata);
    }


    public void _handledispatcherEvents(String eventName , Object  eventInfo, String channelName, metaData metadata )
    {
        this._dispatch.emit_channelStatus(eventName, eventInfo, metadata);

        String sid =  this._channelname_sid.get(channelName);
        channelstore m_object = (channelstore) this._channelsid_registry.get(sid);
        if(m_object == null) return;
        if( m_object.type ==  "s") {
            ((channel)m_object.ino).emit_channelStatus(eventName, eventInfo, metadata);
        }else{
            ((channelnbd)m_object.ino).emit_channelStatus(eventName, eventInfo, metadata);
        }

    }

    public void _handledispatcherEvents(String eventName , dBError  eventInfo, String channelName, metaData metadata )
    {
        this._dispatch.emit_channelStatus(eventName, eventInfo, metadata);

        String sid =  this._channelname_sid.get(channelName);
        channelstore m_object = (channelstore) this._channelsid_registry.get(sid);
        if(m_object == null) return;
        if( m_object.type.equals("s")) {
            ((channel)m_object.ino).emit_channelStatus(eventName, eventInfo, metadata);
        }else{
            ((channelnbd)m_object.ino).emit_channelStatus(eventName, eventInfo, metadata);
        }

    }


    private boolean isPrivateChannel(String channelName)
    {
        boolean flag = false;
        if(channelName.contains(":")){
            String[] sdata = channelName.toLowerCase().split(":");
            if(Arrays.asList(this._channel_type).contains(sdata[0])) {
                flag = true;
            }else{
                flag=false;
            }
        }
        return flag;
    }


    private void _communicateR(int mtype , String channelName, String sid , String access_token) throws dBError{
        boolean cStatus =  false;
        if(mtype ==  0){
            cStatus = util.updatedBNewtworkSC(this._dbcore, MessageTypes.SUBSCRIBE_TO_CHANNEL, channelName, sid ,  access_token,  null, null,0 , 0);
        }else{
            cStatus = util.updatedBNewtworkSC(this._dbcore, MessageTypes.CONNECT_TO_CHANNEL, channelName, sid ,  access_token, null,null,0 , 0);
        }
        if (!cStatus) {
            if (mtype == 0) {
                throw (new dBError("E024"));
            } else {
                throw (new dBError("E090"));
            }
        }
    }

    private void _ReSubscribe(String sid , String channelName)
    {
        channelstore m_object = (channelstore) this._channelsid_registry.get(sid);
        String access_token = "";
        boolean mprivate =  this.isPrivateChannel(m_object.name);

        switch(m_object.status)
        {
            case channelStatus.SUBSCRIPTION_ACCEPTED:
            case channelStatus.SUBSCRIPTION_INITIATED:
                try {
                    if(!mprivate){
                        this._communicateR(0, m_object.name , sid, access_token);
                    }else{
                        cresponse response =  new cresponse(0 , m_object.name ,  sid , this);
                        this._dbcore._accesstoken_dispatcher(m_object.name , accessTokenActions.CHANNELSUBSCRIBE ,  response );
                    }

                } catch (dBError error) {
                    String [] eventsexp ={systemEvents.OFFLINE};
                    this._handleSubscribeEvents(eventsexp , error,  m_object);
                    return;
                }
                break;
            case channelStatus.CONNECTION_INITIATED:
            case channelStatus.CONNECTION_ACCEPTED:
                try {
                    if(!mprivate){
                        this._communicateR(1, m_object.name , sid, access_token);
                    }else{
                        cresponse response =  new cresponse(1 , m_object.name ,  sid , this);
                        this._dbcore._accesstoken_dispatcher(m_object.name , accessTokenActions.CHANNELCONNECT ,  response );
                    }


                } catch (dBError e) {
                    String [] eventsexpc ={systemEvents.OFFLINE};
                    this._handleSubscribeEvents(eventsexpc , e ,  m_object);
                    return;
                }
                break;

            case channelStatus.UNSUBSCRIBE_INITIATED:
                ((channel)m_object.ino).set_isOnline(false);
                String [] eventsexpus ={systemEvents.UNSUBSCRIBE_SUCCESS, systemEvents.REMOVE};
                this._handleSubscribeEvents( eventsexpus, "" ,  m_object);
                this._channelname_sid.remove(m_object.name);
                this._channelsid_registry.remove(sid);

                break;

            case channelStatus.DISCONNECT_INITIATED:
                ((channelnbd)m_object.ino).set_isOnline(false);
                String [] eventsexpdi ={systemEvents.DISCONNECT_SUCCESS, systemEvents.REMOVE};
                this._handleSubscribeEvents(eventsexpdi , "" ,  m_object);
                this._channelname_sid.remove(m_object.name);
                this._channelsid_registry.remove(sid);
                break;
        }
    }


    public void _ReSubscribeAll() {
        for (Map.Entry<String,String> entry : this._channelname_sid.entrySet()) {
            this._ReSubscribe(entry.getValue(), entry.getKey());
        }
    }

    public boolean isEmptyOrSpaces(String str){
        return TextUtils.isEmpty(str.trim());
    }


    public boolean _validateChanelName(String channelName) throws dBError {
        return this._validateChanelName(channelName , 0);
    }

    public static boolean isAlphaNumeric(String s) {
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
        }

    public boolean _validateChanelName(String channelName,int error_type) throws dBError {
        if (!this._dbcore.connectionstate.isConnected()) {
            switch (error_type) {
                case 0:
                    throw (new dBError("E024"));
                case 1:
                    throw (new dBError("E090"));
                default:
                    break;
            }

        }
        if (this.isEmptyOrSpaces(channelName)) {
            switch (error_type) {
                case 0:
                    throw (new dBError("E025"));
                case 1:
                    throw (new dBError("E095"));

                default:
                    break;
            }
        }
        if (channelName.length() > 64) {
            switch (error_type) {
                case 0:
                    throw (new dBError("E027"));
                case 1:
                    throw (new dBError("E095"));

                default:
                    break;
            }

        }
            if(!isAlphaNumeric(channelName)){
            switch (error_type) {
                case 0:
                    throw (new dBError("E028"));
                case 1:
                    throw (new dBError("E095"));

                default:
                    break;
            }
        }

            if (channelName.contains(":")) {
                String[] sdata = channelName.toLowerCase().split(":");
                if(!Arrays.asList(this._channel_type).contains(sdata[0])) {
                    switch (error_type) {
                    case 0:
                        throw (new dBError("E028"));
                    case 1:
                        throw (new dBError("E095"));
                    default:
                }
            }
        }
        return true;
    }

    private Object _communicate(int mtype , String channelName, boolean mprivate , String action) throws dBError{
        boolean cStatus =  false;
        Object m_channel = null;
        channelstore  m_value;
        String access_token =  null;
        String sid =  util.GenerateUniqueId();

        if(!mprivate){
            if(mtype ==  0){
                cStatus = util.updatedBNewtworkSC(this._dbcore, MessageTypes.SUBSCRIBE_TO_CHANNEL, channelName, sid ,  access_token, null ,null,0 , 0);
            }else{
                cStatus = util.updatedBNewtworkSC(this._dbcore, MessageTypes.CONNECT_TO_CHANNEL, channelName, sid ,  access_token, null , null, 0 ,0);
            }
            if(!cStatus) {
                if (mtype == 0) {
                    throw (new dBError("E024"));
                } else {
                    throw (new dBError("E090"));
                }
            }
        }else{
            cresponse response =  new cresponse(mtype , channelName ,  sid , this);
            this._dbcore._accesstoken_dispatcher(channelName , action ,  response );
        }

        if(mtype ==  0){
            m_channel =  new channel(channelName ,  sid, this._dbcore );
            m_value = new channelstore(channelName , "s", channelStatus.SUBSCRIPTION_INITIATED, m_channel );
        }else{
            m_channel =  new channelnbd(channelName ,  sid, this._dbcore );
            m_value = new channelstore(channelName , "c", channelStatus.CONNECTION_INITIATED, m_channel );
        }
        this._channelsid_registry.put(sid , m_value);
        this._channelname_sid.put(channelName, sid);
        return m_channel;
    }


    public void _failure_dispatcher(int mtype , String sid , String reason)
    {

        channelstore m_object = (channelstore) this._channelsid_registry.get(sid);

        if(mtype == 0){
            ((channel)m_object.ino).set_isOnline(false);
            String []eventssu = {systemEvents.SUBSCRIBE_FAIL};
            this._handleSubscribeEvents( eventssu, reason ,  m_object);

        }else{
            ((channelnbd)m_object.ino).set_isOnline(false);
            String []eventsds = {systemEvents.CONNECT_FAIL};
            this._handleSubscribeEvents(eventsds, reason ,  m_object);

        }
        this._channelname_sid.remove(m_object.name);
        this._channelsid_registry.remove(sid);
    }

    public void _send_to_dbr(int mtype , String channelName , String sid , caccess access_data)
    {
        boolean cStatus = false;

            if(access_data.statuscode != 0 ){
                this._failure_dispatcher(mtype , sid ,  access_data.error_message);
                return;
            }


        if(mtype ==  0){
            cStatus = util.updatedBNewtworkSC(this._dbcore, MessageTypes.SUBSCRIBE_TO_CHANNEL, channelName, sid ,  access_data.accesskey , null, null, 0, 0);
        }else{
            cStatus = util.updatedBNewtworkSC(this._dbcore, MessageTypes.CONNECT_TO_CHANNEL, channelName, sid ,  access_data.accesskey , null, null, 0, 0);
        }

        if(!cStatus){
            this._failure_dispatcher(mtype , sid ,  "library is not connected with the dbridges network");
        }
    }


    public channel subscribe(String channelName) throws dBError{
        String access_token = null;
        try {
            this._validateChanelName(channelName);
        } catch (dBError e) {
            throw(e);
        }


        if(this._channelname_sid.containsKey(channelName)) throw (new dBError("E093"));

        boolean mprivate =  this.isPrivateChannel(channelName);

        channel m_channel = null;
        String m_actiontype = "";
        if (channelName.toLowerCase().startsWith("sys:")) {
            m_actiontype = accessTokenActions.SYSTEM_CHANNELSUBSCRIBE;
        } else {
            m_actiontype = accessTokenActions.CHANNELSUBSCRIBE;
        }


        try {
            m_channel = (channel) this._communicate(0 , channelName, mprivate, m_actiontype);
        } catch (Exception e) {
            throw(e);
        }
        return m_channel;
    }


    public channelnbd connect(String channelName) throws dBError{
        String access_token = null;

        if (channelName.toLowerCase() != "sys:*") {
            try {
                this._validateChanelName(channelName, 1);
            } catch (Exception e) {
                throw (e);
            }
        }

        if(this._channelname_sid.containsKey(channelName)) throw (new dBError("E094"));
        if (channelName.toLowerCase().startsWith("sys:")) throw (new dBError("E095"));

        boolean mprivate =  this.isPrivateChannel(channelName);

        channelnbd m_channel = null;

        try {
            m_channel = (channelnbd) this._communicate(1 , channelName, mprivate , accessTokenActions.CHANNELCONNECT);
        } catch (Exception e) {
            throw(e);
        }
        return m_channel;
    }


    public void unsubscribe(String channelName) throws dBError{

        if (!this._channelname_sid.containsKey(channelName)) throw (new dBError("E030"));

        String  sid = this._channelname_sid.get(channelName);
        channelstore m_object = (channelstore) this._channelsid_registry.get(sid);
        boolean m_status = false;
        if(m_object.type != "s") throw (new dBError("E096"));

        if(m_object.status == channelStatus.UNSUBSCRIBE_INITIATED) throw (new dBError("E097"));

        if(m_object.status == channelStatus.SUBSCRIPTION_ACCEPTED ||
                m_object.status == channelStatus.SUBSCRIPTION_INITIATED ||
                m_object.status == channelStatus.SUBSCRIPTION_PENDING ||
                m_object.status == channelStatus.SUBSCRIPTION_ERROR ||
                m_object.status == channelStatus.UNSUBSCRIBE_ERROR ){
            m_status = util.updatedBNewtworkSC(this._dbcore, MessageTypes.UNSUBSCRIBE_DISCONNECT_FROM_CHANNEL, channelName, sid , null , null, null, 0, 0);
        }

        if(!m_status) throw (new dBError("E098"));

        ((channelstore) this._channelsid_registry.get(sid)).status =  channelStatus.UNSUBSCRIBE_INITIATED;
    }

    public void disconnect(String channelName) throws dBError{
        if (!this._channelname_sid.containsKey(channelName)) throw (new dBError("E099"));

        String sid = this._channelname_sid.get(channelName);
        channelstore m_object = (channelstore) this._channelsid_registry.get(sid);
        boolean m_status = false;

        if(m_object.type != "c") throw (new dBError("E100"));

        if(m_object.status == channelStatus.DISCONNECT_INITIATED) throw (new dBError("E101"));

        if(m_object.status == channelStatus.CONNECTION_ACCEPTED ||
                m_object.status == channelStatus.CONNECTION_INITIATED ||
                m_object.status == channelStatus.CONNECTION_PENDING ||
                m_object.status == channelStatus.CONNECTION_ERROR ||
                m_object.status == channelStatus.DISCONNECT_ERROR ){
            m_status = util.updatedBNewtworkSC(this._dbcore, MessageTypes.UNSUBSCRIBE_DISCONNECT_FROM_CHANNEL, channelName, sid ,  null , null, null, 0, 0);
        }

        if(!m_status) throw (new dBError("E102"));

        ((channelstore)this._channelsid_registry.get(sid)).status =  channelStatus.DISCONNECT_INITIATED;
    }

    public void dispatchEvents(String eventName ,  String eventData , channelstore m_object) {
            metaData metadata = new metaData();
            metadata.eventname =  eventName;
        if(m_object.type == "s") {
            metadata.channelname = ((channel)m_object.ino).getChannelName();
            ((channel)m_object.ino).emit_channelStatus(eventName, eventData, metadata);
            this._handledispatcher(eventName, eventData, metadata);
        }else{
            metadata.channelname = ((channelnbd)m_object.ino).getChannelName();
            ((channelnbd)m_object.ino).emit_channelStatus(eventName, eventData,metadata);
            this._handledispatcher(eventName, eventData, metadata);
        }
    }


    public void dispatchEvents(String eventName ,  Object eventData , channelstore m_object) {
        metaData metadata = new metaData();
        metadata.eventname =  eventName;
        if(m_object.type == "s") {
            metadata.channelname = ((channel)m_object.ino).getChannelName();
            ((channel)m_object.ino).emit_channelStatus(eventName, eventData,metadata);
            this._handledispatcher(eventName, eventData, metadata);
        }else{
            metadata.channelname = ((channelnbd)m_object.ino).getChannelName();
            ((channelnbd)m_object.ino).emit_channelStatus(eventName, eventData, metadata);
            this._handledispatcher(eventName, eventData, metadata);
        }
    }


    public void _handleSubscribeEvents(String []eventName , String eventData , channelstore m_object)
    {
        for(int i =0; i < eventName.length; i++)
        {
            this.dispatchEvents(eventName[i] ,  eventData ,  m_object);
        }
    }

    public void _handleSubscribeEvents(String []eventName , Object eventData , channelstore m_object)
    {
        for(int i =0; i < eventName.length; i++)
        {
            this.dispatchEvents(eventName[i] ,  eventData ,  m_object);
        }
    }

    public void _updateSubscribeStatus(String sid , String status , Object reason)
    {
        if(!this._channelsid_registry.containsKey(sid)) return;
        channelstore m_object = (channelstore) this._channelsid_registry.get(sid);

        switch(m_object.type)
        {
            case "s":
                switch(status){
                    case channelStatus.SUBSCRIPTION_ACCEPTED:
                        ((channelstore) this._channelsid_registry.get(sid)).status = status;
                        ((channel)m_object.ino).set_isOnline(true);
                        String [] events =  {systemEvents.SUBSCRIBE_SUCCESS, systemEvents.ONLINE};
                        this._handleSubscribeEvents( events , "" ,  m_object);
                        break;
                    default:
                        ((channelstore) this._channelsid_registry.get(sid)).status = status;
                        ((channel)m_object.ino).set_isOnline(false);
                        String [] eventsf = {systemEvents.SUBSCRIBE_FAIL};
                        this._handleSubscribeEvents( eventsf, reason ,  m_object);
                        this._channelname_sid.remove(m_object.name);
                        this._channelsid_registry.remove(sid);

                        break;

                }
                break;
            case "c":
                switch(status){
                    case channelStatus.CONNECTION_ACCEPTED:
                        ((channelstore) this._channelsid_registry.get(sid)).status = status;
                        ((channelnbd)m_object.ino).set_isOnline(true);
                        String [] eventsc =  {systemEvents.CONNECT_SUCCESS,  systemEvents.ONLINE};
                        this._handleSubscribeEvents( eventsc, "" ,  m_object);
                        break;
                    default:
                        ((channelstore) this._channelsid_registry.get(sid)).status = status;
                        ((channelnbd)m_object.ino).set_isOnline(false);
                        String [] eventcf = {systemEvents.CONNECT_FAIL};
                        this._handleSubscribeEvents( eventcf, reason ,  m_object);
                        this._channelname_sid.remove(m_object.name);
                        this._channelsid_registry.remove(sid);

                        break;

                }
                break;
            default:
                break;
        }

    }



    public void _updateSubscribeStatusRepeat(String sid , String status , String reason)
    {
        if(!this._channelsid_registry.containsKey(sid)) return;
        channelstore m_object = (channelstore) this._channelsid_registry.get(sid);

        switch(m_object.type)
        {
            case "s":
                switch(status){
                    case channelStatus.SUBSCRIPTION_ACCEPTED:
                        ((channelstore) this._channelsid_registry.get(sid)).status = status;
                        ((channel) m_object.ino).set_isOnline(true);
                        String [] events = {systemEvents.RESUBSCRIBE_SUCCESS,  systemEvents.ONLINE};
                        this._handleSubscribeEvents( events, "" ,  m_object);
                        break;
                    default:
                        ((channelstore) this._channelsid_registry.get(sid)).status = status;
                        ((channel) m_object.ino).set_isOnline(false);
                        String [] eventsf = {systemEvents.OFFLINE};
                        this._handleSubscribeEvents( eventsf, reason ,  m_object);
                        break;

                }
                break;
            case "c":
                switch(status){
                    case channelStatus.CONNECTION_ACCEPTED:
                        ((channelstore) this._channelsid_registry.get(sid)).status = status;
                        ((channelnbd) m_object.ino).set_isOnline(true);
                        String [] eventc = {systemEvents.RECONNECT_SUCCESS,  systemEvents.ONLINE};
                        this._handleSubscribeEvents(eventc , "" ,  m_object);
                        break;
                    default:
                        ((channelstore) this._channelsid_registry.get(sid)).status = status;
                        ((channelnbd) m_object.ino).set_isOnline(false);
                        String [] eventcf = {systemEvents.OFFLINE};
                        this._handleSubscribeEvents(eventcf , reason ,  m_object);
                        break;

                }
                break;
            default:
                break;
        }

    }





    public void _updateChannelsStatusAddChange(int life_cycle, String sid , String  status , Object  reason)
    {
        if(life_cycle == 0)
        {
            if( reason instanceof String )
                this._updateSubscribeStatus(sid , status, (String) reason);
            else
                this._updateSubscribeStatus(sid,  status, reason);

        }else{
            if( reason instanceof String )
                this._updateSubscribeStatusRepeat(sid , status, (String) reason);
            else
                this._updateSubscribeStatus(sid,  status, reason);
        }
    }

    public void _updateChannelsStatusRemove(String sid, String status, String reason)
    {
        if(!this._channelsid_registry.containsKey(sid)) return;
        channelstore m_object = (channelstore) this._channelsid_registry.get(sid);

        switch(m_object.type)
        {
            case "s":
                switch(status){
                    case channelStatus.UNSUBSCRIBE_ACCEPTED:
                        ((channelstore) this._channelsid_registry.get(sid)).status = status;
                        ((channel)m_object.ino).set_isOnline(false);
                        String []  eventsua = {systemEvents.UNSUBSCRIBE_SUCCESS,systemEvents.REMOVE};
                        this._handleSubscribeEvents( eventsua, "" ,  m_object);
                        this._channelname_sid.remove(m_object.name);
                        this._channelsid_registry.remove(sid);
                        break;
                    default:
                        ((channelstore) this._channelsid_registry.get(sid)).status = channelStatus.SUBSCRIPTION_ACCEPTED;
                        ((channel)m_object.ino).set_isOnline(true);
                        String []  eventssf = {systemEvents.UNSUBSCRIBE_FAIL,systemEvents.ONLINE};
                        this._handleSubscribeEvents(eventssf , reason ,  m_object);
                        break;

                }
                break;
            case "c":
                switch(status){
                    case channelStatus.DISCONNECT_ACCEPTED:
                        ((channelstore) this._channelsid_registry.get(sid)).status = status;
                        ((channelnbd)m_object.ino).set_isOnline(false);
                        String [] eventsda = {systemEvents.DISCONNECT_SUCCESS, systemEvents.REMOVE};
                        this._handleSubscribeEvents( eventsda, "" ,  m_object);
                        this._channelname_sid.remove(m_object.name);
                        this._channelsid_registry.remove(sid);
                        break;
                    default:
                        ((channelstore) this._channelsid_registry.get(sid)).status = channelStatus.CONNECTION_ACCEPTED;
                        ((channelnbd)m_object.ino).set_isOnline(true);
                        String [] eventsdf = {systemEvents.DISCONNECT_FAIL,   systemEvents.ONLINE};
                        this._handleSubscribeEvents( eventsdf, reason ,  m_object);
                        break;

                }
                break;
            default:
                break;
        }
    }



    public boolean _isonline(String sid)
    {
        if(!this._channelsid_registry.containsKey(sid)) return false;
        channelstore m_object = (channelstore) this._channelsid_registry.get(sid);
        if(m_object.status == channelStatus.CONNECTION_ACCEPTED ||
                m_object.status == channelStatus.SUBSCRIPTION_ACCEPTED ) return true;

        return false;
    }


    public boolean isOnline(String channelName)
    {
        if(!this._channelname_sid.containsKey(channelName)) return  false;
        if(!this._dbcore._isSocketConnected()) return false;

        String sid =  this._channelname_sid.get(channelName);
        return this._isonline(sid);
    }


    public String [] list()
    {
        ArrayList<String> m_data =  new ArrayList<>();

        for (Map.Entry<String,Object> entry : this._channelsid_registry.entrySet())
        {
            String sid =  entry.getKey();
            channelstore value =  (channelstore) entry.getValue();
            String type =   (value.type == "s")? "subscribed": "connect";
            String isonline = String.valueOf(this._isonline(sid));
            String i_data = "{\"name\":  " +  value.name  + ", \"type\": " + type + ",  \"isonline\": " + isonline + "}";
            m_data.add(i_data);
        }
        return m_data.toArray( new String[0]);
    }


    public void _send_OfflineEvents(){
        for (Map.Entry<String,Object> entry : this._channelsid_registry.entrySet()) {
            String sid =  entry.getKey();
            channelstore value =  (channelstore) entry.getValue();
            metaData metadata = new metaData();
            metadata.channelname = value.name;
            metadata.eventname = systemEvents.OFFLINE;

            if(value.type == "s"){

                ((channel)value.ino).set_isOnline(false);
            }else{
                ((channelnbd)value.ino).set_isOnline(false);
            }

            this._handledispatcherEvents(systemEvents.OFFLINE , "" , value.name,metadata);



        }
    }


    public String _get_subscribeStatus(String sid)
    {
        return this._channelsid_registry.containsKey(sid) ? ((channelstore) this._channelsid_registry.get(sid)).status : "";
    }


    public String _get_channelType(String sid)
    {
        return this._channelsid_registry.containsKey(sid) ? ((channelstore)this._channelsid_registry.get(sid)).type : "";
    }


    public String _get_channelName(String sid)
    {
        return (this._channelsid_registry.containsKey(sid))? ((channelstore)this._channelsid_registry.get(sid)).name : "";
    }


    public String getConnectStatus(String sid)
    {
        return ((channelstore)this._channelsid_registry.get(sid)).status;
    }


    public Object getChannel(String sid)
    {
        if(!this._channelsid_registry.containsKey(sid)) return null;
        return ((channelstore)this._channelsid_registry.get(sid)).ino;
    }

    public String getChannelName(String sid)
    {
        if(!this._channelsid_registry.containsKey(sid)) return null;
        return ((channelstore)this._channelsid_registry.get(sid)).name;
    }

    public boolean isSubscribedChannel(String sid)
    {
        if(!this._channelsid_registry.containsKey(sid)) return false;
        if(((channelstore)this._channelsid_registry.get(sid)).type == "s"){
            return ((channel)((channelstore)this._channelsid_registry.get(sid)).ino).isOnline();
        }else{
            return false;
        }

    }


    public void  clean_channel(String sid)
    {
        channelstore mobject = (channelstore) this._channelsid_registry.get(sid);
        if(mobject.type == "s"){
            ((channel)mobject.ino).unbind();
            ((channel)mobject.ino).unbind_all();
        }else{
            ((channelnbd)mobject.ino).unbind();
        }
    }

    public void cleanUp_All()
    {
        for (Map.Entry<String,String> entry : this._channelname_sid.entrySet()) {
            metaData metadata = new metaData();
            String key = entry.getKey();
            String sid = entry.getValue();
            metadata.channelname =  key;
            metadata.eventname = systemEvents.REMOVE;
            this._handledispatcherEvents(systemEvents.REMOVE , "" , key, metadata);
            clean_channel(sid);
            this._channelname_sid.remove(key);
            this._channelsid_registry.remove(sid);
        }
    }

}
