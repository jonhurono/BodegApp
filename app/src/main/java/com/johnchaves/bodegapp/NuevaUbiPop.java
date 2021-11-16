package com.johnchaves.bodegapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.renderscript.Sampler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class NuevaUbiPop extends Activity {

    EditText        inputUbi;
    Button          btnVerificar, btnUpdate;
    Switch          switchi;
    TableLayout     tablita;
    Spinner         bodegas, racks, alturas, profundidades;
    private boolean success = false; // boolean
    TextView        bd_bodega, bd_rack, bd_altura, bd_prof;
    TextView        NumPalet = MainActivity.getNumPalet();
    TextView        CodUbi = MainActivity.getCodUbi();

    //private MyAppAdapter    myAppAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevaubi_pop);

        inputUbi        =   (EditText) findViewById(R.id.inputUbi);
        btnVerificar    =   (Button) findViewById(R.id.btnVerificar);
        switchi         =   (Switch) findViewById(R.id.switchi);
        tablita         =   (TableLayout) findViewById(R.id.tablita);
        bodegas         =   (Spinner) findViewById(R.id.spinnerBod);
        racks           =   (Spinner) findViewById(R.id.spinnerRack);
        alturas         =   (Spinner) findViewById(R.id.spinnerAlt);
        profundidades   =   (Spinner) findViewById(R.id.spinnerProf);
        btnUpdate       =   (Button) findViewById(R.id.btnUpdate);
        bd_bodega       =   (TextView) findViewById(R.id.bd_bod);
        bd_rack         =   (TextView) findViewById(R.id.bd_rack);
        bd_altura       =   (TextView) findViewById(R.id.bd_altura);
        bd_prof         =   (TextView) findViewById(R.id.bd_prof);

        GetBodegas();
        /*GetRacks();
        GetAlturas();
        GetProfundidades();*/

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width   = dm.widthPixels;
        int height  = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        getWindow().setAttributes(params);

        inputUbi.requestFocus();

        tablita.setVisibility(View.INVISIBLE);
        btnUpdate.setVisibility(View.INVISIBLE);

        btnVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetInfoUbi();
                inputUbi.requestFocus();
            }
        });

        inputUbi.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length()>0){
                    bodegas.setEnabled(false);
                    racks.setEnabled(false);
                    alturas.setEnabled(false);
                    profundidades.setEnabled(false);

                    bd_bodega.setText(null);
                    bd_rack.setText(null);
                    bd_altura.setText(null);
                    bd_prof.setText(null);

                } else {
                    bodegas.setEnabled(true);
                    racks.setEnabled(true);
                    alturas.setEnabled(true);
                    profundidades.setEnabled(true);
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

        inputUbi.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == inputUbi.getImeActionId()){
                    if (inputUbi.length() > 0){
                        bd_bodega.setText(null);
                        bd_rack.setText(null);
                        bd_altura.setText(null);
                        bd_prof.setText(null);
                        btnUpdate.callOnClick();
                        //btnVerificar.callOnClick();
                        //GetInfoUbi();
                        inputUbi.requestFocus();
                    }
                    else{
                        inputUbi.requestFocus();
                    }
                    inputUbi.requestFocus();
                    //handled = true;
                }
                return handled;
            }
        });

        /*bodegas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetBodegas();
            }
        });*/

        bodegas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetRacks();
                //racks.performClick();
                bd_bodega.setText(bodegas.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        racks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetAlturas();
                bd_rack.setText(racks.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        alturas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetProfundidades();
                bd_altura.setText(alturas.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        profundidades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bd_prof.setText(profundidades.getSelectedItem().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asignarUbi();
            }
        });

    }

    private void asignarUbi() {
        try {
            Statement pst = conexionDB().createStatement();

            int rs = pst.executeUpdate("EXEC SP_U_MOV_PALLET " +
                    " @NUMPALET = '"+NumPalet.getText().toString()+"' , " +
                    " @UBICACION = '"+inputUbi.getText().toString()+"' ");
                    /*" @UBICACION = '"+(bd_bodega.getText().toString()+bd_rack.getText().toString()+
                                        bd_altura.getText().toString()+bd_prof.getText().toString())+"' ");*/

            Toast.makeText(getApplicationContext(),"NUEVA UBICACIÓN ASIGNADA CORRECTAMENTE", Toast.LENGTH_LONG).show();

            NumPalet.setText(null);
            CodUbi.setText(null);


            finish();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void GetInfoUbi(){

        //if (inputUbi.toString().length() > 0){
            try {
                Statement stm = conexionDB().createStatement();
            /*ResultSet rs = stm.executeQuery("EXEC Sp_c_BodegApp '7', " +
                    "@CodUbi = '" + inputUbi.getText().toString() + "' ");*/

                ResultSet rs = stm.executeQuery("EXEC Sp_c_Ubicacion @Modo = 'E', " +
                        "@Ubicacion = '" + inputUbi.getText().toString() + "' ");

                if (rs.getString(6) != ("0"))
                {
                    Toast.makeText(getApplicationContext(),"UBICACIÓN YA OCUPADA", Toast.LENGTH_LONG).show();
                }

                else {

                    asignarUbi();
                    //insertUbi();
                    Toast.makeText(getApplicationContext(), "UBICACIÓN ASIGNADA", Toast.LENGTH_SHORT).show();
                    success = false;

                }

                inputUbi.setText(null);
                inputUbi.requestFocus();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }/*
        }

        /*else{
            try {
                Statement stm = conexionDB().createStatement();
            ResultSet rs = stm.executeQuery("EXEC Sp_c_BodegApp '7', " +
                    "@CodUbi = '" + inputUbi.getText().toString() + "' ");

                ResultSet rs = stm.executeQuery("EXEC Sp_c_Ubicacion @Modo = 'E', " +
                        "@Ubicacion = '" + bodegas.getSelectedItem().toString()+racks.getSelectedItem().toString()+
                        alturas.getSelectedItem().toString()+profundidades.getSelectedItem().toString()+ "' ");

                if (rs.next())
                {
                    Toast.makeText(getApplicationContext(),"UBICACIÓN YA OCUPADA", Toast.LENGTH_LONG).show();
                }

                else {

                    asignarUbi();
                    //insertUbi();
                    Toast.makeText(getApplicationContext(), "UBICACIÓN CREADA Y/O DISPONIBLE", Toast.LENGTH_SHORT).show();
                    success = false;

                }

                inputUbi.requestFocus();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }*/
        inputUbi.requestFocus();
    }

    public void GetBodegas(){
        try {
            Statement stm = conexionDB().createStatement();
            ResultSet rs = stm.executeQuery("SELECT DISTINCT Bodega " +
                    "FROM Bdg_Bodega ");

            if (rs != null)
            {
                ArrayList<String> data = new ArrayList<String>();
                while (rs.next()) {
                    String bodega = rs.getString(1);
                    data.add(bodega);
                }

                ArrayAdapter array = new ArrayAdapter(this, R.layout.spinner_item2,data);
                array.setDropDownViewResource(R.layout.spinner_dropdown_item);
                bodegas.setAdapter (array);
                success = true;

            } else {
                Toast.makeText(getApplicationContext(), "ERROR EN LA CONSULTA A BODEGAS", Toast.LENGTH_SHORT).show();
                success = false;
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void GetRacks(){
        try {
            Statement stm = conexionDB().createStatement();

            /*ResultSet rs = stm.executeQuery("SELECT DISTINCT Rack " +
                    "FROM bdg_Rack ");*/

            ResultSet rs = stm.executeQuery("SELECT DISTINCT Rack " +
                    "FROM bdg_ubicacion " +
                    "WHERE Bodega = '"+bodegas.getSelectedItem().toString()+"' " +
                    "ORDER BY Rack ASC");

            if (rs != null)
            {
                ArrayList<String> data = new ArrayList<String>();
                while (rs.next()) {
                    String rack = rs.getString(1);
                    data.add(rack);
                }

                ArrayAdapter array = new ArrayAdapter(this, R.layout.spinner_item2,data);
                array.setDropDownViewResource(R.layout.spinner_dropdown_item);
                racks.setAdapter (array);
                success = true;

            } else {
                Toast.makeText(getApplicationContext(), "ERROR EN LA CONSULTA A BODEGAS", Toast.LENGTH_SHORT).show();
                success = false;
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void GetAlturas(){
        try {
            Statement stm = conexionDB().createStatement();

            /*ResultSet rs = stm.executeQuery("SELECT DISTINCT Altura " +
                    "FROM bdg_Altura ");*/

            ResultSet rs = stm.executeQuery("SELECT DISTINCT Altura " +
                    "FROM bdg_ubicacion " +
                    "WHERE Bodega = '"+bodegas.getSelectedItem().toString()+"' " +
                    "AND Rack = '"+racks.getSelectedItem().toString()+"' ");

            if (rs != null)
            {
                ArrayList<String> data = new ArrayList<String>();
                while (rs.next()) {
                    String altura = rs.getString(1);
                    data.add(altura);
                }

                ArrayAdapter array = new ArrayAdapter(this, R.layout.spinner_item2,data);
                array.setDropDownViewResource(R.layout.spinner_dropdown_item);
                alturas.setAdapter (array);
                success = true;

            } else {
                Toast.makeText(getApplicationContext(), "ERROR EN LA CONSULTA A BODEGAS", Toast.LENGTH_SHORT).show();
                success = false;
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void GetProfundidades(){
        try {
            Statement stm = conexionDB().createStatement();

            /*ResultSet rs = stm.executeQuery("SELECT DISTINCT Profundidad " +
                    "FROM Bdg_Profundidad ");*/
            ResultSet rs = stm.executeQuery("SELECT DISTINCT Profundidad " +
                    "FROM bdg_ubicacion " +
                    "WHERE Bodega = '"+bodegas.getSelectedItem().toString()+"' " +
                    "AND Rack = '"+racks.getSelectedItem().toString()+"' " +
                    "AND Altura = '"+alturas.getSelectedItem().toString()+"' ");

            if (rs != null)
            {
                ArrayList<String> data = new ArrayList<String>();
                while (rs.next()) {
                    String profundidad = rs.getString(1);
                    data.add(profundidad);
                }

                ArrayAdapter array = new ArrayAdapter(this, R.layout.spinner_item2,data);
                array.setDropDownViewResource(R.layout.spinner_dropdown_item);
                profundidades.setAdapter (array);
                success = true;

            } else {
                Toast.makeText(getApplicationContext(), "ERROR EN LA CONSULTA", Toast.LENGTH_SHORT).show();
                success = false;
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void insertUbi(){

        if (inputUbi.toString().length() > 0){

            try {
                Statement pst = conexionDB().createStatement();

                int rs = pst.executeUpdate("EXEC Sp_i_Ubicacion" +
                        " @Ubicacion = '"+bd_bodega.getText()+bd_rack.getText()+
                        bd_altura.getText()+bd_prof.getText()+"'," +
                        " @Bodega = '"+bd_bodega.getText()+"'," +
                        " @Rack = '"+bd_rack.getText()+"'," +
                        " @Altura = '"+bd_altura.getText()+"'," +
                        " @Profundidad = '"+bd_prof.getText()+"' ");

                Toast.makeText(getApplicationContext(),"NUEVA UBICACIÓN CREADA CORRECTAMENTE", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

        else {

            try {
                Statement pst = conexionDB().createStatement();

                int rs = pst.executeUpdate("EXEC Sp_i_Ubicacion" +
                        " @Ubicacion = '"+bd_bodega.getText()+bd_rack.getText()+
                        bd_altura.getText()+bd_prof.getText()+"'," +
                        " @Bodega = '"+bd_bodega.getText()+"'," +
                        " @Rack = '"+bd_rack.getText()+"'," +
                        " @Altura = '"+bd_altura.getText()+"'," +
                        " @Profundidad = '"+bd_prof.getText()+"' ");

                Toast.makeText(getApplicationContext(),"NUEVA UBICACIÓN CREADA CORRECTAMENTE", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Connection conexionDB(){
        Connection conexion=null;
        try{
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            //conexion= DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.11;databaseName=Bodega;user=Movil;password=Mv2021;");
            conexion= DriverManager.getConnection("jdbc:jtds:sqlserver://172.16.15.61;databaseName=Bodega;user=sa;password=avanW3lc01;");

        }catch(Exception e){
            Toast toast = Toast.makeText(getApplicationContext(),"SIN CONEXIÓN A BASE DE DATOS",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        return conexion;
    }

    public void cambiarUI(View view) {
        if (switchi.isChecked()) {
            inputUbi.setVisibility(View.INVISIBLE);
            btnVerificar.setVisibility(View.INVISIBLE);

            tablita.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.VISIBLE);

        }else{

            inputUbi.setVisibility(View.VISIBLE);
            btnVerificar.setVisibility(View.VISIBLE);

            tablita.setVisibility(View.INVISIBLE);
            btnUpdate.setVisibility(View.INVISIBLE);
        }
    }
}
