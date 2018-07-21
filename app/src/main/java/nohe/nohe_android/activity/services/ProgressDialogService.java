package nohe.nohe_android.activity.services;

import android.app.Activity;
import android.app.ProgressDialog;

public class ProgressDialogService {
    private ProgressDialog pDialog;

    public ProgressDialogService(Activity parent) {
        this.pDialog = new ProgressDialog(parent);
        this.pDialog.setCancelable(false);
    }

    public void showDialog(String text) {
        pDialog.setMessage(text);
        if (!pDialog.isShowing())
            pDialog.show();
    }

    public void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

