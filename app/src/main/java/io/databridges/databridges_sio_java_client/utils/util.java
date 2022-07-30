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

package io.databridges.databridges_sio_java_client.utils;

import android.text.TextUtils;
import java.nio.charset.StandardCharsets;
import io.databridges.databridges_sio_java_client.dBridges;

public class util {
    public static  byte[] utf8AbFromStr(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public static String GenerateUniqueId(){
        return ("" + Math.random()).substring(2, 8);
    }

    public static boolean updatedBNewtworkSC(dBridges dbcore , int dbmsgtype , String  channelName , String sid, String channelToken, String subject, String source_id, long t1, long seqnum){
        if(TextUtils.isEmpty(subject)) subject = null;
        if(TextUtils.isEmpty(channelToken)) channelToken =  null;
        if(TextUtils.isEmpty(source_id)) source_id = null;
        if(t1==0) t1 = 0;
        String tseqnum = null;
        if(seqnum!=0) tseqnum = new Long(seqnum).toString() ;

        messageStructure msgDbp =  new messageStructure();
        msgDbp.eventname= "db";
        msgDbp.dbmsgtype= dbmsgtype;
        msgDbp.subject= subject;
        msgDbp.rsub= null;
        msgDbp.sid= sid;
        msgDbp.payload= (!TextUtils.isEmpty(channelToken))? utf8AbFromStr(channelToken):utf8AbFromStr("");
        msgDbp.fenceid= channelName;
        msgDbp.rspend= false;
        msgDbp.rtrack= false;
        msgDbp.rtrackstat= null;
        msgDbp.t1= t1;
        msgDbp.latency= 0;
        msgDbp.globmatch= 0;
        msgDbp.sourceid= source_id;
        msgDbp.sourceip= null;
        msgDbp.replylatency= 0;
        msgDbp.oqueumonitorid= tseqnum;
        return dbcore.send(msgDbp);
    }

    public static boolean updatedBNewtworkCF(dBridges dbcore , int dbmsgtype , String sessionid , String functionName , String returnSubject , String sid , String payload , boolean rspend , boolean rtrack ){
        if(TextUtils.isEmpty(functionName) ) functionName = null;
        if(TextUtils.isEmpty(returnSubject)) returnSubject =  null;
        if(TextUtils.isEmpty(sid)) sid = null;
        if(TextUtils.isEmpty(payload)) payload = null;
        if(TextUtils.isEmpty(sessionid)) sessionid = null;
        messageStructure msgDbp =  new messageStructure();
        msgDbp.eventname= "db";
        msgDbp.dbmsgtype= dbmsgtype;
        msgDbp.subject= functionName;
        msgDbp.rsub= returnSubject;
        msgDbp.sid= sid;
        msgDbp.payload= (!TextUtils.isEmpty(payload))? utf8AbFromStr(payload):utf8AbFromStr("");
        msgDbp.fenceid= sessionid;
        msgDbp.rspend= rspend;
        msgDbp.rtrack= rtrack;
        msgDbp.rtrackstat= null;
        msgDbp.t1= 0;
        msgDbp.latency= 0;
        msgDbp.globmatch= 0;
        msgDbp.sourceid= null;
        msgDbp.sourceip= null;
        msgDbp.replylatency= 0;
        msgDbp.oqueumonitorid= null;
        return dbcore.send(msgDbp);
    }


}
