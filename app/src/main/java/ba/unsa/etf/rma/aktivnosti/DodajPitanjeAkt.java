package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.AdapterZaListuOdgovora;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajPitanjeAkt extends AppCompatActivity {

    Button dodajTacan;
    Button dodajOdgovor;
    Button dodajPitanje;
    ListView lvOdgovori;
    EditText etNaziv;
    EditText etOdgovor;
    ArrayList<String> alOdgovori = new ArrayList<>();
    AdapterZaListuOdgovora adapterZaListuOdgovora;
    Pitanje trenutnoPitanje = new Pitanje();
    public static boolean tacanDodan = false;
    public static String tacanOdgovor = null;
    Kviz trenutniKviz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_pitanje_akt);

        dodajTacan = (Button) findViewById(R.id.btnDodajTacan);
        dodajOdgovor = (Button) findViewById(R.id.btnDodajOdgovor);
        dodajPitanje = (Button) findViewById(R.id.btnDodajPitanje);
        lvOdgovori = (ListView) findViewById(R.id.lvOdgovori);
        etNaziv = (EditText) findViewById(R.id.etNaziv);
        etOdgovor = (EditText) findViewById(R.id.etOdgovor);
        adapterZaListuOdgovora = new AdapterZaListuOdgovora(this, alOdgovori);
        lvOdgovori.setAdapter(adapterZaListuOdgovora);
        trenutniKviz = (Kviz)getIntent().getSerializableExtra("trenutniKviz");
        dodajTacan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tacanDodan && !etOdgovor.getText().toString().equals("")) {
                    boolean vecDodano = false;
                    for (String odg : alOdgovori) {
                        if (odg.equals(etOdgovor.getText().toString()))
                            vecDodano = true;
                    }
                    if (!vecDodano) {
                        trenutnoPitanje.setTacan(etOdgovor.getText().toString());
                        alOdgovori.add(etOdgovor.getText().toString());
                        tacanOdgovor = etOdgovor.getText().toString();
                        trenutnoPitanje.setOdgovori(alOdgovori);
                        etOdgovor.setText("");
                        adapterZaListuOdgovora.notifyDataSetChanged();
                        tacanDodan = true;
                        lvOdgovori.setBackgroundColor(Color.parseColor("#fafafa"));
                    }

                }
            }
        });

        dodajOdgovor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!etOdgovor.getText().toString().equals("")) {
                    boolean vecDodano = false;
                    for (String odg : alOdgovori) {
                        if (odg.equals(etOdgovor.getText().toString()))
                            vecDodano = true;
                    }
                    if (!vecDodano) {
                        alOdgovori.add(etOdgovor.getText().toString());
                        trenutnoPitanje.setOdgovori(alOdgovori);
                        etOdgovor.setText("");
                    }
                    adapterZaListuOdgovora.notifyDataSetChanged();
                }
            }
        });

        lvOdgovori.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String odabraniOdgovor = parent.getItemAtPosition(position).toString();
                if (odabraniOdgovor.equals(tacanOdgovor)) {
                    tacanOdgovor = null;
                    tacanDodan = false;
                }
                alOdgovori.remove(odabraniOdgovor);
                adapterZaListuOdgovora.notifyDataSetChanged();
            }
        });

        dodajPitanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean nazivPrazan = false;
                boolean pitanjeVecPostoji = false;
                boolean nemaTacnogOdgovora = false;
                boolean pokreni = true;
                if (etNaziv.getText().toString().equals("")) {
                    etNaziv.setBackgroundColor(Color.parseColor("#ff0006"));
                    nazivPrazan = true;
                    pokreni = false;
                }
                if (tacanOdgovor == null) {
                    lvOdgovori.setBackgroundColor(Color.parseColor("#ff0006"));
                    nemaTacnogOdgovora = true;
                    pokreni = false;
                }
                if( !trenutniKviz.getNaziv().equals("Dodaj kviz") ) {
                    for (Pitanje p : trenutniKviz.getPitanja())
                        if( p.getNaziv().equals( etNaziv.getText().toString() ) ) {
                            pitanjeVecPostoji = true;
                            pokreni = false;
                            etNaziv.setBackgroundColor(Color.parseColor("#ff0006"));
                        }
                }
                if (!pokreni) {
                    String s = "";
                    if( nazivPrazan ) s += "Unesi naziv pitanja!";
                    if( pitanjeVecPostoji ) s += "Pitanje vec postoji!";
                    int trenutnaDuzina = s.length();
                    if( !s.equals("") ) s += "\n";
                    if( nemaTacnogOdgovora ) s += "Unesi tacan odgovor!";
                    int novaDuzina = s.length();
                    if( trenutnaDuzina + 1 == novaDuzina && nazivPrazan ) s = "Unesi naziv pitanja!";
                    if( trenutnaDuzina + 1 == novaDuzina && pitanjeVecPostoji ) s = "Pitanje vec postoji!";
                    Toast.makeText(v.getContext(), s, Toast.LENGTH_SHORT ).show();
                }
                else{
                    Pitanje pitanje = new Pitanje();
                    pitanje.setNaziv( etNaziv.getText().toString() );
                    pitanje.setOdgovori( alOdgovori );
                    pitanje.setTekstPitanja( etNaziv.getText().toString() );
                    pitanje.setTacan( tacanOdgovor );
                    tacanOdgovor = null;
                    tacanDodan = false;
                    Intent resIntent = new Intent();
                    resIntent.putExtra("novoPitanje", pitanje );
                    setResult( RESULT_OK, resIntent );
                    finish();
                }
            }
        });

        etNaziv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!etNaziv.getText().toString().equals("")) {
                    etNaziv.setBackgroundColor(Color.parseColor("#fafafa"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}

