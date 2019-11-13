package com.leidi.tcpclient;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by 40208 on 2019/11/11.
 */

public class RemoteCmd {
    static final String LOG_TAG = "td-lte";

    private static final int MSG_NEW_CTRL_INFO = 17; // Business Data Package
    private static final int MSG_SEND_CTRL_FAILED = 18; // Server Transfer Fail
    private static final int MSG_SEND_CTRL_OK = 19; // Server Transfer Success
    private static final int MSG_CONNECTED = 20; // Connect to Server Success

    public static Socket mSocket;
    Socket mNewSocket;
    Handler mHandler;

    HandlerThread mSenderThread;
    CmdSender mSender;

    Thread mReceiverThread;
    CmdReceiver mReceiver;

    byte[] sendBuffer;

    // ***** Events
    static final int EVENT_SEND_HEART = 1;
    static final int EVENT_SEND_DATA = 2;
    static final int CMD_MAX_COMMAND_BYTES = 1024;

    static String str_domainname = "192.168.27.251";

    static int mPort = 18080;

    static final int SOCKET_OPEN_RETRY_MILLIS = 4 * 1000;
    static final int SOCKET_HEART_MILLIS=5*1000;//心跳时间间隔

    int mSeqNum = 1;
    int mSendFrom = 0;
    int mHeart = 0;// heart beating times

    private String heartNormal = "0";
    private String heartOff = "1";// 与CAD系统连接状态 0：连接 1：未连接
    private String socketStatusNormal = "0";
    private String socketStatusOff = "1";// socket断开
    private boolean heartregular = false;// 心跳正常

    private Context context;
    public  Socket s = null;

    class CmdReceiver implements Runnable {
        byte[] buffer;

        CmdReceiver() {
            buffer = new byte[CMD_MAX_COMMAND_BYTES];
        }

        // Execute after thread start
        public void run() {
            int retryCount = 0;
            // boolean iscatch=false;
            int newdata = 0;
            int olddata = 1;
            try {
                for (;;) {
//					Socket s = null;
                    try {
                        s = new Socket(str_domainname,20108);
                        s.setTcpNoDelay(true);
                        LogUtils.Log("Socket IP："+str_domainname);

                    } catch (IOException ex) {
                        LogUtils.Loge(LOG_TAG, str_domainname+"--连接异常IOException:" + ex.getMessage());

                        try {
                            if (s != null) {
                                s.close();
                                LogUtils.Loge(LOG_TAG, str_domainname+"--socket关闭,重新连接");
                            }
                        } catch (IOException ex2) {
                        }
                        LogUtils.Loge(LOG_TAG, "retryCount:");
                        // get server Ip, try 8 times
                        if (retryCount == 8) {
//							LogUtils.Loge(LOG_TAG, "Couldn't find '"+ str_domainname + "' socket after "+ retryCount+ " times, continuing to retry silently");
                        } else if (retryCount > 0 && retryCount < 8) {
//							LogUtils.Log(LOG_TAG, "Couldn't find '"+ str_domainname+ "' socket; retrying after timeout");
                        }

                        try {
                            Thread.sleep(SOCKET_OPEN_RETRY_MILLIS);
                        } catch (InterruptedException er) {
                        }

                        retryCount++;
                        continue;
                    }

                    retryCount = 0;

                    mSocket = s;
                    LogUtils.Log(LOG_TAG, "连接成功:"+str_domainname);

                    // Connect to Server Success
                    if (mHandler != null){
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_CONNECTED, null));
                    }

                    int length = 0;
                    try {
                        InputStream is = mSocket.getInputStream();
                        for (;;) {
                            byte[] buffer = new byte[CMD_MAX_COMMAND_BYTES];
                            length = readMessage(is, buffer);
                            if (length < 0) {
                                // End-of-stream reached
                                break;
                            }
                            LogUtils.Log("length:"+length);
                            // 读取
                            processResponse(buffer, length);
                        }
                    } catch (IOException ex) {
                        LogUtils.Log(LOG_TAG, str_domainname+"--异常IOException:"+ex.getMessage());
                    } catch (Throwable tr) {
                        LogUtils.Loge(LOG_TAG,"异常Throwable:" + tr.toString());
                    }

                    LogUtils.Log(LOG_TAG, str_domainname + "断连");

                    try {
                        mSocket.close();
                    } catch (IOException ex) {
                    }

                    mSocket = null;
                }

            } catch (Throwable tr) {
                LogUtils.Loge(LOG_TAG, str_domainname+"--异常socket Throwable:" + tr);
            }
        }
    }


    class CmdSender extends Handler implements Runnable {
        public CmdSender(Looper looper) {
            super(looper);
        }

        public void run() {
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_SEND_DATA:
                    SendPack p = (SendPack) (msg.obj);
                    writeMessage(p.mAddr, p.mText,p.mText2,p.mText3, p.mText4,p.mType,p.mPage);
                    break;
                case EVENT_SEND_HEART:
                    if (mSocket != null) {
                        if (mHeart >= 3) {
                            LogUtils.Log("mHeart>=3");
                            heartregular = false;
                            heartOff = "1";
//                            PhoneCADStatus.STATUS = 1;
                            try {
                                mSocket.close();
                            } catch (IOException ex) {
                            }
                            LogUtils.Log("连接超时");
                            mHeart = 0;
                        } else {
                            if (!heartregular) {
                                heartOff = "1";
//                                PhoneCADStatus.STATUS = 1;
                            } else {
                                heartOff = "0";
//                                PhoneCADStatus.STATUS = 0;
                            }
//                            writeMessage(0, "", 1);// heart beating package
                            mHeart++;
                        }
                        if (!heartNormal.equals(heartOff)) {
                            LogUtils.Log("发送CAD状态广播 Heartbeat thread");
                            // 与CAD系统连接状态
//                            Intent intent = new Intent(PhoneConstants.ACTION_CAD_STATUS);
//                            context.sendBroadcast(intent);
                            heartNormal = heartOff;
                        }
                    }else {
                        heartOff = "1";
//                        PhoneCADStatus.STATUS = 1;
                    }

                    if (!heartNormal.equals(heartOff)) {
                        LogUtils.Log("发送CAD连接状态广播 CmdSender");
                        // 与CAD系统连接状态
//                        Intent intent = new Intent(PhoneConstants.ACTION_CAD_STATUS);
//                        context.sendBroadcast(intent);
                        heartNormal = heartOff;
                    }
                    sendMessageDelayed(obtainMessage(EVENT_SEND_HEART), SOCKET_HEART_MILLIS);
                    break;
                default:
                    break;
            }
        }
    }

    //mRCmd.send(Integer.parseInt(number), text,text2,text3,type, false);
    //0,strtext,0x02,false
    public void send(int sendTo, float str,float str2,float str3,String str4, int type, int page,boolean bSyn) {
        Message msg;
        if (bSyn)
        {
            writeMessage(sendTo, str,str2,str3,str4, type,page);
        } else {
            SendPack p = new SendPack(type, sendTo, str,str2,str3,str4,page);  //0x02,0,strtext
            //SendPack p = (SendPack) (msg.obj);
            //writeMessage(p.mAddr, p.mText, p.mType);
            msg = mSender.obtainMessage(EVENT_SEND_DATA, p);
            msg.sendToTarget();
        }
    }

    // Send Data Structure
    class SendPack {
        SendPack(int type, int sendTo, float text,float text2,float text3,String text4,int page) {
            mType = type;
            mAddr = sendTo;
            mText = text;
            mText2 = text2;
            mText3 = text3;
            mText4 = text4;
            mPage = page;
        }
        int mType;
        int mAddr;
        float mText;
        float mText2;
        float mText3;
        String mText4;
        int mPage;

    }

    // Send Data
    private synchronized void writeMessage(int addr, float str, float str2,float str3,String str4,int type,int page) {
        try {
            Socket s = mSocket;

            if (s == null) {
                LogUtils.Log(LOG_TAG, "发送失败: mSocket为空");

                mHandler.sendMessage(mHandler.obtainMessage(3));
                return;
            }
            int len = EnPackage(addr, str,str2,str3,str4,type,page);
            if (len > 0) {
                s.getOutputStream().write(sendBuffer, 0, len);

                mHandler.sendMessage(mHandler.obtainMessage(4));
            }
        } catch (IOException ex) {
            LogUtils.Log(LOG_TAG, "发送异常IOException:" + ex.getMessage());

            mHandler.sendMessage(mHandler.obtainMessage(5));

        } catch (RuntimeException exc) {
            LogUtils.Log(LOG_TAG, "发送异常RuntimeException:" + exc.getMessage());

            mHandler.sendMessage(mHandler.obtainMessage(6));
        }
    }

    // Get Data From Buffer
    private int readMessage(InputStream is, byte[] buffer) throws IOException {
        int countRead;
        countRead = is.read(buffer);

        if (countRead < 0) {
            LogUtils.Log(LOG_TAG, "异常 读取长度:"+countRead);
            return -1;
        }
        return countRead;
    }

    private static String byte2hex(byte[] buffer, int len) {
        String h = "";

        for (int i = 0; i < len; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            h = h + " " + temp;
        }
        return h;
    }
    /**
     * 字节转换为浮点
     */

    //方法一：
    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

   // 方法二：

    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum|(b[0] & 0xff) << 0;
        accum = accum|(b[1] & 0xff) << 8;
        accum = accum|(b[2] & 0xff) << 16;
        accum = accum|(b[3] & 0xff) << 24;
        return Float.intBitsToFloat(accum);
    }


    /**
     * 浮点转换为字节
     *
     */
     public static byte[] float2byte(float f) {

                // 把float转换为byte[]
                int fbit = Float.floatToIntBits(f);

                byte[] b = new byte[4];
                for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
                    }

                // 翻转数组
                 int len = b.length;
                // 建立一个与源数组元素类型相同的数组
                byte[] dest = new byte[len];
                // 为了防止修改源数组，将源数组拷贝一份副本
                 System.arraycopy(b, 0, dest, 0, len);
        byte temp;
                // 将顺位第i个与倒数第i个交换
                 for (int i = 0; i < len / 2; ++i) {
                         temp = dest[i];
                         dest[i] = dest[len - i - 1];
                         dest[len - i - 1] = temp;
                     }

                return dest;

             }

    // Package Data
    private int EnPackage(int addr, float str, float str2,float str3,String str4,int type,int page) {
        byte[] ii = float2byte(str);
        byte[] ii2 = float2byte(str2);
        byte[] ii3 = float2byte(str3);

        //  >>>无符号右移，顾名思义是指移动的时候不考虑符号，右移的时候高位补0.
        sendBuffer[0] = 0x2A;//head
        sendBuffer[1] = 0x12;
        sendBuffer[2] = (byte) type;// type
        sendBuffer[3] = (byte) page; //页码 启用0x01 停用0xFF

        sendBuffer[4] = ii[0];
        sendBuffer[5] = ii[1];
        sendBuffer[6] = ii[2];;
        sendBuffer[7] = ii[3];
        byte[] newbuf ={sendBuffer[4],sendBuffer[5],sendBuffer[6],sendBuffer[7]};
        float aFloat = getFloat(newbuf);
        LogUtils.Log("第一个数:"+aFloat);

        if(aFloat > 20){
            sendBuffer[4] = 0x00;
            sendBuffer[5] = 0x00;
            sendBuffer[6] = (byte)0x80;
            sendBuffer[7] = 0x7F;
            byte[] newbuf1 ={sendBuffer[4],sendBuffer[5],sendBuffer[6],sendBuffer[7]};
            float aFloat1 = getFloat(newbuf1);
            LogUtils.Log("大于20 第一个数:"+aFloat1);
        }

        sendBuffer[8] = ii2[0];
        sendBuffer[9] = ii2[1];
        sendBuffer[10] = ii2[2];
        sendBuffer[11] = ii2[3];
        byte[] newbuf2 ={sendBuffer[8],sendBuffer[9],sendBuffer[10],sendBuffer[11]};
        float aFloat2 = getFloat(newbuf2);
        LogUtils.Log("第二个数:"+aFloat2);

        if(aFloat2 > 20){
            sendBuffer[8] = 0x00;
            sendBuffer[9] = 0x00;
            sendBuffer[10] = (byte)0x80;
            sendBuffer[11] = 0x7F;
            byte[] newbuf3 ={sendBuffer[8],sendBuffer[9],sendBuffer[10],sendBuffer[11]};
            float aFloat3 = getFloat(newbuf3);
            LogUtils.Log("大于20 第二个数:"+aFloat3);
        }

        sendBuffer[12] = ii3[0];
        sendBuffer[13] = ii3[1];
        sendBuffer[14] = ii3[2];
        sendBuffer[15] = ii3[3];
        byte[] newbuf4 ={sendBuffer[12],sendBuffer[13],sendBuffer[14],sendBuffer[15]};
        float aFloat4 = getFloat(newbuf4);
        LogUtils.Log("第三个数:"+aFloat4);

        if(aFloat2 > 20){
            sendBuffer[8] = 0x00;
            sendBuffer[9] = 0x00;
            sendBuffer[10] = (byte)0x80;
            sendBuffer[11] = 0x7F;
            byte[] newbuf5 ={sendBuffer[12],sendBuffer[13],sendBuffer[14],sendBuffer[15]};
            float aFloat5 = getFloat(newbuf5);
            LogUtils.Log("大于20 第二个数:"+aFloat5);
        }

        int i = Integer.parseInt(str4);
        sendBuffer[16] = (byte)i; // end flag
        sendBuffer[17] = 0x1A;

//        LogUtils.Log("sendBuffer.length:"+sendBuffer.length);
        return 18;
    }

    private int Caculate_CRC16_Byte(int DAT, int CRC) {
        CRC = CRC ^ DAT;
        for (int j = 0; j < 8; j++) {
            if ((CRC & 0x01) != 0) {
                CRC = CRC >>> 1;
                CRC = CRC ^ 0xA001;
            } else {
                CRC = CRC >>> 1;
            }
        }
        return (CRC);
    }

    // Calculate CRC
    private int Caculate_CRC16(byte[] data, int len) {
        int CRC = 0xffff; // CRC initialized to 0xffff
        for (int i = 0; i < len; i++) {
            short sdata = (short) (data[i] & 0xff);
            CRC = Caculate_CRC16_Byte(sdata, CRC); // get CRC
        }
        return (CRC);
    }

    // Process Received Data
    private byte[] halfdata;

    private void processResponse(byte[] text, int length) {

        for(int i=0;i<length;i++){
            LogUtils.Log("i:"+Integer.toHexString(text[i] & 0xff));
        }

        LogUtils.Log("length:"+length);
        byte b = text[0];
        byte b1 = text[1];
        LogUtils.Log("test11："+b+"  "+b1);
        int i11 = text[0] & 0xff;
        float aFloat111 = Float.intBitsToFloat(i11);
        LogUtils.Log("test1："+aFloat111);

        int i12 = text[1] & 0xff;
        float aFloat112 = Float.intBitsToFloat(i12);
        LogUtils.Log("test2："+aFloat112);


        int i1 = text[3] & 0xff;
        float aFloat11 = Float.intBitsToFloat(i1);
        LogUtils.Log("接收第一个："+aFloat11);

        byte[] newbuf ={text[4],text[5],text[6],text[7]};
        float aFloat = getFloat(newbuf);
        LogUtils.Log("接收第二个："+aFloat);

        byte[] newbuf2 ={text[8],text[9],text[10],text[11]};
        float aFloat2= getFloat(newbuf2);
        LogUtils.Log("接收第三个："+aFloat2);

        byte[] newbuf3 ={text[12],text[13],text[14],text[15]};
        float aFloat3 = getFloat(newbuf3);
        LogUtils.Log("接收第四个："+aFloat3);

        int i = text[16] & 0xff;
        float aFloat4 = Float.intBitsToFloat(i);
        LogUtils.Log("接收第五个："+aFloat4);

        int i17= text[17] & 0xff;
        int i18= text[18] & 0xff;
        LogUtils.Log("test17 18："+i17+"  "+i18);

        Message msg = new Message();
        msg.what = 2;
        Bundle bundle = new Bundle();
        bundle.putFloat("aFloat11", aFloat11);
        bundle.putFloat("aFloat", aFloat);
        bundle.putFloat("aFloat2", aFloat2);
        bundle.putFloat("aFloat3", aFloat3);
        bundle.putFloat("aFloat4", aFloat4);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    // ***** Constructors

    public RemoteCmd(Context context, Handler h) {

        this.context = context;
        mHandler = h;
        sendBuffer = new byte[CMD_MAX_COMMAND_BYTES];

        // Create Send Thread  心跳线程
        mSenderThread = new HandlerThread("CmdSender");
        mSenderThread.start();

        Looper looper = mSenderThread.getLooper();
        mSender = new CmdSender(looper);

        // Create Receive Thread
        mReceiver = new CmdReceiver();
        mReceiverThread = new Thread(mReceiver, "CmdReceiver");
        mReceiverThread.start();

//        mSender.sendMessageDelayed(mSender.obtainMessage(EVENT_SEND_HEART),SOCKET_HEART_MILLIS);
    }
}
