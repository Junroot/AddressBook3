package com.addressbook;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class AddressTab extends Fragment
{
	String search="";
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
		Cursor cursor = db.rawQuery("SELECT * FROM people WHERE (name like "+"'%"+search+"%'" +") or ( number like "+"'%"+search+"%'" +")order by name, number asc", null);
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
			Cursor cursor = db.rawQuery("SELECT * FROM people WHERE (name like "+"'%"+search+"%'" +") or ( number like "+"'%"+search+"%'" +")order by name, number asc", null);
			cursor.moveToPosition(position);
			Intent intent = new Intent(getActivity(), EditAddress.class);
	    	intent.putExtra("name", cursor.getString(1));
			intent.putExtra("number", cursor.getString(2));
			intent.putExtra("addressid", id);
			intent.putExtra("search", search);
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
		        Cursor cursor = db.rawQuery("SELECT * FROM people WHERE (name like "+"'%"+search+"%'" +") or ( number like "+"'%"+search+"%'" +")order by name, number asc", null);
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
		
		 SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
	        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

	            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
	            searchView.setIconifiedByDefault(true);   

	        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() 
	        {
	            @Override
	            public boolean onQueryTextChange(String newText) 
	            {
	                // this is your adapter that will be filtered
	            	search = newText;
	            	Cursor cursor = db.rawQuery("SELECT * FROM people WHERE (name like "+"'%"+search+"%'" +") or ( number like "+"'%"+search+"%'" +")order by name, number asc", null);
					cursor.moveToFirst();
					adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, cursor, new String[] { "name", "number" }, new int[] { android.R.id.text1, android.R.id.text2 }, 0);
			        lv_people = (ListView)getActivity().findViewById(R.id.addresslist);
					lv_people.setAdapter(adapter);
	                return true;
	            }
	            @Override
	            public boolean onQueryTextSubmit(String query) 
	            {
	                // this is your adapter that will be filtered
	            	Toast toast = Toast.makeText(getActivity(), "�̸��� �Է����ּ���.", Toast.LENGTH_SHORT);
					toast.show();
	                return true;
	            }
	        };
	        searchView.setOnQueryTextListener(textChangeListener);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
	    switch(item.getItemId()){
	    case R.id.add:
	        // your action goes here
	    	Intent intent = new Intent(getActivity(), AddAddress.class);
	    	//intent.putExtra("name", "");
			//intent.putExtra("number", "");
	    	intent.putExtra("search", search);
			getActivity().startActivityForResult(intent, 0);
			return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
