package com.example.formlocationapp.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.formlocationapp.AlertBuilder;
import com.example.formlocationapp.MainActivity;
import com.example.formlocationapp.R;
import com.example.formlocationapp.net.RetrofitClient;
import com.example.formlocationapp.net.SimpleApi;
import com.google.common.base.Strings;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView mkeyTextView;
    private TextView mValueTextView;
    private int mPosition;
    private TextView mUrlTextView;
    private Button mButtonSend;
    private Button mButtonShowMap;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        mkeyTextView = view.findViewById(R.id.key);
        mValueTextView = view.findViewById(R.id.value);
        mButtonShowMap = view.findViewById(R.id.show_map);
        mButtonSend = view.findViewById(R.id.send);
        mButtonSend.setOnClickListener(this);
        mButtonShowMap.setOnClickListener(this);
        mUrlTextView = view.findViewById(R.id.url);
        Spinner mSpinnerMethod = view.findViewById(R.id.spinner_method);
        mSpinnerMethod.setOnItemSelectedListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int sendForm = mButtonSend.getId();
        int showMap = mButtonShowMap.getId();
        if (id == sendForm) {
            sendData();
        } else if (id == showMap) {
            showMap();
        }
    }

    private void showMap() {
        MainActivity activity = (MainActivity) getActivity();
        Objects.requireNonNull(activity).showMap();
    }

    private void sendData() {
        String key = mkeyTextView.getText().toString();
        String value = mValueTextView.getText().toString();
        String url = mUrlTextView.getText().toString();
        if (!Strings.isNullOrEmpty(key) && !Strings.isNullOrEmpty(value) && !Strings.isNullOrEmpty(url)) {
            if (URLUtil.isValidUrl(url)) {
                SimpleApi simpleApiEndPoint = RetrofitClient.getSimpleApiEndPoint(url);
                Call<String> result = null;
                switch (mPosition) {
                    case 0:
                        result = simpleApiEndPoint.get(key, value);
                        break;
                    case 1:
                        result = simpleApiEndPoint.post(key, value);
                        break;
                    case 2:
                        result = simpleApiEndPoint.put(key, value);
                        break;
                    case 3:
                        result = simpleApiEndPoint.delete(key, value);
                        break;
                }
                if (result != null) {
                    result.enqueue(getCallback());
                }
            }
        }
    }

    @NonNull
    private Callback<String> getCallback() {
        return new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String body = response.body();
                if (Strings.isNullOrEmpty(body)) {
                    try {
                        body = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                AlertBuilder.ShowAlert(getActivity(), body, new AlertBuilder.OnResponseUser<Boolean>() {
                    @Override
                    public void onResponse(Boolean response) {
                        AlertBuilder.HideAlert();
                    }

                    @Override
                    public void onError() {
                        AlertBuilder.HideAlert();
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "Network error, please try later", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mPosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getActivity(), "Please select some method", Toast.LENGTH_SHORT).show();
    }
}
