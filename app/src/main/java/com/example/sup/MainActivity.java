package com.example.sup;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    MyPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nastavi vsebino aktivnosti glede na definicijo v activity_main.xml
        setContentView(R.layout.activity_main);

        // Poveže ViewPager iz XML-ja z dejanskim objektom ViewPager v kodi
        viewPager = findViewById(R.id.viewPager);

        // Ustvari adapter, ki bo upravljal s fragmenti v ViewPager-ju
        adapterViewPager = new MyPagerAdapter(this);

        // Nastavi adapter za ViewPager, ki bo uporabljen za prikazovanje fragmentov
        viewPager.setAdapter(adapterViewPager);

        // Nastavi trenutni element v ViewPager-ju na drugi element (indeks 1)
        viewPager.setCurrentItem(1);
    }



// Adapter za upravljanje fragmentov v ViewPager-ju
public static class MyPagerAdapter extends FragmentStateAdapter {

    // Konstruktor, ki zahteva FragmentActivity kot parameter
    public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    // Metoda, ki se kliče, ko je potrebno ustvariti nov fragment glede na pozicijo
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Uporaba stikala (switch) za določanje, kateri fragment ustvariti glede na pozicijo
        switch (position) {
            case 0:
                return ChatFragments.newInstance(); // Ustvari in vrni fragment za klepet
            case 1:
                return CameraFragment.newInstance(); // Ustvari in vrni fragment za kamero
            case 2:
                return StoryFragment.newInstance(); // Ustvari in vrni fragment za zgodbe
        }
        return new Fragment(); // Če je pozicija neveljavna, vrni privzeti prazen fragment (lahko prilagodite glede na vaše potrebe)
    }

    // Metoda, ki vrne število fragmentov, ki jih bo adapter upravljal
    @Override
    public int getItemCount() {
        return 3; // V tem primeru je 3, ker imamo tri fragmente (Chat, Camera, Story)
    }
}

}