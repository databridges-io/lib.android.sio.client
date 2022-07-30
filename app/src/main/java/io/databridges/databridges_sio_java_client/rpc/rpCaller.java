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

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.databridges.databridges_sio_java_client.callbacks.ServerEventHandler;
import io.databridges.databridges_sio_java_client.callbacks.callINResponse;
import io.databridges.databridges_sio_java_client.callbacks.callResponse;
import io.databridges.databridges_sio_java_client.dBridges;
import io.databridges.databridges_sio_java_client.dispatcher.dispatcher;
import io.databridges.databridges_sio_java_client.exception.dBError;
import io.databridges.databridges_sio_java_client.msgtypes.MessageTypes;
import io.databridges.databridges_sio_java_client.utils.serverMetaData;
import io.databridges.databridges_sio_java_client.utils.util;

public class rpCaller {

    private dispatcher dispatch;
    private dBridges dbcore;
    public boolean enable;
    private  cRpc rpccore;
    private Map<String, String> _sid_functionname;
    private  String serverName;
    private boolean _isOnline;
    private  String _callerType;

    public rpCaller(String serverName, dBridges dBCoreObject, cRpc rpccoreobject, String callertype) {
        this.dispatch = new dispatcher();
        this.dbcore = dBCoreObject;
        this.rpccore = rpccoreobject;

        this.enable = false;

        this._sid_functionname = new HashMap<String,String> ();
        this.serverName = serverName;
        this._isOnline = false;
        this._callerType = callertype;
    }


    public rpCaller(String serverName, dBridges dBCoreObject, cRpc rpccoreobject) {
        this.dispatch = new dispatcher();
        this.dbcore = dBCoreObject;
        this.rpccore = rpccoreobject;

        this.enable = false;

        this._sid_functionname = new HashMap<String,String> ();
        this.serverName = serverName;
        this._isOnline = false;
        this._callerType = "rpc";
    }



   public String  getServerName() {
        return this.serverName;
    }

   public boolean isOnline() {
        return this._isOnline;
    }

    public void set_isOnline(boolean value) {
        this._isOnline = value;
    }

    public void bind(String eventName , ServerEventHandler handler) throws dBError {
        if(TextUtils.isEmpty(eventName)) throw (new dBError("E076"));
        this.dispatch.bind(eventName , handler);
    }


    public void bind(String eventName , callResponse handler) throws dBError {
        if(TextUtils.isEmpty(eventName)) throw (new dBError("E076"));
        this.dispatch.bind(eventName , handler);
    }

    public void unbind(String eventName)
    {
        this.dispatch.unbind(eventName);
    }

    public void unbind()
    {
        this.dispatch.unbind();
    }


    public void _handle_callResponse(String sid, String payload, boolean isend, String rsub) {
        if (this._sid_functionname.containsKey(sid)) {

            this.dispatch.emit_rpcFunction(sid, payload, isend, rsub);
        } else {
        }
    }

   public void  _handle_tracker_dispatcher(String responseid, dBError errorcode) {
        this.dispatch.emit_rpcFunction("rpc.response.tracker", responseid, errorcode);
    }

   public void  _handle_exceed_dispatcher() {
        dBError err = new dBError("E054");
        err.updatecode("CALLEE_QUEUE_EXCEEDED", "");
        this.dispatch.emit_rpcFunction("rpc.callee.queue.exceeded", err, null);
    }


    public void emit_rpcStatus(String eventName ,  Object payload,  serverMetaData metadata)
    {
        this.dispatch.emit_rpcStatus(eventName ,  payload,  metadata);
    }


    private  String combineGetUniqueSid(String sid){
        String nsid =  ("" + Math.random()).substring(2, 8);
        if(this._sid_functionname.containsKey(nsid)){
            nsid =  ("" + Math.random()).substring(2, 8);
        }
        return nsid;
    }

    public void cleanup(String sid)
    {
        this.dispatch.unbind(sid);
        this._sid_functionname.remove(sid);
    }

    public void call(String functionName, String inparam ,long ttlms ,  callResponse handler ) {

        String sid = "";
        boolean sid_created = true;
        int loop_index = 0;
        int loop_counter = 3;
        boolean mflag = false;

        sid =  util.GenerateUniqueId();
        do {
            if(this._sid_functionname .containsKey(sid))
            {
                sid = this.combineGetUniqueSid(sid);
                loop_index++;
            }else{
                this._sid_functionname.put(sid ,  functionName);
                mflag = true;
            }

        }while((loop_index < loop_counter) && (!mflag));

        if(!mflag){
            sid =  ("" + Math.random()).substring(2, 8);
            if(!this._sid_functionname .containsKey(sid)){
                this._sid_functionname.put(sid ,  functionName);
            }else{
                sid_created = false;
            }
        }

        if(!sid_created){
            dBError dberror = (_callerType == "rpc")? new dBError("E108") : new dBError("E109");
            handler.onError(dberror);
            return;
        }

        this.rpccore.store_object(sid,this);
        Handler timeout_handler = new Handler(Looper.getMainLooper());
        String finalSid1 = sid;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                dBError dberror = (_callerType == "rpc")? new dBError("E080") : new dBError("E042");
                handler.onError(dberror);
                util.updatedBNewtworkCF(dbcore , MessageTypes.RPC_CALL_TIMEOUT, null,finalSid1,null , null , null , false , false );
                cleanup(finalSid1);
            }
        };
        timeout_handler.postDelayed(runnable, ttlms);
        try {
            String finalSid = sid;
            this.dispatch.bind(sid, new callINResponse() {
                @Override
                public void onInternalMessage(String inParameter, boolean isend, String returnsub) {
                    timeout_handler.removeCallbacks(runnable);
                    if(!isend){
                        handler.onResult(inParameter , false);
                    }else{
                        if(TextUtils.isEmpty(returnsub) || returnsub == null){
                            handler.onResult(inParameter ,  true);
                            cleanup(finalSid);
                            return;
                        }else{
                            dBError dberror;
                            switch (returnsub.toUpperCase(Locale.ROOT))
                            {
                                case "EXP":

                                    String c = "";
                                    String m= "";
                                    try {
                                        JSONObject reader = new JSONObject(inParameter);
                                        c= reader.getString("c");
                                        m=reader.getString("m");
                                    }catch (Exception e)
                                    {
                                        if(c.isEmpty()) c="";
                                        if(m.isEmpty()) m="";
                                    }
                                    dberror = (_callerType == "rpc")? new dBError("E055") : new dBError("E041");
                                    dberror.updatecode(c, m);
                                    handler.onError(dberror);
                                    break;
                                default:

                                    dberror = (_callerType == "rpc")? new dBError("E054") : new dBError("E040");
                                    dberror.updatecode(returnsub.toUpperCase(), "");
                                    handler.onError(dberror);
                                    break;
                            }
                            cleanup(finalSid);
                        }
                    }
                }
            });
        }catch (dBError dberror) {
            handler.onError(dberror);
        }
        boolean cstatus=false;
        if(this._callerType == "rpc"){
            cstatus =  util.updatedBNewtworkCF(this.dbcore , MessageTypes.CALL_RPC_FUNCTION, this.serverName, functionName , null , sid ,  inparam , false , false);
        }else{
            cstatus =  util.updatedBNewtworkCF(this.dbcore , MessageTypes.CALL_CHANNEL_RPC_FUNCTION, this.serverName, functionName , null , sid ,  inparam, false , false );
        }

        if(!cstatus) {
            if(this._callerType == "rpc"){
                handler.onError(new dBError("E079"));
            }else{
                handler.onError(new dBError("E033"));
            }
        }

    }
}
