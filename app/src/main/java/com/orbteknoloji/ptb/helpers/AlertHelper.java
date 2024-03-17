package com.orbteknoloji.ptb.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.orbteknoloji.ptb.R;
import com.orbteknoloji.ptb.enums.AlertType;

public class AlertHelper {
    public static void ShowAlertDialog(Context context, AlertType alertType, String title, String message, String positiveButtonTitle, DialogInterface.OnClickListener positiveButtonOnClickEvent, String negativeButtonTitle, DialogInterface.OnClickListener negativeButtonOnClickEvent,boolean isCancelable, DialogInterface.OnCancelListener cancelButtonOnClickEvent){
        if (context == null){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        switch (alertType){
            case INFO:
                builder.setIcon(R.drawable.ic_info_24);
                break;
            case ERROR:
                builder.setIcon(R.drawable.ic_error_24);
                break;
            case QUESTION:
                builder.setIcon(R.drawable.ic_info_24);
                break;
            case SYSTEM_ERROR:
                builder.setIcon(R.drawable.ic_error_24);
                break;
        }
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonTitle, positiveButtonOnClickEvent);
        builder.setNegativeButton(negativeButtonTitle, negativeButtonOnClickEvent);
        builder.setCancelable(isCancelable);
        builder.setOnCancelListener(cancelButtonOnClickEvent);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void ShowAlertDialog(Context context, AlertType alertType, String title, String message, String positiveButtonTitle, DialogInterface.OnClickListener positiveButtonOnClickEvent, String negativeButtonTitle, DialogInterface.OnClickListener negativeButtonOnClickEvent){
        if (context == null){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        switch (alertType){
            case INFO:
                builder.setIcon(R.drawable.ic_info_24);
                break;
            case ERROR:
                builder.setIcon(R.drawable.ic_error_24);
                break;
            case QUESTION:
                builder.setIcon(R.drawable.ic_info_24);
                break;
            case SYSTEM_ERROR:
                builder.setIcon(R.drawable.ic_error_24);
                break;
        }
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonTitle, positiveButtonOnClickEvent);
        builder.setNegativeButton(negativeButtonTitle, negativeButtonOnClickEvent);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void ShowAlertDialog(Context context, AlertType alertType, String title, String message, String positiveButtonTitle, DialogInterface.OnClickListener positiveButtonOnClickEvent, boolean isCancelable, DialogInterface.OnCancelListener cancelButtonOnClickEvent){
        if (context == null){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        switch (alertType){
            case INFO:
                builder.setIcon(R.drawable.ic_info_24);
                break;
            case ERROR:
                builder.setIcon(R.drawable.ic_error_24);
                break;
            case QUESTION:
                builder.setIcon(R.drawable.ic_info_24);
                break;
            case SYSTEM_ERROR:
                builder.setIcon(R.drawable.ic_error_24);
                break;
        }
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonTitle, positiveButtonOnClickEvent);
        builder.setCancelable(isCancelable);
        builder.setOnCancelListener(cancelButtonOnClickEvent);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void ShowAlertDialog(Context context, AlertType alertType, String title, String message, String positiveButtonTitle, DialogInterface.OnClickListener positiveButtonOnClickEvent){
        if (context == null){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        switch (alertType){
            case INFO:
                builder.setIcon(R.drawable.ic_info_24);
                break;
            case ERROR:
                builder.setIcon(R.drawable.ic_error_24);
                break;
            case QUESTION:
                builder.setIcon(R.drawable.ic_info_24);
                break;
            case SYSTEM_ERROR:
                builder.setIcon(R.drawable.ic_error_24);
                break;
        }
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonTitle, positiveButtonOnClickEvent);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void ShowAlertDialog(Context context, AlertType alertType, String title, String message){
        if (context == null){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        switch (alertType){
            case INFO:
                builder.setIcon(R.drawable.ic_info_24);
                break;
            case ERROR:
                builder.setIcon(R.drawable.ic_error_24);
                break;
            case QUESTION:
                builder.setIcon(R.drawable.ic_info_24);
                break;
            case SYSTEM_ERROR:
                builder.setIcon(R.drawable.ic_error_24);
                break;
        }
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void ShowAlertDialog(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
