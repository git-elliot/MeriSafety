package com.developers.droidteam.merisafety;

import android.os.AsyncTask;
import android.util.Log;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

/**
 * Created by paras on 23/11/17.
 */

public class SendMail extends AsyncTask<Void, Void, Void> {

    private String mTo;
    private String mFrom;
    private String mText;
    private String donotreply = "This is an automated mail from MeriSafety.Do not reply to this email,";
    private String mSubject;
    private String api_key;
    public SendMail(String to, String subject, String text,String api){
        mTo=to;
        mFrom="noreply@MeriSafety";
        mText=text+donotreply;
        mSubject=subject;
        api_key=api;

    }

    protected Void doInBackground(Void... params) {
        try {
            SendGrid sendgrid = new SendGrid(api_key);

            SendGrid.Email email = new SendGrid.Email();

            // Get values from edit text to compose email
            // TODO: Validate edit texts

            email.addTo(mTo);
            email.setFrom(mFrom);
            email.setSubject(mSubject);
            email.setText(mText);

            // Attach image
       /*     if (mUri != null) {
                email.addAttachment(mAttachmentName, mAppContext.getContentResolver().openInputStream(mUri));
            }
           */
            // Send email, execute http request
            SendGrid.Response response = sendgrid.send(email);

            Log.d("SendAppExample",response.getMessage());

        }catch(SendGridException e) {
            Log.e("SendAppExample", e.toString());
        }

       return null;
    }
}
