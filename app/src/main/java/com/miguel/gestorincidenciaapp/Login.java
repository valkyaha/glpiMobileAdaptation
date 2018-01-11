package com.miguel.gestorincidenciaapp;

import android.content.Context;
import android.support.design.widget.TabLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {

    private static String usernameGet;
    private static String passwordGet;
    private static String phoneGet;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private String apptoken = "5o9yiRFgOUlOVYxZLnF1taKj67lnW4bSDUXGUlAj";
        private Retrofit retrofit;
        private GlpiClient glpi;
        private TokenInfo data;
        private String sessionToken;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_main, container, false);

            Retrofit.Builder builder = new Retrofit
                    .Builder()
                    .baseUrl("http://5.145.175.176/glpi/apirest.php/")
                    .addConverterFactory(GsonConverterFactory.create());

            retrofit = builder.build();

            View rootView = inflater.inflate(R.layout.mail, container, false);
            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case 1:

                    rootView = inflater.inflate(R.layout.mail, container, false);

                    Button btnLogin = rootView.findViewById(R.id.btnLogin);
                    final EditText mail = rootView.findViewById(R.id.edTxt_mail);
                    final EditText editPassword = rootView.findViewById(R.id.edTxT_passw);


                    btnLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(isEmailValid(mail)){

                                usernameGet = mail.getText().toString();

                            }

                            if(isPasswordValid(editPassword)){

                                passwordGet = editPassword.getText().toString();

                            }


                            Log.d("DATA TO SEND",usernameGet + "    " + passwordGet );

                            login(usernameGet,passwordGet);

                            usernameGet = "";
                            passwordGet = "";

                        }
                    });

                    break;

                case 2:

                    rootView = inflater.inflate(R.layout.phone, container, false);
                    final EditText edPhone = rootView.findViewById(R.id.edtPhone);

                    Button phonelogin = rootView.findViewById(R.id.btnLoginPhone);

                    phonelogin.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            if(isPhoneValid(edPhone)){

                                phoneGet = edPhone.getText().toString();
                            }
                        }
                    });


                    break;
            }

            return rootView;
        }

        private boolean isPhoneValid(EditText auxPhone) {

            Log.d("PHON LOG","Boolean check");

            if(!Patterns.PHONE.matcher(auxPhone.getText().toString()).matches()){

                auxPhone.setError("Format del telefon no valid");
                return false;

            } else if(auxPhone.getText().toString().equals("")){

                auxPhone.setError("El camp telefon no pot estar vuit");
                return false;

            } else {

                return true;
            }

        }

        private boolean isPasswordValid(EditText passwordValid) {

            if(passwordValid.getText().toString().equals("")){

                passwordValid.setError("El camp contrasenya no pot estar vuit");
                return false;

            } else {

                return true;

            }

        }

        private boolean isEmailValid(EditText mailValid) {

            if(mailValid.getText().toString().equals("")){

                mailValid.setError("El camp usuari no pot estar vuit");
                return false;

            } else {

                return true;
            }

        }

        private void login(String username, String password) {
            glpi = retrofit.create(GlpiClient.class);
            Call<TokenInfo> call = glpi.initSession(username, password, apptoken);
            call.enqueue(new Callback<TokenInfo>() {

                @Override
                public void onResponse(Call<TokenInfo> call, Response<TokenInfo> response) {

                    if(response.isSuccessful()){

                        data = response.body();
                        Log.d("InitSessionResponse", response.toString());

                        Log.d("InitSessionResponse", response.body().toString());

                        getFullSession(call, response.body().getSessionToken());


                    } else {

                        ResponseBody body = response.errorBody();
                        Log.d("ERROR", body.toString());

                        try {

                            Toast.makeText(getContext(), "ERROR" + body.string(), Toast.LENGTH_LONG).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TokenInfo> call, Throwable t) {}
            });
        }

        private void getFullSession(Call<TokenInfo> call, final String sessionToken1) {
            call = null;
            call = glpi.getFullSession( sessionToken1 , apptoken);
            call.enqueue(new Callback<TokenInfo>() {
                @Override
                public void onResponse(Call<TokenInfo> call, Response<TokenInfo> response) {

                    Log.d("URL", call.request().url().toString());

                    if(response.isSuccessful()){

                        data = response.body();
                        Log.d("DATA", "DATA" + " Funciona el full session");

                        Toast.makeText(getContext(),"DATA" + sessionToken1, Toast.LENGTH_LONG).show();

                    } else {

                        ResponseBody body = response.errorBody();
                        Log.d("ERROR", body.toString());

                        try {

                            Toast.makeText(getContext(), "ERROR" + body.string(), Toast.LENGTH_LONG).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    sessionToken = sessionToken1;

                }

                @Override
                public void onFailure(Call<TokenInfo> call, Throwable t) {

                    Context context = getContext();
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}