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

import java.util.Arrays;

import io.databridges.databridges_sio_java_client.callbacks.callRecieved;
import io.databridges.databridges_sio_java_client.callbacks.cfeventHandler;

import io.databridges.databridges_sio_java_client.dBridges;
import io.databridges.databridges_sio_java_client.dispatcher.dispatcher;
import io.databridges.databridges_sio_java_client.exception.dBError;
import io.databridges.databridges_sio_java_client.utils.util;
import io.databridges.databridges_sio_java_client.msgtypes.MessageTypes;

public class cfClient {
    private dispatcher dispatch;
    private dBridges dbcore;
    public boolean enable;
    private final String[] functionsNames = {"cf.response.tracker", "cf.callee.queue.exceeded"};

    public cfClient(dBridges dBCoreObject)
    {
        this.dispatch = new dispatcher();
        this.dbcore = dBCoreObject;
        this.enable =  false;
    }


    public boolean _verify_function() throws dBError {
        boolean flag = false;
        if(this.enable){
            if(!this.dispatch.isFunctionDefined()) throw(new dBError("E009"));
            flag=true;
        }else{
            flag = true;
        }
        return flag;
    }

    public  void functions(String eventName , callRecieved handler)  throws dBError{
        if(TextUtils.isEmpty(eventName)) throw (new dBError("E066"));
        this.dispatch.bind(eventName , handler);
    }



    public void regfn(String functionName , callRecieved handler) throws dBError{
        if(TextUtils.isEmpty(functionName)) throw (new dBError("E110"));
        if(handler == null) throw (new dBError("E111"));

        if (Arrays.asList(this.functionsNames).contains(functionName)) throw (new dBError("E110"));
        this.dispatch.bind(functionName , handler);
    }

    public void unregfn(String functionName , callRecieved handler) throws dBError{
        if(TextUtils.isEmpty(functionName)) throw (new dBError("E110"));
        if(handler == null) throw (new dBError("E111"));

        if (Arrays.asList(this.functionsNames).contains(functionName)) throw (new dBError("E110"));
        this.dispatch.unbind(functionName);
    }


    public void bind(String eventName , cfeventHandler handler) throws dBError{
        if(TextUtils.isEmpty(eventName)) throw (new dBError("E066"));
        if(handler == null) throw (new dBError("E067"));

        if (!Arrays.asList(this.functionsNames).contains(eventName)) throw (new dBError("E066"));

        this.dispatch.bind(eventName , handler);
    }



    public void unbind(String eventName)
    {
        this.dispatch.unbind(eventName);
    }


    public void _handle_dispatcher(String functionName ,  String returnSubect ,  String sid , String payload)
    {
        rpcResponse response = new rpcResponse(functionName,returnSubect, sid ,  this.dbcore );
        this.dispatch.emit_clientFunction(functionName ,  payload ,  response);
    }

    public void _handle_tracker_dispatcher(String responseid ,  String errorcode)
    {
        this.dispatch.emit_clientFunction2("cf.response.tracker" ,  responseid , errorcode);
    }

    public void _handle_exceed_dispatcher()
    {
        dBError err = new dBError("E070");
        err.updatecode("CALLEE_QUEUE_EXCEEDED",  "");
        this.dispatch.emit_clientFunction2("cf.callee.queue.exceeded" ,  err , null);
    }


    public void resetqueue() throws dBError {
          boolean m_status  = util.updatedBNewtworkCF(this.dbcore, MessageTypes.CF_CALLEE_QUEUE_EXCEEDED, null, null, null, null, null, false, false);
        if (!m_status){ throw (new dBError("E068")); }
    }

}
