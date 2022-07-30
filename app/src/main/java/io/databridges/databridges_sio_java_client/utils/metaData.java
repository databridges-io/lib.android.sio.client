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

import java.util.Date;

public class metaData {
    public String  channelname="";
    public String  eventname="";
    public String  sourcesysid="";
    public String  sqnum="";
    public String  sessionid="";
    public long  intime=0;
    public boolean isError=false;

    public metaData()
    {
        this.channelname="";
        this.eventname="";
        this.sourcesysid=null;
        this.sqnum=null;
        this.sessionid=null;
        this.intime=new Date().getTime();
        this.isError = false;
    }
    public metaData(String c, String e , String s, String sq , String sid , long it)
    {
        this.channelname=c;
        this.eventname=e;
        this.sourcesysid=s;
        this.sqnum=sq;
        this.sessionid=sid;
        this.intime=it;
    }


    @Override
    public String toString() {
        return new StringBuilder()
                .append("{channelName:").append(this.channelname)
                .append(", eventName:").append(this.eventname)
                .append(", sourcesysid:").append(this.sourcesysid)
                .append(", sqnum:").append(this.sqnum)
                .append(", sessionid:").append(this.sessionid)
                .append(", intime:").append(this.intime)
                .append("}").toString();
    }
}
