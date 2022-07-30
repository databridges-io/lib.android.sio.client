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


package io.databridges.databridges_sio_java_client;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.TextUtils;

import io.socket.engineio.client.transports.WebSocket;
import io.databridges.databridges_sio_java_client.androidpromise.Promise;
import io.socket.client.IO;
import io.socket.client.Socket;

import com.google.gson.Gson;
import org.json.JSONObject;


import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import io.databridges.databridges_sio_java_client.callbacks.privateHandler;
import io.databridges.databridges_sio_java_client.channel.channelStatus;
import io.databridges.databridges_sio_java_client.channel.channels;
import io.databridges.databridges_sio_java_client.channel.cresponse;
import io.databridges.databridges_sio_java_client.connection.connectionState;
import io.databridges.databridges_sio_java_client.connection.states;
import io.databridges.databridges_sio_java_client.dispatcher.dispatcher;
import io.databridges.databridges_sio_java_client.exception.dBError;
import io.databridges.databridges_sio_java_client.msgtypes.MessageTypes;
import io.databridges.databridges_sio_java_client.rpc.cRpc;
import io.databridges.databridges_sio_java_client.rpc.cfClient;
import io.databridges.databridges_sio_java_client.rpc.rpCaller;
import io.databridges.databridges_sio_java_client.rpc.rpcAccessResponse;
import io.databridges.databridges_sio_java_client.rpc.rpcStatus;
import io.databridges.databridges_sio_java_client.utils.ExtraData;
import io.databridges.databridges_sio_java_client.utils.messageStructure;
import io.databridges.databridges_sio_java_client.utils.metaData;
import io.databridges.databridges_sio_java_client.utils.restAPIResponse;

public class dBridges{

    private Socket ClientSocket;
    IO.Options options;
    private int count;
    private long uptimeTimeout;
    private int retryCount;
    private int lifeCycle;
    private boolean isServerReconnect;
    private dispatcher dispatch;


    public String  appkey;
    public String auth_url ;
    public connectionState connectionstate ;
    public channels channel;
    public String sessionid;
    public long maxReconnectionDelay;
    public long  minReconnectionDelay ;
    public double reconnectionDelayGrowFactor;
    public long  minUptime ;
    public long  connectionTimeout;
    public long maxReconnectionRetries;
    public boolean  autoReconnect;
    public cfClient cf;
    public cRpc rpc = null;

    public Emitter.Listener onErrorListener =  null;

    public dBridges(){
        this.ClientSocket = null;
        this.sessionid = null;
        this.connectionstate = new connectionState(this);
        this.channel = new channels(this);
        this.options =  new IO.Options();

        this.uptimeTimeout =  0;
        this.retryCount = 0;

        this.maxReconnectionRetries = 10;
        this.maxReconnectionDelay = 120000;
        this.minReconnectionDelay = (long)(1000 + Math.random() * 4000);
        this.reconnectionDelayGrowFactor = 1.3;
        this.minUptime = 200 ;
        this.connectionTimeout = 10000;
        this.autoReconnect = true;


        this.lifeCycle =  0;
        this.appkey="";
        this.isServerReconnect = false;
        this.dispatch =  new dispatcher();
        this.cf = new cfClient(this);
        this.rpc = new cRpc(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }




    public void access_token(privateHandler callback) throws dBError {
        this.dispatch.bind("dbridges:access_token", callback);
    }


    public void _accesstoken_dispatcher(String channelName ,  String action ,  cresponse response )
    {
        this.dispatch.emit_privateFunction("dbridges:access_token" , channelName ,  this.sessionid , action ,  response );
    }

    public void _accesstoken_dispatcher(String channelName ,  String action ,   rpcAccessResponse response )
    {
        this.dispatch.emit_privateFunction("dbridges:access_token" , channelName ,  this.sessionid , action ,  response );
    }


    private void acceptOpen(){
        this.retryCount = 0;
        this.connectionstate.reconnect_attempt =  this.retryCount;
        if(this.ClientSocket.connected()){
            if(this.lifeCycle == 0){
                this.connectionstate._handledispatcher(states.CONNECTED, null);
                this.lifeCycle++;
            }else{
                this.connectionstate._handledispatcher(states.RECONNECTED, null);
            }
        }
    }

    private long getNextDelay(){
        double delay = 0;
        if (this.retryCount > 0) {
            delay = this.minReconnectionDelay * Math.pow(this.reconnectionDelayGrowFactor, this.retryCount - 1);
            delay = (delay > this.maxReconnectionDelay)? this.maxReconnectionDelay : delay;
            delay = (delay < this.minReconnectionDelay)? this.minReconnectionDelay : delay;
        }

        return (long)delay;
    }

    public Promise my_wait(){
        Promise p = new Promise();

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                p.resolve(true);
            }
        };
        handler.postDelayed(runnable, this.getNextDelay());

        return p;
    }


    private void reconnect() throws dBError {

        if (this.retryCount >= this.maxReconnectionRetries) {
            this.connectionstate._handledispatcher(states.RECONNECT_FAILED, new dBError("E060"));

            if (this.ClientSocket != null) {
                this.ClientSocket.off();
            }

            this.channel.cleanUp_All();
            this.rpc.cleanUp_All();
            this.lifeCycle = 0;
            this.retryCount = 0;
            this.connectionstate.set_newLifeCycle(true);
            this.connectionstate._handledispatcher(states.DISCONNECTED, "");
        } else {
            this.retryCount++;
            this.my_wait()
                    .then(res -> {
                        this.connectionstate.reconnect_attempt = this.retryCount;
                        this.connectionstate._handledispatcher(states.RECONNECTING, "");
                        try {
                            this.connect();
                        } catch (dBError dberr) {
                        }
                        return true;
                    })
                    .error(err -> {
                                throw (new Error(err.toString()));
                            }
                    );
        }
    }

    public void shouldRestart(Object eventdata) throws dBError{
        if(this.autoReconnect) {
            if(!this.connectionstate.get_newLifeCycle())
            {
                this.connectionstate._handledispatcher(states.RECONNECT_ERROR, eventdata );
                this.reconnect();
                return;
            }else{
                this.connectionstate._handledispatcher(states.ERROR, eventdata );
                return;
            }
        }
    }



    public void disconnect()
    {

        this.ClientSocket.disconnect();
    }



    public void connect () throws dBError{

        if(this.retryCount==0  && !this.connectionstate.get_newLifeCycle()  ){

            this.connectionstate.set_newLifeCycle(true);
        }
        if(TextUtils.isEmpty(this.auth_url)) {
            if(this.connectionstate.get_newLifeCycle()){
                throw(new dBError("E001"));
            }else{
                throw(new dBError("E001"));
            }
        }

        if(TextUtils.isEmpty(this.appkey)) {
            if(this.connectionstate.get_newLifeCycle()){
                throw (new dBError("E002"));
            }else{
                throw (new dBError("E002"));
            }
        }

        try {
            this.cf._verify_function();
        }catch (dBError e) {
            if(this.connectionstate.get_newLifeCycle()){
                throw(e);
            }else{
                this.shouldRestart(e);
                return;
            }
        }

        restAPIResponse resR =  null;
        try {
             resR = this.GetdBRInfo(this.auth_url, this.appkey);
        }catch(dBError err){
            if (this.connectionstate.get_newLifeCycle()) {
                throw(err);
            } else {
                this.shouldRestart(err);
                return;
            }
        }

        if(!resR.result) {
            dBError db = new dBError("E008");
            db.updatecode("",resR.errMsg);

            if (this.connectionstate.get_newLifeCycle()) {
                throw (db);
            } else {
                this.shouldRestart(db);
                return;
            }
        }

        String protocol = (resR.secured)? "https://" :  "http://";
        String dbripport = protocol + resR.wsip + ":" +  resR.wsport;

        String myInt = this.cf.enable ? "1" : "0";
        this.options.query = "sessionkey=" + resR.sessionkey + "&" +
                "version="+  "1.1" + "&" +
                "libtype=" + "android-java" + "&" +
                "cf=" +  myInt;
        this.options.auth = new HashMap<>();
        this.options.auth.put("sessioney" ,  resR.sessionkey);
        this.options.auth.put("version" ,  "1.1");
        this.options.auth.put("libtype" ,  "android-java");


        this.options.auth.put("cf" ,  myInt);

        this.options.secure =  true;
        this.options.reconnectionDelay = 5;
        this.options.reconnectionAttempts = 0;
        this.options.reconnection = false;


        this.options.transports =  new String[] { WebSocket.NAME };
        this.options.timeout=   (this.connectionTimeout <= 0)? 10000 : this.connectionTimeout;
        this.options.upgrade = false;

        if(this.lifeCycle == 0){
            this.connectionstate._handledispatcher(states.CONNECTING, null);
        }
        this.isServerReconnect=false;

        dBridges dbinner = this;

        this.connectionstate.set_newLifeCycle(true);
        try {

            this.ClientSocket = IO.socket(dbripport , this.options);
            this.ClientSocket.connect();
            this.ClientSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        IOEventReconnect((String) args[0]);
                    }catch (dBError dberr) {
                    }

                }
            });


            this.ClientSocket.on("db", new Emitter.Listener(){
                @Override
                public void call(Object... args) {
                    int dbmsgtype =    (Integer) args[0];
                    String subject =  (String)args[1];
                    String rsub = (String)args[2];

                    String sid = "";
                    if(args[3] instanceof String) {
                     sid =   (String) args[3];
                    }

                    if(args[3] instanceof Integer) {
                        int isid =   (Integer) args[3];
                        sid = String.valueOf( isid);
                    }


                    byte[] payload = null;
                    if(args[4] instanceof byte[]) payload = (byte []) args[4];

                    String fenceid = (String)args[5];
                    Boolean rspend = false;
                    if(args[6] instanceof String) rspend = Boolean.valueOf((String) args[6]);
                    if(args[6] instanceof Boolean) rspend = (Boolean) args[6];

                    Boolean rtrack = false;
                    if(args[7] instanceof String) rtrack  = Boolean.valueOf((String) args[7]);
                    if(args[7] instanceof Boolean) rtrack = (Boolean) args[7];

                    String rtrackstat =   (String)args[8];

                    long t1  =0;


                    if(args[9] == null){
                        t1 = 0;
                    }else{
                        if (args[9] instanceof  Long) t1 =  (Long) args[9];
                        if (args[9] instanceof  Integer) t1 =  (Integer) args[9];
                    }

                    long latency  =0;
                    if(args[10] == null){
                        latency = 0;
                    }else{
                        if (args[10] instanceof  Long) latency =  (Long) args[10];
                        if (args[10] instanceof  Integer) latency =  (Integer) args[10];
                    }


                    int globmatch =  (args[11] == null)? 0: (Integer) args[11];
                    String sourceid = (String)args[12];
                    String sourceip = (String)args[13];

                    Boolean replylatency = false;
                    if(args[14] instanceof String) replylatency = Boolean.valueOf((String)args[14]);

                    if(args[14] instanceof Integer) {
                        replylatency = ((Integer)args[14] > 0)? true:false;
                    }


                    String oqueumonitorid = "";
                    oqueumonitorid = (args[15] instanceof String)? (String) args[15]: "";
                    oqueumonitorid = (args[15] instanceof Integer)? ((Integer) args[15]).toString(): "";

                    IOMessage(dbmsgtype, subject, rsub,  sid, payload,  fenceid,
                            rspend,  rtrack,  rtrackstat, t1,  latency,  globmatch,
                            sourceid,  sourceip, replylatency, oqueumonitorid);
                }
            });



            this.ClientSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    IOConnect();
                }
            });

            this.ClientSocket.on("connect_timeout", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    IOConnectFailed((String) args[0]);
                }
            });


            this.ClientSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    dbinner.IOError(args[0].toString());
                }
            });

        }catch (Exception e){
            throw (new Error(e.getMessage()));
        }

    }



    public restAPIResponse GetdBRInfo(String url , String api_key) throws dBError{
        restAPIResponse resR = new restAPIResponse();
        try{

            OkHttpClient client = new OkHttpClient();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            RequestBody body = RequestBody.create(JSON, "{}");
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("x-api-key", api_key)
                    .addHeader("lib-transport", "sio")
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();


            if (response.code() != 200) {
                dBError db = new dBError("E006");
                db.updatecode(Integer.toString(response.code()), response.message());
                throw db;
            }else {
                String resTxt = response.body().string();
                Gson access = new Gson();
                resR = access.fromJson(resTxt, restAPIResponse.class);
                resR.result=true;
            }
        }catch(Exception e)
        {
            dBError db = new dBError("E008");
            db.updatecode("",e.getMessage());
            throw db;
        }
        return resR;
    }





    public void IOEventReconnect(String reason) throws dBError{
        this.channel._send_OfflineEvents();
        this.rpc._send_OfflineEvents();
        switch(reason)
        {
            case "io server disconnect":
                this.connectionstate._handledispatcher(states.ERROR, new dBError("E061"));
                if(this.ClientSocket != null) { this.ClientSocket.off(); }

                if (!this.autoReconnect) {

                    this.channel.cleanUp_All();
                    this.rpc.cleanUp_All();
                    this.lifeCycle =  0;
                    this.retryCount = 0;
                    this.connectionstate.set_newLifeCycle(true);
                    this.connectionstate._handledispatcher(states.DISCONNECTED, "");
                }else{
                    this.reconnect();
                }
                break;
            case "io client disconnect":
                if(this.isServerReconnect){
                    this.connectionstate._handledispatcher(states.CONNECTION_BREAK, new dBError("E062"));
                    if(this.ClientSocket != null) { this.ClientSocket.off(); }

                    if (!this.autoReconnect) {
                        this.channel.cleanUp_All();
                        this.rpc.cleanUp_All();
                        this.lifeCycle =  0;
                        this.retryCount = 0;
                        this.connectionstate.set_newLifeCycle(true);
                        this.connectionstate._handledispatcher(states.DISCONNECTED, "");
                    }else{
                        this.reconnect();
                    }


                }else{

                    if(this.ClientSocket!= null) this.ClientSocket.off();
                    this.channel.cleanUp_All();
                    this.rpc.cleanUp_All();
                    this.lifeCycle =  0;
                    this.retryCount = 0;
                    this.connectionstate.set_newLifeCycle(true);
                    this.connectionstate._handledispatcher(states.DISCONNECTED, "");
                }

                break;
            default:

                this.connectionstate._handledispatcher(states.CONNECTION_BREAK, new dBError("E063"));

                if(this.ClientSocket!= null) this.ClientSocket.off();
                if (!this.autoReconnect) {

                    this.channel.cleanUp_All();
                    this.rpc.cleanUp_All();
                    this.lifeCycle =  0;
                    this.retryCount = 0;
                    this.connectionstate.set_newLifeCycle(true);
                    this.connectionstate._handledispatcher(states.DISCONNECTED, "");
                }else {
                    this.reconnect();
                }
                break;
        }

    }

    private void Rttpong(int dbmsgtype,String subject,String rsub, String sid, byte[] payload, String fenceid,
                         Boolean rspend, Boolean rtrack, String rtrackstat,long t1, long latency, int globmatch,
                         String sourceid, String sourceip, Boolean replylatency,String oqueumonitorid){

        if (this.ClientSocket.connected()) {
            try {
                Object []targs = new Object[16];
                targs[0] =  (Integer)dbmsgtype;
                targs[1] =  subject;
                targs[2] =  (rsub == null)? JSONObject.NULL:  rsub;
                targs[3] =   (sid == null)? JSONObject.NULL: sid;
                targs[4] =   payload;
                targs[5] =  (fenceid==null)? JSONObject.NULL : fenceid;
                targs[6] =  rspend;
                targs[7] =  rtrack;
                targs[8] =  (rtrackstat==null)? JSONObject.NULL : rtrackstat;
                targs[9] =  t1;
                targs[10] = latency;
                targs[11] = globmatch;
                targs[12] =  (sourceid==null)? JSONObject.NULL: sourceid;
                targs[13] =  (sourceip==null)? JSONObject.NULL :sourceip;
                targs[14] = replylatency;
                targs[15] = (oqueumonitorid==null)?JSONObject.NULL:oqueumonitorid;


                this.ClientSocket.emit("db", targs);


            }catch(Exception exp){
            }
        }
    }

    public void IOMessage(int dbmsgtype,String subject,String rsub, String sid, byte[] payload, String fenceid,
                          Boolean rspend, Boolean rtrack, String rtrackstat,long t1, long latency, int globmatch,
                          String sourceid, String sourceip, Boolean replylatency,String oqueumonitorid)
    {

        Date now;
        long recieved ;
        long recdlatency;
        String eventData;
        String  mpayload;
        long lib_latency;
        now = new Date();
        lib_latency =  now.getTime() -  t1;
        metaData metadata;
        String m_newchannelName="";
        switch(dbmsgtype)
        {
            case MessageTypes.SYSTEM_MSG:
                switch(subject)
                {
                    case "connection:success":
                        this.sessionid = this.mBufferToString(payload);

                        this.connectionstate.set_newLifeCycle(false);
                        this.rpc._ReSubscribeAll();
                        this.channel._ReSubscribeAll();

                        Handler handler = new Handler(Looper.getMainLooper());
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                acceptOpen();
                            }
                        };
                        handler.postDelayed(runnable, (this.minUptime < 0)? 1000 :  this.minUptime);

                        if(t1 != 0){
                            this.Rttpong(dbmsgtype, "rttpong", rsub, sid, payload, fenceid,
                            rspend, rtrack, rtrackstat, t1, lib_latency, globmatch,
                            sourceid, sourceip, replylatency, oqueumonitorid) ;
                        }

                        break;
                    case "rttpong":
                        if(t1!=0){
                            now = new Date();
                            eventData =  String.valueOf( now.getTime() -  t1);
                            this.connectionstate.set_rttms(now.getTime() -  t1);
                            this.connectionstate._handledispatcher(states.RTTPONG ,  eventData);
                        }
                        break;
                    case "reconnect":
                        this.isServerReconnect = true;
                        this.ClientSocket.disconnect();
                        break;
                    default:
                        this.connectionstate._handledispatcher(states.ERROR, this.mBufferToString(payload));
                        break;
                }

                break;
            case MessageTypes.SUBSCRIBE_TO_CHANNEL:
                switch(subject)
                {
                    case "success":
                        switch(this.channel._get_subscribeStatus(sid))
                        {
                            case channelStatus.SUBSCRIPTION_INITIATED:
                                this.channel._updateChannelsStatusAddChange(0 , sid ,channelStatus.SUBSCRIPTION_ACCEPTED, "" );
                                break;
                            case channelStatus.SUBSCRIPTION_ACCEPTED:
                            case channelStatus.SUBSCRIPTION_PENDING:
                                this.channel._updateChannelsStatusAddChange(1 , sid ,channelStatus.SUBSCRIPTION_ACCEPTED, "" );
                                break;
                        }
                        break;
                    default:
                        dBError dberr = new dBError("E064");
                        dberr.updatecode(subject.toUpperCase(), "");
                        switch(this.channel._get_subscribeStatus(sid))
                        {
                            case channelStatus.SUBSCRIPTION_INITIATED:
                                this.channel._updateChannelsStatusAddChange(0, sid ,channelStatus.SUBSCRIPTION_ERROR, dberr );
                                break;
                            case channelStatus.SUBSCRIPTION_ACCEPTED:
                            case channelStatus.SUBSCRIPTION_PENDING:
                                this.channel._updateChannelsStatusAddChange(1 , sid ,channelStatus.SUBSCRIPTION_PENDING, dberr );
                                break;
                        }
                        break;
                }

                break;
            case MessageTypes.CONNECT_TO_CHANNEL:
                switch(subject)
                {
                    case "success":

                        switch(this.channel._get_subscribeStatus(sid))
                        {
                            case channelStatus.CONNECTION_INITIATED:
                                this.channel._updateChannelsStatusAddChange(0 , sid ,channelStatus.CONNECTION_ACCEPTED, "" );
                                break;
                            case channelStatus.CONNECTION_ACCEPTED:
                            case channelStatus.CONNECTION_PENDING:
                                this.channel._updateChannelsStatusAddChange(1 , sid ,channelStatus.CONNECTION_ACCEPTED, "" );
                                break;
                        }
                        break;
                    default:
                        dBError dberr = new dBError("E084");
                        dberr.updatecode(subject.toUpperCase(), "");
                        switch(this.channel._get_subscribeStatus(sid))
                        {
                            case channelStatus.CONNECTION_INITIATED:
                                this.channel._updateChannelsStatusAddChange(0, sid ,channelStatus.CONNECTION_ERROR, dberr );
                                break;
                            case channelStatus.CONNECTION_ACCEPTED:
                            case channelStatus.CONNECTION_PENDING:
                                this.channel._updateChannelsStatusAddChange(1 , sid ,channelStatus.CONNECTION_PENDING, dberr );
                                break;
                        }
                        break;
                }
                break;
            case MessageTypes.UNSUBSCRIBE_DISCONNECT_FROM_CHANNEL:
                switch(subject)
                {
                    case "success":

                        switch(this.channel._get_channelType(sid))
                        {
                            case "s":
                                this.channel._updateChannelsStatusRemove(sid , channelStatus.UNSUBSCRIBE_ACCEPTED, "");
                                break;
                            case "c":
                                this.channel._updateChannelsStatusRemove(sid , channelStatus.DISCONNECT_ACCEPTED, "");
                                break;
                        }
                        break;
                    default:
                        switch(this.channel._get_channelType(sid))
                        {
                            case "s":
                                this.channel._updateChannelsStatusRemove(sid , channelStatus.UNSUBSCRIBE_ERROR, "");
                                break;
                            case "c":
                                this.channel._updateChannelsStatusRemove(sid , channelStatus.DISCONNECT_ERROR, "");
                                break;
                        }
                        break;
                }
                break;

            case MessageTypes.PUBLISH_TO_CHANNEL:
                now = new Date();
                recieved = now.getTime();
                recdlatency =  recieved - t1;
                metaData cmetadata =  new metaData( this.channel._get_channelName(sid),
                                                    subject ,
                                                    sourceid,
                                                    oqueumonitorid,
                                                    sourceip,
                                                    recieved);
                mpayload = null;
                try {
                    mpayload = (payload.length > 0 )? this.mBufferToString(payload) : "";
                } catch (Exception error) {
                    mpayload = "";
                }

                this.channel._handledispatcherEvents(subject , mpayload , cmetadata.channelname, cmetadata);
                break;
            case MessageTypes.PARTICIPANT_JOIN:
                metadata = new metaData();
                metadata.eventname =  "dbridges:participant.joined";
                m_newchannelName = this.channel._get_channelName(sid);
                metadata.channelname = m_newchannelName;
                metadata.sourcesysid = sourceid;
                metadata.sessionid = sourceip;
                metadata.sqnum = oqueumonitorid;

                if (m_newchannelName.toLowerCase().startsWith("sys:") || m_newchannelName.toLowerCase().startsWith("prs:")) {
                    ExtraData extradata =  this.convertToObject(sourceip, sourceid, fenceid);
                    if (m_newchannelName.toLowerCase().startsWith("sys:*")) {
                        metadata.sessionid = extradata.sessionid;
                        metadata.sourcesysid = extradata.sourcesysid;

                        this.channel._handledispatcherEvents("dbridges:participant.joined", extradata, m_newchannelName,metadata);
                    }else {
                        metadata.sessionid = extradata.sessionid;
                        metadata.sourcesysid = extradata.sourcesysid;
                        this.channel._handledispatcherEvents("dbridges:participant.joined", extradata, m_newchannelName, metadata);
                    }
                } else {
                    JSONObject obj = new JSONObject();
                    try{
                        obj.put("sourcesysid", sourceid);
                    }catch (Exception e)
                    {
                    }

                    this.channel._handledispatcherEvents("dbridges:participant.left", obj.toString() , m_newchannelName, metadata);
                }
                break;

            case MessageTypes.PARTICIPANT_LEFT:


                metadata = new metaData();
                metadata.eventname =  "dbridges:participant.left";
                m_newchannelName = this.channel._get_channelName(sid);
                metadata.channelname = m_newchannelName;
                metadata.sourcesysid = sourceid;
                metadata.sessionid = sourceip;
                metadata.sqnum = oqueumonitorid;

                if (m_newchannelName.toLowerCase().startsWith("sys:") || m_newchannelName.toLowerCase().startsWith("prs:")) {
                    ExtraData extradata =  this.convertToObject(sourceip, sourceid, fenceid);
                    if (m_newchannelName.toLowerCase().startsWith("sys:*")) {
                        metadata.sessionid = extradata.sessionid;
                        metadata.sourcesysid = extradata.sourcesysid;

                        this.channel._handledispatcherEvents("dbridges:participant.left", extradata, m_newchannelName,metadata);
                    }else {
                        metadata.sessionid = extradata.sessionid;
                        metadata.sourcesysid = extradata.sourcesysid;

                        this.channel._handledispatcherEvents("dbridges:participant.left", extradata, m_newchannelName, metadata);
                    }
                } else {
                    JSONObject obj = new JSONObject();
                    try{
                        obj.put("sourcesysid", sourceid);
                    }catch (Exception e)
                    {
                    }

                    this.channel._handledispatcherEvents("dbridges:participant.left", obj.toString() , m_newchannelName, metadata);
                }
                break;
            case MessageTypes.CF_CALL_RECEIVED:

                mpayload = "";
                try {
                    mpayload = (payload != null)? this.mBufferToString(payload): "";
                } catch (Exception e) {
                    mpayload = "";
                }
                this.cf._handle_dispatcher (subject , rsub , sid ,mpayload);


                break;
            case MessageTypes.CF_RESPONSE_TRACKER:
                this.cf._handle_tracker_dispatcher(subject , rsub);
                break;
            case MessageTypes.CF_CALLEE_QUEUE_EXCEEDED:
                this.cf._handle_exceed_dispatcher();
                break;

            case MessageTypes.CONNECT_TO_RPC_SERVER:
                switch(subject)
                {
                    case "success":

                        switch(this.rpc._get_rpcStatus(sid))
                        {
                            case rpcStatus.RPC_CONNECTION_INITIATED:
                                this.rpc._updateRegistrationStatusAddChange(0 , sid , rpcStatus.RPC_CONNECTION_ACCEPTED, "" );
                                break;
                            case rpcStatus.RPC_CONNECTION_ACCEPTED:
                            case rpcStatus.RPC_CONNECTION_PENDING:
                                this.rpc._updateRegistrationStatusAddChange(1 , sid ,rpcStatus.RPC_CONNECTION_ACCEPTED, "" );
                                break;
                        }
                        break;
                    default:
                        switch(this.rpc._get_rpcStatus(sid))
                        {
                            case rpcStatus.RPC_CONNECTION_INITIATED:
                                this.rpc._updateRegistrationStatusAddChange(0 , sid , rpcStatus.RPC_CONNECTION_ERROR, "" );
                                break;
                            case rpcStatus.RPC_CONNECTION_ACCEPTED:
                            case rpcStatus.RPC_CONNECTION_PENDING:
                                this.rpc._updateRegistrationStatusAddChange(1 , sid ,rpcStatus.RPC_CONNECTION_ERROR, "" );
                                break;
                        }
                        break;
                }
                break;
            case MessageTypes.RPC_CALL_RESPONSE:

                mpayload = "";
                try {
                    mpayload = (payload != null)? this.mBufferToString(payload): "";
                } catch (Exception e) {
                    mpayload = "";
                }
                 rpCaller rpccaller = this.rpc.get_object(sid);
                rpccaller._handle_callResponse (sid ,mpayload,rspend, rsub);
                break;


        }
    }


    private ExtraData convertToObject(String sourceip, String sourceid, String channelname)
    {

        ExtraData ed =  new ExtraData();

        if (!sourceid.isEmpty()) {
            String [] strData = sourceid.split("#");
            if (strData.length > 0) ed.sessionid = strData[0];
            if (strData.length > 1) ed.libtype = strData[1];
            if (strData.length > 2) ed.sourceipv4 = strData[2];
            if (strData.length > 3) ed.sourceipv6 = strData[3];
            if (strData.length >= 4) ed.sourcesysid = strData[4];
        }
        ed.sysinfo = sourceip;

        if(!channelname.isEmpty()) ed.channelname =  channelname;
        return ed;
    }





    public String mBufferToString ( byte []buffer ) {

        String str = new String(buffer, StandardCharsets.UTF_8);

        return str;
    }

    public void IOConnect()
    {
    }

    public void IOConnectFailed(String info)
    {
        this.connectionstate._handledispatcher(states.ERROR, info);
        if(this.ClientSocket!= null) this.ClientSocket.off();

        if(this.autoReconnect)  {
            try {
                this.reconnect();
            }catch (dBError dberr){
            }

        }
    }

    public void IOError(String err)
    {
        this.connectionstate._handledispatcher(states.ERROR, err);

        if(this.ClientSocket != null) this.ClientSocket.off();

        if(this.autoReconnect) {
            try {
                this.reconnect();
            }catch (dBError dberr){
            }
        }
    }


    public boolean _isSocketConnected(){
        return (this.ClientSocket!=null)? this.ClientSocket.connected(): false;
    }

    public boolean send(messageStructure msgDbp)
    {
        boolean flag = false;

        if(this.ClientSocket.connected()){

            Object []targs = new Object[16];
            targs[0] =  (Integer)msgDbp.dbmsgtype;
            targs[1] =  (msgDbp.subject == null)?JSONObject.NULL: msgDbp.subject;
            targs[2] =  (msgDbp.rsub==null)? JSONObject.NULL:msgDbp.rsub;
            targs[3] =  (msgDbp.sid==null)?JSONObject.NULL:msgDbp.sid;
            targs[4] =  msgDbp.payload;
            targs[5] =  (msgDbp.fenceid ==null)?JSONObject.NULL:msgDbp.fenceid;
            targs[6] =  msgDbp.rspend;
            targs[7] =  msgDbp.rtrack;
            targs[8] =  (msgDbp.rtrackstat ==null)?JSONObject.NULL:msgDbp.rtrackstat;
            targs[9] =  (Long) msgDbp.t1;
            targs[10] = (Long) msgDbp.latency;
            targs[11] = (Integer) msgDbp.globmatch;
            targs[12] = (msgDbp.sourceid==null)?JSONObject.NULL:msgDbp.sourceid;
            targs[13] = (msgDbp.sourceip==null)?JSONObject.NULL:msgDbp.sourceip;
            targs[14] = (Integer)msgDbp.replylatency;
            targs[15] = (msgDbp.oqueumonitorid ==null)? JSONObject.NULL: msgDbp.oqueumonitorid;
            try {
                this.ClientSocket.emit("db", targs);
            }catch(Exception e)
            {
                return  false;
            }
            flag = true;
        }
        return flag;
    }
}
