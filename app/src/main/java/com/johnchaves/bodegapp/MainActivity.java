package com.johnchaves.bodegapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelStoreOwner;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String [] modos = {"UBICACIÓN","DETALLE"};
    Spinner     Modo;
    RadioGroup  radioGroup;
    RadioButton btnPallet, btnSerial, btnProducto, btnUbic;
    EditText    inputCod;
    Button      btnSubmit, btnNuevaUbi, btnDespacho;
    TextView    result1;
    TableRow    filita2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Modo        = (Spinner) findViewById(R.id.Modo);
        radioGroup  = (RadioGroup) findViewById(R.id.radioGroup);
        btnPallet   = (RadioButton) findViewById(R.id.btn_pallet);
        btnSerial   = (RadioButton) findViewById(R.id.btn_serial);
        btnProducto = (RadioButton) findViewById(R.id.btn_prod);
        btnUbic     = (RadioButton) findViewById(R.id.btn_ubi);
        inputCod    = (EditText) findViewById(R.id.inputCod);
        btnSubmit   = (Button) findViewById(R.id.button);
        result1     = (TextView) findViewById(R.id.result1);
        filita2     = (TableRow) findViewById(R.id.filita2);
        btnNuevaUbi = (Button) findViewById(R.id.btnNuevaUbi);
        btnDespacho = (Button) findViewById(R.id.btnDespachar);
        Modo.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        ArrayAdapter aa = new ArrayAdapter(this, R.layout.spinner_item,modos);
        aa.setDropDownViewResource(R.layout.spinner_dropdown_item);
        Modo.setAdapter(aa);

        inputCod.requestFocus();
        //btnSubmit.setEnabled(false);

        inputCod.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0){
                    btnSubmit.setEnabled(false);
                } else {
                    btnSubmit.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // DO_NOTHING();
            }
        });

        inputCod.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == inputCod.getImeActionId()){
                    if (inputCod.length() > 0){
                        buscarInfo();
                    }
                    else{

                    }
                    handled = true;
                }
                return handled;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarInfo();
                }
            }
        );

        btnNuevaUbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NuevaUbiPop.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String mode = parent.getSelectedItem().toString();

        if(mode.equals("UBICACIÓN")){
            btnUbic.setVisibility(View.GONE);
            btnProducto.setVisibility(View.VISIBLE);

        }
        if(mode.equals("DETALLE")){
            btnProducto.setVisibility(View.GONE);
            btnUbic.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public Connection conexionDB(){
        Connection conexion=null;
        try{
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            conexion= DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.11;databaseName=Bodega;user=Movil;password=Mv2021;");

        }catch(Exception e){
            Toast toast = Toast.makeText(getApplicationContext(),"SIN CONEXIÓN A BASE DE DATOS",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        return conexion;
    }

    public void buscarInfo() {

        if (btnPallet.isChecked()) {
            try {
                Statement stm = conexionDB().createStatement();
                ResultSet rs = stm.executeQuery("EXEC Sp_c_BodegApp '1', " +
                        "@NumPalet = '" + inputCod.getText().toString() + "' ");

                if (rs.next()) {
                    result1.setText(rs.getString(1));
                    filita2.setVisibility(View.VISIBLE);
                } else {
                    result1.setText(null);
                    Toast toast = Toast.makeText(getApplicationContext(), "CÓDIGO INVÁLIDO O INEXISTENTE EN BODEGA", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,250,0);
                    toast.show();
                    filita2.setVisibility(View.INVISIBLE);
                }
                inputCod.setText("");
                inputCod.requestFocus();

            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                filita2.setVisibility(View.INVISIBLE);
            }
            inputCod.setText("");
            inputCod.requestFocus();
       ; }
        else if(btnSerial.isChecked()){
            try {
                Statement stm = conexionDB().createStatement();
                ResultSet rs = stm.executeQuery("EXEC Sp_c_BodegApp '2', " +
                        "@CodSerial = '" + inputCod.getText().toString() + "' ");

                if (rs.next()) {
                    result1.setText(rs.getString(1));
                    filita2.setVisibility(View.VISIBLE);
                } else {
                    result1.setText(null);
                    Toast.makeText(getApplicationContext(), "CÓDIGO INVÁLIDO O INEXISTENTE EN BODEGA", Toast.LENGTH_LONG).show();
                    filita2.setVisibility(View.INVISIBLE);

                }
                inputCod.setText("");
                inputCod.requestFocus();

            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                filita2.setVisibility(View.INVISIBLE);
            }
            inputCod.setText("");
            inputCod.requestFocus();
        }
        else if(btnProducto.isChecked()){
            try {
                Statement stm = conexionDB().createStatement();
                ResultSet rs = stm.executeQuery("EXEC Sp_c_BodegApp '3', " +
                        "@CodProd = '" + inputCod.getText().toString() + "' ");

                if (rs.next()) {
                    result1.setText(rs.getString(1));
                    filita2.setVisibility(View.VISIBLE);
                } else {
                    result1.setText(null);
                    Toast.makeText(getApplicationContext(), "CÓDIGO INVÁLIDO O INEXISTENTE EN BODEGA", Toast.LENGTH_LONG).show();
                    filita2.setVisibility(View.INVISIBLE);
                }
                inputCod.setText("");
                inputCod.requestFocus();

            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                filita2.setVisibility(View.INVISIBLE);
            }
            inputCod.setText("");
            inputCod.requestFocus();
        }
    }
}