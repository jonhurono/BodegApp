package com.johnchaves.bodegapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class NuevaUbiPop extends Activity {

    Spinner bodegas, racks, alturas, profundidades;
    private boolean success = false; // boolean
    //private MyAppAdapter    myAppAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevaubi_pop);

        bodegas         =   (Spinner) findViewById(R.id.spinnerBod);
        racks           =   (Spinner) findViewById(R.id.spinnerRack);
        alturas         =   (Spinner) findViewById(R.id.spinnerAlt);
        profundidades   =   (Spinner) findViewById(R.id.spinnerProf);

        GetBodegas();
        
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

        bodegas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetRacks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        racks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetAlturas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        alturas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetProfundidades();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        

    }

    public void GetBodegas(){
        try {
            Statement stm = conexionDB().createStatement();
            ResultSet rs = stm.executeQuery("SELECT DISTINCT Bodega " +
                    "FROM Bdg_Bodega " +
                    "ORDER BY Bodega ASC ");

            if (rs != null) // if resultset not null, I add items to itemArraylist using class created
            {
                ArrayList<String> data = new ArrayList<String>();
                while (rs.next()) {
                    String bodega = rs.getString(1);
                    data.add(bodega);
                }

                ArrayAdapter array = new ArrayAdapter(this, R.layout.spinnerlayout,data);
                bodegas.setAdapter (array);
                success = true;

            } else {
                Toast.makeText(getApplicationContext(), "ERROR EN LA CONSULTA A BODEGAS", Toast.LENGTH_SHORT).show();
                success = false;
            }
            //myAppAdapter = new MyAppAdapter(itemArrayList, MainActivity.this);
            //recyclerView.setAdapter(myAppAdapter);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void GetRacks(){
        try {
            Statement stm = conexionDB().createStatement();
            ResultSet rs = stm.executeQuery("SELECT DISTINCT Rack " +
                    "FROM bdg_ubicacion " +
                    "WHERE Bodega = '"+bodegas.getSelectedItem().toString()+"' " +
                    "ORDER BY Rack ASC");

            if (rs != null) // if resultset not null, I add items to itemArraylist using class created
            {
                ArrayList<String> data = new ArrayList<String>();
                while (rs.next()) {
                    String rack = rs.getString(1);
                    data.add(rack);
                }

                ArrayAdapter array = new ArrayAdapter(this, R.layout.spinnerlayout,data);
                racks.setAdapter (array);
                success = true;

            } else {
                Toast.makeText(getApplicationContext(), "ERROR EN LA CONSULTA A BODEGAS", Toast.LENGTH_SHORT).show();
                success = false;
            }
            //myAppAdapter = new MyAppAdapter(itemArrayList, MainActivity.this);
            //recyclerView.setAdapter(myAppAdapter);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void GetAlturas(){
        try {
            Statement stm = conexionDB().createStatement();
            ResultSet rs = stm.executeQuery("SELECT DISTINCT Altura " +
                    "FROM bdg_ubicacion " +
                    "WHERE Bodega = '"+bodegas.getSelectedItem().toString()+"' " +
                    "AND Rack = '"+racks.getSelectedItem().toString()+"' ");

            if (rs != null) // if resultset not null, I add items to itemArraylist using class created
            {
                ArrayList<String> data = new ArrayList<String>();
                while (rs.next()) {
                    String altura = rs.getString(1);
                    data.add(altura);
                }

                ArrayAdapter array = new ArrayAdapter(this, R.layout.spinnerlayout,data);
                alturas.setAdapter (array);
                success = true;

            } else {
                Toast.makeText(getApplicationContext(), "ERROR EN LA CONSULTA A BODEGAS", Toast.LENGTH_SHORT).show();
                success = false;
            }
            //myAppAdapter = new MyAppAdapter(itemArrayList, MainActivity.this);
            //recyclerView.setAdapter(myAppAdapter);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void GetProfundidades(){
        try {
            Statement stm = conexionDB().createStatement();
            ResultSet rs = stm.executeQuery("SELECT DISTINCT Profundidad " +
                    "FROM bdg_ubicacion " +
                    "WHERE Bodega = '"+bodegas.getSelectedItem().toString()+"' " +
                    "AND Rack = '"+racks.getSelectedItem().toString()+"' " +
                    "AND Altura = '"+alturas.getSelectedItem().toString()+"' ");

            if (rs != null) // if resultset not null, I add items to itemArraylist using class created
            {
                ArrayList<String> data = new ArrayList<String>();
                while (rs.next()) {
                    String profundidad = rs.getString(1);
                    data.add(profundidad);
                }

                ArrayAdapter array = new ArrayAdapter(this, R.layout.spinnerlayout,data);
                profundidades.setAdapter (array);
                success = true;

            } else {
                Toast.makeText(getApplicationContext(), "ERROR EN LA CONSULTA", Toast.LENGTH_SHORT).show();
                success = false;
            }
            //myAppAdapter = new MyAppAdapter(itemArrayList, MainActivity.this);
            //recyclerView.setAdapter(myAppAdapter);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public Connection conexionDB(){
        Connection conexion=null;
        try{
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            conexion= DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.11;databaseName=Bodega;user=Movil;password=Mv2021;");

        }catch(Exception e){
            Toast toast = Toast.makeText(getApplicationContext(),"SIN CONEXIÓN A BASE DE DATOS",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        return conexion;
    }

}
