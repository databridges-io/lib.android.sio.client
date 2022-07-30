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

package io.databridges.databridges_sio_java_client.exception;


public class dBError extends Exception {

    public  String source = "";
    public String code = "";
    public  String message = "";
    private String _EKEY = "";

    public dBError(String ekey) {
        boolean mflag = false;
        if (dBErrorMessage.errMsg.containsKey(ekey)) {
            this._EKEY = ekey;
            int[] value = dBErrorMessage.errMsg.get(ekey);

            if (value.length == 2) {

                if (dBErrorMessage.source_lookup.containsKey(value[0])) {
                    this.source = dBErrorMessage.source_lookup.get(value[0]);
                    mflag = true;
                } else {
                    this.source = "INTE_" + ekey + "_" + value[0];
                }

                if (dBErrorMessage.code_lookup.containsKey((value[1]))) {
                    this.code = dBErrorMessage.code_lookup.get(value[1]);
                } else {
                    String temp = (mflag) ? this.source : new Integer(value[0]).toString();
                    this.code = "INTE_" + ekey + "_" + temp + "_" + value[1];
                }
            } else {
                this.source =  ekey;
                this.code = ekey;
                this.message = "contact support....";
            }
        }else{
            this.source =  ekey;
            this.code = ekey;
            this.message = "contact support....";
        }
    }

    public dBError(String ekey, String []elist) {
        boolean mflag = false;

        if (dBErrorMessage.errMsg.containsKey(ekey)) {
            this._EKEY = ekey;
            int[] value = dBErrorMessage.errMsg.get(ekey);
            if (value.length == 2) {
                if (dBErrorMessage.source_lookup.containsKey(value[0])) {
                    this.source = dBErrorMessage.source_lookup.get(value[0]);
                    mflag = true;
                } else {
                    this.source = "INTE_" + ekey + "_" + value[0];
                }

                if (dBErrorMessage.code_lookup.containsKey((value[1]))) {
                    this.code = dBErrorMessage.code_lookup.get(value[1]);
                } else {
                    String temp = (mflag) ? this.source : new Integer(value[0]).toString();
                    this.code = "INTE_" + ekey + "_" + temp + "_" + value[1];
                }
                if(elist != null) {
                    if (elist.length > 0) {
                        String stemp = "";
                        for (int i =0; i<elist.length; i++)
                        {
                            stemp =  stemp + elist[i];
                            if((i+1) < elist.length )  stemp =  stemp + ",";
                        }
                        this.message = stemp;
                    }
                }
            } else {

                this.source =  ekey;
                this.code = ekey;
                this.message = "contact support....";

            }
        }else{

            this.source =  ekey;
            this.code = ekey;
            this.message = "contact support....";
        }
    }


    public void updatecode(String code, String message) {
        int itemp = this.code.length() - 1;
        if(this.code.charAt(itemp) != '_'){
            this.code = this.code + "_" +  code;
        }else{
            this.code = this.code + code;
        }
        if (message != "") this.message = message;
    }

    public String getEKEY() {
        return this._EKEY;
    }
}
