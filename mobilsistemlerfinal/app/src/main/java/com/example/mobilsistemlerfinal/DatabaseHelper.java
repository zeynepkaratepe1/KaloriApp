package com.example.mobilsistemlerfinal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "KaloriTakip.db";
    private static final String TABLE_NAME = "kaloriler";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME + " (" +
                        "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "TARIH TEXT, " +
                        "YEMEK TEXT, " +
                        "OGUN TEXT, " +
                        "KALORI INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // EKLE
    public boolean veriEkle(String tarih, String yemek, String ogun, int kalori) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("TARIH", tarih);
        cv.put("YEMEK", yemek);
        cv.put("OGUN", ogun);
        cv.put("KALORI", kalori);
        return db.insert(TABLE_NAME, null, cv) != -1;
    }

    // GETİR
    public Cursor verileriGetir(String tarih) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE TARIH = ?",
                new String[]{tarih}
        );
    }

    // GÜNCELLE
    public void veriGuncelle(String id, String tarih, String yemek, String ogun, int kalori) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("TARIH", tarih);
        cv.put("YEMEK", yemek);
        cv.put("OGUN", ogun);
        cv.put("KALORI", kalori);
        db.update(TABLE_NAME, cv, "ID = ?", new String[]{id});
    }

    // SİL
    public void veriSil(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }
}