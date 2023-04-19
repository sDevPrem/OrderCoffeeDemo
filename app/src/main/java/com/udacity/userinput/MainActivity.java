package com.udacity.userinput;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * This app displays an order form to order coffee.
 */
public class MainActivity extends AppCompatActivity {

    Toppings toppings;

    int quantity = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_main.xml layout file
        setContentView(R.layout.main);
        toppings = addToppings();
    }

    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {
        TextView grandTotal = (TextView) findViewById(R.id.grandTotal);
        EditText edit = ((EditText) findViewById(R.id.name));
        if (findName().length() == 0) {
            edit.requestFocus();
            edit.setError("Field Cannot Be Empty");
        } else {
            String gTotal = "Name : " + findName().toUpperCase();
            gTotal += "\nChocolate cream Topping : " + formatNum((toppings.hasChocolate() ? (quantity * 2) : 0));
            gTotal += "\nWhipped Topping : +" + formatNum((toppings.hasWCream() ? (quantity) : 0));
            gTotal += "\nTotal Coffee Price : +" + formatNum(quantity * 5);
            gTotal += "\nService Charge : +" + ((calculatePrice() >= 200) ? formatNum(0) : formatNum(5));
            gTotal += "\nYou have to pay : " + formatNum(calculatePrice() + 5);
            grandTotal.setText(gTotal);
            sendEmail(gTotal, findName());
        }
    }

    //send email
    private void sendEmail(String msg, String clientName) {
        String[] emails = {"User@example.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); //send only to email
        intent.putExtra(Intent.EXTRA_EMAIL, emails);
        intent.putExtra(Intent.EXTRA_SUBJECT, clientName + " Ordered: " + quantity + ((quantity == 1) ? " cup" : " cups") + " of coffee");
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Choose an email client to send order"));
        }
    }

    //converting numbers to local currency
    private <T extends Number> String formatNum(T num) {
        return NumberFormat.getCurrencyInstance().format(num);
    }

    /**
     * Calculate the total price (coffee with toppings)
     * without service charge
     */
    private int calculatePrice() {
        int price = 0;
        price = quantity * 5;
        if (toppings.hasChocolate()) {
            price += (2 * quantity);
        }
        if (toppings.hasWCream()) {
            price += (quantity);
        }
        return price;
    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private void display() {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        quantityTextView.setText("" + quantity);
        TextView priceView = (TextView) findViewById(R.id.price_text_view);
        priceView.setText(NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(calculatePrice()));

    }

    public void changeQuantity(View view) {
        if (view.getId() == R.id.ButtonIncrement) {
            quantity++;
        }
        if (view.getId() == R.id.ButtonDecrement) {
            if (quantity > 1) {
                quantity--;
            }
            if (quantity <= 1) {
                CharSequence toastText = "You can't have less than 1 cup of coffee";
                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
            }
        }
        display();
    }

    private Toppings addToppings() {
        return new Toppings(((CheckBox) findViewById(R.id.chocolate)), (CheckBox) findViewById(R.id.WCream));
    }

    private String findName() {
        return ((EditText) findViewById(R.id.name)).getText().toString();
    }
}//main activity

class Toppings {
    CheckBox chocolate, wcream;

    Toppings(CheckBox chocolate, CheckBox wcream) {
        this.chocolate = chocolate;
        this.wcream = wcream;
    }

    boolean hasChocolate() {
        return chocolate.isChecked();
    }

    boolean hasWCream() {
        return wcream.isChecked();
    }

}

