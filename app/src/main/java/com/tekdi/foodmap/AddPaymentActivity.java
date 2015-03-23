package com.tekdi.foodmap;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddPaymentActivity extends FragmentActivity {

    /*
     * Change this to your publishable key.
     *
     * You can get your key here: https://manage.stripe.com/account/apikeys
     */
    public static final String PUBLISHABLE_KEY = "pk_test_npiJ9fcyu48YHPFqy9LaOxeM";


    private NumberPicker monthPicker;
    private NumberPicker yearPicker;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        context = this.getApplicationContext();
        

            setContentView(R.layout.activity_payment);

            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);

            monthPicker =
                    (NumberPicker) findViewById(R.id.credit_card_expiry_month);
            monthPicker.setMaxValue(12);
            monthPicker.setMinValue(1);
            monthPicker.setWrapSelectorWheel(false);

            yearPicker =
                    (NumberPicker) findViewById(R.id.credit_card_expiry_year);
            yearPicker.setMaxValue(year + 10);
            yearPicker.setMinValue(year);
            yearPicker.setWrapSelectorWheel(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_credit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save_card:
                setupCreditCard();
                break;
            case R.id.action_cancel:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupCreditCard() {

        EditText editText = (EditText) findViewById(R.id.credit_card_number);
        String creditCardNumber = editText.getText().toString();

        // editText = (EditText) findViewById(R.id.credit_card_expiry_month);
        int creditCardExpiryMonth = monthPicker.getValue();

        //editText = (EditText) findViewById(R.id.credit_card_expiry_year);
        int creditCardExpiryYear = yearPicker.getValue();

        editText = (EditText) findViewById(R.id.credit_card_cvc);
        String creditCardCVC = editText.getText().toString();

        if ((creditCardNumber == null) || (creditCardNumber.isEmpty())) {
            handleError("Please enter a credit card number");
        } else if ((creditCardCVC == null) || (creditCardCVC.isEmpty())) {
            handleError("Please enter the CVC number\n" +
                    "The CVC Number on your credit card or debit card is a 3 digit number on VISA®, MasterCard® and Discover® branded credit and debit cards. On your American Express® branded credit or debit card it is a 4 digit numeric code");
        } else {

            Card card = new Card(creditCardNumber,
                    creditCardExpiryMonth,
                    creditCardExpiryYear,
                    creditCardCVC);

            boolean validation = card.validateCard();

            if (validation) {
                new Stripe().createToken(
                        card,
                        PUBLISHABLE_KEY,
                        new TokenCallback() {
                            public void onSuccess(Token token) {
                                Log.v("sajid","token created " + token.getId());

                                try {
                                    Log.v("sajid","creating customer");
                                    Map<String, Object> customerParams = new HashMap<String, Object>();
                                    com.stripe.Stripe.apiKey = "sk_test_OVP2anIL7SmvBehKb332BL1I";
                                    customerParams.put("description", "Customer for test@example.com");
                                    customerParams.put("source", token.getId()); // Obtained in onSuccess() method of TokenCallback
                                    // while creating token above

                                    //Create a Customer
                                    Customer customer = Customer.create(customerParams);


                                    Log.v("sajid","customer "+customer.getId());
                                    
                                    Prefs.setCreditCard(context, customer);
                                    Prefs.setCreditCardLast4(context,token.getCard().getLast4());
                                    finish();
                                } catch (StripeException e) {
                                    Log.e("sajid", "e.getCode() ...." + e.getMessage());
                                }
                            }

                            public void onError(Exception error) {
                                handleError(error.getLocalizedMessage());
                            }
                        });
            } else if (!card.validateNumber()) {
                handleError("The card number that you entered is invalid");
            } else if (!card.validateExpiryDate()) {
                handleError("The expiration date that you entered is invalid");
            } else if (!card.validateCVC()) {
                handleError("The CVC code that you entered is invalid. " +
                        "The CVC Number on your credit card or debit card is a 3 digit number on VISA®, MasterCard® and Discover® branded credit and debit cards. On your American Express® branded credit or debit card it is a 4 digit numeric code");
            } else {
                handleError("The card details that you entered are invalid");
            }
        }

    }

    private void handleError(String error) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(R.string.validationErrors, error);
        fragment.show(getSupportFragmentManager(), "error");
    }
}