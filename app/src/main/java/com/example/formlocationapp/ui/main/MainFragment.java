package com.example.formlocationapp.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.formlocationapp.AlertBuilder;
import com.example.formlocationapp.MainActivity;
import com.example.formlocationapp.R;
import com.example.formlocationapp.net.RetrofitClient;
import com.example.formlocationapp.net.SimpleApi;
import com.google.common.base.Strings;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private TextView mKeyTextView;
    private TextView mValueTextView;
    private int mPosition;
    private TextView mUrlTextView;
    private Button mButtonSend;
    private Button mButtonShowMap;
    private ToggleButton mButtonTracking;
    private OnFragmentInteractionListener mListener;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        mKeyTextView = view.findViewById(R.id.key);
        mValueTextView = view.findViewById(R.id.value);
        mButtonShowMap = view.findViewById(R.id.show_map);
        mButtonSend = view.findViewById(R.id.send);
        mButtonSend.setOnClickListener(this);
        mButtonShowMap.setOnClickListener(this);
        mUrlTextView = view.findViewById(R.id.url);
        mButtonTracking = view.findViewById(R.id.start_tracking);
        mButtonTracking.setOnCheckedChangeListener(this);
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

    private void switchTraking(boolean isChecked) {
        mListener.setStatusTracking(isChecked);
    }

    private void showMap() {
        MainActivity activity = (MainActivity) getActivity();
        Objects.requireNonNull(activity).showMap();
    }

    private void sendData() {
        String key = mKeyTextView.getText().toString();
        String value = mValueTextView.getText().toString();
        String url = mUrlTextView.getText().toString();
        boolean isEmptyKey = Strings.isNullOrEmpty(key);
        boolean isEmptyValue = Strings.isNullOrEmpty(value);
        boolean isEmptyUrl = Strings.isNullOrEmpty(url);
        if (!isEmptyKey && !isEmptyValue && !isEmptyUrl) {
            if (URLUtil.isValidUrl(url)) {
                mKeyTextView.setError(null);
                mValueTextView.setError(null);
                mUrlTextView.setError(null);
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
            } else {
                mUrlTextView.setError("Please insert a valid url");
            }
        }
        if (isEmptyKey) {
            mKeyTextView.setError("Key is empty");
        }
        if (isEmptyUrl) {
            mUrlTextView.setError("Url is empty");
        }
        if (isEmptyValue) {
            mValueTextView.setError("Value is empty");
        }
    }

    @NonNull
    private Callback<String> getCallback() {
        return new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                String body = response.body();
                if (Strings.isNullOrEmpty(body)) {
                    try {
                        body = Objects.requireNonNull(response.errorBody()).string();
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
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        int traking = mButtonTracking.getId();
        if (id == traking) {
            switchTraking(isChecked);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        super.onAttach(context);
    }

    public interface OnFragmentInteractionListener {
        void setStatusTracking(boolean tracking);
    }
}
