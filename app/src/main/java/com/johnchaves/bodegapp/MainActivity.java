package com.johnchaves.bodegapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelStoreOwner;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String [] modos = {"UBICACIÓN","DETALLE"};
    Spinner Modo;
    RadioGroup radioGroup;
    RadioButton btnPallet, btnSerial, btnProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Modo        = (Spinner) findViewById(R.id.Modo);
        radioGroup  = (RadioGroup) findViewById(R.id.radioGroup);
        btnPallet   = (RadioButton) findViewById(R.id.btn_pallet);
        btnSerial   = (RadioButton) findViewById(R.id.btn_serial);
        btnProducto = (RadioButton) findViewById(R.id.btn_prod);

        Modo.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        ArrayAdapter aa = new ArrayAdapter(this, R.layout.spinner_item,modos);
        aa.setDropDownViewResource(R.layout.spinner_dropdown_item);
        Modo.setAdapter(aa);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}