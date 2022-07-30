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

package io.databridges.databridges_sio_java_client.androidpromise;


import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;


public class Promise {

    private static final String TAG = "Promise";
    private Handler handler;
    private OnSuccessListener onSuccessListener;
    private OnErrorListener onErrorListener;
    private Promise child;
    private boolean isResolved;
    private Object resolvedObject;
    private boolean isRejected;
    private Object rejectedObject;
    private Object tag;


    public Promise() {
        this.handler = new Handler(Looper.getMainLooper());
    }

    public static Promise chain(Object obj) {
        Promise p = new Promise();
        p.resolve(obj);
        return p;
    }

    public static Promise all(Promise... list) {
        Promise p = new Promise();
        int size = 0;
        if (list != null) {
            size = list.length;
        }
        Object results[] = new Object[size];
        if (list == null || list.length <= 0) {
            p.resolve(results);
            return p;
        }

        if (list != null && list.length > 0) {
            new Runnable() {
                int completedCount = 0;

                @Override
                public void run() {
                    for (int i = 0; i < list.length; i++) {
                        Promise promise = list[i];
                        promise.setTag(i);
                        promise.then(res -> {
                            results[(int) promise.getTag()] = res;
                            completed(null);
                            return res;
                        }).error(err -> {
                            completed(err);
                        });
                    }
                }

                private void completed(Object err) {
                    completedCount++;
                    if (err != null) {
                        p.reject(err);
                    } else if (completedCount == list.length) {
                        p.resolve(results);
                    }
                }
            }.run();
        } else {
            p.resolve(results);
        }


        return p;
    }

    public static Promise series(List<?> list, OnSuccessListener listener) {
        Promise p = new Promise();
        int size = 0;
        if (list != null) {
            size = list.size();
        }
        ArrayList<Object> results = new ArrayList<>(size);
        if (list == null || listener == null || list.size() <= 0) {
            p.resolve(results);
            return p;
        }

        new Runnable() {
            int index = -1;
            int completedCount = 0;

            @Override
            public void run(){
                index++;
                if (index < list.size()) {
                        handleSuccess(index, list.get(index));
                } else {
                    p.resolve(results);
                }
            }

            private void handleSuccess(int index, Object object) {
                Object res = listener.onSuccess(object);
                results.add(index, res);
                if (res instanceof Promise) {
                    Promise pro = (Promise) res;
                    pro.setTag(index);
                    pro.then(r -> {
                        results.set((int) pro.getTag(), r);
                        if (!completed(null)) {
                            run();
                        }
                        return r;
                    }).error(err -> completed(err));
                } else if (!completed(null)) {
                    run();
                }

            }

            private boolean completed(Object err) {
                completedCount++;
                if (err != null) {
                    p.reject(err);
                } else if (completedCount == list.size()) {
                    p.resolve(results);
                    return true;
                }
                return false;
            }

        }.run();

        return p;
    }

    public static Promise parallel(List<?> list, OnSuccessListener listener) {
        Promise p = new Promise();
        int size = 0;
        if (list != null) {
            size = list.size();
        }
        ArrayList<Object> results = new ArrayList<>(size);
        if (list == null || listener == null || list.size() <= 0) {
            p.resolve(results);
            return p;
        }

        new Runnable() {
            int completedCount = 0;

            @Override
            public void run() {
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {

                            handleSuccess(i, list.get(i));

                    }
                } else {
                    p.resolve(results);
                }
            }

            private void handleSuccess(int index, Object object) {
                Object res = listener.onSuccess(object);
                results.add(index, res);
                if (res instanceof Promise) {
                    Promise pro = (Promise) res;
                    pro.setTag(index);
                    pro.then(r -> {
                        results.set((int) pro.getTag(), r);
                        completed(null);
                        return r;
                    }).error(err -> completed(err));
                } else {
                    completed(null);
                }

            }

            private void completed(Object err) {
                completedCount++;
                if (err != null) {
                    p.reject(err);
                } else if (completedCount == list.size()) {
                    p.resolve(results);
                }
            }

        }.run();

        return p;
    }

    /**
     * When you want to execute some operations parallelly with some parallel excution limit,
     * then you can use this function.
     *
     * @param list     List over which you want to execute operation.
     * @param limit    this is limit to your parallel execution.
     * @param listener
     * @return This will return the result after completion of entire execution.
     */
    public static Promise parallelWithLimit(List<?> list, int limit, OnSuccessListener listener) {

        Promise p = new Promise();
        if (list == null || listener == null || list.size() <= 0) {
            return null;
        }

        if (limit <= 0) {
            return null;
        }

        int[] count = new int[1];
        int[] iteration = new int[1];
        ArrayList childList = new ArrayList<>();

        Promise
                .series(list, object -> {
                    Promise pro = new Promise();
                    count[0]++;
                    iteration[0]++;
                    if (count[0] < limit) {
                        childList.add(object);
                        if (iteration[0] == list.size()) {
                            parallel(childList, listener)
                                    .then(res -> {
                                        pro.resolve(res);
                                        count[0] = 0;
                                        childList.clear();
                                        return res;
                                    });
                        } else {
                            pro.resolve(true);
                        }

                    } else if (count[0] == limit) {
                        childList.add(object);
                        parallel(childList, listener)
                                .then(res -> {
                                    pro.resolve(res);
                                    count[0] = 0;
                                    childList.clear();
                                    return res;
                                });
                    }

                    return pro;

                })
                .then(res -> {
                    p.resolve(res);
                    return res;
                });

        return p;
    }

    /**
     * Call this function with your resultant value, it will be available
     * in following `then()` function call.
     *
     * @param object your resultant value (any type of data you can pass as argument
     *               e.g. int, String, List, Map, any Java object)
     * @return This will return the resultant value you passed in the function call
     */
    public Object resolve(Object object){
        if (!isResolved) {
            isResolved = true;
            resolvedObject = object;
            if (onSuccessListener != null) {
                if (handler != null) {
                        handler.post(() -> handleSuccess(child, resolvedObject));

                } else {
                    new Thread(() -> handleSuccess(child, resolvedObject)).start();
                }
            }
        } else {
        }
        return object;
    }

    /**
     * Call this function with your error value, it will be available
     * in following `error()` function call.
     *
     * @param object your error value (any type of data you can pass as argument
     *               e.g. int, String, List, Map, any Java object)
     * @return This will return the error value you passed in the function call
     */
    public Object reject(Object object) {
        if (!isRejected) {
            isRejected = true;
            rejectedObject = object;
            if (onErrorListener != null) {
                if (handler != null) {
                    handler.post(() -> handleError(onErrorListener));
                } else {
                    new Thread(() -> handleError(rejectedObject)).start();
                }
            } else {
                if (child != null) {
                    child.reject(object);
                }
            }
        } else {
        }
        return object;
    }

    /**
     * After executing asyncronous function the result will be available in the success listener
     * as argument.
     *
     * @param listener OnSuccessListener
     * @return It returns a promise for satisfying next chain call.
     */

    public Promise then(OnSuccessListener listener) {
        onSuccessListener = listener;
        child = new Promise();
        if (isResolved) {
            if (handler != null) {
                handler.post(() -> handleSuccess(child, resolvedObject));
            } else {
                new Thread(() -> handleSuccess(child, resolvedObject)).start();
            }
        }
        return child;
    }

    /**
     * This function must call at the end of the `then()` cain, any `reject()` occurs in
     * previous execution this function will be called.
     *
     * @param listener
     */
    public void error(OnErrorListener listener) {
        onErrorListener = listener;
        if (isRejected) {
            if (handler != null) {
                handler.post(() -> handleError(onErrorListener));
            } else {
                new Thread(() -> handleError(rejectedObject)).start();
            }
        }
    }

    private void handleSuccess(Promise child, Object object) {
        if (onSuccessListener != null) {
            Object res = onSuccessListener.onSuccess(object);
            if (res != null) {
                if (res instanceof Promise) {
                    if (child != null) {
                        Promise p = (Promise) res;
                        p.onSuccessListener = child.onSuccessListener;
                        p.onErrorListener = child.onErrorListener;
                        p.child = child.child;

                        if (p.isResolved) {
                            p.handleSuccess(p.child, p.resolvedObject);
                        } else if (p.isRejected) {
                            p.handleError(p.rejectedObject);
                        }
                    }
                } else if (child != null) {
                    child.resolve(res);
                }
            } else {
                if (child != null) {
                    child.resolve(res);
                }
            }
        }
    }

    private void handleError(Object object) {
        if (onErrorListener != null) {
            onErrorListener.onError(object);
        } else if (child != null) {
            child.reject(object);
        }
    }

    private Object getTag() {
        return tag;
    }

    private void setTag(Object tag) {
        this.tag = tag;
    }

    public interface OnSuccessListener {
        Object onSuccess(Object object);
    }

    public interface OnErrorListener {
        void onError(Object object);
    }


}
