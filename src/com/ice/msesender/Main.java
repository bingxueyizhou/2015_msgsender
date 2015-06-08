package com.ice.msesender;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class Main extends Activity implements OnClickListener{
	
	private EditText eT_name;
	private EditText eT_number;
	private EditText eT_msg;
	private TextView tV_content;
	private Button   bu_send;
	private Button   bu_add;
	private ArrayList<String> listName = new ArrayList<String>();
	private ArrayList<String> listNumber = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		eT_name = (EditText) findViewById(R.id.eT_name);
		eT_number = (EditText) findViewById(R.id.eT_number);
		eT_msg = (EditText) findViewById(R.id.eT_msg);
		tV_content = (TextView) findViewById(R.id.tV_content);
		bu_add = (Button) findViewById(R.id.bu_add);
		bu_send = (Button) findViewById(R.id.bu_send);
		
		bu_add.setOnClickListener(this);
		bu_send.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bu_add:
			Log.i("≤‚ ‘","Ω¯»Î");
			String name = eT_name.getText().toString();
			String number = eT_number.getText().toString();
			Log.i("≤‚ ‘",""+name+number);
			if (name.compareTo("") !=0 && number.compareTo("") != 0){
				listName.add(name);
				listNumber.add(number);
				tV_content.setText(tV_content.getText().toString() + "\n"
						+name+"|"+number);
				Log.i("≤‚ ‘","µΩ’‚");
			}
			break;
		case R.id.bu_send:
			if (listName.size() == listNumber.size() && 
					eT_msg.getText().toString().compareTo("") != 0){
				Intent intent = new Intent(Main.this,Send.class);
				intent.putStringArrayListExtra("NAME",listName);
				intent.putStringArrayListExtra("NUMBER", listNumber);
				intent.putExtra("MSG", eT_msg.getText().toString());
				startActivity(intent);
			}else {
				Toast.makeText(Main.this, "Œ¥∆•≈‰", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
}
