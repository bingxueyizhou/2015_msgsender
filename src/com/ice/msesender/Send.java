package com.ice.msesender;

import java.util.ArrayList;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Send extends Activity{
	
	private ListView lv;
	private Button bu_send_makesured;
	private Context mContext;
	private long waiting_long;
	private long sum_msg = 0;
	
	/**��������յĹ㲥**/
    String SENT_SMS_ACTION = "SENT_SMS_ACTION";
    String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";

	private ArrayList<String> listName = new ArrayList<String>();
	private ArrayList<String> listNumber = new ArrayList<String>();
	private ArrayList<String> listContet = new ArrayList<String>();
	private ArrayList<String> listShow = new ArrayList<String>();
	
	
	//����message���߳�
	long t = 5000;
	private final int MSG_SUCCESS = 0;
	private final int MSG_SEND    = 10;
	@SuppressLint("HandlerLeak")
	private Handler uihandler = new Handler(){	
		public void handleMessage(Message msg){
			switch(msg.what){
			case MSG_SUCCESS:
				sum_msg -- ;
		        Toast.makeText(Send.this, "��ʣ"+sum_msg+"��", 
		        		Toast.LENGTH_SHORT).show();
				break;
			case MSG_SEND:
			    if (t<30000)
			    	t += t;
			    Waiting(t, "���Ͷ�����");
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send);
		mContext = this;
		
		listName = getIntent().getStringArrayListExtra("NAME");
		listNumber = getIntent().getStringArrayListExtra("NUMBER");
		Bundle info = getIntent().getExtras();
		String msg = info.getString("MSG");
		Log.i("����2",msg);
		Log.i("����2",listName.get(0));
		Log.i("����2",listNumber.get(0));
		Log.i("����2","��С"+listName.size());
		
		for(int i=0;i<listName.size();i++){
			listContet.add(msg.replace("xx", listName.get(i)));
			listShow.add(""+listNumber.get(i) + ":" +listContet.get(i));
		}
		lv = (ListView)findViewById(R.id.lV_makesure);
		ArrayAdapter<String> lv_adapter = new ArrayAdapter<String>(this,  
                android.R.layout.simple_list_item_1, listShow);
		lv.setAdapter(lv_adapter);
		
		bu_send_makesured = (Button)findViewById(R.id.bu_send_makesured);
		bu_send_makesured.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sum_msg = listNumber.size();
				(new Thread(new Runnable() {
					
					@Override
					public void run() {
						int i=0;
						for(i=0;i<listNumber.size();i++){
							Log.i("�绰:"+listNumber.get(i),"����:"+listContet.get(i));
						    Message msg = new Message();
						    msg.what = MSG_SEND;
						    uihandler.sendMessage(msg);
							sendSMS( listNumber.get(i),listContet.get(i) );
							registerReceiver(sendMessage, new IntentFilter(SENT_SMS_ACTION));
						    registerReceiver(receiver, new IntentFilter(DELIVERED_SMS_ACTION));
						    try {
								Thread.sleep(t);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				})).start();
			}
		});
	}
	
    private BroadcastReceiver sendMessage = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //�ж϶����Ƿ��ͳɹ�
            switch (getResultCode()) {
            case Activity.RESULT_OK:
                Toast.makeText(context, "���ŷ��ͳɹ�", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(mContext, "����ʧ��", Toast.LENGTH_LONG).show();
                break;
            }
        }
    };
    
   
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //��ʾ�Է��ɹ��յ�����
            Toast.makeText(mContext, "�Է����ճɹ�",Toast.LENGTH_LONG).show();
        }
    };
    
    /**
     * ����˵��
     * destinationAddress:�����˵��ֻ�����
     * scAddress:�����˵��ֻ����� 
     * text:������Ϣ������ 
     * sentIntent:�����Ƿ�ɹ��Ļ�ִ�����ڼ��������Ƿ��ͳɹ���
     * DeliveryIntent:�����Ƿ�ɹ��Ļ�ִ�����ڼ������ŶԷ��Ƿ���ճɹ���
     */
    private void sendSMS(String phoneNumber, String message) {
    	// ---sends an SMS message to another device---
    	SmsManager sms = SmsManager.getDefault();

	    // create the sentIntent parameter
	    Intent sentIntent = new Intent(SENT_SMS_ACTION);
	    PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, sentIntent,0);
	
	    // create the deilverIntent parameter
	    Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
	    PendingIntent deliverPI = PendingIntent.getBroadcast(this, 0,
	            deliverIntent, 0);
	
	    //����������ݳ���70���ַ� ���������Ų�ɶ������ŷ��ͳ�ȥ
	    if (message.length() > 70) {
	        ArrayList<String> msgs = sms.divideMessage(message);
	        for (String msg : msgs) {
	            sms.sendTextMessage(phoneNumber, null, msg, sentPI, deliverPI);
	        }
	    } else {
	        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliverPI);
	    }
	}
    
    private void Waiting(long l,String info){
		waiting_long = l;
		final ProgressDialog proDialog = android.app.ProgressDialog.show(
				Send.this, info, "�����ĵȴ�����");
		Thread thread = new Thread(){
			public void run(){
				try{
					sleep(waiting_long);
		        }catch (InterruptedException e){
		            e.printStackTrace();
		        }
				Message msg = new Message();
				msg.what = MSG_SUCCESS;
				uihandler.sendMessage(msg);
		        proDialog.dismiss();//���򲻿�����䣬��������Ῠ����
		    }
		};
		thread.start();
	}
}
