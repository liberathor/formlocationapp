package com.example.formlocationapp;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by DATA on 26/08/2015.
 */
public class AlertBuilder {
    private static AlertDialog mAlertDialog;

    public static void ShowAlert(final Context context, String resoruceMessage, final OnResponseUser<Boolean> mOnResponse) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(resoruceMessage)
                .setCancelable(false)
                .setNegativeButton("Close", (dialog, which) -> mOnResponse.onResponse(false))
                .setPositiveButton("Ok", (dialog, which) -> mOnResponse.onResponse(true));
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    public static void HideAlert() {
        if (mAlertDialog != null) {
            mAlertDialog.hide();
        }
    }

    @SuppressWarnings("EmptyMethod")
    public interface OnResponseUser<Clazz> {
        void onResponse(Clazz response);

        void onError();
    }
}
