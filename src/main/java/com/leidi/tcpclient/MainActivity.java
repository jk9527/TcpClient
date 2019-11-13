package com.leidi.tcpclient;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

import static com.leidi.tcpclient.RemoteCmd.LOG_TAG;
//AppCompatActivity
public class MainActivity extends AppCompatActivity  implements View.OnClickListener{
    private Button btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8;
    private EditText edit_11,edit_12,edit_13,edit_14;
    private EditText edit_21,edit_22,edit_23,edit_24;
    private EditText edit_31,edit_32,edit_33,edit_34;
    private EditText edit_41,edit_42,edit_43,edit_44;
    private EditText edit_51,edit_52,edit_53,edit_54;
    private EditText edit_61,edit_62,edit_63,edit_64;
    private EditText edit_71,edit_72,edit_73,edit_74;
    private EditText edit_81,edit_82,edit_83,edit_84,edit_91;
    private Switch btn_switch;

    private static final int MSG_SEND_CTRL_FAILED = 18; // Server Transfer Fail
    // Package
    private static final int MSG_SEND_CTRL_OK = 19; // Server Transfer Success
    // Package
    private static final int MSG_CONNECTED = 20; // Connect to Server Success

    private RemoteCmd mRCmd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);

        mRCmd = new RemoteCmd(this,handle);

        btn_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    LogUtils.Log("点击启用");
                    btn_switch.setText("启用");
                    sendSms("0",0.00f,0.00f,0.00f,"0",0xF6,0x01);
                }else{
                    LogUtils.Log("点击停用");
                    btn_switch.setText("停用");
                    sendSms("0",0.00f,0.00f,0.00f,"0",0xF6,0xff);
                }
            }
        });
    }

    /**
     * 发送
     */
    public void sendSms(String number, float text,float text2,float text3,String text4,int type,int page) {
        mRCmd.send(Integer.parseInt(number), text,text2,text3,text4,type, page,false);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn1:
                if(!edit_11.getText().toString().isEmpty() && !edit_12.getText().toString().isEmpty()
                        && !edit_13.getText().toString().isEmpty()
                        && !edit_14.getText().toString().isEmpty()) {
                    String str_edit_11 = edit_11.getText().toString();
                    String str_edit_12 = edit_12.getText().toString();
                    String str_edit_13 = edit_13.getText().toString();
                    String str_edit_14 = edit_14.getText().toString();

                    //转成float型
                    float fl_edit_11 = Float.parseFloat(str_edit_11);
                    float fl_edit_12 = Float.parseFloat(str_edit_12);
                    float fl_edit_13 = Float.parseFloat(str_edit_13);

                    LogUtils.Log("发送："+fl_edit_11+" "+fl_edit_12+" "+fl_edit_13+" "+str_edit_14);

                    sendSms("0",fl_edit_11,fl_edit_12,fl_edit_13,str_edit_14,0xF4,0xFF);
                    Toast.makeText(this,"发送成功", Toast.LENGTH_SHORT).show();
                    }else{
                    Toast.makeText(this,"值不能为空,发送失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn2:
                if(!edit_21.getText().toString().isEmpty() && !edit_22.getText().toString().isEmpty()
                        && !edit_23.getText().toString().isEmpty()
                        && !edit_24.getText().toString().isEmpty()) {

                    String str_edit_21 = edit_21.getText().toString();
                    String str_edit_22 = edit_22.getText().toString();
                    String str_edit_23 = edit_23.getText().toString();
                    String str_edit_24 = edit_24.getText().toString();

                    //转成float型
                    float fl_edit_21 = Float.parseFloat(str_edit_21);
                    float fl_edit_22 = Float.parseFloat(str_edit_22);
                    float fl_edit_23 = Float.parseFloat(str_edit_23);

                    LogUtils.Log("发送：" + fl_edit_21 + " " + fl_edit_22 + " " + fl_edit_23 + " " + str_edit_24);

                    sendSms("0", fl_edit_21, fl_edit_22, fl_edit_23, str_edit_24, 0xF4, 0xFF);
                    Toast.makeText(this,"发送成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"值不能为空,发送失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn3:
                if(!edit_31.getText().toString().isEmpty() && !edit_32.getText().toString().isEmpty()
                        && !edit_33.getText().toString().isEmpty()
                        && !edit_34.getText().toString().isEmpty()) {

                    String str_edit_31 = edit_31.getText().toString();
                    String str_edit_32 = edit_32.getText().toString();
                    String str_edit_33 = edit_33.getText().toString();
                    String str_edit_34 = edit_34.getText().toString();

                    //转成float型
                    float fl_edit_31 = Float.parseFloat(str_edit_31);
                    float fl_edit_32 = Float.parseFloat(str_edit_32);
                    float fl_edit_33 = Float.parseFloat(str_edit_33);

                    LogUtils.Log("发送："+fl_edit_31+" "+fl_edit_32+" "+fl_edit_33+" "+str_edit_34);

                    sendSms("0",fl_edit_31,fl_edit_32,fl_edit_33,str_edit_34,0xF4,0xFF);
                    Toast.makeText(this,"发送成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"值不能为空,发送失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn4:
                if(!edit_41.getText().toString().isEmpty() && !edit_42.getText().toString().isEmpty()
                        && !edit_43.getText().toString().isEmpty()
                        && !edit_44.getText().toString().isEmpty()) {

                    String str_edit_41 = edit_41.getText().toString();
                    String str_edit_42 = edit_42.getText().toString();
                    String str_edit_43 = edit_43.getText().toString();
                    String str_edit_44 = edit_44.getText().toString();

                    //转成float型
                    float fl_edit_41 = Float.parseFloat(str_edit_41);
                    float fl_edit_42 = Float.parseFloat(str_edit_42);
                    float fl_edit_43 = Float.parseFloat(str_edit_43);

                    LogUtils.Log("发送："+fl_edit_41+" "+fl_edit_42+" "+fl_edit_43+" "+str_edit_44);

                    sendSms("0",fl_edit_41,fl_edit_42,fl_edit_43,str_edit_44,0xF4,0xFF);
                    Toast.makeText(this,"发送成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"值不能为空,发送失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn5:
                if(!edit_51.getText().toString().isEmpty() && !edit_52.getText().toString().isEmpty()
                        && !edit_53.getText().toString().isEmpty()
                        && !edit_54.getText().toString().isEmpty()) {

                    String str_edit_51 = edit_51.getText().toString();
                    String str_edit_52 = edit_52.getText().toString();
                    String str_edit_53 = edit_53.getText().toString();
                    String str_edit_54 = edit_54.getText().toString();

                    //转成float型
                    float fl_edit_51 = Float.parseFloat(str_edit_51);
                    float fl_edit_52 = Float.parseFloat(str_edit_52);
                    float fl_edit_53 = Float.parseFloat(str_edit_53);

                    LogUtils.Log("发送："+fl_edit_51+" "+fl_edit_52+" "+fl_edit_53+" "+str_edit_54);

                    sendSms("0",fl_edit_51,fl_edit_52,fl_edit_53,str_edit_54,0xF4,0xFF);
                    Toast.makeText(this,"发送成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"值不能为空,发送失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn6:
                if(!edit_61.getText().toString().isEmpty() && !edit_62.getText().toString().isEmpty()
                        && !edit_63.getText().toString().isEmpty()
                        && !edit_64.getText().toString().isEmpty()) {

                    String str_edit_61 = edit_61.getText().toString();
                    String str_edit_62 = edit_62.getText().toString();
                    String str_edit_63 = edit_63.getText().toString();
                    String str_edit_64 = edit_64.getText().toString();

                    //转成float型
                    float fl_edit_61 = Float.parseFloat(str_edit_61);
                    float fl_edit_62 = Float.parseFloat(str_edit_62);
                    float fl_edit_63 = Float.parseFloat(str_edit_63);

                    LogUtils.Log("发送："+fl_edit_61+" "+fl_edit_62+" "+fl_edit_63+" "+str_edit_64);

                    sendSms("0",fl_edit_61,fl_edit_62,fl_edit_63,str_edit_64,0xF4,0xFF);
                    Toast.makeText(this,"发送成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"值不能为空,发送失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn7:
                if(!edit_71.getText().toString().isEmpty() && !edit_72.getText().toString().isEmpty()
                        && !edit_73.getText().toString().isEmpty()
                        && !edit_74.getText().toString().isEmpty()) {

                    String str_edit_71 = edit_71.getText().toString();
                    String str_edit_72 = edit_72.getText().toString();
                    String str_edit_73 = edit_73.getText().toString();
                    String str_edit_74 = edit_74.getText().toString();

                    //转成float型
                    float fl_edit_71 = Float.parseFloat(str_edit_71);
                    float fl_edit_72 = Float.parseFloat(str_edit_72);
                    float fl_edit_73 = Float.parseFloat(str_edit_73);

                    LogUtils.Log("发送："+fl_edit_71+" "+fl_edit_72+" "+fl_edit_73+" "+str_edit_74);

                    sendSms("0",fl_edit_71,fl_edit_72,fl_edit_73,str_edit_74,0xF4,0xFF);
                    Toast.makeText(this,"发送成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"值不能为空,发送失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn8:
                LogUtils.Log("点击查询");
                sendSms("0",0.00f,0.00f,0.00f,"0",0xF5,0xFF);
                break;
        }
    }

    private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEND_CTRL_FAILED: // 转发服务器转发失败
                {
                    int seq = msg.arg1;
                    LogUtils.Log("转发服务器失败:" + seq);
                    break;
                }
                case MSG_SEND_CTRL_OK: // 转发服务器转发成功
                {
//                    int seq = msg.arg1;
//                    LogUtils.Log("转发服务器成功:" + seq);

                    break;

                }
                case MSG_CONNECTED: // 连接服务器成功
                {
                    LogUtils.Log("连接服务器成功:");
                    break;
                }
                case 2:
                    Bundle bundle = msg.getData();
                    float aFloat11 = bundle.getFloat("aFloat11");
                    float aFloat1 = bundle.getFloat("aFloat11");
                    float aFloat2 = bundle.getFloat("aFloat2");
                    float aFloat3 = bundle.getFloat("aFloat3");
                    float aFloat4 = bundle.getFloat("aFloat4");

                    String str11 = String.valueOf(aFloat11);
                    String str1 = String.valueOf(aFloat1);
                    String str2 = String.valueOf(aFloat2);
                    String str3 = String.valueOf(aFloat3);
                    String str4 = String.valueOf(aFloat4);
                    edit_81.setText(str1);
                    edit_82.setText(str2);
                    edit_83.setText(str3);
                    edit_84.setText(str4);
                    edit_91.setText(str11);
                    break;
                case 3:
                    Toast.makeText(MainActivity.this,"发送失败", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(MainActivity.this,"发送成功", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(MainActivity.this,"发送异常IOException", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(MainActivity.this,"发送异常RuntimeException", Toast.LENGTH_SHORT).show();
                    break;
//                case MSG_INIT: // 初始化请求
//                {
//                    if (mDelay < 200000) //200000
//                        mDelay += 10000; //10000
//                    if (!bInit)
//                        sendRequestInitInfo(0);
//                    break;
//                }
//                case MSG_NEW_CTRL_INFO: // 列车控制信息
//                {
//                    String text = (String) msg.obj;
//                    int iSender = msg.arg1;
//                    if (text.startsWith(tagTicket) && text.endsWith(tagTicketEnd)) {
//                        // 电子工单信息
//                        onNewTicket(iSender, text);
//                    } else if (text.startsWith(tagReply)
//                            && text.endsWith(tagReplyEnd)) {
//                        // 请求回应
//                        onReplyInfo(text);
//                    } else if (text.startsWith(tagRequest)
//                            && text.endsWith(tagRequestEnd)) {
//                        onRequestInfo(text);
//                    } else if (text.startsWith(tagNotify)
//                            && text.endsWith(tagNotifyEnd)) {
//                        // 转组通知
//                        onNotifyInfo(text);
//                    } else {
//                        // 其他信息
//                        onControlInfo(text);   //没有用到
//                    }
//                    break;
//                }
                default:
                    break;
            }
        }
    };

    private void initView() {
        btn_switch = findViewById(R.id.btn_switch);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        edit_11 = findViewById(R.id.edit_11);
        edit_12 = findViewById(R.id.edit_12);
        edit_13 = findViewById(R.id.edit_13);
        edit_14 = findViewById(R.id.edit_14);

        edit_21 = findViewById(R.id.edit_21);
        edit_22 = findViewById(R.id.edit_22);
        edit_23 = findViewById(R.id.edit_23);
        edit_24 = findViewById(R.id.edit_24);

        edit_31 = findViewById(R.id.edit_31);
        edit_32 = findViewById(R.id.edit_32);
        edit_33 = findViewById(R.id.edit_33);
        edit_34 = findViewById(R.id.edit_34);

        edit_41 = findViewById(R.id.edit_41);
        edit_42 = findViewById(R.id.edit_42);
        edit_43 = findViewById(R.id.edit_43);
        edit_44 = findViewById(R.id.edit_44);

        edit_51 = findViewById(R.id.edit_51);
        edit_52 = findViewById(R.id.edit_52);
        edit_53 = findViewById(R.id.edit_53);
        edit_54 = findViewById(R.id.edit_54);

        edit_61 = findViewById(R.id.edit_61);
        edit_62 = findViewById(R.id.edit_62);
        edit_63 = findViewById(R.id.edit_63);
        edit_64 = findViewById(R.id.edit_64);

        edit_71 = findViewById(R.id.edit_71);
        edit_72 = findViewById(R.id.edit_72);
        edit_73 = findViewById(R.id.edit_73);
        edit_74 = findViewById(R.id.edit_74);

        edit_81 = findViewById(R.id.edit_81);
        edit_82 = findViewById(R.id.edit_82);
        edit_83 = findViewById(R.id.edit_83);
        edit_84 = findViewById(R.id.edit_84);
        edit_91 = findViewById(R.id.edit_91);
    }
}
