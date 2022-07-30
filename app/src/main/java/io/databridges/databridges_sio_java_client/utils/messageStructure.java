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

public class messageStructure {
    public String eventname;
    public int dbmsgtype;
    public String subject;
    public String rsub;
    public String sid;
    public byte[] payload;
    public String fenceid;
    public boolean rspend;
    public boolean rtrack;
    public String rtrackstat;
    public long t1;
    public long latency;
    public int globmatch;
    public String sourceid;
    public String sourceip;
    public int replylatency;
    public String oqueumonitorid;

    public messageStructure(){
        this.eventname =  "db";
        this.dbmsgtype =  0;
        this.subject=null;
        this.rsub = null;
        this.sid = null;
        this.payload = null;
        this.fenceid = null;
        this.rspend = false;
        this.rtrack= false;
        this.rtrackstat = null;
        this.t1 = 0;
        this.latency=0;
        this.globmatch = 0;
        this.sourceid ="";
        this.sourceip = null;
        this.replylatency=0;
        this.oqueumonitorid = null;
    }
}
