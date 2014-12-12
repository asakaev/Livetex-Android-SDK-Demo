package nit.livetex.livetexsdktestapp;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import org.apache.thrift.TException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import livetex.sdk.Livetex;
import livetex.sdk.handler.AHandler;
import livetex.sdk.handler.IInitHandler;
import livetex.sdk.handler.INotificationDialogHandler;
import livetex.sdk.models.Abuse;
import livetex.sdk.models.Department;
import livetex.sdk.models.DialogState;
import livetex.sdk.models.Employee;
import livetex.sdk.models.FileMessage;
import livetex.sdk.models.HoldMessage;
import livetex.sdk.models.TextMessage;
import livetex.sdk.models.TypingMessage;
import livetex.sdk.models.VoteType;

/**
 * Created by sergey.so on 05.11.2014.
 *
 *
 */
public class MainApplication extends Application {

    private static final String API_KEY = "dev_key_test";
    private static final String DEPARTMENT_STATUS = "online";
    private static final String OPERATOR_STATUS = "online";

    public static final String ACTION_RECIEVER = "nit.livetex.livetexsdktestapp.RESULT_RECIEVER";

    public static final String KEY_RESULT_CODE = "result_code";
    public static final String KEY_REQUEST_NAME = "request_name";
    public static final String KEY_RESULT_OBJECT = "result_object";

    public static final String REQUEST_INIT = "request_init";
    public static final String REQUEST_DEPARTMENTS = "request_departments";
    public static final String REQUEST_OPERATORS = "request_operators";
    public static final String REQUEST_DIALOG = "request_dialog";
    public static final String REQUEST_SEND_MSG = "request_send_msg";
    public static final String REQUEST_MSG_HISTORY = "request_msg_history";
    public static final String REQUEST_VOTE = "request_vote";
    public static final String REQUEST_RECIEVE_MSG = "req_recieve_msg";
    public static final String REQUEST_HOLD_MSG = "req_hold_msg";
    public static final String REQUEST_RECIEVE_FILE = "req_recieve_file";
    public static final String REQUEST_UPDATE_STATE = "req_update_dialog_state";
    public static final String REQUEST_OPERATOR_TYPING = "req_operator_typing";
    public static final String REQUEST_SET_NAME = "req_set_name";
    public static final String REQUEST_GET_STATE = "req_get_state";
    public static final String REQUEST_CONFIRM_MSG = "req_confirm_msg";
    public static final String REQUEST_CLOSE_CHAT = "req_close_chat";
    public static final String REQUEST_CLIENT_TYPING = "req_client_typing";

    public static final int VALUE_RESULT_ERR = -1;
    public static final int VALUE_RESULT_OK = 0;


    private static Livetex sLiveTex;
    private static MainApplication instance;
    private static String sLastEmployee = null;

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static void initLivetex(String id) {
        sLiveTex = new Livetex.Builder(getInstance(), API_KEY, id)
                .addAuthUrl("http://192.168.78.14:10010/")
                .setLogEnabled(true).build();
        sLiveTex.init(new IInitHandler() {
            @Override
            public void onSuccess(String s) {
                sendReciver(VALUE_RESULT_OK, REQUEST_INIT, s);
            }

            @Override
            public void onError(String s) {
                sendReciver(VALUE_RESULT_ERR, REQUEST_INIT, s);
            }
        });
        sLiveTex.setNotificationDialogHandler(new INotificationDialogHandler() {

            @Override
            public void ban(String s) throws TException {

            }

            @Override
            public void updateDialogState(livetex.sdk.models.DialogState dialogState) throws TException {
                sendReciver(VALUE_RESULT_OK, REQUEST_UPDATE_STATE, dialogState);
            }

            @Override
            public void receiveFileMessage(FileMessage fileMessage) throws TException {
                sendReciver(VALUE_RESULT_OK, REQUEST_RECIEVE_FILE, fileMessage);
            }

            @Override
            public void receiveTextMessage(livetex.sdk.models.TextMessage textMessage) throws TException {
                sendReciver(VALUE_RESULT_OK, REQUEST_RECIEVE_MSG, textMessage);
            }

            @Override
            public void confirmTextMessage(String textMessage) throws TException {

            }

            @Override
            public void receiveHoldMessage(HoldMessage holdMessage) throws TException {
                sendReciver(VALUE_RESULT_OK, REQUEST_HOLD_MSG, holdMessage);
                Log.d("livetex_sdl", "HOLD_MSG!!!");
            }

            @Override
            public void receiveTypingMessage(TypingMessage typingMessage) throws TException {
                sendReciver(VALUE_RESULT_OK, REQUEST_OPERATOR_TYPING, typingMessage);
            }

            @Override
            public void onError(String s) {

            }
        });
    }

    public static void stopLivetex() {
        if (sLiveTex != null)
            sLiveTex.destroy();
    }

    public static void setLastEmployee(String employeeId) {
        sLastEmployee = employeeId;
    }

    public static String getLastEmployee() {
        return sLastEmployee;
    }

    public static void getDialogState() {
        if (sLiveTex != null)
            sLiveTex.getState(buildHandler(REQUEST_GET_STATE, DialogState.class));
    }

    public static void getDepartments() {
        if (sLiveTex != null)
            sLiveTex.getDepartments(DEPARTMENT_STATUS, new AHandler<ArrayList<Department>>() {
                @Override
                public void onError(String s) {
                    sendReciver(VALUE_RESULT_ERR, REQUEST_DEPARTMENTS, s);
                }

                @Override
                public void onResultRecieved(ArrayList<Department> departments) {
                    sendReciver(VALUE_RESULT_OK, REQUEST_DEPARTMENTS, departments);
                }
            });
    }

    public static void getOperators() {
        if (sLiveTex != null)
            sLiveTex.getOperators(OPERATOR_STATUS, new AHandler<ArrayList<Employee>>() {
                @Override
                public void onError(String s) {
                    sendReciver(VALUE_RESULT_ERR, REQUEST_OPERATORS, s);
                }

                @Override
                public void onResultRecieved(ArrayList<Employee> employees) {
                    sendReciver(VALUE_RESULT_OK, REQUEST_OPERATORS, employees);
                }
            });
    }

    public static void requestDialog() {
        if (sLiveTex != null) {
            sLiveTex.requestDialog(buildHandler(REQUEST_DIALOG, DialogState.class));
        }
    }

    public static void requestDialogByEmployee(String id){
        if (sLiveTex != null) {
            sLiveTex.requestDialogByOperator(id, buildHandler(REQUEST_DIALOG, DialogState.class));
        }
    }

    public static void requestDialogByDepartment(String id){
        if (sLiveTex != null) {
            sLiveTex.requestDialogByDepartment(id, buildHandler(REQUEST_DIALOG, DialogState.class));
        }
    }

    public static void sendMsg(String msg) {
        if (sLiveTex != null)
            sLiveTex.sendTextMessage(msg,buildHandler(REQUEST_SEND_MSG, TextMessage.class));
    }

    public static void getMsgHistory(int limit, int offset) {
        if (sLiveTex != null)
            sLiveTex.messageHistory((short) limit, (short) offset, new AHandler<List<TextMessage>>() {
                @Override
                public void onError(String s) {
                    sendReciver(VALUE_RESULT_ERR, REQUEST_MSG_HISTORY, s);
                }

                @Override
                public void onResultRecieved(List<TextMessage> messages) {
                    sendReciver(VALUE_RESULT_OK, REQUEST_MSG_HISTORY, (ArrayList<TextMessage>) messages); //todo поправить
                }
            });
    }

    public static void setName(String name) {
        if (sLiveTex != null) {
            sLiveTex.setName(name, buildHandler(REQUEST_SET_NAME));
        }
    }

    public static void abuse(String name, String msg) {
        if (sLiveTex != null)
            sLiveTex.abuse(new Abuse(name, msg), buildHandler(REQUEST_VOTE));
    }

    public static void vote(boolean isLike) {
        if (sLiveTex != null) {
            VoteType vote = isLike ? VoteType.GOOD : VoteType.BAD;
            sLiveTex.vote(vote, buildHandler(REQUEST_VOTE));
        }
    }

    public static void confirmMsg(String msg) {
        if (sLiveTex != null) {
            sLiveTex.confirmTextMessage(msg, buildHandler(REQUEST_CONFIRM_MSG));
        }
    }

    public static void closeDialog() {
        if (sLiveTex != null) {
            sLiveTex.close(buildHandler(REQUEST_CLOSE_CHAT, DialogState.class));
        }
    }

    public static void typing(String text) {
        if (sLiveTex != null) {
            TypingMessage msg = new TypingMessage();
            msg.setText(text);
            sLiveTex.typing(msg, buildHandler(REQUEST_CLIENT_TYPING));
        }
    }

    private static AHandler buildHandler(final String request) {
        return new AHandler() {

            @Override
            public void onError(String s) {
                sendReciver(VALUE_RESULT_ERR, request, s);
            }

            @Override
            public void onResultRecieved(Object o) {
                sendReciver(VALUE_RESULT_OK, request, null);
            }
        };
    }

    private static <T> AHandler<T> buildHandler(final String request, final Class<T> classOfT) {
        return new AHandler<T>() {

            @Override
            public void onError(String s) {
                sendReciver(VALUE_RESULT_ERR, request, s);
            }

            @Override
            public void onResultRecieved(T t) {
                sendReciver(VALUE_RESULT_OK, request, (Serializable) t);
            }
        };
    }

    private static void sendReciver(int code, String request, Serializable o) {
        Intent intent = new Intent(ACTION_RECIEVER);
        intent.putExtra(KEY_RESULT_CODE, code);
        intent.putExtra(KEY_REQUEST_NAME, request);
        intent.putExtra(KEY_RESULT_OBJECT, o);
        getInstance().sendBroadcast(intent);
    }
}
