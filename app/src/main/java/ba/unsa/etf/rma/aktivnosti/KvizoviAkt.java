package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.AdapterZaListuKvizova;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class KvizoviAkt extends AppCompatActivity {

    private ListView listaKvizova;
    private Spinner spinnerKategorije;

    //Lista 'kvizovi' se koristi za cuvanje svih postojecih kvizova,
    //dok se lista 'prikazaniKvizovi' koristi za prikazivanje svih/filtriranih kvizova.
    private ArrayList<Kviz> kvizovi = new ArrayList<>();
    private ArrayList<Kviz> prikazaniKvizovi = new ArrayList<>();

    public static ArrayList<Kategorija> kategorije = new ArrayList<>();
    private ArrayAdapter<Kategorija> adapterZaSpinner;
    private AdapterZaListuKvizova adapterZaListuKvizova;
    String spinnerOdabir;
    private static int pozicijaKviza;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Potrebno je dodijeliti sve vrijednosti pomocu id-a.
        listaKvizova = (ListView) findViewById(R.id.lvKvizovi);
        spinnerKategorije = (Spinner) findViewById(R.id.spPostojeceKategorije);

        //Postavljanje adaptera.
        adapterZaSpinner = new ArrayAdapter<Kategorija>(this, android.R.layout.simple_list_item_1, kategorije);
        spinnerKategorije.setAdapter(adapterZaSpinner);
        adapterZaListuKvizova = new AdapterZaListuKvizova(this,prikazaniKvizovi);
        listaKvizova.setAdapter(adapterZaListuKvizova);

        //Aplikacija se na pocetku ne puni nikakvim podacima, osim onim potrebnim za sam rad aplikacije.
        inicijalizirajApp();

        //Slusac koji vrsi filtriranje listView-a svih kvizova na osnovu odabrane kategorije
        //u spinneru, prikazivanje Toast poruke za korisnika, npr. "Odabrano: Svi".
        spinnerKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapterZaSpinner.notifyDataSetChanged();

                //Stvaranje string vrijednosti za Toast poruku.
                String text = parent.getItemAtPosition( position ).toString();
                spinnerOdabir = text;
                Toast.makeText( parent.getContext(), "Odabrano: " + text, Toast.LENGTH_SHORT ).show();

                //Odabaremo li element (koji je uvijek prisutan) u spinneru kategorija, prikazat ce se
                //svi kreirani kvizovi unutar aplikacije.
                if( text.equals("Svi") ){
                    prikazaniKvizovi.clear();
                    for( int i = 0; i < kvizovi.size(); i++ )
                        prikazaniKvizovi.add( kvizovi.get(i) );
                }
                //Odaberemo li bilo koji drugi element, prikazat ce se svi kvizovi koji pripadaju kategoriji
                //odabranoj u spinneru kategorija.
                else{
                    prikazaniKvizovi.clear();
                    for( int i = 0; i < kvizovi.size(); i++ )
                        if( !kvizovi.get(i).getNaziv().equals("Dodaj kviz") && kvizovi.get(i).getKategorija().getNaziv().equals(text) )
                            prikazaniKvizovi.add( kvizovi.get(i) );

                    //Filtrirali smo sve potrebne kvizove, potrebno je i dodati element "Dodaj kviz"
                    //pomocu kojeg dodajemo novi kviz.

                }
                Kviz k = new Kviz();
                k.setNaziv("Dodaj kviz");
                prikazaniKvizovi.add( k );
                adapterZaListuKvizova.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Ukoliko ne selektujemo nista, nista se nece desiti.
            }
        });

        //Ukoliko kliknemo na neki od elemenata kviza, otvara se nova aktivnost za kreiranje novog kviza
        //ukoliko je odabran element "Dodaj kviz", odnosno za uredjivanje ukoliko je odabran bilo koji drugi element.
        listaKvizova.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent dodajKvizAkt = new Intent( KvizoviAkt.this, DodajKvizAkt.class );
                dodajKvizAkt.putExtra( "sviKvizovi", kvizovi );
                dodajKvizAkt.putExtra("trenutniKviz", (Kviz)parent.getItemAtPosition(position) );
                dodajKvizAkt.putExtra( "sveKategorije", kategorije );
                for( int i = 0; i < kvizovi.size(); i++ )
                    if( kvizovi.get(i).getNaziv().equals( ((Kviz) parent.getItemAtPosition(position)).getNaziv() ) )
                        pozicijaKviza = i;
                KvizoviAkt.this.startActivityForResult( dodajKvizAkt, pozicijaKviza );
            }
        });
    }

    private void napuniPodacima() {
        //FUNKCIJU MOZEMO KORISTITI UKOLIKO ZELIMO NAPUNITI APLIKACIJU RANDOM PODACIMA ODMAH NAKON
        //POKRETANJA APLIKACIJE I TO TAKO STO LINIJU KODA 55 'inicijalizirajApp();' ZAMIJENIMO SA
        // 'napuniPodacima();'.

        //Kreirajmo kategoriju
        Kategorija k1 = new Kategorija();
        k1.setNaziv("Lagana");
        k1.setId("0");

        Kategorija k2 = new Kategorija();
        k2.setNaziv("Srednja");
        k2.setId("1");

        //Dodajmo par pitanja
        ArrayList<String> odgovori1 = new ArrayList<>();
        odgovori1.add("Plavo");
        odgovori1.add("Crveno");
        odgovori1.add("Zeleno");
        Pitanje p1 = new Pitanje();
        p1.setNaziv("Pitanje 1");
        p1.setTekstPitanja("Koje je boje nebo?");
        p1.setOdgovori(odgovori1);
        p1.setTacan("Plavo");

        ArrayList<String> odgovori2 = new ArrayList<>();
        odgovori2.add("Luk");
        odgovori2.add("Paprika");
        odgovori2.add("Jabuka");
        Pitanje p2 = new Pitanje();
        p2.setNaziv("Pitanje 2");
        p2.setTekstPitanja("Sta od sljedeceg je voce?");
        p2.setOdgovori(odgovori2);
        p2.setTacan("Jabuka");

        ArrayList<String> odgovori3 = new ArrayList<>();
        odgovori3.add("C4");
        odgovori3.add("A3");
        odgovori3.add("X5");
        Pitanje p3 = new Pitanje();
        p3.setNaziv("Pitanje 3");
        p3.setTekstPitanja("Koji model Audija postoji?");
        p3.setOdgovori(odgovori3);
        p3.setTacan("A3");

        ArrayList<String> odgovori4 = new ArrayList<>();
        odgovori4.add("7");
        odgovori4.add("6");
        odgovori4.add("5");
        Pitanje p4 = new Pitanje();
        p4.setNaziv("Pitanje 4");
        p4.setTekstPitanja("Koliko ima dana u sedmici?");
        p4.setOdgovori(odgovori4);
        p4.setTacan("7");

        ArrayList<String> odgovori5 = new ArrayList<>();
        odgovori5.add("3");
        odgovori5.add("4");
        odgovori5.add("5");
        Pitanje p5 = new Pitanje();
        p5.setNaziv("Pitanje 5");
        p5.setTekstPitanja("Koliko ima godisnjih doba?");
        p5.setOdgovori(odgovori5);
        p5.setTacan("4");

        //Napravimo i kvizove
        ArrayList<Pitanje> pitanjaZaKviz1 = new ArrayList<>();
        pitanjaZaKviz1.add(p1);
        pitanjaZaKviz1.add(p2);
        pitanjaZaKviz1.add(p3);
        Kviz kviz1 = new Kviz();
        kviz1.setNaziv("Kviz 1");
        kviz1.setKategorija(k1);
        kviz1.setPitanja(pitanjaZaKviz1);

        ArrayList<Pitanje> pitanjaZaKviz2 = new ArrayList<>();
        pitanjaZaKviz2.add(p3);
        pitanjaZaKviz2.add(p4);
        pitanjaZaKviz2.add(p5);
        Kviz kviz2 = new Kviz();
        kviz2.setNaziv("Kviz 2");
        kviz2.setKategorija(k2);
        kviz2.setPitanja(pitanjaZaKviz2);

        Kategorija apstraktnaKategorija = new Kategorija();
        apstraktnaKategorija.setNaziv("Svi");
        apstraktnaKategorija.setId("10");
        Kviz apstraktniKviz = new Kviz();
        apstraktniKviz.setNaziv("Dodaj kviz");

        kvizovi.add(kviz1);
        kvizovi.add(kviz2);
       // kvizovi.add(apstraktniKviz);

        kategorije.add(apstraktnaKategorija);
        kategorije.add(k1);
        kategorije.add(k2);
        adapterZaSpinner.notifyDataSetChanged();

        prikazaniKvizovi.add( kviz1 );
        prikazaniKvizovi.add( kviz2 );
        prikazaniKvizovi.add( apstraktniKviz );
        adapterZaListuKvizova.notifyDataSetChanged();
    }

    private void inicijalizirajApp(){
        //Podaci za inicijalizaciju aplikacije.
        Kategorija kategorijaSvi = new Kategorija();
        kategorijaSvi.setNaziv("Svi");
        kategorijaSvi.setId("5");
        kategorije.add( kategorijaSvi );
        adapterZaListuKvizova.notifyDataSetChanged();
        adapterZaSpinner.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == pozicijaKviza ){
            if( resultCode == RESULT_OK ){
                spinnerKategorije.setSelection(0);
                Kviz kvizZaDodati = (Kviz)data.getExtras().get("noviKviz");
                boolean dodajNovi = (boolean)data.getExtras().get("dodajNoviKviz");
                if( dodajNovi ) {
                    kvizovi.add(kvizovi.size(), kvizZaDodati);
                }
                else {
//                    for (int i = 0; i < kvizovi.size(); i++)
//                        if (i == pozicijaKviza) {
//                            kvizovi.get(i).setNaziv(kvizZaDodati.getNaziv());
//                            kvizovi.get(i).setKategorija(kvizZaDodati.getKategorija());
//                            kvizovi.get(i).setPitanja(kvizZaDodati.getPitanja());
//                            System.out.println( kvizovi.get(i).getKategorija().getId() );
//                        }
                    ArrayList<Kviz> tempKvizovi = new ArrayList<>();
                    tempKvizovi.addAll( kvizovi );
                    kvizovi.clear();
                    for( int i = 0; i < tempKvizovi.size(); i++ ){
                        if( pozicijaKviza == i ) {
                            kvizovi.add(kvizZaDodati);
                           System.out.println( kvizZaDodati.getKategorija().getId() );
                        }
                        else
                            kvizovi.add( tempKvizovi.get(i) );
                    }
                    tempKvizovi.clear();
                }
                prikazaniKvizovi.clear();
                prikazaniKvizovi.addAll( kvizovi );

                Kviz k = new Kviz();
                k.setNaziv("Dodaj kviz");
                prikazaniKvizovi.add( k );
                adapterZaListuKvizova.notifyDataSetChanged();
                adapterZaSpinner.notifyDataSetChanged();
            }
            else{
                spinnerKategorije.setSelection(0);
                adapterZaSpinner.notifyDataSetChanged();
            }
        }
    }
}
