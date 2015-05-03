package com.addressbook;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class AddressTab extends Fragment
{
	
	SQLiteDatabase db;
	SimpleCursorAdapter adapter;
	ListView lv_people;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_address_tab, container, false);
		
		
	return rootView;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		DBHelper helper = new DBHelper(getActivity());
		db = helper.getWritableDatabase();
		//db.execSQL("INSERT INTO people VALUES (null, '" + "testname" + "', '" + "testnum" + "');");
		
		/*
		 * ���⼭�� Cursor�� �츮�� �ƴ� �� Ŀ���� ������ �۾��� DB�� ���� �����ϱ� ���� ���˴ϴ�.
		 * �� ���� �ڵ忡�� Cursor�� people ���̺��� �� ��Ҹ� �ڵ����� Ž���ϸ� ListView�� ä�� �ִ� �ڵ忡 ���ǰ� �ֽ��ϴ�.
		 */
		Cursor cursor = db.rawQuery("SELECT * FROM people", null);
		cursor.moveToFirst();
		
		/*
		 * SCA�� argument�� ���� Cursor�� ���������� ����ϸ� ListView�� ������ ä�� �ݴϴ�.
		 * �Ʒ� �ڵ��� new �ٷ� ������ �ִ� Ŭ���� �̸��� ���콺�� ��� argument�� �ǹ̸� �� �� �ֽ��ϴ�.
		 * 
		 * android.R���� '�����, �̸� ����� �� ������'�� ��� ������
		 * ��� ���� �̿ܿ� �ٸ� ������ ���� ���� ���� �����ϴ�.
		 */
		adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, cursor, new String[] { "name", "number" }, new int[] { android.R.id.text1, android.R.id.text2 }, 0);

		/*
		 * .xml���� ���� �� �̸��� ���� ���ϴ� ȭ�� ���� ��Ҹ� ������ ����
		 * �ش� Activity �ڵ� �ȿ��� findViewById()�� ȣ���ϰ�
		 * R.id. �� �Է��� ���� ���ϴ� �̸��� ã�� �־� �ָ� �˴ϴ�.
		 * 
		 * .xml ������ ����(Ctrl+S)�ؾ� �������� �ݿ��ȴٴ� ���� ���� ������!
		 */
		lv_people = (ListView)getActivity().findViewById(R.id.addresslist);
		lv_people.setAdapter(adapter);
		lv_people.setOnItemLongClickListener(mItemLongClickListener);
		lv_people.setOnItemClickListener(mItemClickListener);
	}
	
	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, final long id)
		{
			Cursor cursor = db.rawQuery("SELECT * FROM people", null);
			cursor.moveToPosition(position);
			Intent intent = new Intent(getActivity(), EditAddress.class);
	    	intent.putExtra("name", cursor.getString(1));
			intent.putExtra("number", cursor.getString(2));
			intent.putExtra("addressid", id);
			getActivity().startActivityForResult(intent, 0);
		}
	};
	
	private AdapterView.OnItemLongClickListener mItemLongClickListener = new AdapterView.OnItemLongClickListener(){
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View v, int position, final long id) { 
			Vibrator mVib;
			mVib = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
			mVib.vibrate(100);
			AlertDialog.Builder delete_dialog = new AlertDialog.Builder(getActivity());
			delete_dialog.setTitle("����");
			delete_dialog.setMessage("����ó�� �����մϴ�.");
			delete_dialog.setNegativeButton("���", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				}
			});
			delete_dialog.setPositiveButton("����", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String string =String.valueOf(id);
		        db.execSQL("DELETE FROM people WHERE _id = '" + string + "'");
		        Cursor cursor = db.rawQuery("SELECT * FROM people", null);
				cursor.moveToFirst();
				adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, cursor, new String[] { "name", "number" }, new int[] { android.R.id.text1, android.R.id.text2 }, 0);
		        lv_people = (ListView)getActivity().findViewById(R.id.addresslist);
				lv_people.setAdapter(adapter);
				}
			});
			AlertDialog alert = delete_dialog.create();
			alert.show();	
			return true;
		}	 
    };
	
	@Override
    public void onAttach(Activity activity) {
	    super.onAttach(activity);
	
	    setHasOptionsMenu(true);
    }
		
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		    // Inflate the menu items for use in the action bar
			super.onCreateOptionsMenu(menu, inflater);
		    inflater.inflate(R.menu.menu_address, menu);
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
	    switch(item.getItemId()){
	    case R.id.add:
	        // your action goes here
	    	Intent intent = new Intent(getActivity(), AddAddress.class);
	    	//intent.putExtra("name", "");
			//intent.putExtra("number", "");
			getActivity().startActivityForResult(intent, 0);
			return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	
}
