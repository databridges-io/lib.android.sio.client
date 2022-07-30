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

public class cresponse {
    private
        channels dbchannel;
        String channelName;
        String sid;
        int mtype;

    public cresponse(int m_type , String channelName , String sid , channels channel) {
        this.dbchannel = channel;
        this.mtype = m_type;
        this.channelName = channelName;
        this.sid =  sid;
    }

    public void end(caccess data) {
        this.dbchannel._send_to_dbr(this.mtype ,  this.channelName , this.sid , data );
    }

    public void exception(String info) {

        caccess result = new caccess(9 , info , "");
        this.dbchannel._send_to_dbr(this.mtype ,  this.channelName , this.sid , result );
    }
}
