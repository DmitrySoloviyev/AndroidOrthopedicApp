package com.example.orthopedicdb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class DB {

	private static final String DB_NAME = "SHOES";
	private static final int DATABASE_VERSION = 10;
	private final Context mCtx;
	private DBHelper mDBHelper;
	public SQLiteDatabase mDB;

	public DB(Context ctx) {
		mCtx = ctx;
	}

	// открываем подключение
	public void open() {
		mDBHelper = new DBHelper(mCtx, DB_NAME, null, DATABASE_VERSION);
		mDB = mDBHelper.getWritableDatabase();
	}

	// закрываем подключение
	public void close() {
		if (mDBHelper != null) mDBHelper.close();
	}

	// НОВЫЙ ЗАКАЗ
	public void addNewOrder(String OrderID, String model_id,
			String model_picture_src, long material, String size_left,
			String size_right, String urk_left, String urk_right,
			String height_left, String height_right, String top_volume_left,
			String top_volume_right, String ankle_volume_left,
			String ankle_volume_right, String kv_volume_left,
			String kv_volume_right, String customerSN, String customerFN,
			String customerP, long employee_id) {

		ContentValues values_in_order = new ContentValues();

		values_in_order.put("OrderID", OrderID);
		values_in_order.put("ModelID", model_id);
		values_in_order.put("ModelPictureSRC", Uri.parse(model_picture_src).toString());
		values_in_order.put("MaterialID", material);
		values_in_order.put("SizeLEFT", size_left);
		values_in_order.put("SizeRIGHT", size_right);
		values_in_order.put("UrkLEFT", urk_left);
		values_in_order.put("UrkRIGHT", urk_right);
		values_in_order.put("HeightLEFT", height_left);
		values_in_order.put("HeightRIGHT", height_right);
		values_in_order.put("TopVolumeLEFT", top_volume_left);
		values_in_order.put("TopVolumeRIGHT", top_volume_right);
		values_in_order.put("AnkleVolumeLEFT", ankle_volume_left);
		values_in_order.put("AnkleVolumeRIGHT", ankle_volume_right);
		values_in_order.put("KvVolumeLEFT", kv_volume_left);
		values_in_order.put("KvVolumeRIGHT", kv_volume_right);
		values_in_order.put("CustomerSN", customerSN);
		values_in_order.put("CustomerFN", customerFN);
		values_in_order.put("CustomerP", customerP);
		values_in_order.put("EmployeeID", employee_id);
		mDB.insert("Orders", null, values_in_order);
	}

	// Количество заказов в базе
	public int countOrders(){
		Cursor orders  = mDB.rawQuery("SELECT OrderID FROM Orders", null);
		return orders.getCount();
	}
	
	// Количество модельеров в базе
	public int countEmployees(){
		Cursor employees = mDB.rawQuery("SELECT _id FROM Employees WHERE _id > 1", null);
		return employees.getCount();
	}
	
	// НОВЫЙ СОТРУДНИК
	public long addNewEmployee(String surname, String firstname, String patronymic) {
		ContentValues cv = new ContentValues();

		cv.put("EmployeeSN", surname);
		cv.put("EmployeeFN", firstname);
		cv.put("EmployeeP", patronymic);

		long id = mDB.insert("Employees", null, cv);
		return id;
	}

	// НОВЫЙ МАТЕРИАЛ
	public long addNewMaterial(String name) {
		ContentValues cv = new ContentValues();

		cv.put("MaterialValue", name);

		long id = mDB.insert("Materials", null, cv);
		return id;
	}

	// ВСЕ ЗАПИСИ (КРАТКО)
	public Cursor getAllShortOrders() {
		String sql = "SELECT o._id, o.OrderID AS OrderID, o.ModelID AS Model, mat.MaterialValue AS Material, " +
					 "SUBSTR(CustomerSN, 1)||'.'||SUBSTR(CustomerFN, 1, 1)||'.'||SUBSTR(CustomerP, 1, 1) as Customer, " +
					 "SUBSTR(emp.EmployeeSN, 1)||'.'||SUBSTR(emp.EmployeeFN, 1, 1)||'.'||SUBSTR(emp.EmployeeP, 1, 1) as Employee " +
					 "FROM Orders AS o " +
					 "INNER JOIN Materials AS mat ON o.MaterialID=mat._id " +
					 "INNER JOIN Employees AS emp ON o.EmployeeID=emp._id;";
		return mDB.rawQuery(sql, null);
	}

	// ВСЕ ЗАПИСИ (ПОДРОБНО)
	public Cursor getDetailedOrderById(long ID) {
		
		String sql = "SELECT o._id, " +
							"o.OrderID AS OrderID, " +
							"o.ModelID AS Model, " +
							"mat._id AS MaterialID, " +
							"mat.MaterialValue AS Material, " +
							"o.SizeLEFT, " +
							"o.SizeRIGHT, " +
							"o.UrkLEFT, " +
							"o.UrkRIGHT, " +
							"o.HeightLEFT, " +
							"o.HeightRIGHT, " +
							"o.TopVolumeLEFT, " +
							"o.TopVolumeRIGHT, " +
							"o.AnkleVolumeLEFT, " +
							"o.AnkleVolumeRIGHT, " +
							"o.KvVolumeLEFT, " +
							"o.KvVolumeRIGHT, " +
							"o.CustomerSN, " +
							"o.CustomerFN, " +
							"o.CustomerP, " +
							"emp._id AS EmployeeID, " +
							"emp.EmployeeSN, " +
							"emp.EmployeeFN, " +
							"emp.EmployeeP," +
							"o.ModelPictureSRC AS ModelIMG " +
							"FROM Orders AS o " +
							"INNER JOIN Materials AS mat ON o.MaterialID=mat._id " +
							"INNER JOIN Employees AS emp ON o.EmployeeID=emp._id " +
							"WHERE o._id=?;";
		return mDB.rawQuery(sql, new String[]{ String.valueOf(ID) });
	}


	// Проверка уникальности ID //
	public boolean checkID(String id) {
		Cursor cursor = mDB.rawQuery("SELECT OrderID FROM Orders WHERE OrderID=?", new String[] { id });
		if (cursor.getCount() == 0)
			return true;
		else
			return false;
	}

	// Автозаполнение AutoCompleteTextView МОДЕЛЬ//
	public List<String> getModelList() {
		List<String> labels = new ArrayList<String>();

		Cursor cursor = mDB.rawQuery("SELECT DISTINCT ModelID FROM Orders", null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				String str;
				do {
					str = "";
					str = cursor.getString(0);
					labels.add(str);
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		
		return labels;
	}

	// Автозаполнение SPINNER МАТЕРИАЛЫ
	public List<String> getMaterialList() throws SQLException {
		List<String> labels = new ArrayList<String>();
		Cursor cursor = mDB.rawQuery("SELECT DISTINCT MaterialValue FROM Materials", null);	
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				String str;
				do {
					str = "";
					str = cursor.getString(0);
					labels.add(str);
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return labels;
	}
	
	
	public Cursor getMaterialCursor() {
		return mDB.rawQuery("SELECT _id, MaterialValue, MaterialChecked FROM Materials WHERE _id>1", null);
	}
	

	// Автозаполнение SPINNER МОДЕЛЬЕРЫ//
	public List<String> getEmployeeList() {
		List<String> labels = new ArrayList<String>();
		Cursor cursor = mDB.rawQuery("SELECT EmployeeSN, EmployeeFN, EmployeeP, EmployeeChecked FROM Employees", null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				String str;
				do {
					str = "";
					int employeesn = cursor.getColumnIndex("EmployeeSN");
					int employeefn = cursor.getColumnIndex("EmployeeFN");
					int employeep = cursor.getColumnIndex("EmployeeP");
					str = cursor.getString(employeesn) + " "
							+ cursor.getString(employeefn) + " "
							+ cursor.getString(employeep);
					labels.add(str);
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return labels;
	}
	
	public Cursor getEmployeeCursor() {
		return mDB.rawQuery("SELECT _id, SUBSTR(EmployeeSN, 1)||'.'||SUBSTR(EmployeeFN, 1, 1)||'.'||SUBSTR(EmployeeP, 1, 1) as Employee, EmployeeChecked FROM Employees WHERE _id>1", null);
	}
	
	// УДАЛЕНИЕ ЗАПИСИ
	public void deleteOrderById(String[] whereArgs){
		
		
		
		
		if(whereArgs.length == 1){
			Cursor c = mDB.rawQuery("SELECT ModelPictureSRC FROM Orders WHERE _id=?  AND ModelPictureSRC <> ''", whereArgs);
			if(c != null){
				if (c.moveToFirst()) {
					do{
						String path = c.getString(c.getColumnIndex("ModelPictureSRC"));
						if(!path.isEmpty()){
							File img = new File(path);
							img.delete();
						}
					}while(c.moveToNext());
				}c.close();
			}
			mDB.delete("Orders", "_id = ?", whereArgs);
		}else{
			for (String id : whereArgs) {
				Cursor c = mDB.rawQuery("SELECT ModelPictureSRC FROM Orders WHERE _id=?  AND ModelPictureSRC <> ''", new String[]{id});
				if(c != null){
					if (c.moveToFirst()) {
						do{
							String path = c.getString(c.getColumnIndex("ModelPictureSRC"));
							if(!path.isEmpty()){
								File img = new File(path);
								img.delete();
							}
						}while(c.moveToNext());
					}c.close();
				}
				mDB.delete("Orders", "_id = ?", new String[]{id});
			}
		}
	}
	
	
	// РЕДАКТИРОВАНИЕ ЗАПИСИ
	public void updateOrderById(long id, 
								String OrderID, 
								String model_id,
								String model_picture_src, 
								long material,
								String size_left,
								String size_right, 
								String urk_left, 
								String urk_right,
								String height_left, 
								String height_right, 
								String top_volume_left,
								String top_volume_right, 
								String ankle_volume_left,
								String ankle_volume_right, 
								String kv_volume_left,
								String kv_volume_right, 
								String customerSN, 
								String customerFN,
								String customerP, 
								long employee_id){

		String sql_update_orders = "UPDATE Orders " +
									"SET OrderID = '"+OrderID+"', " +
										"ModelID='"+model_id+"', " +
										"ModelPictureSRC='"+model_picture_src+"', " +
										"MaterialID = '"+material+"', " +
										"SizeLEFT = '"+size_left+"', " +
										"SizeRIGHT = '"+size_right+"', " +
										"UrkLEFT = '"+urk_left+"', " +
										"UrkRIGHT = '"+urk_right+"', " +
										"HeightLEFT = '"+height_left+"', " +
										"HeightRIGHT = '"+height_right+"', " +
										"TopVolumeLEFT = '"+top_volume_left+"', " +
										"TopVolumeRIGHT = '"+top_volume_right+"', " +
										"AnkleVolumeLEFT = '"+ankle_volume_left+"', " +
										"AnkleVolumeRIGHT = '"+ankle_volume_right+"', " +
										"KvVolumeLEFT = '"+kv_volume_left+"', " +
										"KvVolumeRIGHT = '"+kv_volume_right+"', " +
										"CustomerSN = '"+customerSN+"', " +
										"CustomerFN = '"+customerFN+"', " +
										"CustomerP = '"+customerP+"', " +
										"EmployeeID = '"+employee_id+"' " +
										"WHERE _id = '"+id+"';";
		mDB.execSQL(sql_update_orders);
	}
	
	// БЫСТРЫЙ ПОИСК
	public Cursor quicklySearch(String query) {
		String sql = "SELECT o._id, o.OrderID AS OrderID, o.ModelID AS Model, mat.MaterialValue AS Material, " +
							"SUBSTR(o.CustomerSN, 1)||'.'||SUBSTR(o.CustomerFN, 1, 1)||'.'||SUBSTR(o.CustomerP, 1, 1) as Customer, " +
							"SUBSTR(emp.EmployeeSN, 1)||'.'||SUBSTR(emp.EmployeeFN, 1, 1)||'.'||SUBSTR(emp.EmployeeP, 1, 1) as Employee " +
				    "FROM Orders AS o " +
						"INNER JOIN Materials AS mat ON o.MaterialID=mat._id " +
						"INNER JOIN Employees AS emp ON o.EmployeeID=emp._id " +
					"WHERE OrderID = '"+query+"' " +
						"OR ModelID = '"+query+"' "+
						"OR MaterialValue = '"+query+"' "+
						"OR SizeLEFT = '"+query+"' "+
						"OR SizeRIGHT = '"+query+"' "+
						"OR UrkLEFT = '"+query+"' "+
						"OR UrkRIGHT = '"+query+"' "+
						"OR HeightLEFT = '"+query+"' "+
						"OR HeightRIGHT = '"+query+"' "+
						"OR TopVolumeLEFT = '"+query+"' "+
						"OR TopVolumeRIGHT = '"+query+"' "+
						"OR AnkleVolumeLEFT = '"+query+"' "+
						"OR AnkleVolumeRIGHT = '"+query+"' "+
						"OR KvVolumeLEFT = '"+query+"' "+
						"OR KvVolumeRIGHT = '"+query+"' "+
						"OR EmployeeSN = '"+query+"' "+
						"OR EmployeeFN = '"+query+"' "+
						"OR EmployeeP = '"+query+"' "+
						"OR CustomerSN = '"+query+"' "+
						"OR CustomerFN = '"+query+"' "+
						"OR CustomerP = '"+query+"' ;";
		Cursor cursor = mDB.rawQuery(sql, null);
		return cursor;
	}
	
	// РАСШИРЕННЫЙ ПОИСК
	public Cursor extendedSearch(String where) {
		String sql = "SELECT o._id, o.OrderID AS OrderID, o.ModelID AS Model, mat.MaterialValue AS Material, " +
							"SUBSTR(o.CustomerSN, 1)||'.'||SUBSTR(o.CustomerFN, 1, 1)||'.'||SUBSTR(o.CustomerP, 1, 1) as Customer, " +
							"SUBSTR(emp.EmployeeSN, 1)||'.'||SUBSTR(emp.EmployeeFN, 1, 1)||'.'||SUBSTR(emp.EmployeeP, 1, 1) as Employee " +
				    "FROM Orders AS o " +
						"INNER JOIN Materials AS mat ON o.MaterialID=mat._id " +
						"INNER JOIN Employees AS emp ON o.EmployeeID=emp._id " +
					"WHERE "+where+" ;";
		Cursor cursor = mDB.rawQuery(sql, null);
		return cursor;
	}
	
	
	public void changeMaterialFlag(int pos, boolean isChecked) {
	      ContentValues cv = new ContentValues();
	      cv.put("MaterialChecked", (isChecked) ? 1 : 0);
	      mDB.update("Materials", cv, " _id = " + (pos+2), null);
	}
	
	public void changeEmployeeFlag(int pos, boolean isChecked) {
	      ContentValues cv = new ContentValues();
	      cv.put("EmployeeChecked", (isChecked) ? 1 : 0);
	      mDB.update("Employees", cv, " _id = " + (pos+2), null);
	}
	
	
	public void cleanMaterialChecked(){
		ContentValues cv = new ContentValues();
	    cv.put("MaterialChecked", 0);
		mDB.update("Materials", cv, null, null);
	}
	
	public void cleanEmployeeChecked(){
		ContentValues cv = new ContentValues();
	    cv.put("EmployeeChecked", 0);
		mDB.update("Employees", cv, null, null);
	}
	
	public Cursor getModelsGallery(){
		return mDB.rawQuery("SELECT _id, ModelID, ModelPictureSRC as ModelIMG FROM Orders WHERE ModelPictureSRC <> '' ;", null);
	}
	
	// ///////////////////////////////////////////////////////////////////////////////////// //

	private class DBHelper extends SQLiteOpenHelper {

		ContentValues cv = new ContentValues();

		public DBHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// ТАБЛИЦА МАТЕРИАЛОВ
			db.execSQL("CREATE TABLE Materials ( "
					+ "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
					+ "MaterialValue TEXT NOT NULL, "
					+ "MaterialChecked INTEGER DEFAULT '0');");

			String[] materials = { " +++ новый материал +++", "К/П", "Траспира",
					"Мех Натуральный", "Мех Искусственный", "Мех Полушерстяной" };

			for (int i = 0; i < materials.length; i++) {
				cv.clear();
				cv.put("MaterialValue", materials[i]);
				db.insert("Materials", null, cv);
			}


			// ТАБЛИЦА СОТРУДНИКОВ
			db.execSQL("CREATE TABLE Employees ( "
					+ "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
					+ "EmployeeSN TEXT NOT NULL," + "EmployeeFN TEXT NOT NULL,"
					+ "EmployeeP TEXT NOT NULL, "
					+ "EmployeeChecked INTEGER DEFAULT '0');");
			cv.clear();
			cv.put("EmployeeSN", " +++ ");
			cv.put("EmployeeFN", "новый модельер");
			cv.put("EmployeeP", " +++ ");
			db.insert("Employees", null, cv);

			// ТАБЛИЦА ЗАКАЗОВ
			final String CREATE_TABLE_Orders = "CREATE TABLE Orders ( "
					+ "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
					+ "OrderID TEXT NOT NULL, "
					+ "ModelID TEXT NOT NULL, "
					+ "ModelPictureSRC TEXT NOT NULL DEFAULT '', "
					+ "MaterialID INTEGER NOT NULL, "
					+ "SizeLEFT INTEGER NOT NULL, "
					+ "SizeRIGHT INTEGER NOT NULL, "
					+ "UrkLEFT INTEGER NOT NULL, "
					+ "UrkRIGHT INTEGER NOT NULL, "
					+ "HeightLEFT INTEGER NOT NULL, "
					+ "HeightRIGHT INTEGER NOT NULL, "
					+ "TopVolumeLEFT REAL NOT NULL, "
					+ "TopVolumeRIGHT REAL NOT NULL, "
					+ "AnkleVolumeLEFT REAL NOT NULL, "
					+ "AnkleVolumeRIGHT REAL NOT NULL, "
					+ "KvVolumeLEFT REAL NOT NULL, "
					+ "KvVolumeRIGHT REAL NOT NULL, "
					+ "CustomerSN TEXT NOT NULL, "
					+ "CustomerFN TEXT NOT NULL, "
					+ "CustomerP TEXT NOT NULL, "
					+ "EmployeeID INTEGER NOT NULL, "
					+ "FOREIGN KEY(EmployeeID) REFERENCES Employees(_id) "
					+ "FOREIGN KEY(MaterialID) REFERENCES Materials(_id));";
			db.execSQL(CREATE_TABLE_Orders);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS Orders");
			db.execSQL("DROP TABLE IF EXISTS Materials");
			db.execSQL("DROP TABLE IF EXISTS Models");
			db.execSQL("DROP TABLE IF EXISTS Employees");
			onCreate(db);
		}
	}
}