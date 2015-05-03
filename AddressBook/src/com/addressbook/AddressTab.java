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
		 * 여기서의 Cursor는 우리가 아는 그 커서와 동일한 작업을 DB에 대해 수행하기 위해 사용됩니다.
		 * 이 예제 코드에서 Cursor는 people 테이블의 각 요소를 자동으로 탐색하며 ListView를 채워 주는 코드에 사용되고 있습니다.
		 */
		Cursor cursor = db.rawQuery("SELECT * FROM people", null);
		cursor.moveToFirst();
		
		/*
		 * SCA는 argument로 받은 Cursor를 내부적으로 사용하며 ListView에 내용을 채워 줍니다.
		 * 아래 코드의 new 바로 다음에 있는 클래스 이름에 마우스를 대면 argument의 의미를 알 수 있습니다.
		 * 
		 * android.R에는 '내장된, 미리 만들어 둔 정보들'이 들어 있으며
		 * 사실 여기 이외에 다른 곳에서 쓰일 일은 거의 없습니다.
		 */
		adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, cursor, new String[] { "name", "number" }, new int[] { android.R.id.text1, android.R.id.text2 }, 0);

		/*
		 * .xml에서 정해 둔 이름을 통해 원하는 화면 구성 요소를 가져올 때는
		 * 해당 Activity 코드 안에서 findViewById()를 호출하고
		 * R.id. 을 입력한 다음 원하는 이름을 찾아 넣어 주면 됩니다.
		 * 
		 * .xml 파일을 저장(Ctrl+S)해야 변경점이 반영된다는 것을 잊지 마세요!
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
			delete_dialog.setTitle("삭제");
			delete_dialog.setMessage("연락처를 삭제합니다.");
			delete_dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				}
			});
			delete_dialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
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
