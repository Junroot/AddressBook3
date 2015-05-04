package com.addressbook;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


public class AddAddress extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_address);
		
		Intent intent = getIntent();
		
		EditText tb_number = (EditText)findViewById(R.id.tb_number);
		EditText tb_name = (EditText)findViewById(R.id.tb_name);
		
		tb_number.setText(intent.getStringExtra("number"));
		tb_name.setText(intent.getStringExtra("name"));
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_address, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.accept)
		{
			EditText tb_name = (EditText)findViewById(R.id.tb_name);
			if (tb_name.getText().toString().equals(""))
			{
				Toast toast = Toast.makeText(this, "�̸��� �Է����ּ���.", Toast.LENGTH_SHORT);
				toast.show();
			}
			else
			{
				Intent intent = new Intent();
				
				/*
				 * EditText�� �Է¹��� ���ڿ��� �����ϱ� ���� intent�� ��� �ڵ��Դϴ�.
				 */
				EditText tb_number = (EditText)findViewById(R.id.tb_number);
				
				intent.putExtra("name", tb_name.getText().toString());
				intent.putExtra("number", tb_number.getText().toString());
				
				/*
				 * �� Activity�� �۾��� ���������� �����ϰ� ���� Activity�� ���ư��� ���� �ڵ��Դϴ�.
				 */
				SQLiteDatabase db;
				DBHelper helper = new DBHelper(this);
				db = helper.getWritableDatabase();
				db.execSQL("INSERT INTO people VALUES (null, '" + tb_name.getText().toString() + "', '" + tb_number.getText().toString() + "');");
				Cursor cursor = db.rawQuery("SELECT * FROM people order by name, number asc", null);
				cursor.moveToFirst();
				setResult(RESULT_OK, intent);
				finish();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
}
