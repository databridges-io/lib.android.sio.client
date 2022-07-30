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

import io.databridges.databridges_sio_java_client.dBridges;
import io.databridges.databridges_sio_java_client.exception.dBError;
import io.databridges.databridges_sio_java_client.msgtypes.MessageTypes;
import io.databridges.databridges_sio_java_client.utils.util;

public class rpcSResponse {
    private String functionName;
    private boolean tracker;
    private String returnSubsect;
    private dBridges dbcore;
    private String sid;
    private boolean isend;
    public String id;


    public rpcSResponse(String functionName , String returnSubect, String sid , dBridges dbcoreobject)
    {
        this.functionName = functionName;
        this.returnSubsect = returnSubect;
        this.sid = sid;
        this.dbcore = dbcoreobject;
        this.isend = false;
        this.id =  returnSubect;
        this.tracker = false;
    }

    public void next(String data) throws dBError {
        if(!this.isend){
            boolean cstatus =   util.updatedBNewtworkCF(this.dbcore ,  MessageTypes.RPC_CALL_RESPONSE ,null , this.returnSubsect , null , this.sid , data , this.isend, this.tracker);
            if(!cstatus) throw(new dBError("E079"));
        }else{
            throw(new dBError("E106"));
        }

    }


    public void  end(String data) throws dBError{
        if(!this.isend){
            this.isend = true;
            boolean cstatus =   util.updatedBNewtworkCF(this.dbcore ,  MessageTypes.RPC_CALL_RESPONSE ,null,  this.returnSubsect , null , this.sid , data , this.isend, this.tracker);

            if(!cstatus) throw(new dBError("E079"));

        }else{
            throw(new dBError("E106"));
        }

    }


    public void  exception(int expCode , String expShortMessage) throws dBError{
        String epayload = "{\"c\": " + String.valueOf(expCode) + ", \"m\": " + expShortMessage + "}" ;

        if(!this.isend){
            this.isend = true;
            boolean cstatus =   util.updatedBNewtworkCF(this.dbcore ,  MessageTypes.RPC_CALL_RESPONSE ,null, this.returnSubsect , "EXP" , this.sid , epayload , this.isend, this.tracker);

            if(!cstatus) throw(new dBError("E079"));

        }else{
            throw(new dBError("E106"));
        }
    }

}
