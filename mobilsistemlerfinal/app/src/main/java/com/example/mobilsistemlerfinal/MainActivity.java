package com.example.mobilsistemlerfinal;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText editTarih, editYemek, editKalori;
    Spinner spinnerOgun;
    Button btnEkle, btnSil;
    TextView txtToplam;
    ListView listView;

    DatabaseHelper db;
    ArrayList<String> liste = new ArrayList<>();
    ArrayList<String> idList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    int secilenIndex = -1;

    String[] ogunler = {"Kahvaltı", "Öğle", "Akşam", "Ara Öğün"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View bağlantıları
        editTarih = findViewById(R.id.editTarih);
        editYemek = findViewById(R.id.editYemek);
        editKalori = findViewById(R.id.editKalori);
        spinnerOgun = findViewById(R.id.spinnerOgun);
        btnEkle = findViewById(R.id.btnEkle);
        btnSil = findViewById(R.id.btnSil);
        txtToplam = findViewById(R.id.txtToplam);
        listView = findViewById(R.id.listViewYemekler);

        // Database
        db = new DatabaseHelper(this);

        // Spinner
        spinnerOgun.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                ogunler
        ));

        // ListView
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                liste
        );
        listView.setAdapter(adapter);

        // Tarih seçici
        editTarih.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(
                    this,
                    (view, y, m, d) ->
                            editTarih.setText(d + "." + (m + 1) + "." + y),
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // EKLE / GÜNCELLE
        btnEkle.setOnClickListener(v -> {

            String tarih = editTarih.getText().toString();
            String yemek = editYemek.getText().toString();
            String kaloriStr = editKalori.getText().toString();
            String ogun = spinnerOgun.getSelectedItem().toString();

            if (tarih.isEmpty() || yemek.isEmpty() || kaloriStr.isEmpty()) {
                Toast.makeText(this, "Eksik alan bırakmayın", Toast.LENGTH_SHORT).show();
                return;
            }

            int kalori = Integer.parseInt(kaloriStr);

            if (secilenIndex == -1) {
                // EKLE
                db.veriEkle(tarih, yemek, ogun, kalori);
            } else {
                // GÜNCELLE
                String id = idList.get(secilenIndex);
                db.veriGuncelle(id, tarih, yemek, ogun, kalori);

                secilenIndex = -1;
                btnEkle.setText("EKLE");
                btnSil.setVisibility(View.GONE);
            }

            verileriYukle();
            editYemek.setText("");
            editKalori.setText("");
        });

        // Listeye tıkla → güncelleme modu
        listView.setOnItemClickListener((parent, view, position, id) -> {
            secilenIndex = position;
            btnEkle.setText("GÜNCELLE");
            btnSil.setVisibility(View.VISIBLE);
        });

        // SİL
        btnSil.setOnClickListener(v -> {
            if (secilenIndex != -1) {
                String id = idList.get(secilenIndex);
                db.veriSil(id);

                secilenIndex = -1;
                btnEkle.setText("EKLE");
                btnSil.setVisibility(View.GONE);

                verileriYukle();
            }
        });
    }

    private void verileriYukle() {
        liste.clear();
        idList.clear();
        int toplam = 0;

        Cursor c = db.verileriGetir(editTarih.getText().toString());
        while (c.moveToNext()) {
            idList.add(c.getString(0)); // ID
            String satir = c.getString(2) + " (" + c.getString(3) + ") - "
                    + c.getInt(4) + " kcal";
            toplam += c.getInt(4);
            liste.add(satir);
        }
        c.close();

        txtToplam.setText("Toplam Kalori: " + toplam);
        adapter.notifyDataSetChanged();
    }
}
